package nl.saxion.adapter;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.Models.printer.printerTypes.StandardFDM;
import nl.saxion.utils.FilamentType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVAdapterReader implements AdapterReader {
    private static CSVAdapterReader reader;

    private CSVAdapterReader() {
    }

    public static CSVAdapterReader getReader() {
        if (reader == null) {
            return reader = new CSVAdapterReader();
        }
        return reader;
    }

    @Override
    public boolean supportsFileType(String filename) {
        return filename.endsWith(".csv");
    }

    @Override
    public List<Spool> readSpools(String filePath) {
        List<Spool> spools = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Spool spool = new Spool(
                        Integer.parseInt(values[0]), // id
                        values[1], // color
                        FilamentType.valueOf(values[2]), // filamentType
                        Double.parseDouble(values[3]) // length
                );
                spools.add(spool);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spools;
    }


    @Override
    public List<Printer> readPrinters(String filePath) {
        List<Printer> printers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip the header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                int id = Integer.parseInt(values[0]);
                int type = Integer.parseInt(values[1]);
                String name = values[2];
                String model = values[3];
                String manufacturer = values[4];
                int maxX = Integer.parseInt(values[5]);
                int maxY = Integer.parseInt(values[6]);
                int maxZ = Integer.parseInt(values[7]);
                int maxColors = Integer.parseInt(values[8]);
                boolean isHoused = (type == 3 || type == 4);

                if (type == 1 || type == 3) {
                    // StandardFDM Printer
                    Printer printer = new StandardFDM(id, name, model, manufacturer, maxX, maxY, maxZ, isHoused);
                    printers.add(printer);
                } else if (type == 2 || type == 4) {
                    // MultiColor Printer
                    Printer printer = new MultiColor(id, name, model, manufacturer, maxX, maxY, maxZ, isHoused, maxColors);
                    printers.add(printer);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return printers;
    }


    @Override
    public List<Print> readPrints(String filePath) {
        List<Print> prints = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip the header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                String name = values[0]; // name
                int width = Integer.parseInt(values[1]); // width
                int length = Integer.parseInt(values[2]); // length
                int height = Integer.parseInt(values[3]); // height
                int printTime = Integer.parseInt(values[8]); // printTime

                // filament lengths, handling empty fields
                ArrayList<Double> filamentLengths = new ArrayList<>();
                for (int i = 4; i <= 7; i++) {
                    if (!values[i].isEmpty()) {
                        filamentLengths.add(Double.parseDouble(values[i]));
                    }
                }

                // Create and add the Print object
                Print print = new Print(name, height, width, length, filamentLengths, printTime);
                prints.add(print);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return prints;
    }
}
