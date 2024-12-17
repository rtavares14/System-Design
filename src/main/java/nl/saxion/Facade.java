package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;

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
    public void addNewPrintTask(int choice){
        Print selectedPrint = null;
        PrintTask printTask = null;

        System.out.println("Choose a print number:");
        for(Print print: printManager.getPrints()){
            System.out.println(print);
        }

        System.out.println("Choose a color:");
        for(Spool spool:spoolManager.getSpools()){
            System.out.println(spool.getColor());
        }



        printManager.addPrintTask();
    }
    public void registerPrinterFailure(){}
    public void registerPrintCompletion(){}
    public void changePrintStrategy(){}
    private void exit(){}
}
