package task2;

public abstract class Office {
    protected final ResultMediator mediator;
    protected final String officeName;

    protected Office(ResultMediator mediator, String officeName) {
        this.mediator = mediator;
        this.officeName = officeName;
    }

    public String getOfficeName() {
        return officeName;
    }
}
