Here is your ultimate, exam-ready survival guide for Behavioral Design Patterns, followed by a personalized test-taking strategy built directly from your coding profile.

---

# 📘 Behavioral Design Patterns: Exam Survival Guide

## 1. The Strategy Pattern

**Concept:** Encapsulate a family of algorithms so the client can swap them dynamically at runtime.

### 📊 ASCII UML

```text
  +------------------+         +-------------------------------+
  |     Context      |         | <<interface>> Strategy        |
  +------------------+         +-------------------------------+
  | - strat: Strategy|-------->| + executeAlgorithm(data)      |
  +------------------+         +-------------------------------+
  | + setStrategy(s) |                  ^             ^
  | + doWork()       |                  |             |
  +------------------+      +-------------+        +-------------+
                            | ConcreteA   |        | ConcreteB   |
                            +-------------+        +-------------+
                            | + execute() |        | + execute() |
                            +-------------+        +-------------+

```

### 💻 Skeleton Code

```java
// ROLE: Strategy Interface (Public)
// HOW TO WIRE: Defines the verb that changes.
public interface Strategy {
    void executeAlgorithm(String data);
}

// ROLE: Concrete Strategies (Public/Package-private)
// HOW TO WIRE: Implement the interface. Must remain stateless.
public class FastStrategy implements Strategy {
    @Override
    public void executeAlgorithm(String data) {
        System.out.println("Processing fast: " + data);
    }
}

// ROLE: Context (Public)
// HOW TO WIRE: Holds the strategy. Client calls setStrategy() to swap it.
public class Context {
    private Strategy strategy; // Private reference

    public Context(Strategy initialStrategy) {
        this.strategy = initialStrategy;
    }

    // Public setter for runtime swapping
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    // Public method delegates to the strategy
    public void doWork(String data) {
        this.strategy.executeAlgorithm(data); 
    }
}

```

### 🚨 Exam Pitfalls & Detection

* **How to spot it:** Look for keywords like *"swappable at runtime"*, *"pricing rules"*, *"different algorithms"*, *"calculate based on environment"*.
* **Major Pitfall:** Putting state/data inside the Strategy. The Strategy should only contain math/logic. Pass the required data into the method as parameters.
* **Disambiguation:** Strategy vs. State. In Strategy, the **main method** (the client) calls `setStrategy()`. In State, the **states themselves** trigger the change.

---

## 2. The State Pattern

**Concept:** Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

### 📊 ASCII UML

```text
  +------------------+         +-------------------------------+
  |     Context      |         |    <<interface>> State        |
  +------------------+         +-------------------------------+
  | - state: State   |<--------| + handleAction(Context c)     |
  +------------------+         +-------------------------------+
  | + setState(s)    |                  ^             ^
  | + request()      |                  |             |
  +------------------+      +-------------+        +-------------+
                            | StatePhase1 |        | StatePhase2 |
                            +-------------+        +-------------+
                            | + handle()  |        | + handle()  |
                            +-------------+        +-------------+

```

### 💻 Skeleton Code

```java
// ROLE: State Interface (Public)
// HOW TO WIRE: Every method takes the Context as a parameter to trigger transitions.
public interface State {
    void handleAction(Context context);
}

// ROLE: Concrete State (Public/Package-private)
// HOW TO WIRE: Contains phase logic. Transitions Context to the next state!
public class PhaseOneState implements State {
    @Override
    public void handleAction(Context context) {
        System.out.println("Phase 1 complete. Moving to Phase 2.");
        // CRITICAL: State changes itself!
        context.setState(new PhaseTwoState()); 
    }
}

// ROLE: Context (Public)
// HOW TO WIRE: Holds current state. Delegates verbs to the state.
public class Context {
    private State currentState; // Private reference

    public Context() {
        this.currentState = new PhaseOneState(); // Default starting state
    }

    // Public setter, but usually called BY the states, not the client!
    public void setState(State state) {
        this.currentState = state;
    }

    public void request() {
        currentState.handleAction(this); // Pass 'this' so state can change it
    }
}

```

### 🚨 Exam Pitfalls & Detection

