
/**
 * 
 * Refactoring choices:
 * 
 * 1. The Home -> Room -> SmartDevice structure is implemented via Composite Pattern.
 * 2. The upgrades to the rooms and devices are implemented via Decorator Pattern.
 * 
 * 
 */

import java.util.*;

// The COMPONENT
interface SmartDevice {
    void activate();

    void deactivate();

    double getPowerUsage();

    String getStatus();

    // for polymorphically determining the type of device
    Class<?> getDeviceType();
}

// --- COMPONENT ---

// The LEAVES : The smart devices
abstract class AbstractSmartDevice implements SmartDevice {

    private boolean deviceOn = false;

    @Override
    public void activate() {
        this.deviceOn = true;
    }

    @Override
    public void deactivate() {
        this.deviceOn = false;
    }

    protected abstract double activePowerUsage();

    public double getPowerUsage() {
        return deviceOn ? activePowerUsage() : 0.0;
    }

    protected abstract String deviceLabel();

    public String getLabel() {
        return deviceLabel();
    }

    public String getStatus() {
        return getLabel() + ": " + (deviceOn ? "ON" : "OFF");
    }

    @Override
    public Class<?> getDeviceType() {
        return this.getClass();
    }

}

class SmartLight extends AbstractSmartDevice {

    @Override
    protected double activePowerUsage() {
        return 10.0;
    }

    @Override
    protected String deviceLabel() {
        return "Light";
    }
}

class SmartThermostat extends AbstractSmartDevice {

    @Override
    protected double activePowerUsage() {
        return 150.0;
    }

    @Override
    protected String deviceLabel() {
        return "Thermostat";
    }
}

class SmartSpeaker extends AbstractSmartDevice {

    @Override
    protected double activePowerUsage() {
        return 5.0;
    }

    @Override
    protected String deviceLabel() {
        return "Speaker";
    }
}

// ---- LEAVES ---

// The COMPOSITES : The rooms and the home
abstract class AbstractComposite implements SmartDevice {

    protected final String name;

    protected final List<SmartDevice> childrenDevices = new ArrayList<>();

    protected AbstractComposite(String name) {
        this.name = name;
    }

    @Override
    public void activate() {
        for (SmartDevice device : childrenDevices) {
            device.activate();
        }
    }

    @Override
    public void deactivate() {
        for (SmartDevice device : childrenDevices) {
            device.deactivate();
        }
    }

    @Override
    public double getPowerUsage() {
        double totalPowerUsage = 0.0;
        for (SmartDevice device : childrenDevices) {
            totalPowerUsage += device.getPowerUsage();
        }
        return totalPowerUsage;
    }

    // this will return the format [Room: <name>] or [Home: <name>]
    protected abstract String compositeLabel();

}

class Home extends AbstractComposite {

    public Home(String name) {
        super(name);
    }

    @Override
    protected String compositeLabel() {
        return "[Home " + name + "]";
    }

    @Override
    public Class<?> getDeviceType() {
        return Home.class;
    }

    // @Override
    void addRoom(SmartDevice room) {
        childrenDevices.add(room);
    }

    @Override
    public String getStatus() {

        StringBuilder status = new StringBuilder(compositeLabel() + " Status:\n");

        for (SmartDevice device : childrenDevices) {
            status.append(device.getStatus()).append("\n");
        }

        return status.toString();

    }
}

class Room extends AbstractComposite {

    public Room(String name) {
        super(name);
    }

    @Override
    protected String compositeLabel() {
        return "[Room " + name + "]";
    }

    @Override
    public Class<?> getDeviceType() {
        return Room.class;
    }

    // @Override
    void addDevice(SmartDevice device) {
        childrenDevices.add(device);
    }

    List<SmartDevice> getDevices() {
        return childrenDevices;
    }

    @Override
    public String getStatus() {
        StringBuilder status = new StringBuilder(compositeLabel() + " Status:\n");
        for (SmartDevice device : childrenDevices) {
            status.append("").append(device.getStatus()).append("\n");
        }
        return status.toString();
    }
}

// --- COMPOSITES ---

// The DECORATORS : upgrades to rooms and devices

abstract class AbstractDeviceDecorator implements SmartDevice {

    protected final SmartDevice wrappedDevice;

    protected AbstractDeviceDecorator(SmartDevice device) {
        this.wrappedDevice = device;
    }

    @Override
    public void activate() {
        wrappedDevice.activate();
    }

    @Override
    public void deactivate() {
        wrappedDevice.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return wrappedDevice.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return wrappedDevice.getStatus();
    }

    // the original device type is presrved, for example, a decorated SmartLight
    // will still return SmartLight,
    // and a decorated Room will stilll return Room
    @Override
    public Class<?> getDeviceType() {
        return wrappedDevice.getDeviceType();
    }
}

// --- Room and Device Level Decorators : AccessRestricted, TImerControlled,
// PowerThrottled ---

