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
import java.util.*;

public class PrinterManager {
    public PrinterFactory printerFactory = new PrinterFactory(this);
    public final Map<Printer, ArrayList<PrintTask>> printersMap = new HashMap<>();
    public final List<Printer> printersList = new ArrayList<>();
    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    public List<Printer> freePrinters = new ArrayList<>();
    private Map<Printer, PrintTask> runningPrintTasks = new HashMap();
    private List<Spool> freeSpools = new ArrayList<>();
    private final List<PrintTaskObserver> observers = new ArrayList<>();

    public PrinterManager(SpoolManager spoolManager, PrintManager printManager) {
        freeSpools = spoolManager.getSpools();
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
        return printersList;
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

    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        if (spools[0] != null) {
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.printFits(printTask.getPrint())) {
                    if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                        if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                            runningPrintTasks.put(printer, printTask);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                            break;
                        }
                        // The housed printer is the only one that can print ABS, but it can also print the others.
                    } else if (printer instanceof StandardFDM && ((StandardFDM) printer).isHoused() && printTask.getColors().size() == 1) {
                        if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                            runningPrintTasks.put(printer, printTask);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                            break;
                        }
                        // For multicolor the order of spools does matter, so they have to match.
                    } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                        boolean printWorks = true;
                        for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                            if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                                printWorks = false;
                            }
                        }
                        if (printWorks) {
                            runningPrintTasks.put(printer, printTask);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                            break;
                        }
                    }
                }
            }
        }
        if (chosenTask != null) {
            pendingPrintTasks.remove(chosenTask);
            System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
        } else {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
                    if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : freeSpools) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            freeSpools.add(printer.getSpools()[0]);
                            System.out.println("- Spool change: Please place spool in printer " + printer.getName());
                            freeSpools.remove(chosenSpool);
                            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof StandardFDM && ((StandardFDM) printer).isHoused() && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : freeSpools) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            freeSpools.add(printer.getSpools()[0]);
                            System.out.println("- Spool change: Please place spool in printer " + printer.getName());
                            freeSpools.remove(chosenSpool);
                            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                        ArrayList<Spool> chosenSpools = new ArrayList<>();
                        for (int i = 0; i < printTask.getColors().size(); i++) {
                            for (Spool spool : freeSpools) {
                                if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())
                                    //&& !containsSpool(chosenSpools, printTask.getColors().get(i))
                                ) {
                                    chosenSpools.add(spool);
                                }
                            }
                        }
                        // We assume that if they are the same length that there is a match.
                        if (chosenSpools.size() == printTask.getColors().size()) {
                            runningPrintTasks.put(printer, printTask);
                            for (Spool spool : printer.getSpools()) {
                                freeSpools.add(spool);
                            }
                            printer.setCurrentSpools(chosenSpools);
                            int position = 1;
                            for (Spool spool : chosenSpools) {
                                System.out.println("- Spool change: Please place spool in printer " + printer.getName() + " position " + position);
                                freeSpools.remove(spool);
                                position++;
                            }
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    }
                }
            }
            if (chosenTask != null) {
                pendingPrintTasks.remove(chosenTask);
                System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
            }
        }
    }

    public void registerPrinterFailure(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            printError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(foundEntry.getKey());

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getSpools();
        for (int i = 0; i < spools.length && i < task.getColors().size(); i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(0));
        }
        selectPrintTask(printer);
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if (!printersMap.containsKey(printer)) {
            return null;
        }
        return printersMap.get(printer).get(0);
    }

    private void printError(String s) {
        System.out.println("---------- Error Message ----------");
        System.out.println("Error: " + s);
        System.out.println("--------------------------------------");
    }

    public void registerCompletion(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, ArrayList<PrintTask>> entry : printersMap.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                //foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            printError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.remove(task);

        System.out.println("Task " + task + " removed from printer " + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getSpools();
        for (int i = 0; i < spools.length && i < task.getColors().size(); i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(0));
        }
        selectPrintTask(printer);


    }

    public void startInitialQueue() {
        for (Printer printer : printersList) {
            selectPrintTask(printer);
        }
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
        notifyObservers("completed", 0);
    }

    public void failTask() {
        notifyObservers("failed", 0);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
    }

    public void assignPrintTask(Printer printer, PrintTask printTask) {
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
        System.out.println("Number of spools changed: " + printer.getSpools().length);
    }

    public void startPrintQueue2() {
        Iterator<PrintTask> iterator = getPendingPrintTasks().iterator();
        while (iterator.hasNext()) {
            PrintTask printTask = iterator.next();
            boolean taskAssigned = false;
            
            for (Printer printer : getFreePrinters()) {
                if (printer.printFits(printTask.getPrint()) && printer.acceptsFilamentType(printTask.getFilamentType())) {
                    List<Spool> bestSpools = findBestSpools(printTask);
                    if (!bestSpools.isEmpty()) {
                        printer.setCurrentSpools((ArrayList<Spool>) bestSpools);
                        assignPrintTask(printer, printTask);
                        iterator.remove(); //remove the print task from the pending list
                        notifyObservers("changedSpool", bestSpools.size()); //observers about the number of spools changed
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