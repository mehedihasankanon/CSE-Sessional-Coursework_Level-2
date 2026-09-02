package task2;

public class Student extends Person {
    public Student(String name) {
        super(name);
    }
    
    public void receiveNotification(Notification notification) {
        System.out.println("[ " + this.name + " ] received: " + notification.getMessage());
    }
}