package task2;

/**
 * Abstract Colleague class representing an administrative office in BUET.
 * All offices communicate strictly through the ResultProcessingCoordinator.
 */
public abstract class Office {
    protected ResultProcessingCoordinator coordinator;
    protected String officeName;

    public Office(ResultProcessingCoordinator coordinator, String officeName) {
        this.coordinator = coordinator;
        this.officeName = officeName;
    }

    public String getOfficeName() {
        return officeName;
    }
}
