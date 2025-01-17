package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.observer.Dashboard;
import nl.saxion.Models.records.PrintBP;
import nl.saxion.Models.records.PrintTaskBP;
import nl.saxion.Models.records.PrinterBP;
import nl.saxion.Models.records.SpoolBP;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;
import nl.saxion.utils.FilamentType;

import java.util.List;

public class Facade {
    private final SpoolManager spoolManager;
    private final PrinterManager printerManager;
    private final PrintManager printManager;
    private final Dashboard dashboard;
    private boolean optimizedSpoolStrategy = false; // Default strategy (raf)

    public Facade() {
        this.spoolManager = new SpoolManager();
        this.printManager = new PrintManager();
        this.printerManager = new PrinterManager(spoolManager);
        this.dashboard = new Dashboard();
        printerManager.addObserver(dashboard);
    }

    /**
     * This method is used to read the data from the files
     */
    public void readData() {
        printerManager.readPrintersFromFile("printers.csv");
        printManager.readPrintsFromFile("prints.csv");
        spoolManager.readSpoolsFromFile("spools.csv");
    }


    public void addPrintTask(PrintBP printBP, List<String> colors, FilamentType filamentType) {
        Print print = printManager.findPrint(printBP.name());
        printerManager.addPrintTask(print, colors, filamentType);

    }

    public boolean getOptimizedSpoolStrategy() {
        return optimizedSpoolStrategy;
    }

    /**
     * This method is used to show the available colors for the filament type
     *
     * @param filamentType FilamentType chosen by the user
     * @return List<String> available colors for the filament type
     */
    public List<String> getAvailableColors(FilamentType filamentType) {
        return spoolManager.getAvailableColors(filamentType);

    }

    /**
     * OPTION : 2
     * This method is used to register the printer completion
     */
    public void registerPrintCompletion() {
        printerManager.registerCompletion();
    }

    /**
     * OPTION : 3
     * This method is used to register the printer failure
     */
    public void registerPrinterFailure() {
        printerManager.registerFailure();
    }

    /**
     * OPTION : 4
     * This method is used to change the print strategy
     */
    public void changePrintStrategy(boolean choice) {
        optimizedSpoolStrategy = choice;
    }

    /**
     * OPTION : 5
     * This method is used to start the print queue
     */
    public void initPrintQueue() {
        //false = FastestSpoolStrategy
        if (!optimizedSpoolStrategy) {
            printerManager.startFastestSpoolStrategy();
        } else {
            printerManager.startOptimizedSpoolStrategy();
        }
    }

    /**
     * OPTION : 6
     * This method is used to get the prints
     *
     * @return List<PrintBP> prints
     */
    public List<PrintBP> getPrints() {
        return printManager.getPrints().stream()
                .map(print -> new PrintBP(print.getName(), print.getHeight(), print.getWidth(), print.getLength(), print.getFilamentLength(), print.getPrintTime()))
                .toList();
    }

    /**
     * OPTION : 7
     * This method is used to get the printers
     *
     * @return List<PrinterBP> printers
     */
    public List<PrinterBP> getPrinters() {
        return printerManager.getPrinters().stream()
                .map(printer -> new PrinterBP(printer.getId(), printer.getName(), printer.getModel(), printer.getManufacturer(), printer.getMaxX(), printer.getMaxY(), printer.getMaxZ(), printer.isHoused(), printer.getMaxColors()))
                .toList();
    }

    /**
     * OPTION : 8
     * This method is used to get the spools
     *
     * @return List<SpoolBP> spools
     */
    public List<SpoolBP> getSpools() {
        return spoolManager.getSpools().stream()
                .map(spool -> new SpoolBP(spool.getId(), spool.getColor(), spool.getFilamentType(), spool.getLength()))
                .toList();
    }

    /**
     * OPTION : 9
     * This method is used to get the pending print tasks
     *
     * @return List<PrintTaskBP> pending print tasks
     */
    public List<PrintTaskBP> getPendingPrintTasks() {
        return printerManager.getPendingPrintTasks().stream()
                .map(printTask -> new PrintTaskBP(printTask.getPrint(), printTask.getColors(), printTask.getFilamentType()))
                .toList();
    }

    /**
     * OPTION : 10
     * This method is used to show the dashboard stats
     */
    public int[] showDashboardStats() {
        return dashboard.showDashboard();
    }
}