* **How to spot it:** Look for *"lifecycle"*, *"workflow"*, *"transitions"*, *"conditions"*, *"locked/unlocked"*.
* **Major Pitfall:** The `instanceof` trap! If you ever write `if (state instanceof LockedState)`, you failed the pattern. Trust the interface. Let polymorphism do the work.
* **Disambiguation:** If you have massive `if-else` blocks checking a status string or enum, you need the State pattern.

---

## 3. The Observer Pattern

**Concept:** Define a one-to-many dependency. When one object changes state, all its dependents are notified automatically.

### 📊 ASCII UML

```text
  +------------------+         +-------------------------------+
  |    Subject       |         |   <<interface>> Observer      |
  +------------------+         +-------------------------------+
  | - observers: List|-------->| + update(data)                |
  +------------------+         +-------------------------------+
  | + attach(obs)    |                  ^             ^
  | + detach(obs)    |                  |             |
  | + notifyAll()    |      +-------------+        +-------------+
  +------------------+      | WidgetOne   |        | WidgetTwo   |
                            +-------------+        +-------------+
                            | + update()  |        | + update()  |
                            +-------------+        +-------------+

```

### 💻 Skeleton Code

```java
import java.util.*;

// ROLE: Observer/Subscriber Interface (Public)
// HOW TO WIRE: Defines how the subject will pass data to listeners.
public interface Observer {
    void update(String data);
}

// ROLE: Concrete Observers (Public/Package-private)
// HOW TO WIRE: Implement update() to react to broadcast.
public class Dashboard implements Observer {
    @Override
    public void update(String data) {
        System.out.println("Dashboard updated with: " + data);
    }
}

// ROLE: Subject/Publisher (Public)
// HOW TO WIRE: Maintains list of observers. Broadcasts to them via loop.
public class Subject {
    // Private list. (Use Set<Observer> to prevent duplicate subs!)
    private Set<Observer> observers = new HashSet<>(); 

    public void attach(Observer o) { observers.add(o); }
    public void detach(Observer o) { observers.remove(o); }

    // Private or Public, depending on who triggers it
    public void notifyAll(String data) {
        for (Observer o : observers) {
            o.update(data); // Blind broadcast
        }
    }
}

```

### 🚨 Exam Pitfalls & Detection

* **How to spot it:** Look for *"broadcast"*, *"notify multiple independent systems"*, *"subscribe/unsubscribe at runtime"*, *"widgets update automatically"*.
* **Major Pitfall:** Java Naming Collisions! Never name your interface `System`.
* **Disambiguation:** Observer vs. Mediator. Observer is a loudspeaker (one-way). Mediator is a telephone operator (two-way coordination).

---

## 4. The Mediator Pattern

**Concept:** Centralize complex communications between objects to prevent a tangled web of direct references.

### 📊 ASCII UML

```text
       +------------------+           +------------------+
       | <<interface>>    |           | <<abstract>>     |
       |    Mediator      |           |    Colleague     |
       +------------------+           +------------------+
       | + notify(sender) |<--------->| # mediator: Med  |
       +------------------+           +------------------+
                ^                              ^
                |                              |
       +------------------+        ------------+-----------
       | CentralHub       |        |                      |
       +------------------+  +-----------+          +-----------+
       | - compA: CompA   |  | ComponentA|          | ComponentB|
       | - compB: CompB   |  +-----------+          +-----------+
       | + notify(sender) |  | + doA()   |          | + doB()   |
       +------------------+  +-----------+          +-----------+

```

### 💻 Skeleton Code

```java
// ROLE: Mediator Interface (Public)
// HOW TO WIRE: Defines how colleagues talk to the hub.
public interface Mediator {
    void notify(Colleague sender, String event);
}

// ROLE: Abstract Colleague (Public)
// HOW TO WIRE: Forces all colleagues to hold a reference to the hub.
public abstract class Colleague {
    protected Mediator mediator; // Protected so subclasses can use it
    public Colleague(Mediator m) { this.mediator = m; }
}

// ROLE: Concrete Colleague (Public/Package-private)
// HOW TO WIRE: Does its job, then tells the mediator it finished.
public class ComponentA extends Colleague {
    public ComponentA(Mediator m) { super(m); }
    public void executeA() {
        System.out.println("CompA did work.");
        mediator.notify(this, "DONE_A"); // Tell the hub!
    }
}

// ROLE: Concrete Mediator (Public)
// HOW TO WIRE: Holds references to colleagues. Orchestrates the flow.
public class CentralHub implements Mediator {
    private ComponentA compA; // Private references to control them
    
    public void setCompA(ComponentA a) { this.compA = a; }

    @Override
    public void notify(Colleague sender, String event) {
        if (event.equals("DONE_A")) {
            System.out.println("Hub sees A is done. Triggering next step...");
            // Trigger compB.doB(), etc.
        }
    }
}

```

