package task2;

import java.util.ArrayList;
import java.util.List;

public final class Student {
    private final String studentId;
    private final String name;
    private final List<String> notifications;

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.notifications = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("  -> Notified " + name + " (" + studentId + "): " + message);
    }

    public void displayNotifications() {
        System.out.println("Notifications for " + name + " (" + studentId + "):");
        if (notifications.isEmpty()) {
            System.out.println("  (no notifications received)");
            return;
        }
        for (int i = 0; i < notifications.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + notifications.get(i));
        }
    }
}
