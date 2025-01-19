package nl.saxion.Models.printer;

import nl.saxion.Models.Print;
import nl.saxion.Models.Spool;
import nl.saxion.Models.printer.printerTypes.MultiColor;
import nl.saxion.utils.FilamentType;

import java.util.ArrayList;
import java.util.List;

public abstract class Printer {
    private final int id;
    private final String name;
    private final String model;
    private final String manufacturer;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private boolean housed;
    private final List<Spool> spools;
    private final int maxColors;

    public Printer(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean housed, int maxColors) {
        this.id = id;
        this.name = printerName;
        this.model = model;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.housed = housed;
        this.maxColors = maxColors;
        this.spools = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public String getModel() {
        return model;
    }

    public abstract List<Spool> getSpools();

    public abstract void setCurrentSpools(List<Spool> spools);

    /**
     * Will check if the printer can print the print
     */
    public boolean printFits(Print print) {
        return print.getHeight() <= maxZ && print.getWidth() <= maxX && print.getLength() <= maxY;
    }

    public boolean isHoused() {
        return housed;
    }


    public int getMaxColors() {
        return maxColors;
    }

    /**
     * Will check if the printer accepts the filament type
     * If the printer is housed it will accept all filament types
     * If the printer is not housed it will not accept ABS
     *
     * @param filamentType The filament type to check
     * @return True if the printer accepts the filament type
     */
    public boolean acceptsFilamentType(FilamentType filamentType) {
        if (this.isHoused()) {
            return true;
        } else {
            return filamentType != FilamentType.ABS;
        }
    }

    /**
     * Supper important method that was missing
     * This method will check if the print can be printed by the printer
     * It will check if the printer can print the print and if the printer accepts the filament type
     * By checking the instance of the printer we can check if the printer is a MultiColor printer
     *
     * @param printer printer to check
     * @param filamentType filament type to check
     * @param print print to check
     * @return true if the printer can print the print
     */
    public boolean canPrinterPrint(Printer printer, FilamentType filamentType, Print print) {
        boolean isMultiColor = printer instanceof MultiColor;
        boolean hasMultipleColors = print.getFilamentLength().size() > 1;

        if (hasMultipleColors && !isMultiColor) {
            return false; // only multiColor printers can handle multi-color prints.
        }

        return printer.printFits(print) && printer.acceptsFilamentType(filamentType);
    }
}