/*

Subsection: B2 Time: 20 minutes
You are developing a Smart Home Automation Hub. You have various devices: a Light
Sensor, Automatic Blinds, and an Air Conditioner.
To keep the system organized, the devices should not talk to each other directly. Instead,
they all report to the Central Hub.
• When the Light Sensor detects “High Brightness,” it notifies the Hub. The Hub
then tells the Blinds to close.
• When the Blinds close, they notify the Hub. The Hub then tells the Air Conditioner
to turn on because the room will get stuffy.
Task: Choose the appropriate design pattern to solve this problem and implement a
minimal demonstration.

*/

// pattern -> looks like classic mediator :3

// enum Condition {

//     HIGH_BRIGHTNESS,
//     CLOSE_BLINDS,
//     TURN_ON_AC

// }

// interface Mediator {

//     public void action(Condition cd, Device d);

// }

// class CentralHub implements Mediator {



//     public void action (Condition cd, Device d) {

//         switch (cd) {
//             case Condition.HIGH_BRIGHTNESS:

                
        
//             default:
//                 break;
//         }
//     }
// }

// // ===============

// abstract class Device {

//     protected Mediator med;

//     public Device(Mediator m) {
//         med = m;
//     }

// }

// class LightSensor extends Device {

//     public LightSensor(Mediator m) {
//         super(m);
//     }

//     public void signalBrightLight() {

//         med.action(Condition.HIGH_BRIGHTNESS, this);
//     }

// }

// class AutomaticBlinds extends Device {

//     public AutomaticBlinds(Mediator m) {
//         super(m);
//     }

//     public void signalBlindsClosed() {

//         med.action(Condition.CLOSE_BLINDS, this);
//     }

// }

// class AirConditioner extends Device {

//     public AirConditioner(Mediator m) {
//         super(m);
//     }

//     public void signalAcOn() {

//         med.action(Condition.TURN_ON_AC, this);
//     }

// }

// public class B2Mediator {
//     public static void main(String[] args) {

//     }
// }


// 1. Mediator Interface
interface Mediator {
    void notify(Device sender, String event);
}

// 2. Concrete Mediator
class CentralHub implements Mediator {
    private LightSensor lightSensor;
    private AutomaticBlinds blinds;
    private AirConditioner ac;

    // The Hub needs references to the devices to control them
    public void setDevices(LightSensor ls, AutomaticBlinds ab, AirConditioner ac) {
        this.lightSensor = ls;
        this.blinds = ab;
        this.ac = ac;
    }

    @Override
    public void notify(Device sender, String event) {
        if (sender == lightSensor && event.equals("HIGH_BRIGHTNESS")) {
            System.out.println("Hub: High brightness detected. Instructing Blinds to close.");
            blinds.close();
        } else if (sender == blinds && event.equals("CLOSED")) {
            System.out.println("Hub: Blinds are closed. Instructing AC to turn on.");
            ac.turnOn();
        }
    }
}

// 3. Abstract Colleague
abstract class Device {
    protected Mediator med;

    public Device(Mediator m) {
        this.med = m;
    }
}

// 4. Concrete Colleagues
class LightSensor extends Device {
    public LightSensor(Mediator m) { super(m); }

    public void detectLight() {
        System.out.println("LightSensor: Detected HIGH_BRIGHTNESS.");
        med.notify(this, "HIGH_BRIGHTNESS");
    }
}

class AutomaticBlinds extends Device {
    public AutomaticBlinds(Mediator m) { super(m); }

    public void close() {
        System.out.println("AutomaticBlinds: Closing down.");
        // Notify the hub that the action is complete
        med.notify(this, "CLOSED"); 
    }
}

class AirConditioner extends Device {
    public AirConditioner(Mediator m) { super(m); }

    public void turnOn() {
        System.out.println("AirConditioner: Turning on to cool the room.");
    }
}

// 5. Demonstration
public class B2Mediator {
    public static void main(String[] args) {
        CentralHub hub = new CentralHub();

        LightSensor sensor = new LightSensor(hub);
        AutomaticBlinds blinds = new AutomaticBlinds(hub);
        AirConditioner ac = new AirConditioner(hub);

        // Register devices with the Hub
        hub.setDevices(sensor, blinds, ac);

        System.out.println("--- System Simulation Starting ---");
        // Trigger the chain reaction
        sensor.detectLight(); 
    }
}