
/*

**Duration: 30 Minutes**
**Problem: ICU Patient Monitoring Network**

A hospital is upgrading its Intensive Care Unit (ICU). Each bed is equipped with a `VitalsMonitor` that continuously reads the patient's Heart Rate and Oxygen levels.

When the `VitalsMonitor` detects a critical drop in oxygen, multiple independent hospital systems must react simultaneously:

* **Nurses' Station Dashboard:** Flashes a red warning on the central UI.
* **Doctor's Pager:** Sends an SMS to the on-call physician.
* **Automated Oxygen Valve:** Instantly increases the oxygen flow to the patient's mask.

**Important Behaviour:**
The `VitalsMonitor` must be completely decoupled from these external systems. If the hospital buys a new system next year (e.g., an Alarm Siren), it should be attachable without modifying the `VitalsMonitor` code. Furthermore, systems must be able to disconnect dynamically (e.g., when a doctor’s shift ends, their pager is disconnected from the monitor).

**Required Operations:**
Implement the `VitalsMonitor` and the reactive components. Provide operations for `attachSystem(...)`, `detachSystem(...)`, and `triggerCriticalAlert(...)`. Demonstrate a scenario where a doctor's pager is attached, a critical alert fires, the pager is detached, and another alert fires.


*/

// observer pattern

import java.util.*;

interface Sub { // maybe I should implement it as abstract class

    void putAlert(String s);

}

class NurseStation implements Sub {

    public void putAlert(String s) {
        System.out.println("Alert: " + s + "; Red light flashed");
    }

}

class DoctorPager implements Sub {

    private String name;

    public DoctorPager(String name) { this.name = name; }

    public void putAlert(String s) {
        System.out.println("Alert: " + s + "; " + name + " pager notification received");
    }
}

class AutomatedOxyValve implements Sub {

    public void putAlert(String s) {

        System.out.println("Alert: " + s + "; Oxygen flow increased");

    }
} 



interface MonitorSystem {

    void alert(String s);

    void addSub(Sub s);

    void removeSub(Sub s);

}

class VitalsMonitor implements MonitorSystem {

    private Set<Sub> subs;

    public VitalsMonitor() {
        subs = new HashSet<Sub>();
    }

    @Override
    public void addSub(Sub s) {
        subs.add(s);
    }

    @Override
    public void removeSub(Sub s) {
        subs.remove(s);
    }

    @Override
    public void alert(String s) {
        for(Sub ss : subs) {
            ss.putAlert(s);
        }
    }



}


public class Prac2 {
    
    public static void main(String[] args) {
        
        MonitorSystem sys = new VitalsMonitor();

        DoctorPager d1 = new DoctorPager("Sadab");
        DoctorPager d2 = new DoctorPager("Mahin");
        DoctorPager d3 = new DoctorPager("Adib");


        sys.addSub(new AutomatedOxyValve());
        sys.addSub(d1);
        sys.addSub(d2);    
        sys.addSub(d3);
        sys.addSub(new NurseStation());

        sys.alert("Critical COndition");

        System.out.println("Adib signs out");


        sys.removeSub(d3);

        sys.alert("Again critical");
        

    }


}
