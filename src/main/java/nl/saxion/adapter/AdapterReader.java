package nl.saxion.adapter;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.Printer;

import java.util.List;

public interface AdapterReader {
    // Whether the reader supports the file type
    boolean supportsFileType(String filename);

    // All the methods to read the different types of objects
    List<Spool> readSpools(String filePath);

    List<Printer> readPrinters(String filePath);

    List<Print> readPrints(String filePath);
}
