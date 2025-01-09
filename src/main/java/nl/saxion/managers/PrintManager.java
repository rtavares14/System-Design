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
    private ArrayList<Print> prints = new ArrayList<>();
    private ArrayList<PrintTask> printTasks;
    private SpoolManager spoolManager;

    public PrintManager() {
        readPrintsFromFile("");
        this.prints = new ArrayList<>();
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

    public void addPrintTask(Print printName, List<String> colors, FilamentType type) {

        for (String color : colors) {
            ArrayList<Spool> spools = spoolManager.getSpools();
            if (spools.stream().noneMatch(spool -> spool.spoolMatch(color, type))) {
                throw new ColorNotFoundException("Color " + color + " (" + type + ") not found");
            }
        }

        printTasks.add(new PrintTask(printName, colors, type));
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
        if(filename.length() == 0) {
            filename = "prints.json";
        }
        URL printResource = getClass().getResource("/" + filename);
        if (printResource == null) {
            System.err.println("Warning: Could not find prints.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray prints = (JSONArray) jsonParser.parse(reader);
            for (Object p : prints) {
                JSONObject print = (JSONObject) p;
                String name = (String) print.get("name");
                int height = ((Long) print.get("height")).intValue();
                int width = ((Long) print.get("width")).intValue();
                int length = ((Long) print.get("length")).intValue();
                JSONArray fLength = (JSONArray) print.get("filamentLength");
                int printTime = ((Long) print.get("printTime")).intValue();
                ArrayList<Double> filamentLength = new ArrayList();
                for(int i = 0; i < fLength.size(); i++) {
                    filamentLength.add(((Double) fLength.get(i)));
                }
                addPrint(name, height, width, length, filamentLength, printTime);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Print> getPrints() {
        return prints;
    }

    public ArrayList<PrintTask> getPrintTasks() {
        return printTasks;
    }
}

