package task1;

public class SystemTest {
    public static void main(String[] args) {
        BdAlertSystem sys = BdAlertSystem.getInstance();

        Subscriber mehedi = new Citizen("Mehedi");
        sys.registerCitizen(mehedi);
        Subscriber sami = new Citizen("Sami");
        sys.registerCitizen(sami);
        Subscriber sadman = new Citizen("Sadman");
        sys.registerCitizen(sadman);
        Subscriber tanvir = new Citizen("Tanvir");
        sys.registerCitizen(tanvir);
        System.out.println("[ Mehedi, Sami, Sadman, Tanvir registered ]");

        Subscriber rabbi = new Citizen("Rabbi");
        sys.addSubscriber(rabbi, Category.EARTHQUAKE);

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

        for (Subscriber sub : sys.getSubscribers()) {
            sub.displayReceivedAlerts();
        }

        sys.registerCitizen(rabbi);
        System.out.println("[ Rabbi registered ]");
        sys.addSubscriber(rabbi, Category.EARTHQUAKE);
        rabbi.displayReceivedAlerts();

        Alert al5 = new Alert.Builder(Category.EARTHQUAKE)
                .title("Massive Earthquake at Bansree Area!!")
                .location("Dhaka")
                .severityLevel(7)
                .instructions("Move immediately to open space.")
                .build();
        sys.sendAlert(al5);
        System.out.println(al5);
        rabbi.displayReceivedAlerts();

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
        tanvir.displayReceivedAlerts();

        sys.addSubscriber(tanvir, Category.EARTHQUAKE);
        System.out.println("[ Tanvir re-subscribed to EARTHQUAKE alerts ]");

        Alert al8 = new Alert.Builder(Category.EARTHQUAKE)
                .location("Sylhet")
                .severityLevel(6)
                .instructions("Move to open ground immediately.")
                .build();
        sys.sendAlert(al8);
        System.out.println(al8);
        tanvir.displayReceivedAlerts();

        for (Subscriber sub : sys.getSubscribers()) {
            sub.displayReceivedAlerts();
        }
    }
}
