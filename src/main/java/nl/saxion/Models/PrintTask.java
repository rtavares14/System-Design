package nl.saxion.Models;

import java.util.List;
import java.util.UUID;

public class PrintTask {
    private Print print;
    private List<String> colors;
    private FilamentType filamentType;


    public PrintTask(Print print, List<String> colors, FilamentType filamentType){
        this.print = print;
        this.colors = colors;
        this.filamentType = filamentType;

    }

    public List<String> getColors() {
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
