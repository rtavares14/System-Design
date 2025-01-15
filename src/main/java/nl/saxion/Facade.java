package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.observer.Dashboard;
import nl.saxion.Models.printer.Printer;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;
import nl.saxion.utils.FilamentType;

import java.util.*;

public class Facade {
    private final SpoolManager spoolManager;
    private final PrinterManager printerManager;
    private final PrintManager printManager;
    private final Dashboard dashboard;
    private final Scanner scanner = new Scanner(System.in);
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

    /**
     * OPTION : 1
     * This method is used to add a new task to the print queue
     * It will ask the user to select a print, filament type and colors
     */
    public void addNewPrintTask() {
        int choice;

        listPrintsName();

        do {
            System.out.print("Choose a print: ");
            choice = scanner.nextInt();
            if (choice < 1 || choice > printManager.getPrints().size()) {
                System.out.println("Invalid print choice. Please try again.");
            }
        } while (choice < 1 || choice > printManager.getPrints().size());

        Print print = printManager.getPrints().get(choice - 1);

        listOfTypes();

        do {
            System.out.print("Choose a filament type: ");
            choice = scanner.nextInt();
            if (choice < 1 || choice > FilamentType.values().length) {
                System.out.println("Invalid filament type choice. Please try again.");
            }
        } while (choice < 1 || choice > FilamentType.values().length);

        FilamentType filamentType = FilamentType.values()[choice - 1];

        //check available colors
        List<String> colors = selectColors(filamentType, print);

        System.out.println(print.getName() + " " + filamentType + " " + colors);
        //creates the print task
        printerManager.addPrintTask(print, colors, filamentType);
        System.out.println("-----------------------------------");
    }

    /**
     * This method is used to select the colors for the print
     *
     * @param type FilamentType chosen by the user
     * @param print Print chosen by the user
     * @return List<String> colors selected by the user
     */
    private List<String> selectColors(FilamentType type, Print print) {
        List<String> colors = new ArrayList<>();
        List<String> availableColors = showAvailableColors(type);

        for (int i = 0; i < print.getFilamentLength().size(); i++) {
            int colorChoice;
            do {
                System.out.print("Choose color for position: ");
                colorChoice = scanner.nextInt();
                if (colorChoice < 1 || colorChoice > availableColors.size()) {
                    System.out.println("Invalid color choice. Please try again.");
                }
            } while (colorChoice < 1 || colorChoice > availableColors.size());

            colors.add(availableColors.get(colorChoice - 1));
        }
        System.out.println("--------------------------------------");
        return colors;
    }

    /**
     * This method is used to show the available colors for the filament type
     * @param filamentType FilamentType chosen by the user
     * @return List<String> available colors for the filament type
     */
    public List<String> showAvailableColors(FilamentType filamentType) {
        List<String> availableColors = spoolManager.getAvailableColors(filamentType);
        System.out.println("---------- Colors ----------");
        for (int i = 1; i <= availableColors.size(); i++) {
            String colorString = availableColors.get(i - 1);
            System.out.println("- " + i + ": " + colorString + " (" + filamentType.name() + ")");
        }
        return availableColors;
    }

    /**
     * This method is used to start the print queue
     * By displaying the prints name and how many spools are needed
     */
    public void listPrintsName() {
        int i = 1;
        System.out.println("-----------------------------------");
        System.out.println("Choose a print:");
        for (Print print : printManager.getPrints()) {
            System.out.println(i++ + " - " + print.getName() + "(" + print.getFilamentLength().size() + ")");
        }
        System.out.print("Choice:");
    }

    /**
     * This method is used to display the list of filament types
     */
    public void listOfTypes() {
        int i = 1;
        System.out.println("-----------------------------------");
        System.out.println("Choose a filament type:");

        for (FilamentType type : FilamentType.values()) {
            System.out.println(i++ + " - " + type);
        }
        System.out.print("Choice:");
    }

    /**
     * OPTION : 6
     * This method is used to show the prints
     */
    public void showPrints() {
        System.out.println("------------- Prints -------------");
        if (printManager.getPrints().isEmpty()) {
            System.out.println("No prints available");
            return;
        }

        for (Print print : printManager.getPrints()) {
            System.out.println(print);
        }
        System.out.println("-----------------------------------");
    }

    /**
     * OPTION : 7
     * This method is used to show the printers
     */
    public void showPrinters() {
        System.out.println("------------- Printers -------------");
        if (printerManager.getPrinters().isEmpty()) {
            System.out.println("No printers available");
            return;
        }

        for (Printer printer : printerManager.getPrinters()) {
            System.out.println(printer);
        }
        System.out.println("-----------------------------------");
    }

    /**
     * OPTION : 8
     * This method is used to show the spools
     */
    public void showSpools() {
        System.out.println("------------- Spools -------------");
        if (spoolManager.getSpools().isEmpty()) {
            System.out.println("No spools available");
            return;
        }

        for (Spool spool : spoolManager.getSpools()) {
            System.out.println(spool);
        }
        System.out.println("-----------------------------------");
    }

    /**
     * OPTION : 9
     * This method is used to show the pending print tasks
     */
    public void showPendingPrintTask() {
        if (printerManager.getPendingPrintTasks().isEmpty()) {
            System.out.println("No pending print tasks");
            return;
        }

        for (PrintTask printTask : printerManager.getPendingPrintTasks()) {
            System.out.println(printTask);
        }
    }

    public void listSpools() {
        System.out.println("Choose a color and a filament type:");
        for (Spool spool : spoolManager.getSpools()) {
            System.out.println("Color:" + spool.getColor());
            System.out.println("Filament type:" + spool.getFilamentType());
        }
    }

    public void initPrintQueue() {
        if (optimizedSpoolStrategy) {
            printerManager.selectPrintTask();
        } else {
            printerManager.startPrintQueue2();
        }
    }



    public void startPrintQueue() {
        System.out.println("Starting print queue with method 1");
        printerManager.selectPrintTask();
        for (Map.Entry<Printer, PrintTask> showPrints : printerManager.runningPrintTasks.entrySet()) {
            System.out.println("-------" + showPrints.getKey().getName() + "--------");
            System.out.println("Spool used: " + showPrints.getKey().getSpools());
            System.out.println("Print task to be done: " + showPrints.getValue().getPrint().getName());
            System.out.println();
        }
    }

    private int choosePrinter() {
        System.out.println("------Choose right printer by its id:-------");

        int counter = 1;
        for (Map.Entry<Printer,PrintTask> printer : printerManager.runningPrintTasks.entrySet()) {
            System.out.println(counter + ")" + printer.getKey());
            System.out.println(counter + ")" + printer.getValue().getPrint());
            counter++;
        }
        System.out.println("---------What printer finished:----------");
        return scanner.nextInt();
    }

    public void registerPrinterFailure() {
        printerManager.registerFailure(choosePrinter());
    }

    public void registerPrintCompletion() {
        printerManager.registerCompletion(choosePrinter());
    }


    public void changePrintStrategy() {
    }

    private void exit() {
    }

    public void showDashboardStats() {
        dashboard.showDashboard();
    }
}
