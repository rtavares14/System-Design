package nl.saxion.managers;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.Models.printer.printerTypes.MultiColor;
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
    private final List<Printer> freePrinters = new ArrayList<>();

    private final List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private Map<Printer, PrintTask> runningPrintTasks = new HashMap();


    private final List<Spool> allSpools;
    private List<Spool> freeSpools = new ArrayList<>();


    public PrinterManager(SpoolManager spoolManager) {
        allSpools = spoolManager.getSpools();
        freeSpools = allSpools;
    }

    public void selectPrintTask() {
        for (PrintTask printTask : pendingPrintTasks) {
            loopThroughPrinter(printTask);
        }
    }

    private void loopThroughPrinter(PrintTask printTask) {
        for (Printer printer : freePrinters) {
            if (taskSuitsPrinter(printer, printTask)) {
                if (printer.printFits(printTask.getPrint())) {

                    printer.setCurrentSpools(assignProperSpool(printTask));
                    runningPrintTasks.put(printer, printTask);


                    freePrinters.remove(printer);

                }
            }
        }
    }

    private List<Spool> assignProperSpool(PrintTask printTask) {
        List<Spool> printerSpools = new ArrayList<>();

        for (Map.Entry<String, Double> color : printTask.getColors().entrySet()) {
            Spool minSpool = null;

            for (Spool resourceSpool : allSpools) {
                if (resourceSpool.spoolMatch(color.getKey(), printTask.getFilamentType()) && resourceSpool.getLength() >= color.getValue()) {
                    if (minSpool == null || minSpool.getLength() > resourceSpool.getLength()) {
                        minSpool = resourceSpool;
                    }
                }
            }
            printerSpools.add(minSpool);

            freeSpools.remove(minSpool);
        }

        return printerSpools;
    }

    private boolean taskSuitsPrinter(Printer printer, PrintTask printTask) {
        return printer.getCurrentSpool().getFilamentType().equals(printTask.getFilamentType()) &&
                (printer.getMaxColors() >= printTask.getColors().size());

    }

    private void reduceLenghtOfSpools(PrintTask printTask,Printer printer) {
        for(Spool spool:printer.getSpools()){
            for(Map.Entry<String,Double> compareSpool:printTask.getColors().entrySet()){
                if(spool.spoolMatch(compareSpool.getKey(),spool.getFilamentType())){
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
                pendingPrintTasks.remove(entry.getValue());
                runningPrintTasks.remove(entry.getKey());
                reduceLenghtOfSpools(entry.getValue(),entry.getKey());
                break;
            }
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