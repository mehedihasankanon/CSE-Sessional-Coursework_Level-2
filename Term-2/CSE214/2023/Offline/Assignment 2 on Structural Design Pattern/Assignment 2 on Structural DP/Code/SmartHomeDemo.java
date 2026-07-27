import java.util.*;

/* =====================================================================
 *  SmartHome.java  —  Structural refactor (Composite + Decorator)
 *
 *  Two GoF structural patterns from refactoring.guru carry the whole design:
 *
 *    COMPOSITE  — lets a single device, a room full of devices, and the
 *                 whole home be driven through ONE interface (SmartDevice),
 *                 so a room's cascade/aggregation is a plain polymorphic
 *                 loop with NO type checks.
 *
 *    DECORATOR  — adds device upgrades (AccessRestricted, TimerControlled,
 *                 PowerThrottled) and room-level enhancements (EcoMode,
 *                 GuestMode) by *wrapping*, so upgrades stack freely and in
 *                 an order-sensitive way, without editing the wrapped class.
 * ===================================================================== */

/* ---------------------------------------------------------------------
 *  COMPONENT  (Composite pattern: the "Component"; Decorator pattern:
 *              the "Component" the decorators also implement)
 *
 *  This single interface is the contract requirement #1 (Uniform handling):
 *  leaves, composites, and every wrapper are all just a SmartDevice.
 * ------------------------------------------------------------------- */
interface SmartDevice {
    void activate();

    void deactivate();

    double getPowerUsage(); // watts drawn right now (0 when inactive)

    String getStatus();

    // The *underlying* kind of device (SmartLight/SmartThermostat/...).
    // Leaves report their own class; decorators delegate down to the leaf.
    // This lets GuestMode ask "what are you, really?" polymorphically
    // instead of doing `instanceof` chains through wrappers.
    Class<?> deviceType();
}

/*
 * ---------------------------------------------------------------------
 * LEAVES (Composite pattern: the "Leaf")
 *
 * The spaghetti version copy-pasted on/off/power/status into three
 * unrelated classes. Here one abstract base holds the shared behaviour;
 * each concrete device only declares its wattage + label. (DRY / OCP:
 * a fourth device type is a ~4-line subclass, touching nothing else.)
 * -------------------------------------------------------------------
 */
abstract class AbstractDevice implements SmartDevice {
    protected boolean on = false;

    protected abstract double activePower(); // watts when running

    protected abstract String label(); // human name for status

    @Override
    public void activate() {
        on = true;
    }

    @Override
    public void deactivate() {
        on = false;
    }

    @Override
    public double getPowerUsage() {
        return on ? activePower() : 0.0;
    }

    @Override
    public String getStatus() {
        return label() + ": " + (on ? "ON" : "OFF");
    }

    @Override
    public Class<?> deviceType() {
        return getClass();
    }
}

class SmartLight extends AbstractDevice {
    @Override
    protected double activePower() {
        return 10.0;
    }

    @Override
    protected String label() {
        return "Light";
    }
}

class SmartThermostat extends AbstractDevice {
    @Override
    protected double activePower() {
        return 150.0;
    }

    @Override
    protected String label() {
        return "Thermostat";
    }
}

class SmartSpeaker extends AbstractDevice {
    @Override
    protected double activePower() {
        return 5.0;
    }

    @Override
    protected String label() {
        return "Speaker";
    }
}

/*
 * ---------------------------------------------------------------------
 * COMPOSITE (Composite pattern: the "Composite")
 *
 * Room and Home are the same shape: a named node holding SmartDevice
 * children, forwarding every operation to them. Because the children are
 * typed as SmartDevice, these loops NEVER ask whether a child is plain,
 * upgraded, a room, or a home (requirement #3: no special-casing).
 * -------------------------------------------------------------------
 */
abstract class CompositeDevice implements SmartDevice {
    protected final String name;
    protected final List<SmartDevice> children = new ArrayList<>();

    protected CompositeDevice(String name) {
        this.name = name;
    }

    @Override
    public void activate() {
        for (SmartDevice c : children)
            c.activate();
    }

    @Override
    public void deactivate() {
        for (SmartDevice c : children)
            c.deactivate();
    }

    @Override
    public double getPowerUsage() {
        double total = 0;
        for (SmartDevice c : children)
            total += c.getPowerUsage();
        return total;
    }

