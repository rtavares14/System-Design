package nl.saxion.observer;

public interface PrintTaskObserver {
    void update(String event, int spoolsChanged);
}
