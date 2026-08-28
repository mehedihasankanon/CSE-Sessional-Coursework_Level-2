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

enum Condition {

    HIGH_BRIGHTNESS,
    CLOSE_BLINDS,
    TURN_ON_AC

}

interface Mediator {


}

class CentralHub implements Mediator {

}


// ===============

abstract class Device {

}

class LightSensor extends Device {
    
}

class AutomaticBlinds extends Device {


}

class AirConditioner extends Device {


}


public class B2Mediator {
    public static void main(String[] args) {
        
    }
}
