package nl.saxion.Models.observer;

public class Dashboard implements PrintTaskObserver {
    private int completedTasks = 0;
    private int failedTasks = 0;
    private int changedSpools = 0;

    @Override
    public void update(String event, int spoolsChanged) {
        switch (event) {
            case "completed":
                completedTasks++;
                break;
            case "failed":
                failedTasks++;
                break;
            case "changedSpool":
                changedSpools += spoolsChanged;
                break;
        }
    }

    public int[] showDashboard() {
        return new int[]{completedTasks, failedTasks, changedSpools};
    }
}