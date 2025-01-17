package nl.saxion.Models.records;

import nl.saxion.Models.Print;
import nl.saxion.utils.FilamentType;

import java.util.List;

public record PrintTaskBP (Print print, List<String> colors, FilamentType filamentType) {

    @Override
    public String toString() {
        return "< " + print.getName() + " " + filamentType + " " + colors.toString() + " >";
    }
}
