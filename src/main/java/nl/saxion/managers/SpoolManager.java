package nl.saxion.managers;

import nl.saxion.Models.Spool;

import java.util.ArrayList;

public class SpoolManager {
    private ArrayList<Spool> spools;

    public SpoolManager() {
        this.spools = new ArrayList<>();
    }

    public void addSpool(Spool spool){}

    public static void readSpoolsFromFile(String file){}

    public ArrayList<Spool> getSpools() {
        return spools;
    }
}
