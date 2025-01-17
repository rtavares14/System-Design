package nl.saxion.Models.records;

public record PrinterBP(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean housed, int maxColors) {

    @Override
    public String toString() {
        return "-----------------------------------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + printerName + System.lineSeparator() +
                "- Model: " + model + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator() +
                "- Max Dimensions: (" + maxX + " x " + maxY + " x " + maxZ + ")" + System.lineSeparator() +
                "- Housed: " + housed + System.lineSeparator() +
                "- Type: " + this.getClass().getSimpleName();
    }
}
