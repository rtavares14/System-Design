package nl.saxion.Models;


import nl.saxion.utils.FilamentType;

public class Print {
    private String name;
    private int height;
    private int width;
    private int length;
    private double filamentLength;
    private int printTime;
    private FilamentType filamentType;

    public Print(String name, int height, int width, int length, double filamentLength, int printTime, FilamentType filamentType) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength = filamentLength;
        this.printTime = printTime;
        this.filamentType = filamentType;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getFilamentLength() {
        return filamentLength;
    }

    public FilamentType getFilamentType() {
        return filamentType;
    }

    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Height: " + height + System.lineSeparator() +
                "- Width: " + width + System.lineSeparator() +
                "- Length: " + length + System.lineSeparator() +
                "- FilamentLength: " + filamentLength + System.lineSeparator() +
                "- Print Time: " + printTime + System.lineSeparator() +
                "--------";
    }

}