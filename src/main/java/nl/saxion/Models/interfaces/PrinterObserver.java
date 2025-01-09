package nl.saxion.Models.interfaces;

import nl.saxion.Models.printer.Printer;

public interface PrinterObserver {
    void update(Printer printer); // Observer gets notified of changes
}
