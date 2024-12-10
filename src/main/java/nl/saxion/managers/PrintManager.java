package nl.saxion.managers;

import nl.saxion.Models.FilamentType;
import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;

import java.util.ArrayList;
import java.util.List;

public class PrintManager {
    private ArrayList<Print> prints;
    private SpoolManager spoolManager;

    public PrintManager() {
        this.prints = new ArrayList<Print>();
        this.spoolManager = new SpoolManager();
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        Print print = findPrint(printName);

        if (print == null) {
            System.out.println("Could not find print with name " + printName);
            return;
        }

        if (colors.isEmpty()) {
            System.out.println("Need at least one color, but none given");
            return;
        }

        for (String color : colors) {
            boolean found = false;
            for (Spool spool : spools) {
                if (spool.getColor().equals(color) && spool.getFilamentType() == type) {
                    found = true;
                    break;
                }
            }
        }
    }

    public Print findPrint(String print) {
        return null;
    }

    public void addPrint(String name, int height, int width, int length, double filamentLength, int printTime) {
        prints.add(new Print(name, height, width, length, filamentLength, printTime));
    }

    public static void readPrintsFromFile(String file) {
    }

    public ArrayList<Print> getPrints() {
        return prints;
    }
}

