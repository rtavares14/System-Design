package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;

import java.util.ArrayList;

public class Facade {
    private SpoolManager spoolManager;
    private PrinterManager printerManager;
    private PrintManager printManager;

    public void showPrinters(){
        printerManager.getPrinters();
    }
    public void showSpools(){
        spoolManager.getSpools();
    }
    public void showPendingPrintTask(){

    }
    public void startPrintQueue(){

    }
    public void addNewPrintTask(int printChoice, int colorChoice){
        Print selectedPrint;
        Spool spoolPrint ;
        PrintTask printTask;

        System.out.println("Choose a print number:");
        for(Print print: printManager.getPrints()){
            System.out.println(print);
        }
        selectedPrint = printManager.getPrints().get(printChoice+1);

        System.out.println("Choose a color and a filament type:");
        for(Spool spool:spoolManager.getSpools()){
            System.out.println("Color:" + spool.getColor());
            System.out.println("Filament type:" + spool.getFilamentType());
        }

        spoolPrint = spoolManager.getSpools().get(colorChoice);


        printManager.addPrintTask(selectedPrint,new ArrayList<>(),spoolPrint.getFilamentType());
    }
    public void registerPrinterFailure(){}
    public void registerPrintCompletion(){}
    public void changePrintStrategy(){}
    private void exit(){}
}
