package nl.saxion.managers;

import nl.saxion.utils.FilamentType;
import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.exceptions.ColorNotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PrintManager {
    private List<Print> prints = new ArrayList<>();
    private static List<PrintTask> printTasks = new ArrayList<>();
    private final SpoolManager spoolManager; // Reference shared instance

    public PrintManager(SpoolManager spoolManager) {
        this.spoolManager = spoolManager;
    }

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
        printTasks.add(task);
        System.out.print("Task added to the queue");
    }

    public Print findPrint(String print) {
        for (Print allPrints : prints) {
            if (allPrints.getName().equals(print)) {
                return allPrints;
            }
        }
        throw new NoSuchElementException("Printer with such print does not exist");
    }

    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        prints.add(new Print(name, height, width, length, filamentLength, printTime));
    }

    public void readPrintsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if (filename.isEmpty()) {
            filename = "prints.json";
        }
        URL printsResource = PrintManager.class.getResource("/" + filename);
        if (printsResource == null) {
            System.err.println("Warning: Could not find prints.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printsResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray printsArray = (JSONArray) jsonParser.parse(reader);
            for (Object p : printsArray) {
                JSONObject print = (JSONObject) p;
                String name = (String) print.get("name");
                int height = ((Long) print.get("height")).intValue();
                int width = ((Long) print.get("width")).intValue();
                int length = ((Long) print.get("length")).intValue();
                JSONArray filamentLengthArray = (JSONArray) print.get("filamentLength");
                ArrayList<Double> filamentLength = new ArrayList<>();
                for (Object lengthObj : filamentLengthArray) {
                    filamentLength.add((Double) lengthObj);
                }
                int printTime = ((Long) print.get("printTime")).intValue();

                prints.add(new Print(name, height, width, length, filamentLength, printTime));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public List<Print> getPrints() {
        return prints;
    }

    public static List<PrintTask> getPrintTasks() {
        return printTasks;
    }
}

