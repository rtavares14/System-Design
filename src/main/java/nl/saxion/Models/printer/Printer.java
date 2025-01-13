package nl.saxion.Models.printer;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.observer.PrintTaskObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class Printer {
    private final int id;
    private final String name;
    private final String model;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private boolean housed;
    private final List<Spool> spools;
    private String status;
    private final List<PrintTaskObserver> observers; // List of observers

    public Printer(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean housed) {
        this.id = id;
        this.name = printerName;
        this.model = model;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.housed = housed;
        this.spools = new ArrayList<>();
        this.status = "Idle"; // Default status
        this.observers = new ArrayList<>(); // Initialize observers list
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


    public void addSpool(Spool spool) {
        spools.add(spool);
    }

    public void removeSpool(Spool spool) {
        spools.remove(spool);
    }

    public void clearSpools() {
        spools.clear();
    }

    public Spool getCurrentSpool(){
        return spools.get(0);
    }

    public Spool[] getCurrentSpools(){
        return spools.toArray(new Spool[0]);
    }
    public abstract Spool[] getSpools();

    public abstract void setCurrentSpools(ArrayList<Spool> spools);

    public boolean printFits(Print print) {
        return print.getLength() <= maxX &&
                print.getHeight() <= maxY &&
                print.getWidth() <= maxZ;
    }

    public boolean isHoused() {
        return housed;
    }

    @Override
    public String toString() {
        return "-----------------------------------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Model: " + model + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator() +
                "- Max Dimensions: (" + maxX + " x " + maxY + " x " + maxZ + ")" + System.lineSeparator() +
                "- Housed: " + housed + System.lineSeparator() +
                "- Type: " + this.getClass().getSimpleName() + System.lineSeparator() +
                 "- Status: " + status ;
    }
}