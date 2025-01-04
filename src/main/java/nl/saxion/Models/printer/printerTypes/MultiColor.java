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

    public MultiColor(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.maxColors = maxColors;
    }

    @Override
    public Spool[] getCurrentSpools() {
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
        String[] resultArray = result.split("- ");
        String spools = resultArray[resultArray.length - 1];
        if (spool2 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool2.getId() + System.lineSeparator());
        }
        if (spool3 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool3.getId() + System.lineSeparator());
        }
        if (spool4 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool4.getId() + System.lineSeparator());
        }
        spools = spools.replace("--------", "- maxColors: " + maxColors + System.lineSeparator() +
                "--------");
        resultArray[resultArray.length - 1] = spools;
        result = String.join("- ", resultArray);

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
