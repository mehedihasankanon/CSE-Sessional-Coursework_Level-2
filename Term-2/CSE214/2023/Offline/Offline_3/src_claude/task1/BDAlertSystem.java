package task1;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BDAlertSystem implements AlertPublisher {
    private final Map<DisasterCategory, List<CitizenObserver>> subscribersByCategory;
    private final List<Citizen> registeredCitizens;

    public BDAlertSystem() {
        this.subscribersByCategory = new EnumMap<>(DisasterCategory.class);
        for (DisasterCategory category : DisasterCategory.values()) {
            subscribersByCategory.put(category, new ArrayList<>());
        }
        this.registeredCitizens = new ArrayList<>();
    }

    public void registerCitizen(Citizen citizen) {
        registeredCitizens.add(citizen);
        System.out.println("Registered citizen: " + citizen.getObserverName() + " (" + citizen.getCitizenId() + ")");
    }

    @Override
    public void subscribe(CitizenObserver observer, DisasterCategory category) {
        List<CitizenObserver> subscribers = subscribersByCategory.get(category);
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
            System.out.println(observer.getObserverName() + " subscribed to " + category);
        }
    }

    @Override
    public void unsubscribe(CitizenObserver observer, DisasterCategory category) {
        List<CitizenObserver> subscribers = subscribersByCategory.get(category);
        if (subscribers.remove(observer)) {
            System.out.println(observer.getObserverName() + " unsubscribed from " + category);
        }
    }

    @Override
    public void publishAlert(Alert alert) {
        System.out.println("\nPublishing alert: " + alert);
        List<CitizenObserver> subscribers = subscribersByCategory.get(alert.getCategory());
        for (CitizenObserver observer : subscribers) {
            observer.update(alert);
        }
    }
}