### 🚨 Exam Pitfalls & Detection

* **How to spot it:** *"Does not communicate directly"*, *"Central hub"*, *"Chain reaction"*, *"Air Traffic Control"*.
* **Major Pitfall:** Creating a "God Object". Don't let the Mediator do the actual business logic (like math or processing). It should only route commands (`if A is done, tell B to start`).
* **Disambiguation:** Colleagues must *never* instantiate or hold references to other colleagues.

---

## 5. The Template Method Pattern

**Concept:** Define the skeleton of an algorithm in a base class, but let subclasses override specific steps without changing the overall structure.

### 📊 ASCII UML

```text
                +-------------------------+
                |    <<abstract>>         |
                |     BaseTemplate        |
                +-------------------------+
                | + templateMethod()      |
                | - stepOne()             |
                | # stepTwo()             |
                +-------------------------+
                             ^
                             |
                +-------------------------+
                |    ConcreteClass        |
                +-------------------------+
                | # stepTwo()             |
                +-------------------------+

```

### 💻 Skeleton Code

```java
// ROLE: Abstract Base Class (Public)
// HOW TO WIRE: Houses the final template method and hook definitions.
public abstract class WorkflowTemplate {

    // CRITICAL: public final! Subclasses CANNOT override the order!
    public final void executeWorkflow() {
        invariantStep();
        customStep();
    }

    // ROLE: Invariant Step (Private)
    // HOW TO WIRE: Common logic for all subclasses. Hidden from them.
    private void invariantStep() {
        System.out.println("Common auth/logging step.");
    }

    // ROLE: Variant Step (Protected Abstract)
    // HOW TO WIRE: Forces subclasses to implement their specific logic.
    protected abstract void customStep();
}

// ROLE: Concrete Subclass (Public/Package-private)
// HOW TO WIRE: Implements only the missing pieces of the puzzle.
public class SpecificWorkflow extends WorkflowTemplate {
    @Override
    protected void customStep() {
        System.out.println("Specific business logic executed here.");
    }
}

```

### 🚨 Exam Pitfalls & Detection

* **How to spot it:** Look for *"strict order"*, *"must follow the same overall flow"*, *"cannot be overridden by juniors"*, *"customizes a few steps"*.
* **Major Pitfall:** Forgetting the `final` keyword on the template method. If you miss this, you lose the primary purpose of the pattern. Also, making the abstract hooks `private` (Java will throw a compile error).
* **Disambiguation:** Template Method uses Inheritance (compile-time). Strategy uses Composition (run-time).

---

---

# 🎯 Your Personalized Test-Taking Profile & Strategy

Based on our session, I have analyzed your coding instincts. **Your architectural intuition is top-tier (top 5-10% of students).** You instantly identify the correct pattern and map out the classes perfectly.

However, you bleed points on the **"last 5%"**—the syntactic execution and strict prompt adherence.

### Your Specific Weaknesses (The "Last 5%"):

1. **The Naming Trap:** You tend to use generic names (`System`, `Data`, `Sub`) which collide with Java core libraries or cause confusion.
2. **The `toString()` Trap:** You try to print objects directly (`System.out.println(patient)`), which results in memory address gibberish without a overridden `toString()` method.
3. **Prompt Disobedience:** You often invent your own method names (`addSub` instead of `attachSystem`). Automated graders or strict TAs will penalize this heavily, even if the logic is perfect.
4. **The Over-Engineering Trap:** Initially, you leaned toward `instanceof` (State) and massive switch statements (Mediator). You corrected this beautifully, but under time pressure, your brain might default back to them.