    @Override
    public Class<?> deviceType() {
        return getClass();
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder(header());
        for (SmartDevice c : children)
            sb.append("\n  ").append(c.getStatus());
        return sb.toString();
    }

    protected abstract String header();
}

class Room extends CompositeDevice {
    Room(String name) {
        super(name);
    }

    void addDevice(SmartDevice d) {
        children.add(d);
    }

    void removeDevice(SmartDevice d) {
        children.remove(d);
    }

    // Exposed (in insertion order) so room-level enhancements can shed or
    // filter individual children. Callers still see them as SmartDevice.
    List<SmartDevice> getDevices() {
        return children;
    }

    @Override
    protected String header() {
        return "[" + name + "]";
    }
}

class Home extends CompositeDevice {
    Home(String name) {
        super(name);
    }

    // Accepts SmartDevice (not Room) so a *decorated* room is addable too.
    void addRoom(SmartDevice room) {
        children.add(room);
    }

    @Override
    protected String header() {
        return "=== " + name + " ===";
    }
}

/*
 * ---------------------------------------------------------------------
 * DECORATOR BASE (Decorator pattern: the "Base Decorator")
 *
 * Holds one wrapped SmartDevice and, by default, forwards everything to
 * it. Concrete decorators override only the one or two methods they
 * actually change. Because it *is* a SmartDevice and *wraps* a
 * SmartDevice, wrappers nest arbitrarily (requirement #2: composability).
 * -------------------------------------------------------------------
 */
abstract class DeviceDecorator implements SmartDevice {
    protected final SmartDevice inner;

    protected DeviceDecorator(SmartDevice inner) {
        this.inner = inner;
    }

    @Override
    public void activate() {
        inner.activate();
    }

    @Override
    public void deactivate() {
        inner.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return inner.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return inner.getStatus();
    }

    @Override
    public Class<?> deviceType() {
        return inner.deviceType();
    }
}

/*
 * AccessRestricted — PIN gate on control. Locking blocks activate/
 * deactivate but NOT power reporting (a device already running keeps
 * drawing power). Works on a device OR, for free, a whole Room.
 */
class AccessRestricted extends DeviceDecorator {
    private final int pin;
    private boolean locked = true;

    AccessRestricted(SmartDevice inner, int pin) {
        super(inner);
        this.pin = pin;
    }

    void unlock(int attempt) {
        if (attempt == pin)
            locked = false;
    }

    @Override
    public void activate() {
        if (!locked)
            inner.activate();
    }

    @Override
    public void deactivate() {
        if (!locked)
            inner.deactivate();
    }
    // getPowerUsage inherited (delegates) -> locked-but-running still reports.

    @Override
    public String getStatus() {
        return inner.getStatus() + (locked ? " [LOCKED]" : "");
    }
}

/*
 * TimerControlled — auto-off countdown. simulateTimerExpiry() stands in
 * for the countdown firing; a manual deactivate cancels it.
 */
class TimerControlled extends DeviceDecorator {
    private final int seconds;
    private boolean timerRunning = false;

    TimerControlled(SmartDevice inner, int seconds) {
        super(inner);
        this.seconds = seconds;
    }

    @Override
    public void activate() {
        inner.activate();
        timerRunning = true;
    }

    @Override
    public void deactivate() {
        inner.deactivate();
        timerRunning = false;
    }

    void simulateTimerExpiry() {
        if (timerRunning) {
            inner.deactivate();
            timerRunning = false;
        }
    }

    @Override
    public String getStatus() {
        return inner.getStatus() + (timerRunning ? " (auto-off in " + seconds + "s)" : "");
    }
}

/*
 * PowerThrottled — caps the *reported* draw at a ceiling. A real
 * reduction on the aggregate this device contributes, not a room budget.
 */
class PowerThrottled extends DeviceDecorator {
    private final double cap;

    PowerThrottled(SmartDevice inner, double cap) {
        super(inner);
        this.cap = cap;
    }

    @Override
    public double getPowerUsage() {
        return Math.min(inner.getPowerUsage(), cap);
    }

    @Override
    public String getStatus() {
        String s = inner.getStatus();
        if (inner.getPowerUsage() > cap)
            s += " [throttled to " + cap + "W]";
        return s;
    }
}

