package nl.saxion.Models.interfaces;

public interface PrintTimeCalculator {

    private int calculatePrintTime(int length, int speed){
        return length/speed;
    }
}
