package nl.saxion.Models.printer;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.interfaces.PrinterObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class Printer {
    private final int id;
    private final String name;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final List<Spool> spools;
    private String status;
    private final List<PrinterObserver> observers; // List of observers

    public Printer(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        this.id = id;
        this.name = printerName;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.spools = new ArrayList<>();
        this.status = "Idle"; // Default status
        this.observers = new ArrayList<>(); // Initialize observers list
    }

    // Observer methods
    // Add an observer
    public void addObserver(PrinterObserver observer) {
        observers.add(observer);
    }

    // Remove an observer
    public void removeObserver(PrinterObserver observer) {
        observers.remove(observer);
    }

    // Notify all observers of a status change
    private void notifyObservers() {
        for (PrinterObserver observer : observers) {
            observer.update(this);
        }
    }

    // Set status and notify observers
    public void setStatus(String status) {
        this.status = status;
        notifyObservers(); // Notify observers whenever the status is updated
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public List<Spool> getSpools() {
        return spools;
    }

    public void addSpool(Spool spool) {
        spools.add(spool);
    }

    public void removeSpool(Spool spool) {
        spools.remove(spool);
    }

    public void clearSpools() {
        spools.clear();
    }

    public abstract Spool[] getCurrentSpools();

    public abstract void setCurrentSpools(ArrayList<Spool> spools);

    public boolean printFits(Print print) {
        return print.getLength() <= maxX &&
                print.getHeight() <= maxY &&
                print.getWidth() <= maxZ;
    }

    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Model: " + manufacturer + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator() +
                "- Max Dimensions: (" + maxX + " x " + maxY + " x " + maxZ + ")" + System.lineSeparator() +
                "- Status: " + status + System.lineSeparator();
    }
}