package task2;

public abstract class Colleague extends Person {
    protected ResultMediator mediator;
    
    public Colleague(ResultMediator mediator, String name) {
        super(name);
        this.mediator = mediator;
    }

    public void attemptEvent(Event event, Student student) {
        System.out.println("\n>>> " + this.name + " is attempting: " + event + " for " + student.getName());
        mediator.notify(this, event, student);
    }
}