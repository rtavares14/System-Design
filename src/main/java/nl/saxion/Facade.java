package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;
import nl.saxion.utils.FilamentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Facade {
    private SpoolManager spoolManager;
    private PrinterManager printerManager;
    private PrintManager printManager;
    private Scanner scanner = new Scanner(System.in);

    public Facade() {
        this.spoolManager = new SpoolManager();
        this.printerManager = new PrinterManager();
        this.printManager = new PrintManager(spoolManager);

        this.printerManager.readPrintersFromFile(""); //printers from file
        this.printManager.readPrintsFromFile(""); //prints from file
        this.spoolManager.readSpoolsFromFile(""); //prints from file
    }

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

    public void showPendingPrintTask() {
        if (printManager.getPrintTasks().isEmpty()) {
            System.out.println("No pending print tasks");
            return;
        }

        for (PrintTask printTask : printManager.getPrintTasks()) {
            System.out.println(printTask);
        }
    }

    public void startPrintQueue() {

    }

    public void addNewPrintTask() {
        int choice;

        listPrintsName();
        choice = scanner.nextInt();
        Print print = printManager.getPrints().get(choice - 1);

        listOfTypes();
        choice = scanner.nextInt();
        FilamentType filamentType = FilamentType.values()[choice - 1];

        //check available colors
        List<String> colors = selectColors(filamentType, print);

        System.out.println(print.getName() + " " + filamentType + " " + colors);
        //creates the print task
        printManager.addPrintTask(print, colors, filamentType);
        System.out.println("-----------------------------------");
    }

    private List<String> selectColors(FilamentType type, Print print) {
        List<String> colors = new ArrayList<>();
        List<String> availableColors = showAvailableColors(type);
        for (int i = 0; i < print.getFilamentLength().size(); i++) {
            System.out.print("- Color position: ");
            int colorChoice = scanner.nextInt();
            colors.add(availableColors.get(colorChoice - 1));
        }
        System.out.println("--------------------------------------");
        return colors;
    }

    public List<String> showAvailableColors(FilamentType filamentType) {
        List<String> availableColors = spoolManager.getAvailableColors(filamentType);
        System.out.println("---------- Colors ----------");
        for (int i = 1; i <= availableColors.size(); i++) {
            String colorString = availableColors.get(i - 1);
            System.out.println("- " + i + ": " + colorString + " (" + filamentType.name() + ")");
        }
        return availableColors;
    }

    public void listPrintsName() {
        int i = 1;
        System.out.println("-----------------");
        System.out.println("Choose a print:");
        for (Print print : printManager.getPrints()) {
            System.out.println(i++ + " - " + print.getName() + "(" + print.getFilamentLength().size() + ")");
        }
        System.out.print("Choice:");
    }

    public void listOfTypes() {
        int i = 1;
        System.out.println("-----------------");
        System.out.println("Choose a filament type:");

        for (FilamentType type : FilamentType.values()) {
            System.out.println(i++ + " - " + type);
        }
        System.out.print("Choice:");
    }

    public void listSpools() {
        System.out.println("Choose a color and a filament type:");
        for (Spool spool : spoolManager.getSpools()) {
            System.out.println("Color:" + spool.getColor());
            System.out.println("Filament type:" + spool.getFilamentType());
        }
    }

    public void registerPrinterFailure() {
    }

    public void registerPrintCompletion() {
    }

    public void changePrintStrategy() {
    }

    private void exit() {
    }
}
