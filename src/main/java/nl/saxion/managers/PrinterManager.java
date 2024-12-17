package nl.saxion.managers;

import nl.saxion.Models.*;
import nl.saxion.Models.printers.HousedPrinter;
import nl.saxion.Models.printers.MultiColor;
import nl.saxion.Models.printers.Printer;
import nl.saxion.Models.printers.StandardFDM;

import java.util.*;

public class PrinterManager {
    private List<Printer> printers = new ArrayList<Printer>();
    PrinterManager printerManager;

    public List<Printer> getPrinters() {
        return printers;
    }
}
