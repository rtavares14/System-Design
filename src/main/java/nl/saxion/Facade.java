package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;

import java.util.ArrayList;

public class Facade {
    private SpoolManager spoolManager;
    private PrinterManager printerManager;
    private PrintManager printManager;

    public Facade() {
        this.spoolManager = new SpoolManager();
        this.printerManager = new PrinterManager();
        this.printManager = new PrintManager();
        this.printerManager.readPrintersFromFile(""); //printers from file
        this.printManager.readPrintsFromFile(""); //prints from file
        this.spoolManager.readSpoolsFromFile(""); //prints from file
    }

    public void showPrinters() {
        if (printerManager.getPrinters().isEmpty()) {
            System.out.println("No printers available");
            return;
        }

        for (Printer printer : printerManager.getPrinters()) {
            System.out.println(printer);
        }
    }

    public void showSpools(){
        if (spoolManager.getSpools().isEmpty()) {
            System.out.println("No spools available");
            return;
        }

        for(Spool spool:spoolManager.getSpools()){
            System.out.println(spool);
        }
    }

    public void showPrints() {
        if (printManager.getPrints().isEmpty()) {
            System.out.println("No prints available");
            return;
        }

        for (Print print : printManager.getPrints()) {
            System.out.println(print);
        }
    }

    public void showPendingPrintTask(){

    }
    public void startPrintQueue(){

    }
    public void addNewPrintTask(int printChoice, int colorChoice){
        Print selectedPrint;
        Spool spoolPrint ;
        PrintTask printTask;


        selectedPrint = printManager.getPrints().get(printChoice+1);


        spoolPrint = spoolManager.getSpools().get(colorChoice);


        printManager.addPrintTask(selectedPrint,new ArrayList<>(),spoolPrint.getFilamentType());
    }

    public void listPrints(){
        System.out.println("Choose a print number:");
        for(Print print: printManager.getPrints()){
            System.out.println(print);
        }
    }

    public void listSpools(){
        System.out.println("Choose a color and a filament type:");
        for(Spool spool:spoolManager.getSpools()){
            System.out.println("Color:" + spool.getColor());
            System.out.println("Filament type:" + spool.getFilamentType());
        }
    }
    public void registerPrinterFailure(){}
    public void registerPrintCompletion(){}
    public void changePrintStrategy(){}
    private void exit(){}
}
