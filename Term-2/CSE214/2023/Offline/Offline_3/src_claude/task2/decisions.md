# Task 2: BUET Final Result Publication System — Design Decisions

Source: `CSE-214_Offline-3_Formatted.pdf`, page 3–4 ("Task 2: BUET Final Result
Publication System").

## 1. Pattern choice: Mediator

**PDF anchor:** page 3, Background — "To avoid direct communication among all offices,
the system will use a central result-processing coordinator. Every request,
confirmation, and status update must pass through this coordinator. No office should
directly call or control another office." Page 4 reinforces this: "Identify the
behavioural design pattern that best captures this centralized communication process."

This is a direct statement of the Mediator pattern's intent: replace many-to-many
direct object references between colleagues (`DepartmentOffice` ↔ `ControllerOffice` ↔
`DSWOffice` ↔ `Student`) with a single mediator object that all colleagues talk to.
Concretely: `DepartmentOffice`, `ControllerOffice`, and `DSWOffice` hold a reference
only to the `ResultMediator` interface, never to each other, so a department office
object has no way to call the DSW office directly even by mistake — the "no office
should directly call or control another office" constraint is enforced by the class
graph itself, not just by convention.

## 2. Class diagram (ASCII)

```
                    <<interface>> ResultMediator
   -----------------------------------------------------------
    + registerDepartmentOffice(DepartmentOffice)
    + registerControllerOffice(ControllerOffice)
    + registerDSWOffice(DSWOffice)
    + registerStudent(Student)
    + submitDepartmentConfirmation(studentId)
    + issueOfficeOrder(studentId)
    + issueTestimonial(studentId)
    + issueCertificateAndTranscript(studentId)
    + getStatus(studentId) : StudentProcessingState
                          ^
                          |  implements
                          |
                ResultProcessingCoordinator
   -----------------------------------------------------------
    - departmentOffice : DepartmentOffice
    - controllerOffice : ControllerOffice
    - dswOffice : DSWOffice
    - studentRecords : Map<String, StudentRecord>
   -----------------------------------------------------------
    (implements all ResultMediator methods; enforces sequence,
     rejects out-of-order requests, notifies Student)

        ^ mediator             ^ mediator             ^ mediator
        | (held by)            | (held by)            | (held by)
        |                      |                      |
   DepartmentOffice      ControllerOffice          DSWOffice
  -----------------    ----------------------   -------------------
  (extends Office)      (extends Office)         (extends Office)
  + confirmAcademic-    + issueFinalResult-       + issueTestimonial(
    Requirements(id)      OfficeOrder(id)             id)
                         + issueCertificateAnd-
                           Transcript(id)

                     ^ (abstract base, all three above extend)
                     |
                   Office
   -----------------------------------------------------------
    # mediator : ResultMediator
    # officeName : String

                       Student  (colleague, notified — not an Office)
   -----------------------------------------------------------
    - studentId : String
    - name : String
    - notifications : List<String>
   -----------------------------------------------------------
    + receiveNotification(String)
    + displayNotifications()

              <<enum>> StudentProcessingState
   -----------------------------------------------------------
    REGISTERED -> DEPARTMENT_CONFIRMED -> OFFICE_ORDER_ISSUED
    -> TESTIMONIAL_ISSUED -> CERTIFICATE_TRANSCRIPT_ISSUED
```

Relationship summary: `Office` subclasses and `Student` never reference each other;
every cross-office interaction is a method call on `ResultMediator`, and
`ResultProcessingCoordinator` is the only class that knows the full required sequence
and pushes notifications out to `Student`.

## 3. Inheritance / interface decisions

- **`ResultMediator` (interface) implemented by `ResultProcessingCoordinator`.**
  PDF anchor: page 3, System Requirements bullet list (register offices/students,
  submit confirmation, reject out-of-order requests, issue order/testimonial/
  certificate, display status) — each bullet became one interface method. The
  interface exists (rather than colleagues holding a concrete
  `ResultProcessingCoordinator` reference) so that colleagues are coupled to the
  Mediator *role*, not a specific implementation — the standard Mediator structure, and
  it also means a test/mock mediator could be substituted for a colleague in isolation.

- **`Office` abstract base class, extended by `DepartmentOffice`, `ControllerOffice`,
  `DSWOffice`.**
  PDF anchor: page 3, Background — "The process involves the Department Office, the
  Office of the Controller of Examinations, the Directorate of Students' Welfare
  (DSW)..." — three distinct offices that are structurally identical Colleague
  participants (each only needs a mediator reference and a display name) but expose
  *different* action methods, since each office is only allowed to trigger the specific
  step(s) the PDF assigns it (req. sequence, page 3, steps 1–4). An abstract class
  (rather than an interface) was used here, unlike `CitizenObserver` in Task 1, because
  there is real shared *state* (`mediator`, `officeName`) and shared *construction
  logic* to factor out, not just a shared method signature — each subclass would
  otherwise duplicate an identical constructor and field.

- **`Student` does not extend `Office`.**
  PDF anchor: page 3, Background lists the student separately from the three offices
  ("...the Directorate of Students' Welfare (DSW), and the student"), and req. list item
  "notify the student" treats the student as a passive recipient, never an initiator of
  a processing step. Since `Student` never *calls* the mediator to advance the
  workflow (only offices do), giving it the `Office` base class would add an unused
  `mediator` field and imply a capability the PDF never grants it.

## 4. Data structure / type decisions

- **`StudentProcessingState` as an `enum` with a linear progression
  (`REGISTERED → DEPARTMENT_CONFIRMED → OFFICE_ORDER_ISSUED → TESTIMONIAL_ISSUED →
  CERTIFICATE_TRANSCRIPT_ISSUED`).**
  PDF anchor: page 3, "Required Processing Sequence" (steps 1–4, strictly ordered) and
  System Requirements bullet "Reject any request that violates the required sequence."
  A strictly ordered enum lets every mediator method express its precondition as one
  equality check (`if (record.state != <required predecessor>) reject`), which
  mirrors the PDF's sequence table directly and makes an out-of-order call structurally
  easy to detect — there is exactly one "current state" per student at any time, so
  there's no ambiguity about what step is next.

- **`Map<String, StudentRecord> studentRecords` inside `ResultProcessingCoordinator`,
  keyed by `studentId` (`String`).**
  PDF anchor: page 3, System Requirements — "Register... students with the
  coordinator" and "Display the current processing status of a student" both require
  looking up a specific, named student, i.e. keyed access rather than a linear scan. A
  private static nested `StudentRecord` (holding the `Student` reference plus its
  current `StudentProcessingState`) was used instead of two parallel maps
  (`Map<String,Student>` and `Map<String,StudentProcessingState>`) so the two pieces of
  per-student data can never drift out of sync (e.g. a student present in one map but
  missing from the other).

- **Single office fields (`departmentOffice`, `controllerOffice`, `dswOffice`), not
  collections.**
  PDF anchor: page 3, System Requirements — "Register **the** Department Office, **the**
  Controller of Examinations, **the** DSW" (definite article, singular) — unlike
  students, which the PDF treats as plural ("students with the coordinator", "multiple
  citizens" pattern reused from Task 1's phrasing). BUET has exactly one of each office
  in this scenario, so singular fields were used rather than over-generalizing to
  `List<DepartmentOffice>` for a case the spec never asks for.

- **Rejection is a printed message + early return, not an exception.**
  PDF anchor: page 3, System Requirements — "Reject final-result publication when
  departmental confirmation is missing" / "Reject any request that violates the
  required sequence," and page 4's demonstration script explicitly *expects* rejected
  attempts to happen mid-program and be followed by further, successful steps (steps 1
  and 3 are rejections, steps 4–6 succeed afterward in the same run). Using a checked or
  unchecked exception would force every call site (`Main`) to wrap calls in try/catch
  just to continue the demo sequence, which doesn't match "reject and carry on" as
  depicted by the required demonstration order. `IllegalArgumentException` is still
  used, but only for the genuinely exceptional case of an unregistered student ID,
  which the PDF's demo script never triggers.

## 5. Requirement mapping for the demo sequence (`Main.java`)

PDF anchor: page 4, Implementation & Demonstration Requirements, steps 1–7. Each step
in `Main.java` is a direct, in-order translation of the seven bulleted demo steps:
early office-order attempt (rejected) → departmental confirmation → early
certificate/transcript attempt (rejected) → office order issued → testimonial issued →
certificate/transcript issued → student notifications and final status via
`Student.displayNotifications()` and `ResultMediator.getStatus(...)`.
