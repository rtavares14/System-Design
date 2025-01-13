package nl.saxion.Models.printer;

import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.managers.PrinterManager;

import java.util.ArrayList;

public class PrinterFactory {

    private static PrinterManager printerManager = null;

    public PrinterFactory(PrinterManager printerManager) {
        PrinterFactory.printerManager = printerManager;
    }

    /**
     * Adds a printer to the printer manager based on the provided parameters.
     *
     * @param id           the unique ID of the printer
     * @param printerType  the type of printer (1 = StandardFDM, 2 = MultiColor)
     * @param printerName  the name of the printer
     * @param manufacturer the manufacturer of the printer
     * @param maxX         the maximum X dimension
     * @param maxY         the maximum Y dimension
     * @param maxZ         the maximum Z dimension
     * @param maxColors    the maximum colors (only applicable to MultiColor printers)
     */
    public static Printer addPrinter(int id, int printerType, String printerName,String model, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        boolean isHoused;
        if (printerType == 1 || printerType == 2) {
            // Create a StandardFDM printer (or a housed printer)
            StandardFDM printer = new StandardFDM(id, printerName,model, manufacturer, maxX, maxY, maxZ, isHoused = printerType == 2);
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        } else if (printerType == 3 || printerType == 4) {
            // Create a MultiColor printer
            MultiColor printer = new MultiColor(id, printerName,model, manufacturer, maxX, maxY, maxZ, isHoused = printerType == 4, maxColors);
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        }
        return null;
    }
}