class AccessRestricted extends AbstractDeviceDecorator {

    private final int PIN;
    private boolean locked = true;

    public AccessRestricted(SmartDevice device, int PIN) {
        super(device);
        this.PIN = PIN;
    }

    public void unlock(int inputPIN) {
        if (inputPIN == PIN) {
            locked = false;
        }
    }

    @Override
    public void activate() {
        if (!locked) {
            wrappedDevice.activate();
        }
    }

    @Override
    public void deactivate() {
        if (!locked) {
            wrappedDevice.deactivate();
        }
    }

    // the wrappedDevice power report is allowed even if the decorated device is
    // locked

    @Override
    public String getStatus() {
        return wrappedDevice.getStatus() + (locked ? " [LOCKED]" : "");
    }
}

class TimerControlled extends AbstractDeviceDecorator {

    private final int timerDuration;
    private boolean timerActive = false;

    public TimerControlled(SmartDevice device, int timerDuration) {
        super(device);
        this.timerDuration = timerDuration;
    }

    @Override
    public void activate() {
        wrappedDevice.activate();
        timerActive = true;
    }

    @Override
    public void deactivate() {
        wrappedDevice.deactivate();
        timerActive = false;
    }

    public void simulateTimerExpiry() {
        if (timerActive) {
            wrappedDevice.deactivate();
            timerActive = false;
        }
    }

    @Override
    public String getStatus() {
        return wrappedDevice.getStatus()
                + (timerActive ? " [TIMER ACTIVE: auto-off in " + timerDuration + " seconds]" : "");
    }

}

class PowerThrottled extends AbstractDeviceDecorator {

    private final double maxPowerUsage;

    public PowerThrottled(SmartDevice device, double maxPowerUsage) {
        super(device);
        this.maxPowerUsage = maxPowerUsage;
    }

    @Override
    public double getPowerUsage() {
        double usage = wrappedDevice.getPowerUsage();
        return Math.min(usage, maxPowerUsage);
    }

    @Override
    public String getStatus() {
        return wrappedDevice.getStatus()
                + (wrappedDevice.getPowerUsage() > maxPowerUsage ? " [POWER THROTTLED: max " + maxPowerUsage + "W]"
                        : "");
    }
}

// --- Room Level Decorators : EcoMode, GuestMode ---

abstract class RoomEnhancement implements SmartDevice {

    protected final Room wrappedRoom;

    protected RoomEnhancement(Room room) {
        this.wrappedRoom = room;
    }

    @Override
    public void activate() {
        wrappedRoom.activate();
    }

    @Override
    public void deactivate() {
        wrappedRoom.deactivate();
    }

    @Override
    public double getPowerUsage() {
        return wrappedRoom.getPowerUsage();
    }

    @Override
    public String getStatus() {
        return wrappedRoom.getStatus();
    }

    @Override
    public Class<?> getDeviceType() {
        return wrappedRoom.getDeviceType();
    }

}

class EcoMode extends RoomEnhancement {

    private final double powerBudget;

    public EcoMode(Room room, double powerBudget) {
        super(room);
        this.powerBudget = powerBudget;
    }

    @Override
    public void activate() {

        wrappedRoom.activate();

        List<SmartDevice> roomDevices = wrappedRoom.getDevices();

        for (int i = roomDevices.size() - 1; i >= 0 && wrappedRoom.getPowerUsage() > powerBudget; i--) {
            roomDevices.get(i).deactivate();
        }

    }

    @Override
    public String getStatus() {
        return "[ECO: " + powerBudget + "W BUDGET]\n" + wrappedRoom.getStatus();
    }
}

class GuestMode extends RoomEnhancement {

    private final Set<Class<?>> allowedDevices;

    GuestMode(Room room, Set<Class<?>> allowedDevices) {
        super(room);
        this.allowedDevices = allowedDevices;
    }

    private final boolean isDeviceAllowed(SmartDevice device) {
        return allowedDevices.contains(device.getDeviceType());
    }

    @Override
    public void activate() {
        for (SmartDevice device : wrappedRoom.getDevices()) {
            if (isDeviceAllowed(device)) {
                device.activate();
            }
        }
    }

    @Override
    public void deactivate() {
        for (SmartDevice device : wrappedRoom.getDevices()) {
            if (isDeviceAllowed(device)) {
                device.deactivate();
            }
        }
    }

    @Override
    public double getPowerUsage() {
        double usage = 0.0;
        for (SmartDevice device : wrappedRoom.getDevices()) {
            if (isDeviceAllowed(device)) {
                usage += device.getPowerUsage();
            }
        }
        return usage;
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder("[GUEST MODE]\n").append(wrappedRoom.compositeLabel());
        for (SmartDevice device : wrappedRoom.getDevices()) {
            sb.append("").append(device.getStatus());
            if (!isDeviceAllowed(device)) {
                sb.append(" [guest-restricted]");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}

