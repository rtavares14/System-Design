package nl.saxion.Models;

import nl.saxion.utils.FilamentType;

public class Spool {
    private final int id;
    private final String color;
    private final FilamentType filamentType;
    private double length;

    public Spool(int id, String color, FilamentType filamentType, double length) {
        this.color = color;
        this.filamentType = filamentType;
        this.length = length;
        this.id = id;
    }

    public double getLength() {
        return length;
    }

    public boolean spoolMatch(String color, FilamentType type) {
        return color.equals(this.color) && type == this.getFilamentType();
    }

    /**
     * This method will try to reduce the length of the spool.
     *
     * @param byLength
     * @return boolean which tells you if it is possible or not.
     */
    public void reduceLength(double byLength) {
        length = -byLength;
    }

    public String getColor() {
        return color;
    }

    public FilamentType getFilamentType() {
        return filamentType;
    }

    public int getId() {
        return id;
    }

}
