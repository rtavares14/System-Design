package nl.saxion.Models.records;

public record PrinterBP(int id, String printerName, String model, String manufacturer, int maxX, int maxY, int maxZ, boolean housed, int maxColors) {
}
