Task 2 — Decisions (Mediator pattern)

1) Class diagram (ASCII)

               +------------------------------------+
               | ResultProcessingCoordinator (mediator) |
               +------------------------------------+
               | - students: Map<String,Student>     |
               | - states: Map<String,StudentProcessingState> |
               | + registerStudent(s)                |
               | + requestDepartmentalConfirmation(id)|
               | + requestOfficeOrder(id)            |
               | + requestTestimonial(id)            |
               | + requestCertificateAndTranscript(id)|
               +-----------------+------------------+
                                 /
                    communicates only via mediator
                                 |
        +----------------+     +---------------------------+     +----------------+
        | DepartmentOff. |     | ControllerOffice (Controller)|   | DSWOffice      |
        +----------------+     +---------------------------+     +----------------+
        | + confirmAcademicCompletion(id) | + issueOfficeOrder(id) | + issueTestimonial(id)
        +----------------+                               +----------------+
                                 \
                                  v
                              +---------+
                              | Student |
                              +---------+
                              | - studentId:String
                              | - notifications: List<String>
                              +---------+

2) Design pattern decision
- Chosen pattern: Mediator. Rationale: assignment explicitly requires "a central result-processing coordinator" to avoid direct office-to-office calls; every request and status update must pass through this coordinator. This maps directly to assignment lines describing the central coordinator and prohibition of direct calls [Offline3.md](Offline3.md#L96-L100).

3) Inheritance / implementation decisions
- `Office` is an abstract base class (colleague) with concrete subclasses `DepartmentOffice`, `ControllerOffice`, and `DSWOffice`. Offices hold a reference to the `ResultProcessingCoordinator` and send requests only through it — enforces the Mediator constraints from the spec ([Offline3.md](Offline3.md#L96-L100)).
- `ResultProcessingCoordinator` encapsulates student registry and `StudentProcessingState` state machine (enum) to represent progression. Using an enum ensures only valid, named states (NOT_STARTED, DEPARTMENT_CONFIRMED, OFFICE_ORDER_ISSUED, TESTIMONIAL_ISSUED, COMPLETED) and enforces valid transitions in code, matching Required Processing Sequence items 1–4 [Offline3.md](Offline3.md#L104-L124).
- State transition checks are explicit in coordinator methods (`requestOfficeOrder` requires DEPARTMENT_CONFIRMED; `requestTestimonial` requires OFFICE_ORDER_ISSUED; `requestCertificateAndTranscript` requires TESTIMONIAL_ISSUED). This enforces "Reject any request that violates the required sequence" ([Offline3.md](Offline3.md#L156-L158)).

4) API / method design choices and rationale
- `registerStudent(Student)` stores `students` map and initializes state to `NOT_STARTED` — maps to system requirement to "Register ... students with the coordinator" ([Offline3.md](Offline3.md#L130-L134)).
- `requestDepartmentalConfirmation(id)` moves the state to `DEPARTMENT_CONFIRMED` only from `NOT_STARTED` — implements Required Processing Sequence item 1 ([Offline3.md](Offline3.md#L104-L108)).
- `requestOfficeOrder(id)` checks state==DEPARTMENT_CONFIRMED before moving to OFFICE_ORDER_ISSUED — implements Required Processing Sequence item 2 ([Offline3.md](Offline3.md#L110-L114)).
- `requestTestimonial(id)` checks state==OFFICE_ORDER_ISSUED before moving to TESTIMONIAL_ISSUED — implements item 3 ([Offline3.md](Offline3.md#L116-L119)).
- `requestCertificateAndTranscript(id)` requires TESTIMONIAL_ISSUED and moves to COMPLETED — implements item 4 ([Offline3.md](Offline3.md#L121-L124)).
- Each successful step sends a notification string to the `Student` via `student.receiveNotification(...)`, satisfying "Issue ... and notify the student" ([Offline3.md](Offline3.md#L146-L146)).

5) Data types chosen (summary)
- `students: Map<String, Student>` — keyed by studentId for O(1) lookup when offices submit requests (system requirements 130-134).
- `states: Map<String, StudentProcessingState>` — stores current processing state per student; `StudentProcessingState` implemented as `enum` to strictly encode allowed progression (see file `StudentProcessingState.java`) (Required Processing Sequence lines 104-124).
- `notifications` in `Student` : `List<String>` (ArrayList) — store ordered list of notices to display at end (implementation requirement step 7) [Offline3.md](Offline3.md#L172-L200).

6) Mapping of demo steps to assignment checks
- Attempt to publish before departmental confirmation: demo calls `controller.issueOfficeOrder(...)` before confirmation; coordinator rejects — matches Implementation sequence step 1 [Offline3.md](Offline3.md#L174-L176).
- Department submits confirmation: demo calls `dept.confirmAcademicCompletion(...)` — matches sequence step 2 [Offline3.md](Offline3.md#L178-L180).
- Early attempt to issue certificate/transcript: demo calls `controller.issueCertificateAndTranscript(...)` earlier and coordinator rejects — matches sequence step 3 [Offline3.md](Offline3.md#L182-L184).
- Issuance of office order, testimonial, certificate & transcript in correct order: demo executes these steps through the coordinator; notifications recorded and final state displayed — matches sequence steps 4–7 [Offline3.md](Offline3.md#L186-L200).

7) Notes / trade-offs
- Chose synchronous in-process coordinator for clarity. A production mediator could be an event-broker or message-queue with durable state.
- State machine is explicitly enforced in coordinator methods rather than using reflection or dynamic rules to make the control flow clear and demonstrable for grading.

References (assignment specification)
- Mediator requirement and coordinator prohibition of direct calls: [Offline3.md](Offline3.md#L96-L100)
- Required Processing Sequence items: [Offline3.md](Offline3.md#L104-L124)
- System requirements (register offices/students, reject invalid sequences, notify student): [Offline3.md](Offline3.md#L130-L162)
- Implementation & Demonstration sequence: [Offline3.md](Offline3.md#L166-L200)