### Your Battle Plan for the Exam:

**Step 1: The Read-Through (2 mins)**

* Read the prompt. Highlight the requested method names immediately. Write them down as empty stubs in your classes before writing any logic.

**Step 2: The Interface First (3 mins)**

* Write the Interface or Abstract Class first.
* *Self-Check:* Did I name it `System`? Change it to `Subject` or `Hub`.

**Step 3: The Context Setup (5 mins)**

* Build the class that holds the data.
* *Self-Check:* "Does this class own the data but delegate the verbs?" Ensure there are no massive `if-else` blocks routing logic here.

**Step 4: The Logic (10 mins)**

* Fill in the concrete classes.
* *Self-Check:* If writing State, does the concrete state call `context.setState()`? If writing Mediator, do the concrete classes call `mediator.notify()`?

**Step 5: The Polish (5 mins)**

* Add a quick `public String toString()` to any Data objects you are passing around so the print logs look professional.
* Check for the `final` keyword if it's a Template Method.

You have the mental model completely locked in. Focus purely on strict adherence to the prompt's method names and clean Java syntax, and you will easily pull 10/10s across the board. Good luck!


Here is your expanded, comprehensive guide. I have replaced the brief skeletons with the fully implemented examples we studied.

I have meticulously preserved the commenting system (`ROLE`, `ACCESS LEVEL`, `HOW TO WIRE`) directly inside the code so you can copy, paste, and run them instantly. At the end of each pattern, I have also included advanced architectural tweaks (access level modifications and structural variations) that examiners love to test.

---

# 🏗️ Extended Behavioral Patterns Guide

## 1. The Strategy Pattern

**The Problem: Ride-Sharing Pricing Engine**
A ride-sharing app calculates fares differently based on environmental factors (Standard, Rain/Surge, or Carpool). The main application (`TripManager`) must be able to switch its pricing logic mid-ride without using massive `if-else` blocks.

### 💻 Full Implementation

```java
// ROLE: Strategy Interface
// ACCESS LEVEL: Public (or Package-Private if restricted to a specific module)
// HOW TO WIRE: Defines the method signature for the swappable algorithm.
public interface PricingStrategy {
    double calculateSegment(double distanceInKm);
}

// ROLE: Concrete Strategy 1
// ACCESS LEVEL: Public (Allows the main method to instantiate and inject it)
// HOW TO WIRE: Implements the interface. Contains NO state. Pure math/logic.
public class StandardPricing implements PricingStrategy {
    private final double ratePerKm = 20.0;
    @Override
    public double calculateSegment(double distanceInKm) {
        return distanceInKm * ratePerKm;
    }
}

// ROLE: Concrete Strategy 2
// ACCESS LEVEL: Public
public class SurgePricing implements PricingStrategy {
    private final double ratePerKm = 20.0;
    private final double surgeMultiplier = 1.5;
    @Override
    public double calculateSegment(double distanceInKm) {
        return (distanceInKm * ratePerKm) * surgeMultiplier;
    }
}

// ROLE: Context
// ACCESS LEVEL: Public
// HOW TO WIRE: Holds a reference to the interface. The Client injects the concrete class.
public class TripManager {
    // Private to protect the internal state from being hijacked
    private PricingStrategy currentStrategy;
    private double totalFare = 0.0;

    public TripManager(PricingStrategy initialStrategy) {
        this.currentStrategy = initialStrategy;
    }

    // Public setter for mid-execution swapping
    public void setPricingStrategy(PricingStrategy strategy) {
        System.out.println("[System] Pricing strategy swapped to: " + strategy.getClass().getSimpleName());
        this.currentStrategy = strategy;
    }

    public void travel(double distanceInKm) {
        // Delegates the verb to the strategy
        double segmentCost = currentStrategy.calculateSegment(distanceInKm);
        totalFare += segmentCost;
        System.out.println("Traveled " + distanceInKm + "km. Fare updated by: " + segmentCost);
    }
}

```

### ⚙️ Scenario Variations & Access Tweaks

