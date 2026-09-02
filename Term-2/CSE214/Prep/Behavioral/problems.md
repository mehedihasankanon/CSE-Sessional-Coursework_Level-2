# Behavioral Design Patterns: Practice Problem Set

## Category 1: Algorithm Encapsulation & Swapping

**Problem 1.1: Ride-Sharing Pricing Engine**
You are developing the backend for a local ride-sharing app (like Pathao). The cost of a ride is calculated based on distance, but the pricing algorithm must change dynamically based on real-time factors:

* **Standard Fare:** Base rate + (distance × standard rate).
* **Rain/Surge Fare:** Base rate + (distance × standard rate) + 50% weather premium.
* **Carpool Fare:** (Base rate + (distance × standard rate)) / number of passengers.
The `TripManager` system must calculate fares, but it should not contain massive `if-else` blocks for pricing logic. The pricing logic must be swappable at runtime (e.g., if it starts raining mid-ride, the remaining distance is calculated using the Surge fare).
**Task:** Identify and apply the correct design pattern. Write the core classes/interfaces and a `main` method simulating a trip that starts on a Standard fare and switches to a Surge fare halfway through.

**Problem 1.2: Cloud Storage Compression Module**
A cloud storage platform automatically compresses files when users upload them to save server space. Depending on the current server CPU load and the user's subscription tier, the system selects different compression algorithms dynamically:

* **ZIP Compression:** Fast, low CPU usage, moderate size reduction.
* **RAR/7z Compression:** Slow, high CPU usage, maximum size reduction.
* **AI-Lossless Compression:** Requires GPU, available only for premium users.
The `FileUploader` module delegates the compression task. Adding a new compression algorithm in the future (like Brotli) should not require altering the `FileUploader` code.
**Task:** Design the system using the appropriate pattern. Draw an ASCII UML class diagram and provide the interface definition.

---

## Category 2: State Machines & Transitions

**Problem 2.1: University BIIS Registration System**
You are tasked with redesigning the backend of a university course registration portal. A student's registration profile goes through distinct phases, and the actions they can perform change drastically depending on the phase:

* **Pre-Registration Phase:** Student can add/remove draft courses. Cannot print the final admit card.
* **Advisor Approval Phase:** Student cannot add/remove courses. Can only submit a "Request Revision" ping to the advisor.
* **Finalized Phase:** Student cannot add/remove or request revisions. Can print the final admit card.
Currently, the `StudentProfile` class is littered with `if (currentPhase == "PRE_REG")` statements, making it prone to bugs.
**Task:** Refactor this system using a behavioral pattern that encapsulates phase-specific logic into separate classes. Demonstrate a student trying to add a course in the Pre-Registration phase, transitioning to the Advisor Approval phase, and being denied when trying to add another course.

**Problem 2.2: Dhaka Metro Rail Turnstile**
A Metro Rail automated turnstile must track its internal status to prevent fare evasion.

* When **Locked**, pushing the gate triggers an alarm; tapping a valid MRT pass transitions it to the **Unlocked** state.
* When **Unlocked**, tapping another card does nothing; pushing the gate allows the passenger through and immediately transitions it back to the **Locked** state.
* If the network fails, it enters a **Maintenance** state where both pushing and tapping do nothing.
**Task:** Implement this system ensuring that state transitions are handled cleanly without massive switch statements. Show a simulation of a passenger tapping a card and passing through.

---

## Category 3: Broadcast & Notification

**Problem 3.1: Cyclone Early Warning System**
The Bangladesh Meteorological Department (BMD) maintains a `StormTracker` object that continuously monitors wind speeds and coordinates in the Bay of Bengal. When a severe cyclone forms, multiple independent government subsystems must be notified immediately:

1. **CoastalSirenSystem:** Activates physical alarms in coastal districts.
2. **SMSGateway:** Sends alert messages to registered mobile numbers.
3. **RadioBroadcaster:** Interrupts standard radio frequencies with a warning message.
The `StormTracker` should not be tightly coupled to these systems, as new alert systems (like a Smartphone Push Notification service) may be added in the future.
**Task:** Design the system. Implement a mechanism where the `SMSGateway` can dynamically disconnect from the warning system for maintenance without crashing the `StormTracker`.

**Problem 3.2: Live Cricket Dashboard**
A sports analytics company processes live ball-by-ball data from a cricket match via a `MatchFeed` class. Whenever a wicket falls or a boundary is hit, the data must instantly reflect on:

* A Web Scoreboard Widget.
* An Automated Betting Odds Calculator.
* A Live Commentary Database.
**Task:** Use a behavioral design pattern to implement this one-to-many dependency. Show the implementation of the `MatchFeed` and at least two display modules, demonstrating an update (e.g., "Shakib hits a 6").

---

## Category 4: Decentralized Communication Hubs

**Problem 4.1: Shahjalal Airport Air Traffic Control (ATC)**
You are simulating the airspace over an international airport. There are multiple `Flight` objects (e.g., Biman01, Emirates99). Flights must never communicate directly with one another to coordinate landings—doing so would result in a catastrophic web of dependencies. Instead, all flights communicate exclusively through an `ATCTower`.

* If Biman01 wants to land, it asks the Tower.
* The Tower checks if the runway is clear. If Emirates99 is currently on the runway, the Tower tells Biman01 to circle the airport.
* Once Emirates99 takes off, it notifies the Tower, and the Tower then clears Biman01 for landing.
**Task:** Implement this scenario using the appropriate pattern. The `Flight` objects must only hold a reference to the `ATCTower`, not to other flights.

**Problem 4.2: Microservice Order Broker**
In a distributed e-commerce backend, you have three distinct services: `InventoryService`, `PaymentService`, and `ShippingService`.
When a user clicks "Checkout", the system must reserve the item, process the card, and print the shipping label. However, the `InventoryService` should not have direct API calls to the `PaymentService`. Instead, an `OrderBroker` sits in the middle.

* Inventory tells the Broker: "Item reserved."
* Broker then tells Payment: "Charge card."
* Payment tells Broker: "Card charged."
* Broker tells Shipping: "Dispatch item."
**Task:** Write the skeleton code for this central coordinator pattern to decouple the three services.

---

## Category 5: Algorithm Skeletons & Inheritance

**Problem 5.1: Bank Loan Processing Pipeline**
A bank processes different types of loans (Personal, Mortgage, Corporate). Every loan application strictly follows a mandatory 4-step pipeline:

1. **Collect Documents** (Same for all: ID and Bank Statements)
2. **Credit Check** (Same for all: Call National Credit Bureau API)
3. **Calculate Risk** (Different for each: Mortgage relies on property value, Corporate relies on audit reports)
4. **Final Decision** (Different for each: Personal is auto-approved if score > 700; Corporate requires manual board review)
The bank strictly forbids developers from changing the order of these 4 steps, but allows them to provide custom logic for steps 3 and 4.
**Task:** Implement the framework using a design pattern that enforces this pipeline. Demonstrate the implementation of the base class and the `CorporateLoan` subclass.

**Problem 5.2: CI/CD Deployment Automation**
A DevOps team is building a custom deployment script engine. Every software deployment follows this sequence:

1. Pull code from Git.
2. Run Unit Tests.
3. Build Executable.
4. Deploy to Server.
Steps 1 and 2 are identical across all projects. However, a `JavaBackendProject` builds a `.jar` and deploys via Docker, while a `ReactFrontendProject` builds using Node and deploys to an AWS S3 bucket.
**Task:** Write the design pattern that defines the invariant steps while deferring the variant build/deploy steps to subclasses. Ensure that the main execution sequence cannot be overridden by a junior developer.