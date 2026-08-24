package task2;

public abstract class Colleague extends Person {

    protected ResultMediator mediator;

    public Colleague(ResultMediator mediator, String name) {
        super(name);

        this.mediator = mediator;

        // mediator.onboardColleague(this);
    }


}
