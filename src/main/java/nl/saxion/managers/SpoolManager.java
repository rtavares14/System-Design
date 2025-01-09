package nl.saxion.managers;

import nl.saxion.utils.FilamentType;
import nl.saxion.Models.Spool;
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

public class SpoolManager {
    private final ArrayList<Spool> spools;

    public SpoolManager() {
        this.spools = new ArrayList<>();
    }

    public void addSpool(String color, FilamentType filamentType, double length){
        spools.add(new Spool(spools.size()+1,color,filamentType,length));
    }

    public void getSpool(){}

    public void readSpoolsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if (filename.isEmpty()) {
            filename = "spools.json";
        }
        URL spoolsResource = getClass().getResource("/" + filename);
        if (spoolsResource == null) {
            System.err.println("Warning: Could not find spools.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(spoolsResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray spoolsArray = (JSONArray) jsonParser.parse(reader);
            for (Object p : spoolsArray) {
                JSONObject spool = (JSONObject) p;
                String color = (String) spool.get("color");
                String filamentType = (String) spool.get("filamentType");
                double length = (Double) spool.get("length");
                FilamentType type;
                switch (filamentType) {
                    case "PLA":
                        type = FilamentType.PLA;
                        break;
                    case "PETG":
                        type = FilamentType.PETG;
                        break;
                    case "ABS":
                        type = FilamentType.ABS;
                        break;
                    default:
                        System.out.println("- Not a valid filamentType, bailing out");
                        return;
                }
                addSpool(color, type, length);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Spool> getSpools() {
        return spools;
    }
}
