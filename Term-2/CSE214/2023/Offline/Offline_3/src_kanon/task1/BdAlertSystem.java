package task1;

import java.util.*;

public class BdAlertSystem {

    private Set<Subscriber> allUsers = new HashSet<>();
    private Map<Category, Set<Subscriber>> subscribers;
    private static BdAlertSystem instance;

    public void registerCitizen(Subscriber sub) {
        allUsers.add(sub);
    }

    public Set<Subscriber> getSubscribers() {
        return allUsers;
    }

    public static BdAlertSystem getInstance() {
        if (instance == null) {
            return instance = new BdAlertSystem();
        }
        return instance;
    }

    private BdAlertSystem() {
        subscribers = new HashMap<Category, Set<Subscriber>>();
    }

    public void addSubscriber(Subscriber sub, Category cat) {

        if (!allUsers.contains(sub)) {
            System.out.println("Error: Citizen " + sub.getName() + " must be registered first.");
            return; 
        }

        if (subscribers.get(cat) == null) {
            subscribers.put(cat, new HashSet<Subscriber>());
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
        Set<Subscriber> subList = subscribers.get(alert.getCat());
        if (subList == null) {
            return;
        }

        for (Subscriber sub : subList) {
            sub.notifySubscriber(alert);
        }
    }

}