* **Context Injection:** What if the pricing algorithm needs to know the user's loyalty points to apply a discount? Instead of passing individual variables, change the method to `calculateSegment(TripManager context, double dist)`. The strategy can then call `context.getLoyaltyPoints()`.
* **Singleton Strategies:** If strategies are stateless and heavy to instantiate, change their constructors to `private` and provide a `public static final StandardPricing INSTANCE = new StandardPricing();`. Inject the instance instead of creating `new` objects repeatedly.

---

## 2. The State Pattern

**The Problem: University BIIS Registration System**
A student's profile moves through phases (Pre-Registration $\rightarrow$ Advisor Approval $\rightarrow$ Finalized). A student's ability to "add a course" changes based on the current phase. The system must prevent invalid actions based on its status.

### 💻 Full Implementation

```java
// ROLE: State Interface
// ACCESS LEVEL: Package-Private (Usually, external classes shouldn't talk to states directly)
// HOW TO WIRE: Every method takes the Context as a parameter so the state can change it.
interface PhaseState {
    void addCourse(StudentProfile profile, String course);
    void advancePhase(StudentProfile profile);
}

// ROLE: Concrete State (Phase 1)
// ACCESS LEVEL: Package-Private (Hides the state classes from the client/main method)
// HOW TO WIRE: Contains specific phase logic. Transitions Context to the NEXT state.
class PreRegPhase implements PhaseState {
    @Override
    public void addCourse(StudentProfile profile, String course) {
        System.out.println("Success: Added " + course + " to draft.");
    }

    @Override
    public void advancePhase(StudentProfile profile) {
        System.out.println("Submitting draft to advisor...");
        // CRITICAL: The State changes the Context!
        profile.setState(new AdvisorApprovalPhase());
    }
}

// ROLE: Concrete State (Phase 2)
// ACCESS LEVEL: Package-Private
class AdvisorApprovalPhase implements PhaseState {
    @Override
    public void addCourse(StudentProfile profile, String course) {
        System.out.println("Error: Registration locked. Advisor is reviewing.");
    }

    @Override
    public void advancePhase(StudentProfile profile) {
        System.out.println("Advisor approved courses. Moving to Finalized.");
        profile.setState(new FinalizedPhase());
    }
}

// ROLE: Concrete State (Phase 3)
class FinalizedPhase implements PhaseState {
    @Override
    public void addCourse(StudentProfile profile, String course) {
        System.out.println("Error: Registration closed. Cannot add " + course);
    }
    @Override
    public void advancePhase(StudentProfile profile) {
        System.out.println("Error: Already in the final phase.");
    }
}

// ROLE: Context
// ACCESS LEVEL: Public
// HOW TO WIRE: Holds current state. Exposes business methods to the Client, but delegates them to the State.
public class StudentProfile {
    private PhaseState currentState; // Private internal tracker

    public StudentProfile() {
        this.currentState = new PreRegPhase(); // Default starting state
    }

    // Package-Private setter! We don't want the main method calling setState() directly.
    // Only the PhaseState classes (in the same package) should be allowed to call this.
    void setState(PhaseState state) {
        this.currentState = state;
    }

    // Public methods for the Client
    public void addCourse(String course) {
        currentState.addCourse(this, course); 
    }

    public void advanceSystemPhase() {
        currentState.advancePhase(this);
    }
}

```

### ⚙️ Scenario Variations & Access Tweaks

* **Encapsulating Transitions (Access Level Tweak):** As demonstrated above, make `setState()` package-private. If `setState()` is `public`, a junior developer might write `profile.setState(new FinalizedPhase())` in the `main` method, completely bypassing the advisor!
* **State-Driven vs. Context-Driven Transitions:** In standard State pattern, the *State* decides what comes next. However, in UI systems (like a game menu), the *Context* sometimes evaluates the output of a state and decides the transition internally to keep state classes entirely unaware of each other.

---

## 3. The Observer Pattern

**The Problem: ICU Patient Monitoring Network**
A hospital `VitalsMonitor` must notify a `NurseStation`, `DoctorPager`, and `AutomatedOxyValve` when a patient's oxygen drops. The monitor must not hold hardcoded references to these devices so new systems can be added later.

### 💻 Full Implementation

