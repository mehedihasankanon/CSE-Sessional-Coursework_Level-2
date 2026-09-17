package task2;

import java.util.HashMap;
import java.util.Map;

/**
 * Mediator: Coordinates communication between offices and students for final result publication.
 */
public class ResultProcessingCoordinator {
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, StudentProcessingState> states = new HashMap<>();

    public void registerStudent(Student student) {
        students.put(student.getStudentId(), student);
        states.put(student.getStudentId(), StudentProcessingState.NOT_STARTED);
        System.out.println("[Coordinator] Registered student: " + student.getName() + " (" + student.getStudentId() + ")");
    }

    public StudentProcessingState getState(String studentId) {
        return states.getOrDefault(studentId, StudentProcessingState.NOT_STARTED);
    }

    public boolean requestDepartmentalConfirmation(String studentId) {
        StudentProcessingState current = getState(studentId);
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("[Coordinator] Unknown student ID: " + studentId + ". Cannot confirm.");
            return false;
        }

        if (current != StudentProcessingState.NOT_STARTED) {
            System.out.println("[Coordinator] Departmental confirmation already processed or sequence violated for " + studentId);
            return false;
        }

        states.put(studentId, StudentProcessingState.DEPARTMENT_CONFIRMED);
        String msg = "Department has confirmed academic completion.";
        student.receiveNotification(msg);
        System.out.println("[Coordinator] Departmental confirmation accepted for " + student.getName());
        return true;
    }

    public boolean requestOfficeOrder(String studentId) {
        StudentProcessingState current = getState(studentId);
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("[Coordinator] Unknown student ID: " + studentId + ". Cannot issue office order.");
            return false;
        }

        if (current != StudentProcessingState.DEPARTMENT_CONFIRMED) {
            System.out.println("[Coordinator] Cannot issue office order. Departmental confirmation is required first for " + student.getName());
            return false;
        }

        states.put(studentId, StudentProcessingState.OFFICE_ORDER_ISSUED);
        String msg = "Final-result office order has been issued by the Controller of Examinations.";
        student.receiveNotification(msg);
        System.out.println("[Coordinator] Office order issued for " + student.getName());
        return true;
    }

    public boolean requestTestimonial(String studentId) {
        StudentProcessingState current = getState(studentId);
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("[Coordinator] Unknown student ID: " + studentId + ". Cannot issue testimonial.");
            return false;
        }

        if (current != StudentProcessingState.OFFICE_ORDER_ISSUED) {
            System.out.println("[Coordinator] Cannot issue testimonial. Office order must be issued first for " + student.getName());
            return false;
        }

        states.put(studentId, StudentProcessingState.TESTIMONIAL_ISSUED);
        String msg = "DSW has issued the testimonial.";
        student.receiveNotification(msg);
        System.out.println("[Coordinator] Testimonial issued for " + student.getName());
        return true;
    }

    public boolean requestCertificateAndTranscript(String studentId) {
        StudentProcessingState current = getState(studentId);
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("[Coordinator] Unknown student ID: " + studentId + ". Cannot issue certificate & transcript.");
            return false;
        }

        if (current != StudentProcessingState.TESTIMONIAL_ISSUED) {
            System.out.println("[Coordinator] Cannot issue certificate & transcript. All prior steps must be completed for " + student.getName());
            return false;
        }

        states.put(studentId, StudentProcessingState.COMPLETED);
        String msg = "Certificate and academic transcript have been issued. Process completed.";
        student.receiveNotification(msg);
        System.out.println("[Coordinator] Certificate & Transcript issued for " + student.getName());
        return true;
    }

    public void displayStudentStatus(String studentId) {
        StudentProcessingState state = getState(studentId);
        Student student = students.get(studentId);
        System.out.println("\n[Coordinator] Current processing status for " + (student != null ? student.getName() : studentId) + ": " + state.getDescription());
    }
}
