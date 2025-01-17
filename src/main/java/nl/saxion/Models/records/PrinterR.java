package nl.saxion.Models.records;

public record PrinterR(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean housed, int maxColors) {
}
