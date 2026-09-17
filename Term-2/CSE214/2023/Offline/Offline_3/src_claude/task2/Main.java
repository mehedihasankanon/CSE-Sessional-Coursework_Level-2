package task2;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TASK 2: BUET FINAL RESULT PUBLICATION SYSTEM ===\n");

        ResultProcessingCoordinator coordinator = new ResultProcessingCoordinator();

        DepartmentOffice departmentOffice = new DepartmentOffice(coordinator);
        ControllerOffice controllerOffice = new ControllerOffice(coordinator);
        DSWOffice dswOffice = new DSWOffice(coordinator);
        Student student = new Student("S001", "Fahim");

        coordinator.registerDepartmentOffice(departmentOffice);
        coordinator.registerControllerOffice(controllerOffice);
        coordinator.registerDSWOffice(dswOffice);
        coordinator.registerStudent(student);
        System.out.println();

        // 1. Attempt to publish a result before departmental confirmation.
        System.out.println("--- Step 1: Early office order attempt ---");
        controllerOffice.issueFinalResultOfficeOrder(student.getStudentId());

        // 2. Submission of departmental confirmation.
        System.out.println("\n--- Step 2: Departmental confirmation ---");
        departmentOffice.confirmAcademicRequirements(student.getStudentId());

        // 3. Early attempt to issue the certificate or transcript.
        System.out.println("\n--- Step 3: Early certificate/transcript attempt ---");
        controllerOffice.issueCertificateAndTranscript(student.getStudentId());

        // 4. Issuance of the final-result office order.
        System.out.println("\n--- Step 4: Office order issuance ---");
        controllerOffice.issueFinalResultOfficeOrder(student.getStudentId());

        // 5. Issuance of the testimonial.
        System.out.println("\n--- Step 5: Testimonial issuance ---");
        dswOffice.issueTestimonial(student.getStudentId());

        // 6. Issuance of the certificate and transcript.
        System.out.println("\n--- Step 6: Certificate/transcript issuance ---");
        controllerOffice.issueCertificateAndTranscript(student.getStudentId());

        // 7. Student notifications and the final status.
        System.out.println("\n--- Step 7: Final notifications and status ---");
        student.displayNotifications();
        System.out.println("Final processing status for " + student.getStudentId() + ": "
                + coordinator.getStatus(student.getStudentId()));
    }
}
