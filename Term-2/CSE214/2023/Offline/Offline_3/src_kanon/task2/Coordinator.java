package task2;
import java.util.*;

public class Coordinator implements ResultMediator {

    private class ClearanceState {
        boolean deptReqConfirmed = false;
        boolean officeOrderIssued = false;
        boolean testimonialIssued = false; // Fixed typo here
        boolean certificatesIssued = false;
    }

    private Map<Student, ClearanceState> studentStates;

    public Coordinator() {
        studentStates = new HashMap<>();
    }

    private static class NotifHelper {
        private static String getMessage(Event state) {
            switch (state) {
                case SUBMIT_DEPT_REQ_CONFIRMATION:
                    return "Academic Requirements not confirmed. Contact your Dept.";
                case ISSUE_OFFICE_ORDER:
                    return "Office order not issued. Contact the Controller Office.";
                case ISSUE_TESTIMONIAL:
                    return "Testimonial not issued. Contact DSW.";
                case ISSUE_CERTIFICATE:
                    return "Certificate not issued. Contact Controller office";
                default:
                    return "Some error happened. Debugging is required.";
            }
        }
    }

    @Override
    public void notify(Colleague col, Event event, Student student) {
        if (!studentStates.containsKey(student)) {
            System.out.println("Student " + student.getName() + " not registered yet.");
            return;
        }

        ClearanceState state = studentStates.get(student);

        switch (event) {
            case SUBMIT_DEPT_REQ_CONFIRMATION:

                state.deptReqConfirmed = true;
                student.receiveNotification(new Notification(event, student, "Departmental confirmation successful."));
                break;

            case ISSUE_OFFICE_ORDER:
                if (!state.deptReqConfirmed) {
                    student.receiveNotification(new Notification(event, student, "REJECTED - " + NotifHelper.getMessage(Event.SUBMIT_DEPT_REQ_CONFIRMATION)));
                    return; 
                }

                state.officeOrderIssued = true;
                student.receiveNotification(new Notification(event, student, "Final-result office order has been successfully issued."));
                break;

            case ISSUE_TESTIMONIAL:

                if (!state.deptReqConfirmed) {
                    student.receiveNotification(new Notification(event, student, "REJECTED - " + NotifHelper.getMessage(Event.SUBMIT_DEPT_REQ_CONFIRMATION)));
                    return;
                }
                if (!state.officeOrderIssued) {
                    student.receiveNotification(new Notification(event, student, "REJECTED - " + NotifHelper.getMessage(Event.ISSUE_OFFICE_ORDER)));
                    return;
                }
                
                state.testimonialIssued = true;
                student.receiveNotification(new Notification(event, student, "Testimonial successfully issued."));
                break;

            case ISSUE_CERTIFICATE:
                
                if (!state.deptReqConfirmed) {
                    student.receiveNotification(new Notification(event, student, "REJECTED - " + NotifHelper.getMessage(Event.SUBMIT_DEPT_REQ_CONFIRMATION)));
                    return;
                }
                if (!state.officeOrderIssued) {
                    student.receiveNotification(new Notification(event, student, "REJECTED - " + NotifHelper.getMessage(Event.ISSUE_OFFICE_ORDER)));
                    return;
                }
                if (!state.testimonialIssued) {
                    student.receiveNotification(new Notification(event, student, "REJECTED - " + NotifHelper.getMessage(Event.ISSUE_TESTIMONIAL)));
                    return;
                }
                
                state.certificatesIssued = true; // Fixed the missing state update here
                student.receiveNotification(new Notification(event, student, "Certificate and transcript successfully issued. CLEARANCE COMPLETE."));
                break;

            default:
                break;
        }
    }

    @Override
    public void registerStudent(Student student) {
        studentStates.put(student, new ClearanceState());
        System.out.println("Registered student [ " + student.getName() + " ]");
    }

    @Override
    public void displayStatus(Student student) {
        if (!studentStates.containsKey(student)) return;
        ClearanceState state = studentStates.get(student);
        
        System.out.println("\n" + student.getName() + " status:");
        System.out.println("\tDepartment Confirmed : " + (state.deptReqConfirmed ? "YES" : "NO"));
        System.out.println("\tOffice Order Issued  : " + (state.officeOrderIssued ? "YES" : "NO"));
        System.out.println("\tTestimonial Issued   : " + (state.testimonialIssued ? "YES" : "NO"));
        System.out.println("\tCertificate Issued   : " + (state.certificatesIssued ? "YES" : "NO"));
        System.out.println("\n");
    }
}