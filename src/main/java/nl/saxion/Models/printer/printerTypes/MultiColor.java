package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;

import java.util.ArrayList;
import java.util.List;

public class MultiColor extends Printer {
    private int maxColors;
    private List<Spool> spools = new ArrayList<>();

    public MultiColor(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean isHoused, int maxColors) {
        super(id, printerName, model, manufacturer, maxX, maxY, maxZ, isHoused, maxColors);
        this.maxColors = maxColors;
    }

    @Override
    public List<Spool> getSpools() {
        return spools;
    }

    public void setCurrentSpools(List<Spool> spools) {
        this.spools = spools;
    }

    public Spool getCurrentSpool() {
        return spools.getFirst();
    }

    public int getMaxColors() {
        return maxColors;
    }
}
