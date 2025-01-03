package nl.saxion.Models.printer;

import nl.saxion.Models.printer.printerTypes.HousedPrinter;
import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.managers.PrinterManager;

import java.util.ArrayList;

public class PrinterFactory {

    private PrinterManager printerManager;

    public PrinterFactory(PrinterManager printerManager) {
        this.printerManager = printerManager;
    }

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        if (printerType == 1) {
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ);
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        } else if (printerType == 2) {
            HousedPrinter printer = new HousedPrinter(id, printerName, manufacturer, maxX, maxY, maxZ);
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        } else if (printerType == 3) {
            MultiColor printer = new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        }
    }

}
