package nl.saxion.managers;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.utils.FilamentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PrinterManager {

    public PrinterFactory printerFactory = new PrinterFactory(this);

    //keeps track of the printers and its tasks
    public final Map<Printer, ArrayList<PrintTask>> printersMap = new HashMap<>();

    public final List<Printer> printersList = new ArrayList<>();

    // printers not in use
    private final List<Printer> freePrinters = new ArrayList<>();

    // spools not in use
    private final List<Spool> freeSpools = new ArrayList<>();


    private final List<PrintTask> pendingPrintTasks = PrintManager.getPrintTasks();


    private Map<Printer, PrintTask> runningPrintTasks = new HashMap();

    public PrinterManager(PrinterFactory printerFactory) {
        this.printerFactory = new PrinterFactory(this);
    }

    public void selectPrintTask() {
        for (PrintTask printTask : pendingPrintTasks) {
            for (Printer printer : getPrinters()) {
                if (taskSuitsPrinter(printer, printTask)) {
                    ArrayList<PrintTask> newArray = printersMap.get(printer);
                    newArray.add(printTask);
                    printersMap.replace(printer,newArray);
                }

            }
        }
    }

    public boolean taskSuitsPrinter(Printer printer, PrintTask printTask) {
        if (printer.getSpools().length < printTask.getColors().size()) {
            return false;
        }

        return printer.printFits(printTask.getPrint()) && printTask.getColors().get(0).equals(printer.getCurrentSpool().getColor());
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

//    public List<PrintTask> getPendingPrintTasks() {
//        return pendingPrintTasks;
//    }

    public void readPrintersFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if (filename.isEmpty()) {
            filename = "printers.json";
        }
        URL printersResource = getClass().getResource("/" + filename);
        if (printersResource == null) {
            System.err.println("Warning: Could not find printers.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printersResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray printers = (JSONArray) jsonParser.parse(reader);
            for (Object p : printers) {
                JSONObject printer = (JSONObject) p;
                int id = ((Long) printer.get("id")).intValue();
                int type = ((Long) printer.get("type")).intValue();
                String name = (String) printer.get("name");
                String manufacturer = (String) printer.get("manufacturer");
                int maxX = ((Long) printer.get("maxX")).intValue();
                int maxY = ((Long) printer.get("maxY")).intValue();
                int maxZ = ((Long) printer.get("maxZ")).intValue();
                int maxColors = ((Long) printer.get("maxColors")).intValue();
                boolean isHoused = type == 2;

                printerFactory.addPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors, isHoused);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public List<Printer> getPrinters() {
        return printersList;
    }
}