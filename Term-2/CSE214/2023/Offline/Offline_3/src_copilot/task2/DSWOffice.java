package task2;

/**
 * Concrete Colleague: Directorate of Students' Welfare (DSW).
 * Responsible for issuing testimonials.
 */
public class DSWOffice extends Office {
    public DSWOffice(ResultProcessingCoordinator coordinator) {
        super(coordinator, "Directorate of Students' Welfare (DSW)");
    }

    public boolean issueTestimonial(String studentId) {
        System.out.println("\n[" + officeName + "] Attempting to issue Testimonial for Student ID: " + studentId);
        return coordinator.requestTestimonial(studentId);
    }
}
