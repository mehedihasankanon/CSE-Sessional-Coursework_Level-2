/*

A tech company offers a monthly brain-support subscription for patients with severe
brain injury, and the service must remain active for the patient to function normally.
The subscription has three tiers: Common, Plus, and Lux. Each tier provides a
coverage radius from regional server towers that determines whether the patient stays
responsive during travel: in Common, the safe range is 0–10 km, while in Plus and Lux
it is 0–50 km. Implement a small simulator with five operations:
• travelCheck(km)
• activateLux(hours)
• setMood(calm|exhausted|happy)
• promote() : Takes common to Plus, Plus to Lux, Lux to Lux (no change)
• demote() : Takes Lux to Plus, Plus to Common, Common to Common (no change)
When travelCheck(km) is called, the system should print whether the patient is STA-
BLE or UNSTABLE at that distance under the current tier; if the distance exceeds
the tier’s safe range, the patient becomes UNSTABLE (the patient blacks out), and the
log should print alert message which asks to bring the patient back into coverage. When
in coverage after which the simulation continues by calling the travelCheck(0km) and
the patient regains consciousness.
The activateLux(hours) operation should temporarily switch the service into Lux for
the specified duration (you may use sleep()), then automatically return to whichever tier
(Common or Plus) was active right before Lux was activated. Finally, setMood(...)
should only work while Lux is active; otherwise it prints “Mood control unavailable.”
Demonstrate your design using print logs showing a few distance checks, some promotion-
demotions, one Lux activation and return to the earlier tier and mood changes.

*/

/**
 * 
 * Solution: State Pattern
 * 
 * 
 * 
 */

// class Context {
// private Subscription sub;

// public Context(Subscription sub) {
// this.sub = sub;
// }

// public void travelCheck(int dist) {

// if (dist > sub.getSafeDist()) {
// System.out.println("Not safe");
// return;
// }

// System.out.println("Sfafe");

// }

// public void activateLux(int hrs) {

// }

// public void setMood(Mood m) {

// if (sub instanceof Lux) {
// ((Lux)this.sub).setMood(m);
// System.out.println(m.toString() + " mood set");
// }

// System.out.println("Not in Lux mode");

// }

// public void promote() {
// this.sub = this.sub.getNext();
// }

// public void demote() {
// this.sub = this.sub.getPrev();
// }
// }

// interface Subscription {

// public int getSafeDist();

// public Subscription getPrev();

// public Subscription getNext();

// }

// class Common implements Subscription {

// private final int safeDist = 10;

// public int getSafeDist() {
// return safeDist;
// }

// public Subscription getPrev() {
// return this;
// }

// public Subscription getNext() {
// return new Plus();
// }
// }

// class Plus implements Subscription {

// private final int safeDist = 50;

// public int getSafeDist() {
// return safeDist;
// }

// public Subscription getPrev() {
// return new Common();
// }

// public Subscription getNext() {
// return new Lux();
// }
// }

// class Lux implements Subscription {

// private final int safeDist = 50;

// private Mood mood = null;

// public int getSafeDist() {
// return safeDist;
// }

// public Subscription getPrev() {
// return new Plus();
// }

// public Subscription getNext() {
// return this;
// }

// public void setMood(Mood mood) {
// this.mood = mood;
// }
// }

// enum Mood {
// CALM, EXHAUSTED, HAPPY
// }

// =================================================

class Context {
    private Subscription sub;
    private boolean isUnconscious;

    public Context(Subscription sub) {
        this.sub = sub;
        this.isUnconscious = false;
    }

    public void travelCheck(int km) {
        // Handle unconscious state first
        if (isUnconscious) {
            if (km == 0) {
                isUnconscious = false;
                System.out
                        .println("[LOG] Patient back in coverage (0km). Consciousness regained. Simulation continues.");
            } else {
                System.out.println("[ALERT] Patient is currently UNSTABLE (blacked out)! Bring patient back to 0km.");
            }
            return;
        }

        // Handle conscious distance checks
        if (km > sub.getSafeDist()) {
            isUnconscious = true;
            System.out.println("[LOG] Distance " + km + "km exceeds " + sub.getClass().getSimpleName() + " safe range ("
                    + sub.getSafeDist() + "km).");
            System.out.println(
                    "[ALERT] Patient is UNSTABLE (Blacked out). Bring patient back into coverage immediately!");
        } else {
            System.out.println(
                    "[LOG] Distance " + km + "km is STABLE under " + sub.getClass().getSimpleName() + " tier.");
        }
    }

    public void activateLux(int hours) {
        System.out.println("\n[LOG] --- Activating temporary Lux for " + hours + " hours ---");
        Subscription previousTier = this.sub; // Save previous state
        this.sub = new Lux();

        try {
            // Simulate time passing (using 100ms per hour for fast simulation)
            Thread.sleep(hours * 100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.sub = previousTier; // Revert state
        System.out.println(
                "[LOG] --- Temporary Lux expired. Returned to " + this.sub.getClass().getSimpleName() + " ---");
    }

    public void setMood(Mood m) {
        // Pure delegation. No instanceof!
        this.sub.setMood(m);
    }

    public void promote() {
        this.sub = this.sub.getNext();
        System.out.println("[LOG] Promoted to: " + this.sub.getClass().getSimpleName());
    }

    public void demote() {
        this.sub = this.sub.getPrev();
        System.out.println("[LOG] Demoted to: " + this.sub.getClass().getSimpleName());
    }
}

interface Subscription {
    int getSafeDist();

    Subscription getPrev();

    Subscription getNext();

    // Default rejection behavior for tiers that don't support mood control
    // void setMood(Mood mood) {
    default void setMood(Mood mood) {
        System.out.println("[LOG] Mood control unavailable.");
    }
}

class Common implements Subscription {
    private final int safeDist = 10;

    public int getSafeDist() {
        return safeDist;
    }

    public Subscription getPrev() {
        return this;
    }

    public Subscription getNext() {
        return new Plus();
    }
}

class Plus implements Subscription {
    private final int safeDist = 50;

    public int getSafeDist() {
        return safeDist;
    }

    public Subscription getPrev() {
        return new Common();
    }

    public Subscription getNext() {
        return new Lux();
    }
}

class Lux implements Subscription {
    private final int safeDist = 50;
    private Mood mood = null;

    public int getSafeDist() {
        return safeDist;
    }

    public Subscription getPrev() {
        return new Plus();
    }

    public Subscription getNext() {
        return this;
    }

    @Override
    public void setMood(Mood mood) {
        this.mood = mood;
        System.out.println("[LOG] " + mood.toString() + " mood successfully set.");
    }
}

enum Mood {
    CALM, EXHAUSTED, HAPPY
}

public class A1State {
    public static void main(String[] args) {
        Context patientSystem = new Context(new Common());

        patientSystem.travelCheck(5); // STABLE
        patientSystem.travelCheck(15); // UNSTABLE, blacks out
        patientSystem.travelCheck(20); // Still blacked out
        patientSystem.travelCheck(0); // Regains consciousness

        System.out.println("\n--- Upgrading Tiers ---");
        patientSystem.promote(); // Plus
        patientSystem.travelCheck(40); // STABLE under Plus

        System.out.println("\n--- Testing Mood Control ---");
        patientSystem.setMood(Mood.CALM); // Unavailable in Plus

        patientSystem.activateLux(3); // Temporarily jumps to Lux
        patientSystem.setMood(Mood.HAPPY); // Succeeds while in Lux!
        patientSystem.setMood(Mood.CALM); // Fails, because simulation returned to Plus!
    }
}