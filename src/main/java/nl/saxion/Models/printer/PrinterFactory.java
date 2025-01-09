package nl.saxion.Models.printer;

import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.managers.PrinterManager;

import java.util.ArrayList;

public class PrinterFactory {

    private final PrinterManager printerManager;

    public PrinterFactory(PrinterManager printerManager) {
        this.printerManager = printerManager;
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
     * @param isHoused     whether the printer is housed (only applicable to StandardFDM)
     */
    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors, boolean isHoused) {
        if (printerType == 1) {
            // Create a StandardFDM printer
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ);
            printer.setHoused(isHoused); // Set the housed property dynamically
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        } else if (printerType == 2) {
            // Create a MultiColor printer
            MultiColor printer = new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
            printerManager.printersList.add(printer);
            printerManager.printersMap.put(printer, new ArrayList<>());
        }
    }
}
