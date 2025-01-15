package nl.saxion.managers;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.observer.PrintTaskObserver;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;
import nl.saxion.adapter.AdapterReader;
import nl.saxion.exceptions.ColorNotFoundException;
import nl.saxion.utils.FilamentType;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrinterManager {
    public PrinterFactory printerFactory = new PrinterFactory(this);

    // all the printers and print tasks
    public final Map<Printer, ArrayList<PrintTask>> printersMap = new HashMap<>();

    public List<Printer> printersList = new ArrayList<>();
    public List<Printer> freePrinters = new ArrayList<>();

    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    public Map<Printer, PrintTask> runningPrintTasks = new HashMap();


    private List<Spool> freeSpools = new ArrayList<>();

    private final List<PrintTaskObserver> observers = new ArrayList<>();


    // todo discuss: should we make static methods for the spools nad prints insteas of using instances???
    public PrinterManager(SpoolManager spoolManager) {

        freeSpools = spoolManager.getSpools();
        freePrinters = new ArrayList<>(printersList);
    }

    /**
     * loops through all the pending print tasks
     */
    public void selectPrintTask() {

        for (int i = 0; i < pendingPrintTasks.size(); i++) {
            loopThroughPrinter(pendingPrintTasks.get(i));
        }
        startInitalQueue();
    }

    // choose the print and it here
    private void startInitalQueue() {
        for (Map.Entry<Printer, ArrayList<PrintTask>> entry : printersMap.entrySet()) {
            Printer printer = entry.getKey();
            ArrayList<PrintTask> tasks = entry.getValue();

            if (!tasks.isEmpty()) {
                PrintTask firstTask = tasks.removeFirst();
                runningPrintTasks.put(printer, firstTask);
                pendingPrintTasks.remove(firstTask);
            }
        }
    }

    /**
     * loops through all the printers, so to find the appropriate one
     *
     * @param printTask
     */
    private void loopThroughPrinter(PrintTask printTask) {
        for (int i = 0; i < freePrinters.size(); i++) {
            Printer printer = freePrinters.get(i);

            if (taskSuitsPrinter(freePrinters.get(i), printTask)) {
                if (printer.printFits(printTask.getPrint())) {

                    printer.setCurrentSpools(assignProperSpool(printTask));
                    addTasksToPrinter(printer, printTask);

                    freePrinters.remove(printer);
                    return;

                }
            }
        }
    }

    private void addTasksToPrinter(Printer printer, PrintTask printTask) {
        ArrayList<PrintTask> printTasks = printersMap.get(printer);
        printTasks.add(printTask);
        printersMap.replace(printer, printTasks);

    }

    private void removeTasksFromPrinter(Printer printer, PrintTask printTask) {
        ArrayList<PrintTask> printTasks = printersMap.get(printer);
        printTasks.remove(printTask);
        printersMap.replace(printer, printTasks);

    }

    private List<Spool> assignProperSpool(PrintTask printTask) {
        List<Spool> printerSpools = new ArrayList<>();

        // keeps track of the needed color
        for (int i = 0; i < printTask.getColors().size(); i++) {
            Spool minSpool = null;

            for (int j = 0; j < freeSpools.size(); j++) {
                // checks if the color and the size match, and if there is enough filament
                if (freeSpools.get(j).spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) &&
                        printTask.getPrint().getSpecificFilamentLenght(i) <= freeSpools.get(j).getLength()) {
                    if (minSpool == null || minSpool.getLength() > freeSpools.get(j).getLength()) {
                        minSpool = freeSpools.get(j);
                    }
                }
            }

            printerSpools.add(minSpool);

        }

        freeSpools.removeAll(printerSpools);
        return printerSpools;
    }

    private boolean taskSuitsPrinter(Printer printer, PrintTask printTask) {

        FilamentType filamentType = printTask.getFilamentType();

        if ((printer.getMaxColors() >= printTask.getColors().size())) {
            switch (filamentType) {
                case FilamentType.ABS -> {
                    return printer.isHoused();
                }
                case FilamentType.PLA, FilamentType.PETG -> {
                    return true;
                }
            }
        }

        return false;
    }

    private void reduceLenghtOfSpools(PrintTask printTask, Printer printer) {
        for (int j=0;j<printer.getSpools().size();j++) {
            for (int i = 0; i < printTask.getColors().size(); i++) {
                Spool spoolNotEmpty = printer.getSpools().get(j);
                if(printer.getSpools().get(j)!=null){
                if (spoolNotEmpty.spoolMatch(printTask.getColors().get(i), spoolNotEmpty.getFilamentType())) {
                    spoolNotEmpty.reduceLength(printTask.getPrint().getSpecificFilamentLenght(i));
                }
            }}
        }
    }

    public void registerCompletion(int printerId) {
        Printer printer = findPrinterById(printerId);
        PrintTask printTask = runningPrintTasks.remove(printer);

        reduceLenghtOfSpools(printTask,printer);
        removeTasksFromPrinter(printer,printTask);
        freePrinters.add(printer);

        completeTask();
    }

    public void registerFailure(int printerId) {
        Printer printer = findPrinterById(printerId);
        PrintTask printTask = runningPrintTasks.remove(printer);

        pendingPrintTasks.add(printTask);

        freePrinters.add(printer);
        failTask();
    }

    public Printer findPrinterById(int id) {
        for (Printer printer : printersList) {
            if (printer.getId() == id) {
                return printer;
            }
        }
        return null;
    }

    public void completeTask() {
        notifyObservers("completed");
    }

    public void failTask() {
        notifyObservers("failed");
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if (!printersMap.containsKey(printer)) {
            return null;
        }
        return printersMap.get(printer).get(0);
    }


    /**
     * Method to add a printer to the printers list.
     *
     * @param observer The observer to add.
     */
    public void addObserver(PrintTaskObserver observer) {
        observers.add(observer);
    }

    /**
     * Method notify all observers of an event.
     *
     * @param event The observer to notify.
     */
    private void notifyObservers(String event) {
        for (PrintTaskObserver observer : observers) {
            observer.update(event);
        }
    }


    /**
     * Getter for the JSON file handler.
     */
    private AdapterReader getJsonFileHandler() {
        return JSONAdapterReader.getReader();
    }

    /**
     * Getter for the CSV file handler.
     */
    private AdapterReader getCsvFileHandler() {
        return CSVAdapterReader.getReader();
    }

    /**
     * Getter for the printers list.
     */
    public List<Printer> getPrinters() {
        return new ArrayList<>(printersList);
    }

    /**
     * Method to read printers from a file.
     * The file can be either a JSON or CSV file.
     * The method will determine the file type and use the appropriate handler.
     * The printers will be added to the printers list and the free printers list.
     *
     * @param filename The name of the file to read the printers from.
     */
    public void readPrintersFromFile(String filename) {
        URL printerResource = getClass().getResource("/" + filename);
        assert printerResource != null;
        String path = URLDecoder.decode(printerResource.getPath(), StandardCharsets.UTF_8);
        AdapterReader fileHandler;

        if (getJsonFileHandler().supportsFileType(path)) {
            fileHandler = getJsonFileHandler();
        } else if (getCsvFileHandler().supportsFileType(path)) {
            fileHandler = getCsvFileHandler();
        } else {
            System.out.println("Unsupported file type for filename: " + path);
            return;
        }


        List<Printer> printersFromFile = fileHandler.readPrinters(path);
        printersList.addAll(printersFromFile);
        freePrinters.addAll(printersFromFile);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return new ArrayList<>(pendingPrintTasks);
    }

    /**
     * Method to add a new printTask to the list of prints.
     *
     * @param printName the name of the print
     * @param colors    the colors of the print
     * @param type      the type of filament
     */

    public void addPrintTask(Print printName, List<String> colors, FilamentType type) {
        Print print = printName;
        if (print == null || colors.isEmpty()) {
            System.err.println("All fields must be filled in");
            return;
        }

        for (String color : colors) {
            boolean found = false;
            for (Spool spool : freeSpools) {
                if (spool.getColor().equals(color) && spool.getFilamentType().equals(type)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ColorNotFoundException("Color " + color + " (" + type + ") not found");
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);

        System.out.print("Task added to the queue");
    }

}