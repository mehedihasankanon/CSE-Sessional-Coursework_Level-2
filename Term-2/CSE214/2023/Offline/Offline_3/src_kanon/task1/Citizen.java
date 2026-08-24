package task1;

import java.util.*;

public class Citizen implements Subscriber {
    private String name;

    private List<Alert> alerts;

    public Citizen(String name) {
        this.name = name;
        alerts = new ArrayList<Alert>();
    }

    public String getName() {
        return name;
    }

    @Override
    public void notifySubscriber(Alert alert) {
        alerts.add(alert);

        // System.out.println(alert.toString());
    }

    @Override
    public void displayReceivedAlerts() {
        System.out.println("\n[ Citizen " + getName() + " ] Alerts");
        for(Alert a : alerts) {
            System.out.println('\t' + a.toString());
        }
        System.out.println("");
    }

}
