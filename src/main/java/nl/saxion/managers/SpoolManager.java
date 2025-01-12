package nl.saxion.managers;

import nl.saxion.Models.Spool;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;
import nl.saxion.adapter.AdapterReader;
import nl.saxion.utils.FilamentType;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpoolManager {
    private final List<Spool> spools;

    public SpoolManager() {
        this.spools = new ArrayList<>();
    }

    /**
     * Getter for the JSON file handler.
     */
    private AdapterReader getJsonFileHandler() {
        return JSONAdapterReader.getReader();
    }

    /**
     * Getter for the CSV file handler.
     */
    private AdapterReader getCsvFileHandler() {
        return CSVAdapterReader.getReader();
    }

    /**
     * Getter for the list of spools.
     */
    public List<Spool> getSpools() {
        return spools;
    }

    /**
     * Method to read spools from a file.
     * The file can be either a JSON or CSV file.
     * The method will determine the file type and use the appropriate handler.
     * The spools will be added to the spools.
     *
     * @param filename The name of the file to read the spools from.
     */
    public void readSpoolsFromFile(String filename) {
        URL spoolResource = getClass().getResource("/" + filename);
        assert spoolResource != null;
        String path = URLDecoder.decode(spoolResource.getPath(), StandardCharsets.UTF_8);
        AdapterReader fileHandler;

        if (getJsonFileHandler().supportsFileType(path)) {
            fileHandler = getJsonFileHandler();
        } else if (getCsvFileHandler().supportsFileType(path)) {
            fileHandler = getCsvFileHandler();
        } else {
            System.out.println("Unsupported file type for filename: " + path);
            return;
        }

        List<Spool> spoolsFromFile = fileHandler.readSpools(path);
        spools.addAll(spoolsFromFile);
    }

    /**
     * Method that will return a list of available colors for a given filament type.
     *
     * @param filamentType The filament type to get the available colors for.
     * @return A list of available colors for the given filament type.
     */
    public List<String> getAvailableColors(FilamentType filamentType) {
        List<Spool> spools = getSpools();
        Set<String> availableColors = new HashSet<>();
        for (Spool spool : spools) {
            String colorString = spool.getColor();
            if (filamentType == spool.getFilamentType()) {
                availableColors.add(colorString);
            }
        }
        return availableColors.stream().toList();
    }

    public void getSpool(){}
}
