package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;

import java.util.List;

public class StandardFDM extends Printer {
    private List<Spool> allSpools;


    public StandardFDM(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean isHoused, int maxColors) {
        super(id, printerName, model, manufacturer, maxX, maxY, maxZ, isHoused, maxColors);
    }

    public List<Spool> getSpools() {
        return allSpools;
    }

    public void setCurrentSpools(List<Spool> spools) {
        this.allSpools = spools;
    }

}