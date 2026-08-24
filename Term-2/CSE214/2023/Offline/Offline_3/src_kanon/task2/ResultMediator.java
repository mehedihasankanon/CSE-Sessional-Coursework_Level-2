package task2;

public interface ResultMediator {
    void notify(Colleague col, Event event, Student student);
    void registerStudent(Student student);
    void displayStatus(Student student);
}