package nl.saxion.managers;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.observer.PrintTaskObserver;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.adapter.AdapterReader;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;
import nl.saxion.exceptions.ColorNotFoundException;
import nl.saxion.utils.FilamentType;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PrinterManager {
    // all the printers and print tasks
    public final Map<Printer, ArrayList<PrintTask>> printersMap = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private final List<PrintTaskObserver> observers = new ArrayList<>();
    public PrinterFactory printerFactory = new PrinterFactory(this);
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

    private void loopThroughPrinter(PrintTask printTask) {
        for (int i = 0; i < freePrinters.size(); i++) {
            Printer printer = freePrinters.get(i);

            if (taskSuitsPrinter(freePrinters.get(i), printTask)) {
                if (printer.printFits(printTask.getPrint())) {
                    System.out.println("Assigned task: " + printTask + " to printer: " + printer.getName());
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
        notifyObservers("changedSpool", printerSpools.size()); //observers about the number of spools changed
        System.out.println("Number of spools changed: " + printerSpools.size());
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

    /**
     * This method is used to choose the printer
     */
    private Printer choosePrinter() {
        System.out.println("-----Choose printer completion-----");
        List<Printer> printerList = new ArrayList<>(runningPrintTasks.keySet());

        for (int i = 0; i < printerList.size(); i++) {
            Printer printer = printerList.get(i);
            System.out.println((i + 1) + ") " + printer.getName() + " --> " +
                    runningPrintTasks.get(printer).getPrint().getName());
        }

        int choice;
        do {
            System.out.print("Enter a valid option: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Please enter a valid number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
        } while (choice < 1 || choice > printerList.size());

        return printerList.get(choice - 1);
    }

    public void registerCompletion() {
        if (runningPrintTasks.isEmpty()) {
            System.out.println("No running tasks yet");
            return;
        }

        Printer printer = choosePrinter();
        PrintTask printTask = runningPrintTasks.remove(printer);

        if (printTask == null) {
            System.out.println("Error: Selected printer does not have a running task.");
            return;
        }

        reduceLenghtOfSpools(printTask, printer);
        removeTasksFromPrinter(printer, printTask);
        freePrinters.add(printer);

        completeTask();
    }

    public void registerFailure() {
        if (runningPrintTasks.isEmpty()) {
            System.out.println("No running tasks yet");
            return;
        }

        Printer printer = choosePrinter();
        PrintTask printTask = runningPrintTasks.remove(printer);

        if (printTask == null) {
            System.out.println("Error: Selected printer does not have a running task.");
            return;
        }

        pendingPrintTasks.add(printTask);
        freePrinters.add(printer);

        failTask();
    }

    public void completeTask() {
        notifyObservers("completed", 0);
    }

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
            System.out.println("Unsupported file type for filename: " + path);
            return;
        }


        List<Printer> printersFromFile = fileHandler.readPrinters(path);
        printersList.addAll(printersFromFile);
        freePrinters.addAll(printersFromFile);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
    }

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

        System.out.println("Task added to the queue");
    }

    public void assignPrintTask(Printer printer, PrintTask printTask, int spoolsChanged) {
        if (printer == null || printTask == null) {
            System.err.println("Printer or PrintTask is null");
            return;
        }

        // Add the print task to the printer's task list
        printersMap.get(printer).add(printTask);

        // Remove the printer from the free printers list
        freePrinters.remove(printer);

        // Print the number of spools changed
        System.out.println("Assigned task: " + printTask + " to printer: " + printer.getName());
        System.out.println("Number of spools changed: " + spoolsChanged);
    }

    public void startFastestSpoolStrategy() {
        Iterator<PrintTask> iterator = getPendingPrintTasks().iterator();
        while (iterator.hasNext()) {
            PrintTask printTask = iterator.next();
            boolean taskAssigned = false;

            for (Printer printer : getFreePrinters()) {
                if (printer.printFits(printTask.getPrint()) && printer.acceptsFilamentType(printTask.getFilamentType())) {
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
                System.out.println("No suitable printer found for task: " + printTask);
            }
        }
    }

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
                bestSpools.add(bestSpool);
            }
        }
        return bestSpools;
    }
}