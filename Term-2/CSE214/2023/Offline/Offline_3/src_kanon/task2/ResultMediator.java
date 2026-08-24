package task2;

public interface ResultMediator {
    
    void notify(Colleague col, Event event, Student student);

    void onboardColleague(Colleague col);

    void registerStudent(Student student);
}
