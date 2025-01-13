package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Spool;
import nl.saxion.Models.interfaces.PrintTimeCalculator;
import nl.saxion.Models.printer.Printer;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer implements PrintTimeCalculator {
    private Spool currentSpool;

    public StandardFDM(int id, String printerName,String model, String manufacturer, int maxX, int maxY, int maxZ, boolean isHoused) {
        super(id, printerName,model, manufacturer, maxX, maxY, maxZ, isHoused);
    }

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public void setCurrentSpool(Spool spool) {
        this.currentSpool = spool;
    }

    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[1];
        if (currentSpool != null) {
            spools[0] = currentSpool;
        }
        return spools;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        this.currentSpool = spools.get(0);
    }

    /**
     * Check if a print supports ABS material.
     *
//     * @param print the print to check
     * @return true if ABS is supported
     */
//    private boolean supportsABS(Print print) {
//        // Assume a specific property of `Print` (like material type) determines ABS compatibility
//        return print.getFilamentType().equals(FilamentType.ABS);
//    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "";
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId() + System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}