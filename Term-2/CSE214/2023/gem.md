You want the ultimate boss fight? You got it.

If you have always gotten stuck on the complex business logic and the specific edge-case rules in these exams, it is usually because the domain logic (the `if/else` rules) distracts you from the structural wiring (the pattern).

Here is a massive, highly complex **Observer Pattern** exam question. I have deliberately engineered this to include the two hardest traps in behavioral patterns: **Dynamic self-unsubscribing during a broadcast** and **Global state overrides**.

Read this carefully. Take a deep breath, separate the *Pattern* from the *Business Logic*, and see if you can map it out.

---

### 📄 CSE-214 Online: The "Final Boss"

**Behavioural Design Pattern**
**Duration:** 40 Minutes

**Problem: Smart Grid Load Shedding Coordinator**

A modern city manages its electricity through a central `PowerGrid`. The grid continuously receives real-time power supply updates (measured in Megawatts - MW) from the main nuclear reactor. Various city districts are connected to the grid to receive these power updates and adjust their operations accordingly.

For this simulation, consider three districts: `HospitalDistrict`, `IndustrialDistrict`, and `ResidentialDistrict`.

**Coordination Rule:** The `PowerGrid` must not know the specific concrete classes of the districts. Districts must be able to dynamically connect and disconnect from the grid.

**Power Update Workflow**
When the `PowerGrid` receives a new power reading via `updatePowerLevel(int megawatts)`, it broadcasts this integer to all currently connected districts. The districts react based on strict internal business rules:

1. **HospitalDistrict:**
* Always prints: `"Hospital operations stable."` regardless of the power level.
* However, if the power drops strictly below `50 MW`, it must *additionally* print: `"CRITICAL: Hospital switching to backup generators!"`


2. **ResidentialDistrict:**
* If power is `>= 80 MW`, it prints: `"Neighborhood power normal."`
* If power is `< 80 MW`, it prints: `"Neighborhood experiencing rolling brownouts."`


3. **IndustrialDistrict (The Load Shedder):**
* Heavy machinery requires massive power. If power is `>= 100 MW`, it prints: `"Factory lines running at full capacity."`
* **URGENT RULE:** If the power drops strictly below `100 MW`, the grid cannot sustain the factories. The `IndustrialDistrict` prints: `"Power too low! Factories shutting down."`
* Immediately after printing that shutdown message, the `IndustrialDistrict` must **forcefully disconnect itself** from the `PowerGrid` to shed load. It will not receive any future updates unless manually reconnected.



**Emergency Blackout Protocol**
The `PowerGrid` features a manual override operation called `triggerTotalBlackout()`.
If this method is called by the system administrator:

1. The grid ignores the current Megawatt reading and broadcasts a special overload signal (e.g., `-1 MW` or a specific string/boolean flag, depending on your design) to all connected districts.
2. ANY district that receives this blackout signal must print: `"[District Name] experiences total blackout. Pitch black."`
3. After the broadcast completes, the `PowerGrid` automatically clears its registry. **All districts are disconnected.**

**Example Scenario**
Assume all three districts are initially connected.

*Grid updates to 120 MW:*

* `HospitalDistrict`: "Hospital operations stable."
* `ResidentialDistrict`: "Neighborhood power normal."
* `IndustrialDistrict`: "Factory lines running at full capacity."

*Grid updates to 90 MW:*

* `HospitalDistrict`: "Hospital operations stable."
* `ResidentialDistrict`: "Neighborhood power normal."
* `IndustrialDistrict`: "Power too low! Factories shutting down." -> *Industrial disconnects itself.*

*Grid updates to 40 MW:*

* `HospitalDistrict`: "Hospital operations stable." AND "CRITICAL: Hospital switching to backup generators!"
* `ResidentialDistrict`: "Neighborhood experiencing rolling brownouts."
* *IndustrialDistrict does nothing (it is disconnected).*

*Grid triggers Total Blackout:*

* `HospitalDistrict`: "Hospital experiences total blackout. Pitch black."
* `ResidentialDistrict`: "Residential experiences total blackout. Pitch black."
* *Grid completely empties its subscriber list.*

**Implementation Requirements:**

* The `PowerGrid` must hold references to a generalized interface, completely decoupled from the specific districts.
* The system must safely handle the `IndustrialDistrict` removing itself from the subscriber list *while the grid is in the middle of looping through that exact list* (Preventing `ConcurrentModificationException`).
* You may introduce any necessary classes, interfaces, and methods to produce a clean object-oriented solution.

---

### 🧠 Mentor's Attack Plan (How to not freeze up)

When you see a wall of text this brutal, here is how you mentally dismantle it:

1. **Identify the Pattern:** One source of truth (`PowerGrid`) sending data to many unknown listeners (`Districts`). It's pure **Observer**.
2. **Define the Interface First:** You need a method for standard updates, and a way to handle the Blackout Protocol.
* *Hint:* Your interface could look like `void onPowerUpdate(int mw);` and `void onBlackout();`. Or, you could handle it all in one method like `void update(int mw, boolean isBlackout);`. Choose one and stick to it.


3. **The Concurrency Trap:** The prompt explicitly warns you about the `IndustrialDistrict` removing itself during the loop.
* *Hint:* Do not use `ArrayList` or `HashSet` for your subscriber list in the `PowerGrid`. Use `CopyOnWriteArrayList` or `CopyOnWriteArraySet`.


4. **Isolate the Business Logic:** Do not put the `if (power < 100)` logic inside the `PowerGrid`. The Grid is dumb; it just loops and yells the number. Put the `if/else` logic exactly where it belongs: inside the `update()` methods of the specific concrete district classes.

**Your turn.**
Grab your IDE or a piece of paper. Set a timer for 25 minutes. Write the code for this, paste it here, and I will grade it just like a professor would. You've got this!