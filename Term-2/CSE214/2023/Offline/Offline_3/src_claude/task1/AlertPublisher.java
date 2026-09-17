package task1;

public interface AlertPublisher {
    void subscribe(CitizenObserver observer, DisasterCategory category);

    void unsubscribe(CitizenObserver observer, DisasterCategory category);

    void publishAlert(Alert alert);
}
