package nl.saxion.adapter;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.PrinterFactory;
import nl.saxion.utils.FilamentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JSONAdapterReader implements AdapterReader {
    private final JSONParser jsonParser = new JSONParser();
    private static JSONAdapterReader reader;

    private JSONAdapterReader() {
    }

    public static JSONAdapterReader getReader() {
        if (reader == null) {
            return reader = new JSONAdapterReader();
        }
        return reader;
    }

    /**
     * Check if the file is a JSON file
     *
     * @param filename The name of the file
     * @return boolean
     */
    @Override
    public boolean supportsFileType(String filename) {
        return filename.endsWith(".json");
    }

    /**
     * Parse a JSON file and return a JSONArray
     *
     * @param filePath The path to the file
     * @return JSONArray
     */
    private JSONArray parseJsonFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Object obj = jsonParser.parse(reader);
            if (obj instanceof JSONArray) {
                return (JSONArray) obj;
            }
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, "Expected JSONArray, found different structure");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    /**
     * Will read the spools from a file and return a list of spools
     *
     * @param filePath The path to the file
     * @return List of spools
     */
    @Override
    public List<Spool> readSpools(String filePath) {
        JSONArray spoolsJson = parseJsonFromFile(filePath);
        List<Spool> spools = new ArrayList<>();
        for (Object spoolObj : spoolsJson) {
            JSONObject spoolJson = (JSONObject) spoolObj;
            spools.add(convertJsonToSpool(spoolJson));
        }
        return spools;
    }

    /**
     * Will read the printers from a file and return a list of printers
     *
     * @param filePath The path to the file
     * @return List of printers
     */
    @Override
    public List<Printer> readPrinters(String filePath) {
        JSONArray printersJson = parseJsonFromFile(filePath);
        List<Printer> printers = new ArrayList<>();

        for (Object printerObj : printersJson) {
            JSONObject printerJson = (JSONObject) printerObj;
            printers.add(convertJsonToPrinter(printerJson));
        }

        return printers;
    }

    /**
     * Will read the prints from a file and return a list of prints
     *
     * @param filePath The path to the file
     * @return List of prints
     */
    @Override
    public List<Print> readPrints(String filePath) {
        JSONArray printsJson = parseJsonFromFile(filePath);
        List<Print> prints = new ArrayList<>();
        for (Object printObj : printsJson) {
            JSONObject printJson = (JSONObject) printObj;
            prints.add(convertJsonToPrint(printJson));
        }
        return prints;
    }

    /**
     * Convert a JSONObject to a Printer object
     * @param printerJson JSONObject to convert
     * @return Printer object
     */
    private Printer convertJsonToPrinter(JSONObject printerJson) {
        try {
            int id = ((Long) printerJson.get("id")).intValue();
            int type = ((Long) printerJson.get("type")).intValue();
            String name = (String) printerJson.get("name");
            String model = (String) printerJson.get("model");
            String manufacturer = (String) printerJson.get("manufacturer");
            int maxX = ((Long) printerJson.get("maxX")).intValue();
            int maxY = ((Long) printerJson.get("maxY")).intValue();
            int maxZ = ((Long) printerJson.get("maxZ")).intValue();
            int maxColors = ((Long) printerJson.get("maxColors")).intValue();

            Printer printer = PrinterFactory.addPrinter(id, type, name,model, manufacturer, maxX, maxY, maxZ, maxColors);
            if (printer != null) {
                return printer;
            }

            return null;
        } catch (Exception e) {
            System.out.println("Error while converting printer: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert a JSONObject to a Print object
     * @param printJson JSONObject to convert
     * @return Print object
     */
    private Print convertJsonToPrint(JSONObject printJson) {
        String name = (String) printJson.get("name");
        int height = ((Long) printJson.get("height")).intValue();
        int width = ((Long) printJson.get("width")).intValue();
        int length = ((Long) printJson.get("length")).intValue();
        JSONArray fLength = (JSONArray) printJson.get("filamentLength");
        int printTime = ((Long) printJson.get("printTime")).intValue();
        ArrayList<Double> filamentLength = new ArrayList<>();
        for (Object lengthValue : fLength) {
            filamentLength.add(((Number) lengthValue).doubleValue());
        }
        return new Print(name, height, width, length, filamentLength, printTime);
    }


    /**
     * Convert a JSONObject to a Spool object
     * @param spoolJson JSONObject to convert
     * @return Spool object
     */
    private Spool convertJsonToSpool(JSONObject spoolJson) {
        int id = ((Long) spoolJson.get("id")).intValue();
        String color = (String) spoolJson.get("color");
        String filamentTypeStr = (String) spoolJson.get("filamentType");
        double length = ((Number) spoolJson.get("length")).doubleValue();

        FilamentType filamentType;
        try {
            filamentType = FilamentType.valueOf(filamentTypeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid filament type: " + filamentTypeStr);
            return null;
        }

        return new Spool(id, color, filamentType, length);
    }
}
