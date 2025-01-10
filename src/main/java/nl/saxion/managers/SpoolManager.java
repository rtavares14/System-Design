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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.saxion.utils.FilamentType.*;

public class SpoolManager {
    private final ArrayList<Spool> spools;

    public SpoolManager() {
        this.spools = new ArrayList<>();
    }

    public void addSpool(String color, FilamentType filamentType, double length){
        spools.add(new Spool(spools.size()+1,color,filamentType,length));
    }

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
                        type = PLA;
                        break;
                    case "PETG":
                        type = PETG;
                        break;
                    case "ABS":
                        type = ABS;
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

    public List<Spool> getSpools() {
        return spools;
    }
}
