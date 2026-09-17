package task2;

public interface ResultMediator {
    void registerDepartmentOffice(DepartmentOffice office);

    void registerControllerOffice(ControllerOffice office);

    void registerDSWOffice(DSWOffice office);

    void registerStudent(Student student);

    void submitDepartmentConfirmation(String studentId);

    void issueOfficeOrder(String studentId);

    void issueTestimonial(String studentId);

    void issueCertificateAndTranscript(String studentId);

    StudentProcessingState getStatus(String studentId);
}
