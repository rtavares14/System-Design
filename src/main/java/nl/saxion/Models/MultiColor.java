package nl.saxion.Models;

import java.util.ArrayList;

/* Printer capable of printing multiple colors. */
public class MultiColor extends StandardFDM {
    private int maxColors;
    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.maxColors = maxColors;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        setCurrentSpool(spools.get(0));
        if(spools.size() > 1) spool2 = spools.get(1);
        if(spools.size() > 2) spool3 = spools.get(2);
        if(spools.size() > 3) spool4 = spools.get(3);
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

    @Override
    public String toString() {
        String result = super.toString();
        String[] resultArray = result.split("- ");
        String spools = resultArray[resultArray.length-1];
        if(spool2 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool2.getId() + System.lineSeparator());
        }
        if(spool3 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool3.getId() + System.lineSeparator());
        }
        if(spool4 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool4.getId() + System.lineSeparator());
        }
        spools = spools.replace("--------", "- maxColors: " + maxColors + System.lineSeparator() +
               "--------");
        resultArray[resultArray.length-1] = spools;
        result = String.join("- ", resultArray);

        return result;
    }

    public int getMaxColors() {
        return maxColors;
    }
}
