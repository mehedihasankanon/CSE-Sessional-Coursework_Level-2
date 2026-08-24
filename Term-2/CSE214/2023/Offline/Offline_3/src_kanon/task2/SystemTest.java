package task2;

public class SystemTest {
    public static void main(String[] args) {

        Coordinator coordinator = new Coordinator();

        DeptOffice dept = new DeptOffice(coordinator, "Department Office");
        ControllerOfExams coe = new ControllerOfExams(coordinator, "Controller of Examinations");
        DSW dsw = new DSW(coordinator, "DSW");

        coordinator.registerOffice(dept);
        coordinator.registerOffice(coe);
        coordinator.registerOffice(dsw);

        Student mehedi = new Student("Mehedi");
        coordinator.registerStudent(mehedi);

        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, mehedi);

        dept.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, mehedi);

        coe.attemptEvent(Event.ISSUE_CERTIFICATE, mehedi);

        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, mehedi);

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, mehedi);

        coe.attemptEvent(Event.ISSUE_CERTIFICATE, mehedi);

        coordinator.displayStatus(mehedi);

        Student raihan = new Student("Raihan");
        coordinator.registerStudent(raihan);

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, raihan);
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, raihan);

        dept.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, raihan);

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, raihan);

        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, raihan);

        coe.attemptEvent(Event.ISSUE_CERTIFICATE, raihan);

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, raihan);

        coe.attemptEvent(Event.ISSUE_CERTIFICATE, raihan);

        coordinator.displayStatus(raihan);

        Student rakib = new Student("Rakib");
        coordinator.registerStudent(rakib);

        dsw.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, rakib);
        dept.attemptEvent(Event.ISSUE_OFFICE_ORDER, rakib);
        coe.attemptEvent(Event.ISSUE_TESTIMONIAL, rakib);

        ControllerOfExams impostor = new ControllerOfExams(coordinator, "Unregistered COE");
        impostor.attemptEvent(Event.ISSUE_OFFICE_ORDER, rakib);

        System.out.println("\nRakib's status must be completely untouched by the rejected attempts above:");
        coordinator.displayStatus(rakib);
    }
}
