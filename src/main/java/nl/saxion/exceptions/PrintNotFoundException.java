package nl.saxion.exceptions;

public class PrintNotFoundException extends RuntimeException{
    public PrintNotFoundException(String message) {
        super(message);
    }
}
