package task2;

/**
 * Behavioural pattern used: Mediator. Coordinator is the ConcreteMediator;
 * DeptOffice, ControllerOfExams and DSW are Colleagues that never call each
 * other directly -- every request is routed through the Coordinator, which
 * enforces both the required step sequence and which office is allowed to
 * trigger which step.
 */
public class SystemTest {
    public static void main(String[] args) {

        // ---------- Setup & Registration ----------
        Coordinator coordinator = new Coordinator();

        DeptOffice dept = new DeptOffice(coordinator, "Department Office");
        ControllerOfExams coe = new ControllerOfExams(coordinator, "Controller of Examinations");
        DSW dsw = new DSW(coordinator, "DSW");

        coordinator.registerOffice(dept);
        coordinator.registerOffice(coe);
        coordinator.registerOffice(dsw);

        Student mehedi = new Student("Mehedi");
        coordinator.registerStudent(mehedi);

        System.out.println("\n===================== OFFICIAL DEMONSTRATION SEQUENCE (Mehedi) =====================");

        // 1. An attempt to publish a result before departmental confirmation.
        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, mehedi);

        // 2. Submission of departmental confirmation.
        dept.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, mehedi);

        // 3. An early attempt to issue the certificate or transcript.
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, mehedi);

        // 4. Issuance of the final-result office order.
        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, mehedi);

        // 5. Issuance of the testimonial.
        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, mehedi);

        // 6. Issuance of the certificate and transcript.
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, mehedi);

        // 7. Student notifications and the final status.
        coordinator.displayStatus(mehedi);

        System.out.println("\n===================== ADDITIONAL SEQUENCE-VIOLATION COVERAGE (Nadia) =====================");
        // Mehedi's run above only ever hit two of the four "out of sequence"
        // rejections (office order without confirmation, certificate without
        // office order). This run exercises the remaining ones: testimonial
        // requested too early, and certificate requested before the
        // testimonial exists.
        Student nadia = new Student("Nadia");
        coordinator.registerStudent(nadia);

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, nadia);   // reject: no departmental confirmation
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, nadia);   // reject: no departmental confirmation

        dept.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, nadia);

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, nadia);   // reject: no office order yet

        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, nadia);

        coe.attemptEvent(Event.ISSUE_CERTIFICATE, nadia);   // reject: no testimonial yet

        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, nadia);
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, nadia);

        coordinator.displayStatus(nadia);

        System.out.println("\n===================== ROLE ENFORCEMENT: NO OFFICE MAY ACT OUTSIDE ITS ROLE (Rakib) =====================");
        Student rakib = new Student("Rakib");
        coordinator.registerStudent(rakib);

        // Each office attempts a step that belongs to a different office.
        dsw.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, rakib);  // only Dept Office may do this
        dept.attemptEvent(Event.ISSUE_OFFICE_ORDER, rakib);           // only the Controller may do this
        coe.attemptEvent(Event.ISSUE_TESTIMONIAL, rakib);             // only DSW may do this

        // An office that was never registered with the coordinator cannot act at all.
        ControllerOfExams impostor = new ControllerOfExams(coordinator, "Unregistered COE");
        impostor.attemptEvent(Event.ISSUE_OFFICE_ORDER, rakib);

        System.out.println("\nRakib's status must be completely untouched by the rejected attempts above:");
        coordinator.displayStatus(rakib);
    }
}
