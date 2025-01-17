package nl.saxion.Models.records;

import nl.saxion.utils.FilamentType;

public record SpoolBP(int id,String color, FilamentType filamentType, double length) {

    @Override
    public String toString() {
        return "-----------------------------------" + System.lineSeparator() +
                "- Color: " + color + System.lineSeparator() +
                "- FilamentType: " + filamentType + System.lineSeparator() +
                "- Length: " + length;
    }
}