/*
 * ---------------------------------------------------------------------
 * ROOM-LEVEL DECORATORS (Decorator pattern, applied to a Room)
 *
 * EcoMode and GuestMode are premium features that only make sense on an
 * aggregate. Their constructors take a concrete Room, which is the
 * compile-time guard behind requirement / test #13: you physically
 * cannot write `new EcoMode(new SmartLight(), 100)`.
 * -------------------------------------------------------------------
 */
abstract class RoomEnhancement implements SmartDevice {
    protected final Room room;

    protected RoomEnhancement(Room room) {
        this.room = room;
    }

    @Override
    public void activate() {
        room.activate();
    }

    @Override
    public void deactivate() {
        room.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return room.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return room.getStatus();
    }

    @Override
    public Class<?> deviceType() {
        return room.deviceType();
    }
}

/*
 * EcoMode — enforces a total budget on the aggregate. On activation it
 * turns everything on, then sheds the most-recently-added devices (reverse
 * insertion order) until the real total fits. It never throttles a single
 * device down — that is PowerThrottled's job.
 */
class EcoMode extends RoomEnhancement {
    private final double budget;

    EcoMode(Room room, double budget) {
        super(room);
        this.budget = budget;
    }

    @Override
    public void activate() {
        room.activate();
        List<SmartDevice> devices = room.getDevices();
        for (int i = devices.size() - 1; i >= 0 && room.getPowerUsage() > budget; i--) {
            devices.get(i).deactivate(); // shed newest first
        }
    }
    // getPowerUsage inherited: reports the room's *real* post-shed total.

    @Override
    public String getStatus() {
        return "[ECO: " + budget + "W budget]\n" + room.getStatus();
    }
}

/*
 * GuestMode — only the allowed device *types* respond to activation, and
 * only their consumption is reported. The type test is polymorphic
 * (deviceType() delegates through any wrappers), so a locked/timed/
 * throttled light is still recognised as a light.
 */
class GuestMode extends RoomEnhancement {
    private final Set<Class<?>> allowed;

    GuestMode(Room room, Set<Class<?>> allowed) {
        super(room);
        this.allowed = allowed;
    }

    private boolean isAllowed(SmartDevice d) {
        return allowed.contains(d.deviceType());
    }

    @Override
    public void activate() {
        for (SmartDevice d : room.getDevices())
            if (isAllowed(d))
                d.activate(); // disallowed types silently skipped
    }

    @Override
    public double getPowerUsage() {
        double total = 0;
        for (SmartDevice d : room.getDevices())
            if (isAllowed(d))
                total += d.getPowerUsage();
        return total;
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder("[GUEST MODE]\n").append(room.getStatus());
        for (SmartDevice d : room.getDevices())
            if (!isAllowed(d))
                sb.append("\n  ").append(d.getStatus()).append(" [guest-restricted]");
        return sb.toString();
    }
}

/*
 * =====================================================================
 * DEMO (requirement #4: demonstrate order sensitivity concretely)
 * Not used by the test runner; run `java SmartHome.java` to see it.
 * =====================================================================
 */
public class SmartHomeDemo {
    public static void main(String[] args) {
        System.out.println("=== ORDER SENSITIVITY: throttle-then-eco  vs  raw-eco ===\n");

        // (A) Throttle the thermostat to 80W BEFORE it enters the EcoMode room.
        Room a = new Room("Throttled-first");
        a.addDevice(new SmartLight()); // 10W
        a.addDevice(new SmartLight()); // 10W
        a.addDevice(new PowerThrottled(new SmartThermostat(), 80)); // 80W (was 150)
        SmartDevice ecoA = new EcoMode(a, 100);
        ecoA.activate();
        System.out.println("Throttled(80) then Eco(100): total = " + ecoA.getPowerUsage()
                + "W  (10+10+80 = 100 fits, nothing shed)");

        // (B) Same room, but the thermostat is raw when EcoMode runs.
        Room b = new Room("Eco-only");
        b.addDevice(new SmartLight()); // 10W
        b.addDevice(new SmartLight()); // 10W
        b.addDevice(new SmartThermostat()); // 150W
        SmartDevice ecoB = new EcoMode(b, 100);
        ecoB.activate();
        System.out.println("Raw then Eco(100):           total = " + ecoB.getPowerUsage()
                + "W  (10+10+150 > 100, newest (thermostat) shed)");

        System.out.println("\nSame parts, different wrap order => different result. "
                + "That is the Decorator's order sensitivity.");
    }
}
