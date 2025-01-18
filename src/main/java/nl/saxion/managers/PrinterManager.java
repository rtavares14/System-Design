package nl.saxion.managers;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.observer.PrintTaskObserver;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.adapter.AdapterReader;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;
import nl.saxion.exceptions.ColorNotFoundException;
import nl.saxion.exceptions.FileNotSupportedException;
import nl.saxion.utils.FilamentType;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PrinterManager {
    // all the printers and print tasks
    public final Map<Printer, ArrayList<PrintTask>> printersMap = new HashMap<>();
    private final List<PrintTaskObserver> observers = new ArrayList<>();
    private final PrinterFactory printerFactory = new PrinterFactory(this);
    public List<Printer> printersList = new ArrayList<>();
    public List<Printer> freePrinters = new ArrayList<>();
    public Map<Printer, PrintTask> runningPrintTasks = new HashMap();
    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private List<Spool> freeSpools = new ArrayList<>();

    public PrinterManager(SpoolManager spoolManager) {
        freeSpools = spoolManager.getSpools();
        freePrinters = new ArrayList<>(printersList);
    }

    /**
     * loops through all the pending print tasks
     */
    public void startOptimizedSpoolStrategy() {
        for (int i = 0; i < pendingPrintTasks.size(); i++) {
            loopThroughPrinter(pendingPrintTasks.get(i));
        }
        startInitialQueue();
    }

    /**
     * This method is used to start the initial queue
     */
    private void startInitialQueue() {
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
     * This method is used to loop through the printer
     *
     * @param printTask The print task
     */
    private void loopThroughPrinter(PrintTask printTask) {
        for (int i = 0; i < freePrinters.size(); i++) {
            Printer printer = freePrinters.get(i);

            if (taskSuitsPrinter(freePrinters.get(i), printTask) && printer.canPrinterPrint(printer, printTask.getFilamentType(), printTask.getPrint())) {
                if (printer.printFits(printTask.getPrint())) {
                    System.out.println("Assigned task: 1" + printTask.getFilamentType() + " to printer: " + printer.getName());
                    printer.setCurrentSpools(assignProperSpool(printTask));
                    addTasksToPrinter(printer, printTask);
                    freePrinters.remove(printer);
                    return;
                }
            }
        }
    }

    /**
     * This method is used to add tasks to the printer
     *
     * @param printer   The printer
     * @param printTask The print task
     */
    private void addTasksToPrinter(Printer printer, PrintTask printTask) {
        ArrayList<PrintTask> printTasks = printersMap.get(printer);
        printTasks.add(printTask);
        printersMap.replace(printer, printTasks);
    }

    /**
     * This method is used to remove the tasks from the printer
     *
     * @param printer   The printer
     * @param printTask The print task
     */
    private void removeTasksFromPrinter(Printer printer, PrintTask printTask) {
        ArrayList<PrintTask> printTasks = printersMap.get(printer);
        printTasks.remove(printTask);
        printersMap.replace(printer, printTasks);
    }

    /**
     * This method is used to assign the proper spool
     *
     * @param printTask The print task
     * @return List<Spool>
     */
    private List<Spool> assignProperSpool(PrintTask printTask) {
        List<Spool> printerSpools = new ArrayList<>();

        // keeps track of the needed color
        for (int i = 0; i < printTask.getColors().size(); i++) {
            Spool minSpool = null;

            for (int j = 0; j < freeSpools.size(); j++) {
                // checks if the color and the size match, and if there is enough filament
                if (freeSpools.get(j).spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && printTask.getPrint().getSpecificFilamentLenght(i) <= freeSpools.get(j).getLength()) {
                    if (minSpool == null || minSpool.getLength() > freeSpools.get(j).getLength()) {
                        minSpool = freeSpools.get(j);
                    }
                }
            }

            printerSpools.add(minSpool);

        }
        notifyObservers("changedSpool", printerSpools.size()); //observers about the number of spools changed
        System.out.println("Number of spools changed: " + printerSpools.size());
        freeSpools.removeAll(printerSpools);
        return printerSpools;
    }

    /**
     * This method is used to check if the task suits the printer
     *
     * @param printer   The printer
     * @param printTask The print task
     * @return boolean
     */
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

    /**
     * This method is used to reduce the length of the spools
     *
     * @param printTask The print task
     * @param printer   The printer
     */
    private void reduceLengthOfSpools(PrintTask printTask, Printer printer) {
        for (int j = 0; j < printer.getSpools().size(); j++) {
            for (int i = 0; i < printTask.getColors().size(); i++) {
                Spool spoolNotEmpty = printer.getSpools().get(j);
                if (printer.getSpools().get(j) != null) {
                    if (spoolNotEmpty.spoolMatch(printTask.getColors().get(i), spoolNotEmpty.getFilamentType())) {
                        spoolNotEmpty.reduceLength(printTask.getPrint().getSpecificFilamentLenght(i));
                    }
                }
            }
        }
    }


    public void registerCompletion(Printer printer,PrintTask printTask){
        reduceLengthOfSpools(printTask, printer);
        removeTasksFromPrinter(printer, printTask);
        freePrinters.add(printer);

        completeTask();
    }

    public void registerFailure(Printer printer,PrintTask printTask) {
        reduceLengthOfSpools(printTask, printer);
        pendingPrintTasks.add(printTask);
        freePrinters.add(printer);

        failTask();
    }

    /**
     * This method is used to notify the observers that the task has been completed
     */
    public void completeTask() {
        notifyObservers("completed", 0);
    }

    /**
     * This method is used to notify the observers that the task has failed
     */
    public void failTask() {
        notifyObservers("failed", 0);
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
     * @param size
     */
    private void notifyObservers(String event, int size) {
        for (PrintTaskObserver observer : observers) {
            observer.update(event, size);
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
     * Getter for the free printers list.
     */
    public List<Printer> getFreePrinters() {
        return freePrinters;
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
            throw new FileNotSupportedException("Unsupported file type");
        }


        List<Printer> printersFromFile = fileHandler.readPrinters(path);
        printersList.addAll(printersFromFile);
        freePrinters.addAll(printersFromFile);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
    }

    /**
     * Method to add a print task to the pending print tasks list.
     * The print task will be added to the list if all fields are filled in.
     *
     * @param printName The name of the print.
     * @param colors    The colors of the print.
     * @param type      The type of filament.
     */
    public void addPrintTask(Print printName, List<String> colors, FilamentType type) {
        Print print = printName;
        if (print == null || colors.isEmpty()) {
            throw new IllegalArgumentException("Print and colors must be set");
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

    }

    /**
     * Assign a print task to a printer
     *
     * @param printer   The printer to assign the task to
     * @param printTask The print task to assign
     */
    public void assignPrintTask(Printer printer, PrintTask printTask, int spoolsChanged) {
        if (printer == null || printTask == null) {
            throw new IllegalArgumentException("Printer and print task must be set");
        }

        // Add the print task to the printer's task list
        printersMap.get(printer).add(printTask);

        // Remove the printer from the free printers list
        freePrinters.remove(printer);

        // Print the number of spools changed
        System.out.println("Assigned task: 2" + printTask.getPrint() + " to printer: " + printer.getName());
        System.out.println("Number of spools changed: " + spoolsChanged);
    }

    /**
     * Start the fastest spool strategy
     */
    public void startFastestSpoolStrategy() throws Exception {
        Iterator<PrintTask> iterator = getPendingPrintTasks().iterator();
        while (iterator.hasNext()) {
            PrintTask printTask = iterator.next();
            boolean taskAssigned = false;

            for (Printer printer : getFreePrinters()) {
                if (printer.printFits(printTask.getPrint()) && printer.acceptsFilamentType(printTask.getFilamentType()) && taskSuitsPrinter(printer, printTask) && printer.canPrinterPrint(printer, printTask.getFilamentType(), printTask.getPrint())) {
                    List<Spool> bestSpools = findBestSpools(printTask);
                    if (!bestSpools.isEmpty()) {
                        printer.setCurrentSpools((ArrayList<Spool>) bestSpools);
                        assignPrintTask(printer, printTask, bestSpools.size());
                        runningPrintTasks.put(printer, printTask);
                        iterator.remove(); //remove the print task from the pending list
                        notifyObservers("changedSpool", bestSpools.size()); //observers about the number of spools changed
                        freeSpools.removeAll(bestSpools);
                        taskAssigned = true;
                        break;
                    }
                }
            }
            if (!taskAssigned) {
                System.err.println("No suitable printer found for task: " + printTask);
            }
        }
    }

    /**
     * Find the best spools for a print task
     *
     * @param printTask The print task to find spools for
     * @return A list of spools that match the print task
     */
    private List<Spool> findBestSpools(PrintTask printTask) {
        List<Spool> bestSpools = new ArrayList<>();
        for (String color : printTask.getColors()) {
            Spool bestSpool = null;
            for (Spool spool : freeSpools) {
                if (spool.spoolMatch(color, printTask.getFilamentType())) {
                    if (bestSpool == null || spool.getLength() > bestSpool.getLength()) {
                        bestSpool = spool;
                    }
                }
            }
            if (bestSpool != null) {
                freeSpools.remove(bestSpool);
                bestSpools.add(bestSpool);
            }
        }
        return bestSpools;
    }

    /**
     * Get the running printer by name
     *
     * @param name The name of the printer
     * @return The printer with the given name
     */
    public Printer getRunningPrinterByName(String name){
        for(Printer printer:runningPrintTasks.keySet()){
            if(printer.getName().equals(name)){
                return printer;
            }
        }
        return null;
    }
}