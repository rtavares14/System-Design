package nl.saxion.managers;

import nl.saxion.Models.FilamentType;
import nl.saxion.Models.Spool;

import java.util.ArrayList;

public class SpoolManager {
    private ArrayList<Spool> spools;

    public SpoolManager() {
        this.spools = new ArrayList<>();
    }

    public void addSpool(String color, FilamentType filamentType, double length){
        spools.add(new Spool(color,filamentType,length));
    }

    public void getSpool(){}

    public static void readSpoolsFromFile(String file){}

    public ArrayList<Spool> getSpools() {
        return spools;
    }
}
