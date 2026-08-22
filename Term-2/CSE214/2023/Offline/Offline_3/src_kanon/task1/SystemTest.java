package task1;

public class SystemTest {
    public static void main(String[] args) {
        BdAlertSystem sys = BdAlertSystem.getInstance();

        // Req 1: Register citizens
        Subscriber mehedi = new Citizen("Mehedi");
        Subscriber sami = new Citizen("Sami");
        Subscriber sadman = new Citizen("Sadman");

        // Req 2: Subscribe to categories
        sys.addSubscriber(mehedi, Category.EARTHQUAKE);
        sys.addSubscriber(mehedi, Category.FIRE);
        sys.addSubscriber(sami, Category.FLOOD);
        sys.addSubscriber(sadman, Category.EARTHQUAKE);

        // Req 4 & 5: Publish and notify ONLY relevant citizens
        System.out.println("--- 1. INITIAL BROADCASTS ---");
        sys.sendAlert(Alert.generateAlert(Category.EARTHQUAKE)); 
        sys.sendAlert(Alert.generateAlert(Category.FLOOD)); 

        // Req 3: Update/Unsubscribe at any time
        System.out.println("\n--- 2. SUBSCRIPTION UPDATES ---");
        sys.removeSubscriber(mehedi, Category.EARTHQUAKE); 
        
        // Req 6: Newly subscribed citizen receives ONLY future alerts
        Subscriber raihan = new Citizen("Raihan");
        sys.addSubscriber(raihan, Category.EARTHQUAKE); 

        System.out.println("\n--- 3. SECONDARY BROADCASTS ---");
        sys.sendAlert(Alert.generateAlert(Category.EARTHQUAKE)); 

        // Req 7: Display notifications
        System.out.println("\n--- 4. CITIZEN LOGS ---");
        mehedi.displayReceivedAlerts(); // 1 EQ (missed the second)
        sami.displayReceivedAlerts();   // 1 Flood
        sadman.displayReceivedAlerts(); // 2 EQs
        raihan.displayReceivedAlerts(); // 1 EQ (missed the first)
    }
}