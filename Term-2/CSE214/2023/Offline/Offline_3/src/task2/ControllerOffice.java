package task2;

/**
 * Concrete Colleague: Office of the Controller of Examinations.
 * Responsible for issuing office orders and final certificates & transcripts.
 */
public class ControllerOffice extends Office {
    public ControllerOffice(ResultProcessingCoordinator coordinator) {
        super(coordinator, "Office of Controller of Examinations");
    }

    public boolean issueOfficeOrder(String studentId) {
        System.out.println("\n[" + officeName + "] Attempting to issue Office Order for Student ID: " + studentId);
        return coordinator.requestOfficeOrder(studentId);
    }

    public boolean issueCertificateAndTranscript(String studentId) {
        System.out.println("\n[" + officeName + "] Attempting to issue Certificate & Transcript for Student ID: " + studentId);
        return coordinator.requestCertificateAndTranscript(studentId);
    }
}
