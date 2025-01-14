package nl.saxion.managers;

import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.adapter.AdapterReader;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;
import nl.saxion.exceptions.ColorNotFoundException;
import nl.saxion.utils.FilamentType;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PrintManager {
    private final SpoolManager spoolManager; // Reference shared instance
    private List<Print> prints = new ArrayList<>();
    private ArrayList<PrintTask> printTasks = new ArrayList<>();
    private List<PrintTask> pendingPrints = new ArrayList<>();

    public PrintManager(SpoolManager spoolManager) {
        this.spoolManager = spoolManager;
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
     * Getter for the list of prints.
     */
    public List<Print> getPrints() {
        return prints;
    }

    /**
     * Getter for the list of pending prints.
     */
    public List<PrintTask> getPrintTasks() {
        return pendingPrints;
    }

    /**
     * Method to read prints from a file.
     * The file can be either a JSON or CSV file.
     * The method will determine the file type and use the appropriate handler.
     * The prints will be added to the prints list.
     *
     * @param filename the name of the file to read from
     */
    public void readPrintsFromFile(String filename) {
        URL printerResource = getClass().getResource("/" + filename);
        assert printerResource != null;
        String path = URLDecoder.decode(printerResource.getPath(), StandardCharsets.UTF_8);
        AdapterReader fileHandler;

        if (getJsonFileHandler().supportsFileType(path)) {
            fileHandler = getJsonFileHandler();
        } else if (getCsvFileHandler().supportsFileType(path)) {
            fileHandler = getCsvFileHandler();
        } else {
            System.out.println("Unsupported file type for filename: " + path);
            return;
        }

        List<Print> printsFromFile = fileHandler.readPrints(path);
        prints.addAll(printsFromFile);
    }


    /**
     * Method to add a new printTask to the list of prints.
     *
     * @param printName the name of the print
     * @param colors    the colors of the print
     * @param type      the type of filament
     */
    public void addPrintTask(Print printName, List<String> colors, FilamentType type) {
        Print print = findPrint(printName.getName());
        if (print == null || colors.isEmpty()) {
            System.err.println("All fields must be filled in");
            return;
        }

        for (String color : colors) {
            boolean found = false;
            for (Spool spool : spoolManager.getSpools()) {
                if (spool.getColor().equals(color) && spool.getFilamentType() == type) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ColorNotFoundException("Color " + color + " (" + type + ") not found");
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrints.add(task);
        System.out.println("Task added to the queue");
    }

    public Print findPrint(String print) {
        for (Print allPrints : prints) {
            if (allPrints.getName().equals(print)) {
                return allPrints;
            }
        }
        throw new NoSuchElementException("Printer with such print does not exist");
    }
}

