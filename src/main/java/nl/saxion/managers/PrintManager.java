package nl.saxion.managers;

import nl.saxion.Models.Print;
import nl.saxion.adapter.AdapterReader;
import nl.saxion.adapter.CSVAdapterReader;
import nl.saxion.adapter.JSONAdapterReader;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PrintManager {
    private List<Print> prints = new ArrayList<>();

    public PrintManager() {
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


    public Print findPrint(String print) {
        for (Print allPrints : prints) {
            if (allPrints.getName().equals(print)) {
                return allPrints;
            }
        }
        throw new NoSuchElementException("Printer with such print does not exist");
    }
}

