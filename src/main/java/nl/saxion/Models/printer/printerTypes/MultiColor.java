package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.interfaces.PrintTimeCalculator;
import nl.saxion.Models.printer.Printer;

import java.util.ArrayList;

public class MultiColor extends Printer implements PrintTimeCalculator {
    private int maxColors;
    private Spool currentSpool;
    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(int id, String printerName,String model, String manufacturer, int maxX, int maxY, int maxZ, boolean isHoused, int maxColors) {
        super(id, printerName, model, manufacturer, maxX, maxY,  maxZ, isHoused);
        this.maxColors = maxColors;
    }

    @Override
    public Spool[] getSpools() {
        Spool[] spools = new Spool[4];
        spools[0] = getCurrentSpool();
        spools[1] = spool2;
        spools[2] = spool3;
        spools[3] = spool4;
        return spools;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        setCurrentSpool(spools.get(0));
        if (spools.size() > 1) spool2 = spools.get(1);
        if (spools.size() > 2) spool3 = spools.get(2);
        if (spools.size() > 3) spool4 = spools.get(3);
    }

    // New methods: Get and Set for the current spool
    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public void setCurrentSpool(Spool spool) {
        this.currentSpool = spool;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "";
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId();
            if (spool2 != null) append += ", " + spool2.getId();
            if (spool3 != null) append += ", " + spool3.getId();
            if (spool4 != null) append += ", " + spool4.getId();
            append += System.lineSeparator();
        }
        append += "--------";
        result = result.replace("--------", append);
        return result;
    }

    @Override
    public int calculatePrintTime(int length, int speed) {
        // Optionally override the default implementation if needed
        System.out.println("Calculating print time for MultiColor printer...");
        return PrintTimeCalculator.super.calculatePrintTime(length, speed);
    }

    public int getMaxColors() {
        return maxColors;
    }
}
