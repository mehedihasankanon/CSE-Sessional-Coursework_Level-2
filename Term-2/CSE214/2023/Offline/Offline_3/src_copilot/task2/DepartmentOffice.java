package task2;

/**
 * Concrete Colleague: Department Office.
 * Responsible for submitting confirmation of academic completion.
 */
public class DepartmentOffice extends Office {
    public DepartmentOffice(ResultProcessingCoordinator coordinator) {
        super(coordinator, "Department Office");
    }

    public boolean confirmAcademicCompletion(String studentId) {
        System.out.println("\n[" + officeName + "] Submitting departmental confirmation for Student ID: " + studentId);
        return coordinator.requestDepartmentalConfirmation(studentId);
    }
}
