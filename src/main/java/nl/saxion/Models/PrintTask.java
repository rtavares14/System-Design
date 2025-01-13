package nl.saxion.Models;

import nl.saxion.utils.FilamentType;

import java.util.List;
import java.util.Map;

public class PrintTask {
    private Print print;
    private Map<String,Double> colors;
    private FilamentType filamentType;


    public PrintTask(Print print, Map<String,Double> colors, FilamentType filamentType){
        this.print = print;
        this.colors = colors;
        this.filamentType = filamentType;

    }

    public Map<String,Double> getColors() {
        return colors;
    }

    public FilamentType getFilamentType() {
        return filamentType;
    }

    public Print getPrint(){
        return print;
    }

    @Override
    public String toString() {
        return "< " + print.getName() +" " + filamentType + " " + colors.toString() + " >";
    }
}
