package task2;

/**
 * Demonstration of Task 2: BUET Final Result Publication System
 * Pattern used: Mediator Design Pattern
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   TASK 2: BUET FINAL RESULT PUBLICATION SYSTEM");
        System.out.println("   Design Pattern: Mediator Pattern");
        System.out.println("=======================================================\n");

        ResultProcessingCoordinator coordinator = new ResultProcessingCoordinator();

        // create offices (colleagues)
        DepartmentOffice dept = new DepartmentOffice(coordinator);
        ControllerOffice controller = new ControllerOffice(coordinator);
        DSWOffice dsw = new DSWOffice(coordinator);

        // create and register a student
        Student student = new Student("S-2023001", "Aisha Khan", "CSE");
        coordinator.registerStudent(student);

        System.out.println("\n--- Step 1: Attempt to publish result BEFORE departmental confirmation ---");
        controller.issueOfficeOrder(student.getStudentId());

        System.out.println("\n--- Step 2: Department submits confirmation ---");
        dept.confirmAcademicCompletion(student.getStudentId());

        System.out.println("\n--- Step 3: Early attempt to issue certificate/transcript (should be rejected) ---");
        controller.issueCertificateAndTranscript(student.getStudentId());

        System.out.println("\n--- Step 4: Controller issues final-result office order ---");
        controller.issueOfficeOrder(student.getStudentId());

        System.out.println("\n--- Step 5: DSW issues testimonial ---");
        dsw.issueTestimonial(student.getStudentId());

        System.out.println("\n--- Step 6: Controller issues certificate & transcript ---");
        controller.issueCertificateAndTranscript(student.getStudentId());

        System.out.println("\n--- Step 7: Student notifications and final status ---");
        student.displayNotifications();
        coordinator.displayStudentStatus(student.getStudentId());

        System.out.println("\n=======================================================");
        System.out.println("   TASK 2 DEMONSTRATION COMPLETED SUCCESSFULLY");
        System.out.println("=======================================================");
    }
}
