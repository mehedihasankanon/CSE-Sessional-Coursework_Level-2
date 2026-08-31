/*

You are building a small hospital visit simulator. Every patient visit must follow the same
overall flow, and the system should print each step in order. The hospital has different
departments, and each department follows the same visit structure, but customizes a few
steps.
1. Check-In (register patient name + assign visit ID)
2. Record Vitals (print temperature + blood pressure)
3. Assessment
4. Treatment
5. Discharge Summary (print “patient discharged” + notes)
General Department
Assessment: “Doctor performs normal diagnosis”
Treatment: “Prescribe standard medicine”
Pediatrics Department
Assessment: “Doctor checks symptoms by ensuring child comfort level”
Treatment: “Give child-safe medicine, friendly reassurance message”
Emergency Department
Assessment: “Quick triage check (urgent/non-urgent)”
Treatment: “Immediate emergency procedure”
Task: Implement the treatment system with appropriate design pattern and run the
simulation for 1 patient in each department, showing the printed flow clearly

*/

// -> template pattern

/*

How do we hide `protected` methods from external callers?

You have stumbled upon a famous quirk of the Java language. In C++, `protected` means *strictly* subclasses. In Java, `protected` means subclasses **AND** any other class in the exact same package.

Because of this, if your `main` method is in the same package as your `Template` class, you can technically write `new GeneralDept().assess(p);` and Java will allow it.

**How to fix it:**
You cannot use `private` because subclasses must override them. The industry standard way to fix this in Java is through **Package Encapsulation**.

1. You put `Template`, `GeneralDept`, `PediatricsDept`, and `EmergencyDept` into a dedicated package (e.g., `package hospital.visit;`).
2. You leave `visit()` as `public`.
3. You leave `assess()` and `treat()` as `protected`.
4. Your `Main` execution class lives in a different package. Because it is in a different package, `main` can only see the `public visit()` method, and the compiler will completely block it from calling `assess()` directly.

Architecturally, using `protected` is exactly what the Gang of Four book prescribes for the Template Method. You did it correctly.

*/


// class Patient {

//     private String name;
//     private int id;

    

//     public Patient(String name, int id) {
//         this.name = name;
//         this.id = id;
//     }
//     public String getName() {
//         return name;
//     }
//     public int getId() {
//         return id;
//     }

    
// }

// abstract class Template {

//     public final void visit(Patient p) {
//         checkIn(p);
//         recordVitals(p);
//         assess(p);
//         treat(p);
//         dischargeSummary(p);
//     }
    
//     private void checkIn(Patient p) {
//         System.out.println("Patient Checked in: " + p);

//     }

//     private void recordVitals(Patient p) {
//         System.out.println("Temp: 39 C, BP: 120/80");
//     }

//     abstract void assess(Patient p);
//     abstract void treat(Patient p);

//     private void dischargeSummary(Patient p) {
//         System.out.println("Patient Discharged: " + p);
//     }

// }

// class GeneralDept extends Template {

//     protected void assess(Patient p) {
//         System.out.println("Normal Diagnosis" );
//     }

//     protected void treat(Patient p) {
//         System.out.println("Standard Meds");
//     }

// }

// class PediatricsDept extends Template {

//     protected void assess(Patient p) {
//         System.out.println("Symptoms checked by ensuring child comfort level" );
//     }

//     protected void treat(Patient p) {
//         System.out.println("CHild safe meds, friendly assurance");
//     }

// }

// class EmergencyDept extends Template {

//     protected void assess(Patient p) {
//         System.out.println("Triage check: urgent/non-urgent" );
//     }

//     protected void treat(Patient p) {
//         System.out.println("Immdiate emergency procedure");
//     }

// }



// public class C1 {
//     public static void main(String[] args) {
        
//         Patient p = new Patient("Messi", 1);

//         new GeneralDept().visit(p);
//         new PediatricsDept().visit(p);;
//         new EmergencyDept().visit(p);


//     }
// }


// ==================================================================================

// Ai Improved Implementation

import java.util.Random;

class Patient {
    private String name;
    private int visitId; // Removed from constructor, wait for hospital to assign it

    public Patient(String name) {
        this.name = name;
    }

    public void setVisitId(int id) {
        this.visitId = id;
    }

    // This guarantees beautiful print logs instead of memory addresses
    @Override
    public String toString() {
        return name + " (Visit ID: #" + visitId + ")";
    }
}

abstract class HospitalVisitTemplate {

    // The Template Method: final so subclasses cannot break the sequence
    public final void visit(Patient p) {
        checkIn(p);
        recordVitals(p);
        assess(p);
        treat(p);
        dischargeSummary(p);
        System.out.println("-------------------------------------------------");
    }
    
    private void checkIn(Patient p) {
        // Assign the visit ID inside the check-in step, as requested by prompt
        int generatedId = new Random().nextInt(1000, 9999);
        p.setVisitId(generatedId);
        System.out.println("1. Check-In: Registered patient " + p);
    }

    private void recordVitals(Patient p) {
        System.out.println("2. Record Vitals: Temp 37.0 C, BP 120/80");
    }

    // Hooks for subclasses
    protected abstract void assess(Patient p);
    protected abstract void treat(Patient p);

    private void dischargeSummary(Patient p) {
        System.out.println("5. Discharge Summary: Patient discharged. Notes added to file.");
    }
}

class GeneralDept extends HospitalVisitTemplate {
    @Override
    protected void assess(Patient p) {
        System.out.println("3. Assessment: Doctor performs normal diagnosis.");
    }
    @Override
    protected void treat(Patient p) {
        System.out.println("4. Treatment: Prescribe standard medicine.");
    }
}

class PediatricsDept extends HospitalVisitTemplate {
    @Override
    protected void assess(Patient p) {
        System.out.println("3. Assessment: Doctor checks symptoms by ensuring child comfort level.");
    }
    @Override
    protected void treat(Patient p) {
        System.out.println("4. Treatment: Give child-safe medicine, friendly reassurance message.");
    }
}

class EmergencyDept extends HospitalVisitTemplate {
    @Override
    protected void assess(Patient p) {
        System.out.println("3. Assessment: Quick triage check (urgent/non-urgent).");
    }
    @Override
    protected void treat(Patient p) {
        System.out.println("4. Treatment: Immediate emergency procedure.");
    }
}

public class C1Template {
    public static void main(String[] args) {
        // Notice we only pass the name now
        Patient p = new Patient("Lionel Messi");

        System.out.println("=== General Department Visit ===");
        new GeneralDept().visit(p);

        System.out.println("=== Pediatrics Department Visit ===");
        new PediatricsDept().visit(p);

        System.out.println("=== Emergency Department Visit ===");
        new EmergencyDept().visit(p);
    }
}