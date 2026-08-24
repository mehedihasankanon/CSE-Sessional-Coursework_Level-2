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

        sys.addSubscriber(mehedi, Category.EARTHQUAKE);
        System.out.println("\n[ Mehedi subscribed to EARTHQUAKE alerts ]\n");
        sys.addSubscriber(mehedi, Category.FIRE);
        System.out.println("\n[ Mehedi subscribed to FIRE alerts ]\n");
        sys.addSubscriber(sami, Category.FLOOD);
        System.out.println("\n[ Sami subscribed to FLOOD alerts ]\n");
        sys.addSubscriber(sadman, Category.EARTHQUAKE);
        System.out.println("\n[ Sadman subscribed to EARTHQUAKE alerts ]\n");
        sys.addSubscriber(tanvir, Category.FLOOD);
        System.out.println("\n[ Tanvir subscribed to FLOOD alerts ]\n");
        sys.addSubscriber(tanvir, Category.FIRE);
        System.out.println("\n[ Tanvir subscribed to FIRE alerts ]\n");
        sys.addSubscriber(tanvir, Category.EARTHQUAKE);
        System.out.println("\n[ Tanvir subscribed to EARTHQUAKE alerts ]\n");

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
        System.out.println();

        sys.removeSubscriber(tanvir, Category.EARTHQUAKE);
        System.out.println("\n[ Tanvir unsubscribed from EARTHQUAKE alerts ]\n");

        Alert al5 = new Alert.Builder(Category.EARTHQUAKE)
                .title("Massive Earthquake at Bansree Area!!")
                .location("Dhaka")
                .severityLevel(7)
                .instructions("Move immediately to open space.")
                .build();

        sys.sendAlert(al5);
        System.out.println(al5);

        mehedi.displayReceivedAlerts();
        tanvir.displayReceivedAlerts();

        Alert al6 = new Alert.Builder(Category.FIRE)
                .location("Dhaka")
                .severityLevel(5)
                .instructions("Evacuate immediately.")
                .build();
        sys.sendAlert(al6);
        System.out.println(al6);

        for (Subscriber sub : sys.getSubscribers()) {
            sub.displayReceivedAlerts();
        }
    }
}