package nl.saxion.Models.interfaces;

import nl.saxion.Models.printer.Printer;

public interface PrinterObserver {

    private void update(Printer printer){
        System.out.println("Printer " + printer.getName() + " is " + printer.getStatus());
    }
}
