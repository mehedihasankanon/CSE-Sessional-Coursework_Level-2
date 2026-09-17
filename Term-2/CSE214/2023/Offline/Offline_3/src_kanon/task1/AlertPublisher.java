package task1;

public interface AlertPublisher {
    
    void registerCitizen(Subscriber sub);
    void addSubscriber(Subscriber sub, Category cat);
    void removeSubscriber(Subscriber sub, Category cat);
    void sendAlert(Alert alert);
}
