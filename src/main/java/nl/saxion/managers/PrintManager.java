package nl.saxion.managers;

import nl.saxion.Models.FilamentType;
import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.exceptions.ColorNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PrintManager {
    private ArrayList<Print> prints;
    private ArrayList<PrintTask> printTasks;
    private SpoolManager spoolManager;

    public PrintManager() {
        this.prints = new ArrayList<Print>();
        this.spoolManager = new SpoolManager();
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {

        Print print = findPrint(printName);

        for (String color : colors) {
            ArrayList<Spool> spools = spoolManager.getSpools();
            if (spools.stream().noneMatch(spool -> spool.spoolMatch(color, type))) {
                throw new ColorNotFoundException("Color " + color + " (" + type + ") not found");
            }
        }

        printTasks.add(new PrintTask(print, colors, type));
    }

    public Print findPrint(String print) {
        for (Print allPrints : prints) {
            if (allPrints.getName().equals(print)) {
                return allPrints;
            }
        }
        throw new NoSuchElementException("Printer with such print does not exist");
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

