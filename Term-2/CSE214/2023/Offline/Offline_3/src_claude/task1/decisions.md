# Task 1: BD Alert Notification System — Design Decisions

Source: `CSE-214_Offline-3_Formatted.pdf`, page 1–2 ("Task 1: Disaster Alert Notification System").

## 1. Pattern choice: Observer

**PDF anchor:** page 1, Background — "Whenever a new alert is published, every citizen
subscribed to that category must be notified automatically. Citizens who are not
subscribed to the category must not receive the notification." Page 2 also explicitly
instructs: "Identify the behavioural design pattern that best represents the relationship
between the alert categories and subscribed citizens."

This is a textbook one-to-many, push-based notification dependency: one publisher
(`BDAlertSystem`), many dependents (`Citizen`s) that must react automatically whenever
the publisher's state changes (a new `Alert`). The Observer pattern is the standard fit
because:
- The subject does not need to know the concrete type of its observers, only that they
  implement `update(Alert)` — this satisfies req. 5 ("notify only the citizens
  subscribed to the relevant category") without the subject hard-coding citizen logic.
- Subscription is dynamic (`subscribe`/`unsubscribe`), matching req. 3 ("update
  subscriptions or unsubscribe from a category at any time").

## 2. Class diagram (ASCII)

```
                      <<interface>>
                       AlertPublisher
        ------------------------------------------
        + subscribe(CitizenObserver, DisasterCategory)
        + unsubscribe(CitizenObserver, DisasterCategory)
        + publishAlert(Alert)
                        ^
                        |  implements
                        |
                  BDAlertSystem
        ------------------------------------------
        - subscribersByCategory : Map<DisasterCategory, List<CitizenObserver>>
        - registeredCitizens : List<Citizen>
        ------------------------------------------
        + registerCitizen(Citizen)
        + subscribe(...)
        + unsubscribe(...)
        + publishAlert(Alert)
                        |
                        | notifies (1..*)
                        v
                 <<interface>>
                 CitizenObserver
        ------------------------------------------
        + update(Alert)
        + getObserverName() : String
                        ^
                        |  implements
                        |
                     Citizen
        ------------------------------------------
        - citizenId : String
        - name : String
        - receivedAlerts : List<Alert>
        ------------------------------------------
        + update(Alert)
        + displayNotifications()

                     Alert  (data / value object, referenced by both sides)
        ------------------------------------------
        - title : String
        - category : DisasterCategory
        - affectedLocation : String
        - severityLevel : String
        - safetyInstructions : String

               <<enum>> DisasterCategory
        ------------------------------------------
          EARTHQUAKE | FLOOD | FIRE
```

Relationship summary: `BDAlertSystem` (Subject) *notifies* `CitizenObserver`
(Observer role) instances; `Citizen` is the ConcreteObserver; `Alert` is the payload
passed at notification time; `DisasterCategory` is the discriminator used to route a
published `Alert` to the correct subset of observers.

## 3. Inheritance / interface decisions

- **`AlertPublisher` (interface) implemented by `BDAlertSystem`.**
  PDF anchor: page 1, reqs. 1–2, 4–5 ("Register citizens", "Subscribe a citizen...",
  "Publish an alert...", "Notify only the citizens subscribed..."). These four
  requirements are exactly the Subject-side responsibilities of Observer, so they were
  grouped into one `AlertPublisher` contract rather than exposed only as concrete
  methods on `BDAlertSystem`. Using an interface (rather than making `BDAlertSystem`
  a concrete class with no contract) keeps the Subject role substitutable — a grading
  harness or future extension (e.g. an `SmsAlertSystem`) can depend on `AlertPublisher`
  instead of the concrete class, which is the entire point of applying a design pattern
  rather than hand-rolling ad-hoc pub/sub.
  `registerCitizen` was **not** put on the interface: registration ("Register citizens
  in the system", req. 1) is bookkeeping for the demo/roster, not part of the
  Subject/Observer notification contract, so it stays a concrete method on
  `BDAlertSystem`.

- **`CitizenObserver` (interface) implemented by `Citizen`.**
  PDF anchor: page 1, req. 7 ("Display the notifications received by each citizen").
  Only `update(Alert)` is required by the Subject to do its job; `getObserverName()`
  was added purely for readable console demonstration output (page 2: "Demonstrate
  multiple citizens with different subscriptions") and is not part of the pattern's
  essential contract, but is small enough to justify keeping on the interface rather
  than downcasting to `Citizen` everywhere.

- **`Citizen implements CitizenObserver` rather than extending an abstract class.**
  There is only one behavioral variation point (what happens when an alert arrives),
  and Java single inheritance would block any future need for `Citizen` to extend
  something else (e.g. a shared `Person` base across other offline tasks). An interface
  keeps the coupling minimal — exactly what Observer prescribes (Subject depends on an
  abstract Observer, not a concrete class).

## 4. Data structure / type decisions

- **`Map<DisasterCategory, List<CitizenObserver>>` (`EnumMap`) inside `BDAlertSystem`.**
  PDF anchor: page 1, reqs. 2 and 5 ("Subscribe a citizen to one or more disaster
  categories" / "Notify only the citizens subscribed to the relevant category"). Since
  subscriptions are inherently per-category, a map keyed by category avoids scanning
  every citizen's subscription set on every publish (`publishAlert` becomes a single
  `O(k)` lookup + iteration over that category's subscriber list, `k` = subscribers to
  that category, rather than `O(n)` over all citizens with a category check per
  citizen). `EnumMap` was chosen over `HashMap` because the key domain
  (`DisasterCategory`) is a fixed, small enum — `EnumMap` is faster and more compact for
  enum keys and documents that the key space is closed.

- **`DisasterCategory` as an `enum`, not a `String`.**
  PDF anchor: page 1, Background — "alerts for earthquakes, floods, and fires" is a
  closed, fixed set of categories (also implementation reqs, page 2: "Publish at least
  one earthquake, one flood, and one fire alert"). An enum gives compile-time
  exhaustiveness (the `EnumMap` constructor loop over `DisasterCategory.values()` can't
  silently miss a category) and prevents typo bugs like `"Earthquake"` vs
  `"EARTHQUAKE"` that a raw `String` field would allow.

- **`List<Alert> receivedAlerts` inside `Citizen`, append-only.**
  PDF anchor: page 1, req. 7 ("Display the notifications received by each citizen").
  Order of receipt matters for a human-readable notification history, so an
  insertion-ordered `List` (`ArrayList`) was used rather than a `Set` — duplicate alerts
  (two different alerts with the same content) are legitimate and should both be shown,
  which a `Set` with content-based equality could collapse.

- **`Alert` is an immutable value object (`final` fields, no setters).**
  PDF anchor: page 1, req. 4 ("Publish an alert containing the title, category,
  affected location, severity level, and safety instructions") — the alert's content is
  fixed at the moment it is published and is only ever read afterward (displayed to
  citizens, req. 7). Making it immutable prevents a bug class where one citizen's
  stored reference to an alert could be mutated and silently change another citizen's
  notification history, since all subscribed observers receive and store the *same*
  `Alert` object reference.

## 5. Requirement 6 — "newly subscribed citizen receives only future alerts"

**PDF anchor:** page 1, req. 6. This falls out of the design for free rather than
needing special-case code: `subscribersByCategory` only ever contains observers that
have called `subscribe`. A citizen who subscribes *after* `publishAlert` was already
called for past alerts was never in the list at the time those past alerts were
iterated, so it structurally cannot have received them — there is no replay/history
mechanism in `BDAlertSystem`, and none was added, satisfying the requirement by
omission rather than by an explicit "skip old alerts" check. This is demonstrated in
`Main.java` by registering `Nusrat` and subscribing her to `FLOOD` only after three
alerts have already been published.
