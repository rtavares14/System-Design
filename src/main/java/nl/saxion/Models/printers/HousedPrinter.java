package nl.saxion.Models.printers;

/* Printer capable of printing ABS */
public class HousedPrinter extends StandardFDM {
    public HousedPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
    }
}
