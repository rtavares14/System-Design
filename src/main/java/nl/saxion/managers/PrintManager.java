package nl.saxion.managers;

import nl.saxion.Models.Print;

import java.util.ArrayList;

public class PrintManager {
    private ArrayList<Print> prints;

    public PrintManager() {
        this.prints = prints = new ArrayList<Print>();
    }

    public void addPrintTask() {
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

