package task1;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Observer representing an individual citizen.
 */
public class Citizen implements CitizenObserver {
    private final String name;
    private final String nid;
    private final List<Alert> receivedNotifications;

    public Citizen(String name, String nid) {
        this.name = name;
        this.nid = nid;
        this.receivedNotifications = new ArrayList<>();
    }

    @Override
    public void update(Alert alert) {
        receivedNotifications.add(alert);
        System.out.println("  [>>> NOTIFICATION RECEIVED BY " + name.toUpperCase() + " (NID: " + nid + ")]");
        System.out.println("      Alert: " + alert.getTitle() + " (" + alert.getCategory() + ")");
        System.out.println("      Location: " + alert.getLocation() + " | Severity: " + alert.getSeverity());
        System.out.println("      Safety Notice: " + alert.getSafetyInstructions());
    }

    @Override
    public String getName() {
        return name;
    }

    public String getNid() {
        return nid;
    }

    @Override
    public void displayReceivedNotifications() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println(" Notification Log for Citizen: " + name + " (NID: " + nid + ")");
        System.out.println(" Total Alerts Received: " + receivedNotifications.size());
        if (receivedNotifications.isEmpty()) {
            System.out.println("  (No alerts received)");
        } else {
            for (int i = 0; i < receivedNotifications.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + receivedNotifications.get(i));
            }
        }
        System.out.println("-------------------------------------------------------");
    }
}
