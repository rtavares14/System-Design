package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.interfaces.PrintTimeCalculator;
import nl.saxion.Models.printer.Printer;
import nl.saxion.utils.FilamentType;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer implements PrintTimeCalculator {
    private Spool currentSpool;
    private boolean isHoused;

    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.isHoused = false;
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

    public boolean isHoused() {
        return isHoused;
    }

    public void setHoused(boolean housed) {
        isHoused = housed;
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
        String append = "- maxX: " + super.getMaxX() + System.lineSeparator() +
                "- maxY: " + super.getMaxY() + System.lineSeparator() +
                "- maxZ: " + super.getMaxZ() + System.lineSeparator() +
                "- Housed: " + isHoused + System.lineSeparator();
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId() + System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}