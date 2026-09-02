package task2;

public class DepartmentOffice extends Office {

    public DepartmentOffice(ResultMediator mediator) {
        super(mediator, "Department Office");
    }

    public void confirmAcademicRequirements(String studentId) {
        System.out.println("[" + officeName + "] Confirming academic requirements for " + studentId);
        mediator.submitDepartmentConfirmation(studentId);
    }
}
