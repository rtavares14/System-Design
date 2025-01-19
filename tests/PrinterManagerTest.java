import nl.saxion.Models.Print;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;
import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.managers.PrintManager;
import nl.saxion.managers.PrinterManager;
import nl.saxion.managers.SpoolManager;
import nl.saxion.utils.FilamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrinterManagerTest {

    private PrinterManager printerManager;
    private SpoolManager spoolManager;
    private PrintManager printManager;

    @BeforeEach
    public void setUp() {
        spoolManager = new SpoolManager();
        printManager = new PrintManager();
        printerManager = new PrinterManager(spoolManager);

        // data from files
        printerManager.readPrintersFromFile("printers.csv");
        printManager.readPrintsFromFile("prints.csv");
        spoolManager.readSpoolsFromFile("spools.csv");
    }

    @Test
    public void testAllCombinationsWithTable() throws IOException {
        List<Printer> printers = printerManager.getPrinters();
        List<Print> prints = printManager.getPrints();
        List<Spool> spools = spoolManager.getSpools();

        Map<String, Map<String, Map<String, Boolean>>> results = new HashMap<>();
        //filament type -> print name -> printer name -> can print

        // order of prints to print in the table
        // same as the table of truth
        List<String> printOrder = List.of(
                "Acoustic Guitar Cooky Cutter",
                "Stegosaurus Pickholder",
                "Collapsing Jian",
                "Earth Globe",
                "Moon Lamp",
                "Cathedral",
                "Fucktopus",
                "Lizard",
                "Tree Frog"
        );

        // file writer
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tests/print_results.md"))) {

            // for each spool
            for (Spool spool : spools) {
                String filamentType = spool.getFilamentType().toString();
                results.putIfAbsent(filamentType, new HashMap<>());

                // for each print
                for (String printName : printOrder) {
                    Print print = prints.stream().filter(p -> p.getName().equals(printName)).findFirst().orElse(null);
                    if (print == null) continue;

                    results.get(filamentType).putIfAbsent(print.getName(), new HashMap<>());

                    // for each printer
                    for (Printer printer : printers) {
                        // check if the printer can print the print with the spool and filament type
                        boolean canPrint = printer.canPrinterPrint(printer, spool.getFilamentType(), print);

                        // store the result in the map for the table
                        // filament type -> print name -> printer name -> can print
                        results.get(filamentType).get(print.getName()).put(printer.getName(), canPrint);
                    }
                }
            }

            // tables for each filament type
            for (String filamentType : results.keySet()) {
                // table header for the filament type
                writer.write("| " + filamentType + " | " + String.join(" | ", printers.stream().map(Printer::getName).toList()) + " |\n");
                writer.write("|-----|------------|----------|-----------|---------------|--------|-----------|-------|\n");

                // for each print
                for (String printName : printOrder) {
                    Print print = prints.stream().filter(p -> p.getName().equals(printName)).findFirst().orElse(null);
                    if (print == null) continue;

                    writer.write("| " + printName + " | ");
                    // for each printer
                    for (String printerName : printers.stream().map(Printer::getName).toList()) {
                        boolean canPrint = results.get(filamentType).get(print.getName()).getOrDefault(printerName, false);
                        writer.write((canPrint ? "V" : "X") + " | ");
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }
        }
    }
}