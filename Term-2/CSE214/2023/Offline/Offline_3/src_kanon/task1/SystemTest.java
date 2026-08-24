package task1;

/**
 * Behavioural pattern used: Observer (topic-based Publish/Subscribe variant).
 * BdAlertSystem is the Subject/Publisher; Citizen is a ConcreteObserver that
 * implements the Subscriber (Observer) role. Citizens subscribe per Category
 * "topic" and are notified only when a matching Alert is published.
 */
public class SystemTest {
    public static void main(String[] args) {
        BdAlertSystem sys = BdAlertSystem.getInstance();

        System.out.println("===================== 1. REGISTERING CITIZENS =====================");
        Subscriber mehedi = new Citizen("Mehedi");
        sys.registerCitizen(mehedi);
        Subscriber sami = new Citizen("Sami");
        sys.registerCitizen(sami);
        Subscriber sadman = new Citizen("Sadman");
        sys.registerCitizen(sadman);
        Subscriber tanvir = new Citizen("Tanvir");
        sys.registerCitizen(tanvir);
        System.out.println("[ Mehedi, Sami, Sadman, Tanvir registered ]");

        System.out.println("\n===================== EDGE CASE: SUBSCRIBING BEFORE REGISTRATION =====================");
        Subscriber nadia = new Citizen("Nadia");
        sys.addSubscriber(nadia, Category.EARTHQUAKE); // Nadia isn't registered yet -> must be rejected

        System.out.println("\n===================== 2. SUBSCRIBING CITIZENS TO CATEGORIES (DIFFERENT SUBSCRIPTIONS) =====================");
        sys.addSubscriber(mehedi, Category.EARTHQUAKE);
        System.out.println("[ Mehedi subscribed to EARTHQUAKE alerts ]");
        sys.addSubscriber(mehedi, Category.FIRE);
        System.out.println("[ Mehedi subscribed to FIRE alerts ]");
        sys.addSubscriber(sami, Category.FLOOD);
        System.out.println("[ Sami subscribed to FLOOD alerts ]");
        sys.addSubscriber(sadman, Category.EARTHQUAKE);
        System.out.println("[ Sadman subscribed to EARTHQUAKE alerts ]");
        sys.addSubscriber(tanvir, Category.FLOOD);
        System.out.println("[ Tanvir subscribed to FLOOD alerts ]");
        sys.addSubscriber(tanvir, Category.FIRE);
        System.out.println("[ Tanvir subscribed to FIRE alerts ]");
        sys.addSubscriber(tanvir, Category.EARTHQUAKE);
        System.out.println("[ Tanvir subscribed to EARTHQUAKE alerts ]");

        System.out.println("\n===================== 4 & 5. PUBLISHING ALERTS (EARTHQUAKE, FLOOD, FIRE) =====================");
        Alert al1 = new Alert.Builder(Category.EARTHQUAKE)
                .location("Dhaka")
                .severityLevel(8)
                .instructions("Move immediately to open space.")
                .build();
        sys.sendAlert(al1);
        System.out.println(al1);

        Alert al2 = new Alert.Builder(Category.EARTHQUAKE)
                .location("Dhaka")
                .severityLevel(6)
                .instructions("Move immediately to open space.")
                .build();
        sys.sendAlert(al2);
        System.out.println(al2);

        Alert al3 = new Alert.Builder(Category.FIRE)
                .location("Dhaka")
                .severityLevel(4)
                .instructions("Evacuate immediately.")
                .build();
        sys.sendAlert(al3);
        System.out.println(al3);

        Alert al4 = new Alert.Builder(Category.FLOOD)
                .location("Dhaka")
                .severityLevel(9)
                .instructions("Evacuate immediately to higher ground.")
                .build();
        sys.sendAlert(al4);
        System.out.println(al4);

        System.out.println("\n===================== 7. DISPLAYING ALERTS RECEIVED BY EACH CITIZEN =====================");
        for (Subscriber sub : sys.getSubscribers()) {
            sub.displayReceivedAlerts();
        }

        System.out.println("\n===================== 6. A NEWLY SUBSCRIBED CITIZEN RECEIVES ONLY FUTURE ALERTS =====================");
        sys.registerCitizen(nadia);
        System.out.println("[ Nadia registered ]");
        sys.addSubscriber(nadia, Category.EARTHQUAKE);
        System.out.println("[ Nadia subscribed to EARTHQUAKE alerts -- after " + al1.getTitle() + " and " + al2.getTitle() + " were already sent ]");
        System.out.println("Nadia's alerts right after subscribing (must be empty -- no back-fill):");
        nadia.displayReceivedAlerts();

        Alert al5 = new Alert.Builder(Category.EARTHQUAKE)
                .title("Massive Earthquake at Bansree Area!!")
                .location("Dhaka")
                .severityLevel(7)
                .instructions("Move immediately to open space.")
                .build();
        sys.sendAlert(al5);
        System.out.println(al5);
        System.out.println("Nadia's alerts after this new EARTHQUAKE alert (must contain only this one):");
        nadia.displayReceivedAlerts();

        System.out.println("\n===================== 3. UPDATING SUBSCRIPTIONS: UNSUBSCRIBE + VERIFY =====================");
        sys.removeSubscriber(tanvir, Category.EARTHQUAKE);
        System.out.println("[ Tanvir unsubscribed from EARTHQUAKE alerts ]");

        Alert al6 = new Alert.Builder(Category.FIRE)
                .location("Dhaka")
                .severityLevel(5)
                .instructions("Evacuate immediately.")
                .build();
        sys.sendAlert(al6);
        System.out.println(al6);

        Alert al7 = new Alert.Builder(Category.EARTHQUAKE)
                .location("Chittagong")
                .severityLevel(5)
                .instructions("Stay away from damaged structures.")
                .build();
        sys.sendAlert(al7);
        System.out.println(al7);
        System.out.println("Tanvir's alerts (must NOT contain the Chittagong EARTHQUAKE alert sent after unsubscribing):");
        tanvir.displayReceivedAlerts();

        System.out.println("\n===================== 3. UPDATING SUBSCRIPTIONS: RE-SUBSCRIBE + VERIFY =====================");
        sys.addSubscriber(tanvir, Category.EARTHQUAKE);
        System.out.println("[ Tanvir re-subscribed to EARTHQUAKE alerts ]");

        Alert al8 = new Alert.Builder(Category.EARTHQUAKE)
                .location("Sylhet")
                .severityLevel(6)
                .instructions("Move to open ground immediately.")
                .build();
        sys.sendAlert(al8);
        System.out.println(al8);
        System.out.println("Tanvir's alerts (must now include the Sylhet alert, but still not the Chittagong one):");
        tanvir.displayReceivedAlerts();

        System.out.println("\n===================== FINAL STATE: ALL CITIZENS' RECEIVED ALERTS =====================");
        for (Subscriber sub : sys.getSubscribers()) {
            sub.displayReceivedAlerts();
        }
    }
}
