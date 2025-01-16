package nl.saxion.Models.printer.printerTypes;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.interfaces.PrintTimeCalculator;
import nl.saxion.Models.printer.Printer;

import java.util.ArrayList;
import java.util.List;

public class MultiColor extends Printer implements PrintTimeCalculator {
    private int maxColors;
    private List<Spool> spools = new ArrayList<>();

    public MultiColor(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean isHoused, int maxColors) {
        super(id, printerName, model, manufacturer, maxX, maxY, maxZ, isHoused,maxColors);
        this.maxColors = maxColors;
    }

    @Override
    public List<Spool> getSpools() {
        return spools;
    }

    public void setCurrentSpools(List<Spool> spools) {
        this.spools = spools;
    }

    // New methods: Get and Set for the current spool
    public Spool getCurrentSpool() {
        return spools.get(0);
    }

    public void setCurrentSpool(Spool spool) {
        this.spools.set(0,spool);
    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "";
       for(Spool spool:spools){

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
