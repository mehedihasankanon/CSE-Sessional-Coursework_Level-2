package task2;

public class DSWOffice extends Office {

    public DSWOffice(ResultMediator mediator) {
        super(mediator, "Directorate of Students' Welfare");
    }

    public void issueTestimonial(String studentId) {
        System.out.println("[" + officeName + "] Attempting to issue testimonial for " + studentId);
        mediator.issueTestimonial(studentId);
    }
}
