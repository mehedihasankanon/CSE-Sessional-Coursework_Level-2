package task1;

import java.util.*;

/**
 * Concrete Subject implementing the BD Alert disaster notification system.
 * Manages citizens and category subscriptions using the Observer Design Pattern.
 */
public class BDAlertSystem implements AlertPublisher {
    private final Set<CitizenObserver> registeredCitizens;
    private final Map<DisasterCategory, Set<CitizenObserver>> subscriptions;

    public BDAlertSystem() {
        this.registeredCitizens = new HashSet<>();
        this.subscriptions = new EnumMap<>(DisasterCategory.class);

        for (DisasterCategory category : DisasterCategory.values()) {
            subscriptions.put(category, new LinkedHashSet<>());
        }
    }

    @Override
    public void registerCitizen(CitizenObserver citizen) {
        if (citizen == null) return;
        if (registeredCitizens.add(citizen)) {
            System.out.println("[BD Alert System] Citizen '" + citizen.getName() + "' registered in the system.");
        } else {
            System.out.println("[BD Alert System] Citizen '" + citizen.getName() + "' is already registered.");
        }
    }

    @Override
    public void unregisterCitizen(CitizenObserver citizen) {
        if (citizen == null) return;
        if (registeredCitizens.remove(citizen)) {
            for (Set<CitizenObserver> subscribers : subscriptions.values()) {
                subscribers.remove(citizen);
            }
            System.out.println("[BD Alert System] Citizen '" + citizen.getName() + "' unregistered from the system.");
        }
    }

    @Override
    public void subscribe(CitizenObserver citizen, DisasterCategory category) {
        if (!registeredCitizens.contains(citizen)) {
            System.out.println("[BD Alert System] Registering citizen '" + citizen.getName() + "' before subscription.");
            registerCitizen(citizen);
        }

        Set<CitizenObserver> categorySubscribers = subscriptions.get(category);
        if (categorySubscribers.add(citizen)) {
            System.out.println("[BD Alert System] " + citizen.getName() + " subscribed to category: " + category);
        } else {
            System.out.println("[BD Alert System] " + citizen.getName() + " is already subscribed to category: " + category);
        }
    }

    @Override
    public void unsubscribe(CitizenObserver citizen, DisasterCategory category) {
        Set<CitizenObserver> categorySubscribers = subscriptions.get(category);
        if (categorySubscribers != null && categorySubscribers.remove(citizen)) {
            System.out.println("[BD Alert System] " + citizen.getName() + " unsubscribed from category: " + category);
        } else {
            System.out.println("[BD Alert System] " + citizen.getName() + " was not subscribed to category: " + category);
        }
    }

    @Override
    public void publishAlert(Alert alert) {
        System.out.println("\n=======================================================");
        System.out.println(" PUBLISHING NEW ALERT: " + alert.getTitle());
        System.out.println(" Category: " + alert.getCategory() + " | Location: " + alert.getLocation());
        System.out.println(" Severity: " + alert.getSeverity());
        System.out.println(" Instructions: " + alert.getSafetyInstructions());
        System.out.println("=======================================================");

        Set<CitizenObserver> subscribers = subscriptions.get(alert.getCategory());

        if (subscribers == null || subscribers.isEmpty()) {
            System.out.println("[BD Alert System] No subscribers found for category: " + alert.getCategory());
            return;
        }

        System.out.println("[BD Alert System] Notifying " + subscribers.size() + " subscriber(s)...");
        for (CitizenObserver citizen : subscribers) {
            citizen.update(alert);
        }
    }
}
