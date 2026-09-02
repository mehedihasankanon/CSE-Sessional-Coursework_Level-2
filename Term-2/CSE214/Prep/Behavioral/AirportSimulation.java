// mediator pattern

// interface Mediator {

//     boolean askClearance(String fl);
// }

// class ATCTower implements Mediator {



//     public boolean askClearance(String fl) {
//         return true;
//     }
// }

// abstract class Flight {

//     protected Mediator m;

//     abstract void tryLand();
// }

// class Biman01 extends Flight {

//     public void tryLand() {

//         m.

//     }
// }

// class Emirates99 extends Flight {


// }


import java.util.LinkedList;
import java.util.Queue;

// 1. The Mediator Interface
interface ATCMediator {
    void requestLanding(Flight flight);
    void notifyTakeoff(Flight flight);
}

// 2. The Abstract Colleague
abstract class Flight {
    protected ATCMediator atc;
    protected String callsign;

    public Flight(ATCMediator atc, String callsign) {
        this.atc = atc;
        this.callsign = callsign;
    }

    public String getCallsign() {
        return callsign;
    }

    // Actions directed by the Mediator
    public abstract void land();
    public abstract void holdPattern();
    public abstract void clearForTakeoff();

    // Actions triggered by the pilot (calls the mediator)
    public void askToLand() {
        System.out.println("\n[" + callsign + "] Requesting clearance to land...");
        atc.requestLanding(this);
    }

    public void depart() {
        System.out.println("\n[" + callsign + "] Rolling down runway and taking off...");
        atc.notifyTakeoff(this);
    }
}

// 3. Concrete Colleagues
class CommercialFlight extends Flight {
    public CommercialFlight(ATCMediator atc, String callsign) {
        super(atc, callsign);
    }

    @Override
    public void land() {
        System.out.println(">>> [" + callsign + "] Touchdown! Occupying the runway.");
    }

    @Override
    public void holdPattern() {
        System.out.println(">>> [" + callsign + "] Circling airspace over Dhaka. Waiting for clearance.");
    }

    @Override
    public void clearForTakeoff() {
        System.out.println(">>> [" + callsign + "] Cleared to depart.");
    }
}

// 4. The Concrete Mediator (Holds system state & coordinates)
class ShahjalalATCTower implements ATCMediator {
    private boolean runwayClear = true;
    private Flight currentOnRunway = null;
    private Queue<Flight> landingQueue = new LinkedList<>();

    @Override
    public void requestLanding(Flight flight) {
        if (runwayClear) {
            runwayClear = false;
            currentOnRunway = flight;
            System.out.println("[TOWER] Runway 14/32 is clear. " + flight.getCallsign() + " cleared to land.");
            flight.land();
        } else {
            System.out.println("[TOWER] Runway OCCUPIED by " + currentOnRunway.getCallsign() 
                    + "! Denying landing to " + flight.getCallsign() + ".");
            landingQueue.add(flight);
            flight.holdPattern();
        }
    }

    @Override
    public void notifyTakeoff(Flight flight) {
        System.out.println("[TOWER] " + flight.getCallsign() + " has departed. Runway 14/32 is now VACANT.");
        runwayClear = true;
        currentOnRunway = null;

        // Immediately check if another flight was waiting
        if (!landingQueue.isEmpty()) {
            Flight nextFlight = landingQueue.poll();
            System.out.println("[TOWER] Alerting next flight in queue: " + nextFlight.getCallsign());
            requestLanding(nextFlight);
        }
    }
}

// 5. Demonstration
public class AirportSimulation {
    public static void main(String[] args) {
        ATCMediator dacTower = new ShahjalalATCTower();

        Flight emirates99 = new CommercialFlight(dacTower, "Emirates-99");
        Flight biman01 = new CommercialFlight(dacTower, "Biman-01");
        Flight airIndia20 = new CommercialFlight(dacTower, "AirIndia-20");

        // Emirates lands first (Runway is free)
        emirates99.askToLand();

        // Biman tries to land while Emirates is on the runway
        biman01.askToLand();

        // AirIndia also tries to land
        airIndia20.askToLand();

        // Emirates departs -> Tower automatically clears Biman to land
        emirates99.depart();

        // Biman finishes its business and departs -> Tower clears AirIndia
        biman01.depart();
    }
}