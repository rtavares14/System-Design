package nl.saxion.Models.interfaces;

public interface PrintTimeCalculator {
    /**
     * Calculates the print time based on the length and speed of the print.
     * @param length The length of the print (e.g., in millimeters).
     * @param speed The speed of the printer (e.g., in mm/s).
     * @return The calculated print time in seconds.
     */
    default int calculatePrintTime(int length, int speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be greater than 0");
        }
        return length / speed;
    }
}
