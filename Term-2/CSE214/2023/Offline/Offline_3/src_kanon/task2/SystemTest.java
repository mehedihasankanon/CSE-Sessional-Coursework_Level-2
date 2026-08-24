package task2;

public class SystemTest {
    public static void main(String[] args) {
        
        // Setup System
        Coordinator coordinator = new Coordinator();
        
        // Setup Offices (The Colleagues)
        DeptOffice dept = new DeptOffice(coordinator, "Department Office");
        ControllerOfExams coe = new ControllerOfExams(coordinator, "Controller of Examinations");
        DSW dsw = new DSW(coordinator, "DSW");
        
        // Setup Student
        Student student = new Student("Mehedi");
        coordinator.registerStudent(student);

        // 1. An attempt to publish a result before departmental confirmation.
        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, student);

        // 2. Submission of departmental confirmation.
        dept.attemptEvent(Event.SUBMIT_DEPT_REQ_CONFIRMATION, student);

        // 3. An early attempt to issue the certificate or transcript.
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, student);

        // 4. Issuance of the final-result office order.
        coe.attemptEvent(Event.ISSUE_OFFICE_ORDER, student);

        // 5. Issuance of the testimonial.
        dsw.attemptEvent(Event.ISSUE_TESTIMONIAL, student);

        // 6. Issuance of the certificate and transcript.
        coe.attemptEvent(Event.ISSUE_CERTIFICATE, student);

        // 7. Student notifications and the final status.
        coordinator.displayStatus(student);
    }
}