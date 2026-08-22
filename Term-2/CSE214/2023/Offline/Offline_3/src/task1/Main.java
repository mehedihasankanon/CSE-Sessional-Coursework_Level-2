package task1;

/**
 * Demonstration of Task 1: BD Alert Notification System
 * Pattern used: Observer Design Pattern
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("   TASK 1: DISASTER ALERT NOTIFICATION SYSTEM (BD ALERT) ");
        System.out.println("   Design Pattern: Observer Pattern");
        System.out.println("=======================================================\n");

        // 1. Initialize System (Subject)
        BDAlertSystem bdAlertSystem = new BDAlertSystem();

        // 2. Create Citizens (Observers)
        Citizen citizen1 = new Citizen("Rahim Ahmed", "NID-1001");
        Citizen citizen2 = new Citizen("Fatema Begum", "NID-1002");
        Citizen citizen3 = new Citizen("Tanvir Hossain", "NID-1003");
        Citizen citizen4 = new Citizen("Nusrat Jahan", "NID-1004");

        System.out.println("--- Step 1: Registering Citizens & Setting Initial Subscriptions ---");
        bdAlertSystem.registerCitizen(citizen1);
        bdAlertSystem.registerCitizen(citizen2);
        bdAlertSystem.registerCitizen(citizen3);
        bdAlertSystem.registerCitizen(citizen4);

        // Rahim: Earthquake & Flood
        bdAlertSystem.subscribe(citizen1, DisasterCategory.EARTHQUAKE);
        bdAlertSystem.subscribe(citizen1, DisasterCategory.FLOOD);

        // Fatema: Flood & Fire
        bdAlertSystem.subscribe(citizen2, DisasterCategory.FLOOD);
        bdAlertSystem.subscribe(citizen2, DisasterCategory.FIRE);

        // Tanvir: Earthquake & Fire
        bdAlertSystem.subscribe(citizen3, DisasterCategory.EARTHQUAKE);
        bdAlertSystem.subscribe(citizen3, DisasterCategory.FIRE);

        // Nusrat: Fire only
        bdAlertSystem.subscribe(citizen4, DisasterCategory.FIRE);

        System.out.println("\n--- Step 2: Publishing Initial Disaster Alerts ---");

        // 1. Earthquake Alert (Subscribers: Rahim, Tanvir)
        Alert earthquakeAlert = new Alert(
            "Magnitude 6.2 Tremor Detected",
            DisasterCategory.EARTHQUAKE,
            "Sylhet & Surrounding Districts",
            "HIGH",
            "Drop, Cover, and Hold On. Move away from tall buildings and windows."
        );
        bdAlertSystem.publishAlert(earthquakeAlert);

        // 2. Flood Alert (Subscribers: Rahim, Fatema)
        Alert floodAlert = new Alert(
            "Flash Flood Warning",
            DisasterCategory.FLOOD,
            "Sunamganj Low-lying Areas",
            "CRITICAL",
            "Move to higher ground immediately. Keep emergency kits ready."
        );
        bdAlertSystem.publishAlert(floodAlert);

        // 3. Fire Alert (Subscribers: Fatema, Tanvir, Nusrat)
        Alert fireAlert = new Alert(
            "Industrial Area Fire Outbreak",
            DisasterCategory.FIRE,
            "EPZ Zone, Chittagong",
            "MODERATE",
            "Evacuate the perimeter. Keep emergency exits clear for firefighters."
        );
        bdAlertSystem.publishAlert(fireAlert);

        System.out.println("\n--- Step 3: Modifying Subscriptions (Unsubscribing & Subscribing) ---");
        // Rahim unsubscribes from Flood, subscribes to Fire
        System.out.println("> Rahim unsubscribes from FLOOD and subscribes to FIRE...");
        bdAlertSystem.unsubscribe(citizen1, DisasterCategory.FLOOD);
        bdAlertSystem.subscribe(citizen1, DisasterCategory.FIRE);

        // Fatema unsubscribes from Fire
        System.out.println("> Fatema unsubscribes from FIRE...");
        bdAlertSystem.unsubscribe(citizen2, DisasterCategory.FIRE);

        // Register new citizen who only joins now
        System.out.println("> Registering a new citizen (Kamrul Islam) subscribing to FLOOD...");
        Citizen citizen5 = new Citizen("Kamrul Islam", "NID-1005");
        bdAlertSystem.subscribe(citizen5, DisasterCategory.FLOOD);

        System.out.println("\n--- Step 4: Publishing Subsequent Alerts to Verify Updated Subscriptions ---");

        // Subsequent Flood Alert (Subscribers now: only Kamrul)
        Alert floodAlert2 = new Alert(
            "River Erosion & Water Level Rise",
            DisasterCategory.FLOOD,
            "Kurigram Basin",
            "HIGH",
            "Avoid crossing riverbanks. Relocate livestock to embankments."
        );
        bdAlertSystem.publishAlert(floodAlert2);

        // Subsequent Fire Alert (Subscribers now: Rahim, Tanvir, Nusrat)
        Alert fireAlert2 = new Alert(
            "Marketplace Chemical Fire Hazard",
            DisasterCategory.FIRE,
            "Old Dhaka",
            "CRITICAL",
            "Wear gas masks or wet cloth over face. Evacuate immediately."
        );
        bdAlertSystem.publishAlert(fireAlert2);

        System.out.println("\n--- Step 5: Displaying Complete Notification Logs for Citizens ---");
        citizen1.displayReceivedNotifications();
        citizen2.displayReceivedNotifications();
        citizen3.displayReceivedNotifications();
        citizen4.displayReceivedNotifications();
        citizen5.displayReceivedNotifications();

        System.out.println("\n=======================================================");
        System.out.println("   TASK 1 DEMONSTRATION COMPLETED SUCCESSFULLY");
        System.out.println("=======================================================");
    }
}
