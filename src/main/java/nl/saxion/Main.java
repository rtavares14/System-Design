package nl.saxion;

import nl.saxion.Models.records.PrintBP;
import nl.saxion.Models.records.PrintTaskBP;
import nl.saxion.Models.records.PrinterBP;
import nl.saxion.Models.records.SpoolBP;
import nl.saxion.utils.FilamentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    Facade facade = new Facade();
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().run();
    }

    /**
     * Run the program and display menu
     */
    public void run() {
        facade.readData();

        int choice;
        do {
            menu();
            choice = scanner.nextInt();
            chooseMenuOption(choice);
        } while (choice != 0);
    }

    /**
     * Display menu options to the user to choose from
     */
    public void menu() {
        System.out.println("------------- Menu ----------------");
        System.out.println("- 1) Add new Print Task");
        System.out.println("- 2) Register Printer Completion");
        System.out.println("- 3) Register Printer Failure");
        System.out.println("- 4) Change printing strategy");
        System.out.println("- 5) Start Print Queue");
        System.out.println("- 6) Show prints");
        System.out.println("- 7) Show printers");
        System.out.println("- 8) Show spools");
        System.out.println("- 9) Show pending print tasks");
        System.out.println("- 10) Show Dashboard Stats");
        System.out.println("- 0) Exit");
        System.out.println("-----------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Choose menu option based on user input
     * @param choice the choice
     */
    public void chooseMenuOption(int choice) {
        switch (choice) {
            case 1 -> addNewPrintTask();
            case 2 -> facade.registerPrintCompletion();
            case 3 -> facade.registerPrinterFailure();
            case 4 -> changePrintStrategy();
            case 5 -> initPrintQueue();
            case 6 -> showPrints();
            case 7 -> showPrinters();
            case 8 -> showSpools();
            case 9 -> showPendingPrintTask();
            case 10 -> showDashboardStats();
            case 0 -> System.out.println("Exiting...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
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
            if (choice < 1 || choice > facade.getPrints().size()) {
                System.out.println("Invalid print choice. Please try again.");
            }
        } while (choice < 1 || choice > facade.getPrints().size());

        PrintBP print = facade.getPrints().get(choice - 1);

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

        System.out.println(print.name() + " " + filamentType + " " + colors);
        //creates the print task
        facade.addPrintTask(print, colors, filamentType);
        System.out.println("-----------------------------------");
    }

    /**
     * Change print strategy OPTION 4
     */
    private void changePrintStrategy() {
        System.out.println("------------ Change Printing Strategy -------------");
        if (facade.getOptimizedSpoolStrategy()) {
            System.out.println("Current Strategy: Optimized Spool Strategy");
            System.out.println("Changing to Fastest Spool Strategy");
            facade.changePrintStrategy(false);
        } else {
            System.out.println("Current Strategy: Fastest Spool Strategy");
            System.out.println("Changing to Optimized Spool Strategy");
            facade.changePrintStrategy(true);
        }
    }

    /**
     * Initialize print queue OPTION 5
     */
    private void initPrintQueue() {
        if (facade.getPendingPrintTasks().isEmpty()) {
            System.out.println("Queue is empty. Please add a print task first.");
        } else {
        if (facade.getOptimizedSpoolStrategy()) {
            System.out.println("Starting queue with Optimized Spool Strategy");
            facade.initPrintQueue();
        }else {
            facade.initPrintQueue();
            System.out.println("Starting queue with Fastest Spool Strategy");
        }}
    }


    /**
     * Show prints OPTION 6
     */
    private void showPrints() {
        List<PrintBP> prints = facade.getPrints();
        System.out.println("------------- Prints -------------");

        if (prints.isEmpty()) {
            System.out.println("No prints available");
        }

        for (PrintBP print : prints) {
            System.out.println(print.toString());
        }
    }

    /**
     * Show printers OPTION 7
     */
    private void showPrinters() {
        System.out.println("------------- Printers -------------");
        if (facade.getPrinters().isEmpty()) {
            System.out.println("No printers available");
            return;
        }

        for (PrinterBP printer : facade.getPrinters()) {
            System.out.println(printer.toString());
        }
        System.out.println("-----------------------------------");
    }

    /**
     * Show spools OPTION 8
     */
    public void showSpools() {
        System.out.println("------------- Spools -------------");
        if (facade.getSpools().isEmpty()) {
            System.out.println("No spools available");
            return;
        }

        for (SpoolBP spool : facade.getSpools()) {
            System.out.println(spool.toString());
        }
        System.out.println("-----------------------------------");
    }

    /**
     * Show pending print tasks OPTION 9
     */
    public void showPendingPrintTask() {
        System.out.println("------- Pending Print Tasks --------");
        if (facade.getPendingPrintTasks().isEmpty()) {
            System.out.println("No pending print tasks available");
            return;
        }

        for (PrintTaskBP printTask : facade.getPendingPrintTasks()) {
            System.out.println(printTask.toString());
        }
        System.out.println("-----------------------------------");
    }

    /**
     * Show dashboard stats OPTION 10
     */
    public void showDashboardStats() {
        int[] dashboardStats = facade.showDashboardStats();
        System.out.println("------------- Dashboard Stats -------------");
        System.out.println("Completed Tasks: " + dashboardStats[0]);
        System.out.println("Failed Tasks: " + dashboardStats[1]);
        System.out.println("Changed Spools: " + dashboardStats[2]);
        System.out.println("------------------------------------------");
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
     * This method is used to display the list of prints
     */
    public void listPrintsName() {
        int i = 1;

        for (PrintBP print : facade.getPrints()) {
            System.out.println(i++ + " - " + print.name() + "(" + print.filamentLength().size() + " colors)");
        }
        System.out.print("Choice:");
    }

    /**
     * This method is used to select the colors for the print
     *
     * @param type FilamentType chosen by the user
     * @param print Print chosen by the user
     * @return List<String> colors selected by the user
     */
    private List<String> selectColors(FilamentType type, PrintBP print) {
        List<String> colors = new ArrayList<>();
        List<String> availableColors = showAvailableColors(type);

        for (int i = 0; i < print.filamentLength().size(); i++) {
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
        System.out.println("-----------------------------------");
        return colors;
    }

    /**
     * This method is used to show the available colors for the filament type
     * @param filamentType FilamentType chosen by the user
     * @return List<String> available colors for the filament type
     */
    public List<String> showAvailableColors(FilamentType filamentType) {
        List<String> availableColors = facade.getAvailableColors(filamentType);
        System.out.println("---------- Colors ----------");
        for (int i = 1; i <= availableColors.size(); i++) {
            String colorString = availableColors.get(i - 1);
            System.out.println("- " + i + ": " + colorString + " (" + filamentType.name() + ")");
        }
        return availableColors;
    }

}
