package nl.saxion.Models.observer;

import nl.saxion.Models.printer.Printer;

public class Dashboard implements PrintTaskObserver {
    private int completedTasks = 0;
    private int failedTasks = 0;
    private int changedSpools = 0;


    @Override
    public void update(String event) {
        switch (event) {
            case "completed":
                completedTasks++;
                break;
            case "failed":
                failedTasks++;
                break;
            case "changedSpool":
                changedSpools++;
                break;
        }

    }

    public void showDashboard() {
        System.out.println("----------- Dashboard --------------");
        System.out.println("Completed tasks: " + completedTasks);
        System.out.println("Failed tasks: " + failedTasks);
        System.out.println("Changed spools: " + changedSpools);
        System.out.println("-----------------------------------");
    }

}
