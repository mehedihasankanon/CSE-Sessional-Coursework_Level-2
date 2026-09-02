package task1;

/**
 * Observer interface representing a citizen who receives disaster alert notifications.
 */
public interface CitizenObserver {
    /**
     * Receives notification when a new alert is published.
     *
     * @param alert The disaster alert published.
     */
    void update(Alert alert);

    /**
     * Returns the unique name or identifier of the citizen.
     *
     * @return Citizen name.
     */
    String getName();

    /**
     * Displays all alerts received by this citizen.
     */
    void displayReceivedNotifications();
}
