package task1;

import java.util.ArrayList;
import java.util.List;

public class Citizen implements CitizenObserver {
    private final String citizenId;
    private final String name;
    private final List<Alert> receivedAlerts;

    public Citizen(String citizenId, String name) {
        this.citizenId = citizenId;
        this.name = name;
        this.receivedAlerts = new ArrayList<>();
    }

    public String getCitizenId() {
        return citizenId;
    }

    @Override
    public String getObserverName() {
        return name;
    }

    @Override
    public void update(Alert alert) {
        receivedAlerts.add(alert);
        System.out.println("  -> Notified " + name + " (" + citizenId + "): " + alert);
    }

    public void displayNotifications() {
        System.out.println("Notifications for " + name + " (" + citizenId + "):");
        if (receivedAlerts.isEmpty()) {
            System.out.println("  (no notifications received)");
            return;
        }
        for (int i = 0; i < receivedAlerts.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + receivedAlerts.get(i));
        }
    }
}
