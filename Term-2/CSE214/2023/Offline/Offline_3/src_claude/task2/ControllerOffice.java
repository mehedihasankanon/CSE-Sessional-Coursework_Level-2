package task2;

public class ControllerOffice extends Office {

    public ControllerOffice(ResultMediator mediator) {
        super(mediator, "Controller of Examinations");
    }

    public void issueFinalResultOfficeOrder(String studentId) {
        System.out.println("[" + officeName + "] Attempting to issue final-result office order for " + studentId);
        mediator.issueOfficeOrder(studentId);
    }

    public void issueCertificateAndTranscript(String studentId) {
        System.out.println("[" + officeName + "] Attempting to issue certificate and transcript for " + studentId);
        mediator.issueCertificateAndTranscript(studentId);
    }
}
