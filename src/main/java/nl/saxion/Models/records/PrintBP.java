package nl.saxion.Models.records;

import java.util.ArrayList;

public record PrintBP(String name, int height, int width, double length, ArrayList<Double>filamentLength, int printTime) {

    @Override
    public String toString() {
        return "-----------------------------------" + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Height: " + height + System.lineSeparator() +
                "- Width: " + width + System.lineSeparator() +
                "- Length: " + length + System.lineSeparator() +
                "- FilamentLength: " + filamentLength + System.lineSeparator() +
                "- Print Time: " + printTime;
    }
}