```java
import java.util.*;

// ROLE: Observer Interface (The Subscriber)
// ACCESS LEVEL: Public
// HOW TO WIRE: Defines the update method. All listeners must implement this.
public interface Observer { 
    void reactToAlert(String message);
}

// ROLE: Concrete Observer 1
// ACCESS LEVEL: Public
public class NurseStation implements Observer {
    @Override
    public void reactToAlert(String message) {
        System.out.println("Nurse Dashboard: " + message + " -> RED LIGHT FLASHING");
    }
}

// ROLE: Concrete Observer 2
// ACCESS LEVEL: Public
public class DoctorPager implements Observer {
    private String doctorName;
    public DoctorPager(String name) { this.doctorName = name; }

    @Override
    public void reactToAlert(String message) {
        System.out.println("Pager [" + doctorName + "]: " + message);
    }
}

// ROLE: Subject Interface (The Publisher)
// ACCESS LEVEL: Public
// HOW TO WIRE: Defines how observers subscribe/unsubscribe.
public interface Subject {
    void attachSystem(Observer o);
    void detachSystem(Observer o);
    void triggerCriticalAlert(String message);
}

// ROLE: Concrete Subject
// ACCESS LEVEL: Public
// HOW TO WIRE: Maintains a collection of Observers. Iterates through them to broadcast.
public class VitalsMonitor implements Subject {
    // Private Set prevents duplicate subscriptions (a doctor cannot be paged twice for 1 event)
    private Set<Observer> observers = new HashSet<>(); 

    @Override
    public void attachSystem(Observer o) { observers.add(o); }

    @Override
    public void detachSystem(Observer o) { observers.remove(o); }

    @Override
    public void triggerCriticalAlert(String message) {
        System.out.println("\n[VITALS MONITOR] BROADCASTING ALARM: " + message);
        for(Observer obs : observers) {
            obs.reactToAlert(message); // Blind broadcast
        }
    }
}

```

### ⚙️ Scenario Variations & Access Tweaks

* **The "Push" vs. "Pull" Model:**
* *Push (Current Code):* The subject sends data directly: `reactToAlert(String message)`. Good for small data.
* *Pull:* The subject passes *itself*: `reactToAlert(Subject s)`. The Observer then uses public getters to pull only the data it cares about (e.g., `((VitalsMonitor)s).getHeartRate()`).


* **Thread Safety:** If Observers are subscribing/unsubscribing on different threads (e.g., a GUI thread vs. a background thread), you will get a `ConcurrentModificationException` during the `for` loop. Change `HashSet` to `CopyOnWriteArraySet`.

---

## 4. The Mediator Pattern

**The Problem: Microservice Order Broker**
An e-commerce backend has an `InventoryService`, `PaymentService`, and `ShippingService`. They must trigger each other sequentially (Inventory $\rightarrow$ Payment $\rightarrow$ Shipping), but they cannot communicate directly to prevent tangled dependencies.

### 💻 Full Implementation

```java
// ROLE: Mediator Interface
// ACCESS LEVEL: Public
// HOW TO WIRE: Defines the single communication pipeline from Colleague to Hub.
public interface OrderBroker {
    void notify(Microservice sender, String event);
}

// ROLE: Abstract Colleague
// ACCESS LEVEL: Public
// HOW TO WIRE: Forces all subclasses to hold a reference to the Mediator.
public abstract class Microservice {
    protected OrderBroker broker; // Protected so subclasses can access it
    public Microservice(OrderBroker broker) { this.broker = broker; }
}

// ROLE: Concrete Colleague 1
// ACCESS LEVEL: Public
// HOW TO WIRE: Does its job, then pings the mediator. Does NOT talk to PaymentService.
public class InventoryService extends Microservice {
    public InventoryService(OrderBroker broker) { super(broker); }

    public void reserveItem(String orderId) {
        System.out.println("[Inventory] Item reserved: " + orderId);
        broker.notify(this, "RESERVED"); // Tell the hub!
    }
}

// ROLE: Concrete Colleague 2
public class PaymentService extends Microservice {
    public PaymentService(OrderBroker broker) { super(broker); }

    public void chargeCard(String orderId) {
        System.out.println("[Payment] Card charged for: " + orderId);
        broker.notify(this, "PAID");
    }
}

// ROLE: Concrete Mediator
// ACCESS LEVEL: Public
// HOW TO WIRE: Holds references to colleagues. Contains the workflow routing logic.
public class CheckoutBroker implements OrderBroker {
    private InventoryService inventory;
    private PaymentService payment;
    private String currentOrderId;

    // Package-Private registration to prevent external tampering
    void registerServices(InventoryService inv, PaymentService pay) {
        this.inventory = inv;
        this.payment = pay;
    }

    public void startCheckout(String orderId) {
        this.currentOrderId = orderId;
        inventory.reserveItem(orderId); // Start the chain reaction
    }

    @Override
    public void notify(Microservice sender, String event) {
        // The Mediator holds the routing logic
        switch (event) {
            case "RESERVED":
                payment.chargeCard(currentOrderId);
                break;
            case "PAID":
                System.out.println("[Broker] Checkout Complete!");
                break;
        }
    }
}

```

