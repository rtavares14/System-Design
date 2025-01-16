package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Spool;
import nl.saxion.Models.interfaces.PrintTimeCalculator;
import nl.saxion.Models.printer.Printer;
import nl.saxion.exceptions.ColorNotFoundException;

import java.util.ArrayList;
import java.util.List;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer implements PrintTimeCalculator {
    private List<Spool> allSpools;


    public StandardFDM(int id, String printerName,String model, String manufacturer, int maxX, int maxY, int maxZ, boolean isHoused,int maxColors) {
        super(id, printerName,model, manufacturer, maxX, maxY, maxZ, isHoused,maxColors);
    }

    public void setCurrentSpool(Spool spool) {
        this.allSpools.set(0,spool);
    }

    public List<Spool> getSpools() {
        return allSpools;
    }

    public void setCurrentSpools(List<Spool> spools) {
        this.allSpools = spools;
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
//        for(Spool spool:allSpools) {
//            append += "- Spool(s): " + spool.getId() + System.lineSeparator();
//        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }
}