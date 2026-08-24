package task2;

import java.util.*;

public class Coordinator implements ResultMediator {

    private class ClearanceState {
        boolean deptReqConfirmed = false;
        boolean officeOrderIssued = false;
        boolean testimonailIssued = false;
        boolean certificatesIssued = false;
    }

    private Map<Student, ClearanceState> studentStates;
    private DeptOffice deptOffice;
    private ControllerOfExams controllerOfExams;
    private DSW dsw;

    public Coordinator() {
        studentStates = new HashMap<Student, ClearanceState>();

        // these are to be instantiated in the main class
        // deptOffice = new DeptOffice(this, "Dept Office");
        // controllerOfExams = new ControllerOfExams(this, "Controller of Exams");
        // dsw = new DSW(this, "DSW");
    }

    private static class NotifHelper {

        private static String getMessage(Event state) {
            switch (state) {
                case SUBMIT_DEPT_REQ_CONFIRMATION:
                    return "Academic Requirements not confirmed. Contact your Dept.";

                case ISSUE_OFFICE_ORDER:
                    return "Office order not issued. Contact the Controller Office.";

                case ISSUE_TESTIMONIAL:
                    return "Testimionial not issued. Contact DSW.";

                case ISSUE_CERTIFICATE:
                    return "Certificate not issued. Contact Contorller office";

                default:
                    return "Some error happened. Debugging is required.";
            }
        }

    }

    @Override
    public void notify(Colleague col, Event event, Student student) {

        if (!studentStates.containsKey(student)) {
            System.out.println("Student " + student.getName() + " not registered yet. ");
            return;
        }

        ClearanceState state = studentStates.get(student);

        switch (event) {
            case SUBMIT_DEPT_REQ_CONFIRMATION:
                state.deptReqConfirmed = true;
                break;

            case ISSUE_OFFICE_ORDER:
                if (!state.deptReqConfirmed) {
                    student.receiveNotification(new Notification(event, student,
                            NotifHelper.getMessage(Event.SUBMIT_DEPT_REQ_CONFIRMATION)));
                } else {
                    state.officeOrderIssued = true;
                }
                break;

            case ISSUE_TESTIMONIAL:
                if (!state.deptReqConfirmed) {
                    student.receiveNotification(new Notification(event, student,
                            NotifHelper.getMessage(Event.SUBMIT_DEPT_REQ_CONFIRMATION)));
                } else {
                    if (!state.officeOrderIssued) {
                        student.receiveNotification(new Notification(event, student,
                                NotifHelper.getMessage(Event.ISSUE_OFFICE_ORDER)));
                    }
                    state.testimonailIssued = true;
                }
                break;

            case ISSUE_CERTIFICATE:
                if (!state.deptReqConfirmed) {
                    student.receiveNotification(new Notification(event, student,
                            NotifHelper.getMessage(Event.SUBMIT_DEPT_REQ_CONFIRMATION)));
                } else {
                    if (!state.officeOrderIssued) {
                        student.receiveNotification(new Notification(event, student,
                                NotifHelper.getMessage(Event.ISSUE_OFFICE_ORDER)));
                    } else {
                        if (!state.testimonailIssued) {
                            student.receiveNotification(new Notification(event, student,
                                    NotifHelper.getMessage(Event.ISSUE_TESTIMONIAL)));
                        }
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onboardColleague(Colleague col) {

    }

    @Override
    public void registerStudent(Student student) {
        studentStates.put(student, new ClearanceState());
    }
}
