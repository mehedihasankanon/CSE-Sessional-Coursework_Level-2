package task1;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TASK 1: BD ALERT - DISASTER ALERT NOTIFICATION SYSTEM ===\n");

        BDAlertSystem bdAlert = new BDAlertSystem();

        // 1. Register citizens
        Citizen rahim = new Citizen("C001", "Rahim");
        Citizen karim = new Citizen("C002", "Karim");
        Citizen sadia = new Citizen("C003", "Sadia");
        bdAlert.registerCitizen(rahim);
        bdAlert.registerCitizen(karim);
        bdAlert.registerCitizen(sadia);
        System.out.println();

        // 2. Subscribe citizens to different categories
        bdAlert.subscribe(rahim, DisasterCategory.EARTHQUAKE);
        bdAlert.subscribe(rahim, DisasterCategory.FIRE);
        bdAlert.subscribe(karim, DisasterCategory.FLOOD);
        bdAlert.subscribe(sadia, DisasterCategory.EARTHQUAKE);
        bdAlert.subscribe(sadia, DisasterCategory.FLOOD);
        bdAlert.subscribe(sadia, DisasterCategory.FIRE);

        // 3 & 4. Publish one alert per category
        bdAlert.publishAlert(new Alert("6.2 Magnitude Earthquake", DisasterCategory.EARTHQUAKE,
                "Chittagong", "High", "Move to open ground, avoid buildings."));

        bdAlert.publishAlert(new Alert("River Flood Warning", DisasterCategory.FLOOD,
                "Sylhet", "Medium", "Move valuables to higher ground."));

        bdAlert.publishAlert(new Alert("Market Fire Outbreak", DisasterCategory.FIRE,
                "Dhaka", "Critical", "Evacuate immediately, do not use elevators."));

        // 5. Update subscriptions: Karim unsubscribes from FLOOD, subscribes to FIRE
        System.out.println("\n--- Updating subscriptions ---");
        bdAlert.unsubscribe(karim, DisasterCategory.FLOOD);
        bdAlert.subscribe(karim, DisasterCategory.FIRE);

        // 6. Newly subscribed citizen after alerts were already published
        Citizen nusrat = new Citizen("C004", "Nusrat");
        bdAlert.registerCitizen(nusrat);
        bdAlert.subscribe(nusrat, DisasterCategory.FLOOD);

        // Publish another alert to verify updated subscriptions
        bdAlert.publishAlert(new Alert("Flash Flood Alert", DisasterCategory.FLOOD,
                "Sunamganj", "High", "Avoid low-lying areas near riverbanks."));

        bdAlert.publishAlert(new Alert("Residential Fire", DisasterCategory.FIRE,
                "Khulna", "Medium", "Keep exits clear, call fire service."));

        // 7. Display notifications received by each citizen
        System.out.println("\n--- Notification history ---");
        rahim.displayNotifications();
        karim.displayNotifications();
        sadia.displayNotifications();
        nusrat.displayNotifications();
    }
}
