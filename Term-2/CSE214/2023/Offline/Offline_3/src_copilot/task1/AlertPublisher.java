package task1;

/**
 * Subject interface representing the alert management system.
 */
public interface AlertPublisher {
    /**
     * Registers a new citizen into the system.
     */
    void registerCitizen(CitizenObserver citizen);

    /**
     * Unregisters a citizen completely from the system.
     */
    void unregisterCitizen(CitizenObserver citizen);

    /**
     * Subscribes a citizen to a specific disaster category.
     */
    void subscribe(CitizenObserver citizen, DisasterCategory category);

    /**
     * Unsubscribes a citizen from a specific disaster category.
     */
    void unsubscribe(CitizenObserver citizen, DisasterCategory category);

    /**
     * Publishes a disaster alert and notifies all subscribed citizens.
     */
    void publishAlert(Alert alert);
}
