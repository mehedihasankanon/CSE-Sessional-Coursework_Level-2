import java.util.*;
import java.util.concurrent.*;

interface Grid {

    void updatePowerLevel(int mw);

    void connectDist(Distr d);

    void removeDist(Distr d);

}

class PowerGrid implements Grid {

    private Set<Distr> dist;

    public PowerGrid() {
        // dist = new HashSet<>();
        dist = new CopyOnWriteArraySet<>();
    }

    public void connectDist(Distr d) {
        dist.add(d);
    }

    public void removeDist(Distr d) {

        dist.remove(d);
    }

    public void updatePowerLevel(int mw) {

        for (Distr d : dist) {

            d.supply(mw, this);
        }

    }

    public void triggerTotalBlackout() {

        updatePowerLevel(-1);

        dist.clear();

        System.out.println("All districts removed");

    }

}

interface Distr {
    void supply(int mw, Grid g);
}

class HospitalDist implements Distr {

    @Override
    public void supply(int mw, Grid g) {

        if(mw == -1) {
            System.out.println("Total Blackout");
            return;
        }

        System.out.println("Hospital operations stable.");

        if (mw < 50) {
            System.out.println("CRITICAL: Hospital switching to backup generators!");
        }
    }
}

class IndustrialDist implements Distr {

    @Override
    public void supply(int mw, Grid g) {

        if(mw == -1) {
            System.out.println("Total Blackout");
            return;
        }

        if (mw >= 100) {
            System.out.println("Factory lines running at full capacity.");
        }

        else {
            System.out.println("Power too low! Factories shutting down.");

            // -> removal logic lekha lagbe eikhane!
            g.removeDist(this);
        }
    }

}

class ResidentialDist implements Distr {

    @Override
    public void supply(int mw, Grid g) {

        if(mw == -1) {
            System.out.println("Total Blackout");
            return;
        }

        if (mw >= 80) {
            System.out.println("Neighborhood power normal.");
        }

        else {
            System.out.println("Neighborhood experiencing rolling brownouts.");
        }

    }

}


