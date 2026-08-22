package task2;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student undergoing final result publication.
 */
public class Student {
    private final String studentId;
    private final String name;
    private final String department;
    private final List<String> notifications;

    public Student(String studentId, String name, String department) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.notifications = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("  [STUDENT NOTIFICATION -> " + name + " (" + studentId + ")]: " + message);
    }

    public void displayNotifications() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println(" Notifications for Student: " + name + " (ID: " + studentId + ", Dept: " + department + ")");
        if (notifications.isEmpty()) {
            System.out.println("  (No notifications received)");
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + notifications.get(i));
            }
        }
        System.out.println("-------------------------------------------------------");
    }
}