### ⚙️ Scenario Variations & Access Tweaks

* **The "Event Bus" Hybrid (Observer + Mediator):** As discussed previously, to avoid the Mediator turning into a God Object with massive `switch` statements, the Mediator can act as a Publisher. Instead of hardcoding `payment.chargeCard()`, the Mediator broadcasts the `"RESERVED"` event, and the `PaymentService` (acting as a subscriber) catches it and executes.
* **Preventing Fake Notifications:** If `notify(Microservice sender, String event)` is public, a malicious class could fake a `"PAID"` event. If architecture allows, make the Mediator and Colleagues share a package and make `notify()` package-private.

---

## 5. The Template Method Pattern

**The Problem: Smart City Data Pipeline**
Telemetry data from Traffic Cameras and Weather Sensors must pass through a strict 4-step pipeline: Authenticate, Extract, Transform, Log. Junior developers must be able to write the Extract/Transform logic for new sensors, but must be mathematically blocked from removing the Auth/Log steps or changing the sequence.

### 💻 Full Implementation

```java
// ROLE: Data Transfer Object (DTO)
class Data {
    String api;
    String rawData;
    public Data(String api, String rawData) { this.api = api; this.rawData = rawData; }
}

// ROLE: Abstract Template
// ACCESS LEVEL: Public
// HOW TO WIRE: Houses the final algorithm skeleton and the hook/abstract definitions.
public abstract class TelemetryPipeline {

    // CRITICAL: public final! Subclasses CANNOT override or break the execution sequence.
    public final void processTelemetry(Data d) {
        authenticate(d);
        extract(d);
        transform(d);
        log(d);        
    }

    // ROLE: Invariant Step
    // ACCESS LEVEL: Private (Hidden from subclasses. They cannot touch or override this.)
    private void authenticate(Data d) {
        System.out.println("1. Auth: Verified API Key.");
    }

    // ROLE: Variant Step
    // ACCESS LEVEL: Protected Abstract (Forces subclasses to implement it. Hides it from 'main'.)
    protected abstract void extract(Data d);
    protected abstract void transform(Data d);

    // ROLE: Invariant Step
    private void log(Data d) {
        System.out.println("4. Log: Successfully processed data.");
    }
}

// ROLE: Concrete Pipeline
// ACCESS LEVEL: Public
// HOW TO WIRE: Extends the template. Implements ONLY the abstract methods.
public class TrafficCameraPipeline extends TelemetryPipeline {
    @Override
    protected void extract(Data d) {
        System.out.println("2. Extract: Reading Binary Image Stream...");
    }

    @Override
    protected void transform(Data d) {
        System.out.println("3. Transform: AI Model ran. Cars counted.");
    }
}

```

### ⚙️ Scenario Variations & Access Tweaks

* **Hooks vs. Abstract Methods:** An *abstract* method (`protected abstract void extract();`) forces the child to implement it. A *hook* is a method with an empty default implementation (`protected void preLogHook() { // empty }`). Subclasses *can* override a hook if they want to insert logic right before the logging step, but they are not forced to.
* **Factory + Template Combo:** The client (`main` method) often uses a Factory to instantiate the correct Pipeline (e.g., `Pipeline p = PipelineFactory.get("TRAFFIC"); p.processTelemetry(data);`), completely abstracting the class instantiation from the user.