package task1;

import java.util.*;

public class BdAlertSystem {

    Map<Category, List<Subscriber>> subscribers;

    private static BdAlertSystem instance;

    public static BdAlertSystem getInstance() {
        if (instance == null) {
            return instance = new BdAlertSystem();
        }
        return instance;
    }

    private BdAlertSystem() {
        subscribers = new HashMap<Category, List<Subscriber>>();
    }

    public void addSubscriber(Subscriber sub, Category cat) {

        if (subscribers.get(cat) == null) {
            subscribers.put(cat, new ArrayList<Subscriber>());
        }
        subscribers.get(cat).add(sub);
    }

    public void removeSubscriber(Subscriber sub, Category cat) {

        if (subscribers.get(cat) == null) {
            return;
        }
        subscribers.get(cat).remove(sub);
    }

    public void sendAlert(Alert alert) {
        List<Subscriber> subList = subscribers.get(alert.getCat());
        if (subList == null) {
            return;
        }

        for (Subscriber sub : subList) {
            sub.notifySubscriber(alert);
        }
    }

}
