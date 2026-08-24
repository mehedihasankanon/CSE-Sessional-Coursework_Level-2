package task2;

public class Notification {
    
    private final Event event;
    private final Student student;
    private final String message;

    public Notification(Event event, Student student, String message) {
        this.event = event;
        this.student = student;
        this.message = message;
    }

    public Event getEvent() {
        return event;
    }

    public Student getStudent() {
        return student;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[ " + event + " ] " + student.getName() + ": " + message;
    }
}
