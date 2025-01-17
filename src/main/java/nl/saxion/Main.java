package nl.saxion;

import nl.saxion.Models.records.PrintBP;

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
            case 1 -> facade.addNewPrintTask();
            case 2 -> facade.registerPrintCompletion();
            case 3 -> facade.registerPrinterFailure();
            case 4 -> facade.changePrintStrategy();
            case 5 -> facade.initPrintQueue();
            case 6 -> showPrints();
            case 7 -> facade.showPrinters();
            case 8 -> facade.showSpools();
            case 9 -> facade.showPendingPrintTask();
            case 10 -> showDashboardStats();
            case 0 -> System.out.println("Exiting...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
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

    public void addNewPrintTask(){

    }

    public void listPrintsName() {
        int i = 1;

        for (PrintBP print : facade.getPrints()) {
            System.out.println(i++ + " - " + print.name() + "(" + print.length() + ")");
        }
        System.out.print("Choice:");
    }

}