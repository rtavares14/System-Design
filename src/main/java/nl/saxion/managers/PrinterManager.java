package nl.saxion.managers;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;
import nl.saxion.adapter.AdapterReader;
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

    public final List<Printer> printersList = new ArrayList<>();
    private List<Printer> freePrinters = new ArrayList<>();

    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    public Map<Printer, PrintTask> runningPrintTasks = new HashMap();

    private Map<Spool, Integer> spoolsInUse = new HashMap<>();
    private List<Spool> freeSpools = new ArrayList<>();


    public PrinterManager(SpoolManager spoolManager, PrintManager printManager) {
        freePrinters = printersList;
        pendingPrintTasks = printManager.getPrintTasks();
        freeSpools = spoolManager.getSpools();
        spoolsInUse = spoolManager.getAllSpools();
    }

    /**
     * loops through all the pending print tasks
     */
    public void selectPrintTask() {

        for (int i = 0; i < pendingPrintTasks.size(); i++) {
            loopThroughPrinter(pendingPrintTasks.get(i));
        }
//        pendingPrintTasks = new ArrayList<>();
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

                    pendingPrintTasks.remove(printTask);
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

    private void removePendingTasks(ArrayList<PrintTask> printTasks) {
        pendingPrintTasks.removeAll(printTasks);
    }

    private List<Spool> assignProperSpool(PrintTask printTask) {
        List<Spool> printerSpools = new ArrayList<>();

        // keeps track of the size of the needed filament and the color
        for (Map.Entry<String, Double> color : printTask.getColors().entrySet()) {
            Spool minSpool = null;

            for (int i = 0; i < freeSpools.size(); i++) {
                if (freeSpools.get(i).spoolMatch(color.getKey(), printTask.getFilamentType()) && freeSpools.get(i).getLength() >= color.getValue()) {
                    if (minSpool == null || minSpool.getLength() > freeSpools.get(i).getLength()) {
                        minSpool = freeSpools.get(i);
                    }
                }
            }

//            increaseSpoolUsage(minSpool);
            printerSpools.add(minSpool);
            freeSpools.remove(minSpool);
        }


        return printerSpools;
    }

    private boolean taskSuitsPrinter(Printer printer, PrintTask printTask) {

        FilamentType filamentType = printTask.getFilamentType();

        if ((printer.getMaxColors() == printTask.getColors().size())) {
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
        for (Spool spool : printer.getSpools()) {
            for (Map.Entry<String, Double> compareSpool : printTask.getColors().entrySet()) {
                if (spool.spoolMatch(compareSpool.getKey(), spool.getFilamentType())) {
                    spool.reduceLength(compareSpool.getValue());
                }
            }
        }
    }

    public void registerPrinterFailure(int printerId) {

    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if (!printersMap.containsKey(printer)) {
            return null;
        }
        return printersMap.get(printer).get(0);
    }

    public void registerCompletion(int printerId) {

        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {

                runningPrintTasks.remove(entry.getKey());
                freeSpools.addAll(entry.getKey().getSpools());
                reduceLenghtOfSpools(entry.getValue(), entry.getKey());
                break;
            }
        }


    }

    public void increaseSpoolUsage(Spool spool) {
        spoolsInUse.putIfAbsent(spool, 0);
        Integer usage = spoolsInUse.get(spool);
        usage++;
        spoolsInUse.replace(spool, usage);
    }


    public Printer findPrinterById(int id) {
        for (Printer printer : printersList) {
            if (printer.getId() == id) {
                return printer;
            }
        }
        return null;
    }

    public List<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
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
}