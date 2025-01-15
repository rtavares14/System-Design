package nl.saxion.Models.observer;

public interface PrintTaskObserver {
    void update(String event, int spoolsChanged);
}
