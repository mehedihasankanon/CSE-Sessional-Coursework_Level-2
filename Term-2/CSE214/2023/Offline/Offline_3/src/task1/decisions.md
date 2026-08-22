Task 1 — Decisions (Observer pattern)

1) Class diagram (ASCII)

           +-------------------+          implements         +-------------------+
           |   CitizenObserver |<----------------------------|     Citizen       |
           +-------------------+                             +-------------------+
                   ^                                                |
                   |                                                |
            implements update(Alert)                          stores List<Alert>
                   |                                                |
+------------------+------------------+                   +---------+--------+
|   AlertPublisher (interface)         |                   |     Alert         |
| - registerCitizen(c)                |                   | - title: String   |
| - unregisterCitizen(c)              |                   | - category: enum  |
| - subscribe(c,category)             |                   | - location:String |
| - unsubscribe(c,category)           |                   | - severity:String |
| - publishAlert(alert)               |                   | - timestamp:String|
+------------------+------------------+                   +-------------------+
                   |                           uses EnumMap<DisasterCategory,Set<CitizenObserver>>
                   | implements
                   v
             +-----------+
             | BDAlertSys |
             +-----------+
             | - registeredCitizens: Set<CitizenObserver>
             | - subscriptions: EnumMap<DisasterCategory,Set<CitizenObserver>>
             | + registerCitizen(...)
             | + subscribe(...)
             | + unsubscribe(...)
             | + publishAlert(...)
             +-----------+

2) Design pattern decision
- Chosen pattern: Observer (fits requirement: many-to-many publish/subscribe where "every citizen subscribed to that category must be notified automatically" and "citizens who are not subscribed must not receive the notification").
- Mapped requirement lines in assignment: subscription/notification behaviour appears at [Offline3.md](Offline3.md#L13-L19) and the numbered System Requirements items 1–7 at [Offline3.md](Offline3.md#L25-L53). Implementation & demo requirements (publish at least one of each category, show subscription updates) at [Offline3.md](Offline3.md#L61-L79).

3) Inheritance / implementation decisions
- `CitizenObserver` (interface) — a minimal Observer contract with `update(Alert)` and `displayReceivedNotifications()` to match requirement "Display the notifications received by each citizen" ([Offline3.md](Offline3.md#L51-L53)).
- `AlertPublisher` (interface) — Subject contract with methods for register/unregister/subscribe/unsubscribe/publishAlert. These map directly to System Requirements items 1–4 ([Offline3.md](Offline3.md#L25-L41)).
- `BDAlertSystem` (concrete subject) implements `AlertPublisher` and internally stores:
  - `registeredCitizens: Set<CitizenObserver>` — `HashSet` chosen for O(1) add/remove checks (satisfies requirement to register/unregister efficiently) mapped to requirement 1 ([Offline3.md](Offline3.md#L25-L27)).
  - `subscriptions: EnumMap<DisasterCategory, Set<CitizenObserver>>` — `EnumMap` keyed by `DisasterCategory` (enum) used for fast, type-safe lookups per disaster category; `Set` of observers (LinkedHashSet) preserves deterministic notification order and prevents duplicates. This directly implements requirement 2 (subscribe to categories) and requirement 5 (notify only subscribed) [Offline3.md](Offline3.md#L29-L31, [Offline3.md](Offline3.md#L43-L46)).
- `Citizen` stores `List<Alert> receivedNotifications` — `ArrayList` chosen to preserve and display received alerts in arrival order (requirement 7) [Offline3.md](Offline3.md#L51-L53).
- `DisasterCategory` implemented as an `enum` with values EARTHQUAKE, FLOOD, FIRE — matches the domain language in the specification ("earthquakes, floods, and fires") [Offline3.md](Offline3.md#L13-L13).

4) API / method design choices and rationale
- `subscribe(c, category)` ensures `registerCitizen(c)` is called if citizen not yet registered — maps to requirement that citizens can subscribe and must be registered first (requirements 1–3) [Offline3.md](Offline3.md#L25-L35).
- `publishAlert(Alert alert)` iterates only the set for `alert.getCategory()` to notify matching subscribers, ensuring requirement 5 (notify only subscribers) [Offline3.md](Offline3.md#L43-L46).
- Ensured newly subscribed citizens receive only future alerts by adding to subscription set at subscribe time and not replaying past alerts (requirement 6) [Offline3.md](Offline3.md#L47-L49).
- Alert payload fields (title, category, location, severity, safetyInstructions, timestamp) correspond exactly to requirement 4 (title, category, affected location, severity level, safety instructions) [Offline3.md](Offline3.md#L37-L41).

5) Data types chosen (summary)
- `DisasterCategory` : enum — expresses closed set of categories (line: "earthquakes, floods, and fires") [Offline3.md](Offline3.md#L13-L13).
- `subscriptions` : `EnumMap<DisasterCategory, Set<CitizenObserver>>` — O(1) category lookup; prevents null map entries; clear semantic mapping to categories (requirements 2 & 5) [Offline3.md](Offline3.md#L29-L31, [Offline3.md](Offline3.md#L43-L46)).
- `registeredCitizens` : `HashSet<CitizenObserver>` — prevents duplicates; O(1) checks (requirement 1) [Offline3.md](Offline3.md#L25-L27).
- `receivedNotifications` : `List<Alert>` (ArrayList) — preserves ordering for display (requirement 7) [Offline3.md](Offline3.md#L51-L53).
- `timestamp` : `String` produced from `LocalDateTime` — for human-friendly logs and stable ordering; maps to demo needs (display time in logs) [Offline3.md](Offline3.md#L61-L79) (demo requirements).

6) Mapping of implementation demo to assignment checks
- Multiple citizens & different subscriptions: implemented in demo `task1.Main` where citizens subscribe to different categories — matches Implementation requirement lines at [Offline3.md](Offline3.md#L67-L71).
- Publish at least one earthquake, flood, fire alert: demo publishes 3 initial alerts — matches [Offline3.md](Offline3.md#L73-L75).
- Show subscription updates and verify them by publishing another alert: demo unsubscribes/subscribes and publishes subsequent alerts — matches [Offline3.md](Offline3.md#L77-L79).

Notes / trade-offs
- Chose simple in-memory structures (EnumMap/HashSet/ArrayList) for clarity and to satisfy demonstration requirements; a production system would require persistence and asynchronous delivery.
- Deterministic subscriber order uses LinkedHashSet; if strict concurrency is required, replace collections with concurrent equivalents.

References (assignment specification)
- Background + category list: [Offline3.md](Offline3.md#L11-L19)
- System Requirements items 1–7: [Offline3.md](Offline3.md#L25-L53)
- Implementation & Demonstration Requirements (Observer choice & demo expectations): [Offline3.md](Offline3.md#L61-L79)
