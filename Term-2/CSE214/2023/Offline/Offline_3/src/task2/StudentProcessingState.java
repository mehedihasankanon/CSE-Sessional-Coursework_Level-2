package task2;

/**
 * Processing states for a student's final result publication sequence.
 */
public enum StudentProcessingState {
    NOT_STARTED("1. Not Started - Awaiting Departmental Confirmation"),
    DEPARTMENT_CONFIRMED("2. Departmentally Confirmed - Awaiting Final Result Office Order"),
    OFFICE_ORDER_ISSUED("3. Office Order Issued - Awaiting Testimonial"),
    TESTIMONIAL_ISSUED("4. Testimonial Issued - Awaiting Certificate & Transcript"),
    COMPLETED("5. Completed - Certificate & Transcript Issued");

    private final String description;

    StudentProcessingState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
