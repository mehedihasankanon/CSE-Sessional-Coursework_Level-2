package task2;

import java.util.HashMap;
import java.util.Map;

public class ResultProcessingCoordinator implements ResultMediator {

    private static final class StudentRecord {
        final Student student;
        StudentProcessingState state;

        StudentRecord(Student student) {
            this.student = student;
            this.state = StudentProcessingState.REGISTERED;
        }
    }

    private DepartmentOffice departmentOffice;
    private ControllerOffice controllerOffice;
    private DSWOffice dswOffice;
    private final Map<String, StudentRecord> studentRecords;

    public ResultProcessingCoordinator() {
        this.studentRecords = new HashMap<>();
    }

    @Override
    public void registerDepartmentOffice(DepartmentOffice office) {
        this.departmentOffice = office;
    }

    @Override
    public void registerControllerOffice(ControllerOffice office) {
        this.controllerOffice = office;
    }

    @Override
    public void registerDSWOffice(DSWOffice office) {
        this.dswOffice = office;
    }

    @Override
    public void registerStudent(Student student) {
        studentRecords.put(student.getStudentId(), new StudentRecord(student));
        System.out.println("Registered student: " + student.getName() + " (" + student.getStudentId() + ")");
    }

    @Override
    public void submitDepartmentConfirmation(String studentId) {
        StudentRecord record = getRecord(studentId);
        record.state = StudentProcessingState.DEPARTMENT_CONFIRMED;
        System.out.println("  [Coordinator] Departmental confirmation recorded for " + studentId);
        record.student.receiveNotification("Your departmental requirements have been confirmed.");
    }

    @Override
    public void issueOfficeOrder(String studentId) {
        StudentRecord record = getRecord(studentId);
        if (record.state != StudentProcessingState.DEPARTMENT_CONFIRMED) {
            reject(studentId, "office order", "departmental confirmation has not been submitted yet");
            return;
        }
        record.state = StudentProcessingState.OFFICE_ORDER_ISSUED;
        System.out.println("  [Coordinator] Final-result office order issued for " + studentId);
        record.student.receiveNotification("Final-result office order has been issued.");
    }

    @Override
    public void issueTestimonial(String studentId) {
        StudentRecord record = getRecord(studentId);
        if (record.state != StudentProcessingState.OFFICE_ORDER_ISSUED) {
            reject(studentId, "testimonial", "the office order has not been issued yet");
            return;
        }
        record.state = StudentProcessingState.TESTIMONIAL_ISSUED;
        System.out.println("  [Coordinator] Testimonial issued for " + studentId);
        record.student.receiveNotification("Testimonial has been issued.");
    }

    @Override
    public void issueCertificateAndTranscript(String studentId) {
        StudentRecord record = getRecord(studentId);
        if (record.state != StudentProcessingState.TESTIMONIAL_ISSUED) {
            reject(studentId, "certificate/transcript", "the testimonial has not been issued yet");
            return;
        }
        record.state = StudentProcessingState.CERTIFICATE_TRANSCRIPT_ISSUED;
        System.out.println("  [Coordinator] Certificate and transcript issued for " + studentId);
        record.student.receiveNotification("Certificate and academic transcript have been issued.");
    }

    @Override
    public StudentProcessingState getStatus(String studentId) {
        return getRecord(studentId).state;
    }

    private void reject(String studentId, String step, String reason) {
        System.out.println("  [Coordinator] REJECTED request to issue " + step + " for " + studentId + ": " + reason);
    }

    private StudentRecord getRecord(String studentId) {
        StudentRecord record = studentRecords.get(studentId);
        if (record == null) {
            throw new IllegalArgumentException("Student not registered with coordinator: " + studentId);
        }
        return record;
    }
}
