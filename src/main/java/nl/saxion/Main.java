package nl.saxion;

import java.util.Scanner;

public class Main {
    Facade facade = new Facade();
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        int choice;
        do {
            menu();
            choice = scanner.nextInt();
            chooseMenuOption(choice);
        } while (choice != 0);
    }

    public void menu() {
        System.out.println("------------- Menu ----------------");
        System.out.println("- 1) Add new Print Task");
        System.out.println("- 2) Register Printer Completion");
        System.out.println("- 3) Register Printer Failure");
        System.out.println("- 4) Change printing style");
        System.out.println("- 5) Start Print Queue");
        System.out.println("- 6) Show prints");
        System.out.println("- 7) Show printers");
        System.out.println("- 8) Show spools");
        System.out.println("- 9) Show pending print tasks");
        System.out.println("- 0) Exit");
    }

    public void chooseMenuOption(int choice) {
        switch (choice) {
            case 1:
                int printChoice;
                int colorChoice;

                facade.listPrints();
                printChoice = scanner.nextInt();

                facade.listSpools();
                colorChoice = scanner.nextInt();

                facade.addNewPrintTask(printChoice, colorChoice);
                break;
            case 2:
                facade.registerPrintCompletion();
                break;
            case 3:
                facade.registerPrinterFailure();
                break;
            case 4:
                facade.changePrintStrategy();
                break;
            case 5:
                facade.startPrintQueue();
                break;
            case 6:
                facade.showPrints();
                break;
            case 7:
                facade.showPrinters();
                break;
            case 8:
                facade.showSpools();
                break;
            case 9:
                facade.showPendingPrintTask();
                break;
            case 0:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
}