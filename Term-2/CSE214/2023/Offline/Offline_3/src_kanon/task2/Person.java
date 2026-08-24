package task2;

public abstract class Person {

    protected String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void receiveNotification(Notification notification) {
        System.out.println("[ " + this.name + " ] received: " + notification);
    }
}
