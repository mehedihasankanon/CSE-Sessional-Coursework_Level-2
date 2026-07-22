In this template, the new keyword is hiding in plain sight on these lines in main:
// ❌ USES 'new' IN MAIN
Application app = new Application(factory);
Application darkApp = new Application(ThemeFactoryProvider.getFactory("Dark"));

To eliminate every single new from main, delegate the creation of the Application object to a static helper/factory method inside the Application class itself.
Step 1: Add a static method to Application
Add either a static create() or run() method inside your Application class:
class Application {

    private final Button    button;
    private final TextField textField;
    private final Dialog    dialog;

    public Application(GUIFactory factory) {
        this.button    = factory.createButton();
        this.textField = factory.createTextField();
        this.dialog    = factory.createDialog();
    }

    // 🚀 ADD THIS: Static factory method that hides 'new Application(...)'
    public static Application create(String theme) {
        GUIFactory factory = ThemeFactoryProvider.getFactory(theme);
        return new Application(factory);
    }

    // 🚀 OR ADD THIS: One-step runner method (creates AND runs)
    public static void run(String theme) {
        Application app = create(theme);
        app.renderUI();
    }

    public void renderUI() {
        System.out.println("Rendering the user interface:");
        button.render();
        textField.render();
        dialog.render();
        button.onClick();
    }
}

Step 2: Clean up main (Zero new keywords)
Now your main method is completely free of any new keyword:
public static void main(String[] args) {

    // WAY A: Using Application.create(...)
    Application app = Application.create("Light");
    app.renderUI();

    System.out.println();

    Application darkApp = Application.create("Dark");
    darkApp.renderUI();

    System.out.println();

    // WAY B: All-in-one line execution (even cleaner!)
    Application.run("HighContrast");
}

Also: Don't forget ThemeFactoryProvider
To stop anyone from instantiating the provider itself, remember to add a private constructor to ThemeFactoryProvider:
class ThemeFactoryProvider {

    // 🔒 Block 'new ThemeFactoryProvider()'
    private ThemeFactoryProvider() {}

    public static GUIFactory getFactory(String theme) {
        switch (theme.toLowerCase()) {
            case "light":        return new LightThemeFactory();
            case "dark":         return new DarkThemeFactory();
            case "highcontrast": return new HighContrastThemeFactory();
            default:
                throw new IllegalArgumentException("Unknown theme: " + theme);
        }
    }
}


The Builder pattern template has six different uses of new in main.
Here are the specific lines in main that need fixing:
// ❌ USES 'new' IN MAIN:
Director director = new Director();
HolidayPackageBuilder builder1 = new HolidayPackageBuilder();
HolidayPackageBuilder builder2 = new HolidayPackageBuilder();
BrochureBuilder builder3 = new BrochureBuilder();
HolidayPackageBuilder custom = new HolidayPackageBuilder();
Bicycle bike = new Bicycle.Builder()...

Step 1: Add static .create() or .builder() methods
To eliminate all new keywords in main, add static creator methods inside each class:
1. Inside Director
class Director {

    // 🚀 ADD THIS
    public static Director create() { return new Director(); }

    public void constructRelaxationPackage(Builder builder) { ... }
    public void constructAdventurePackage(Builder builder)  { ... }
}

2. Inside HolidayPackageBuilder and BrochureBuilder
class HolidayPackageBuilder implements Builder {

    // 🚀 ADD THIS
    public static HolidayPackageBuilder create() { return new HolidayPackageBuilder(); }
    
    // ... rest of the class ...
}

class BrochureBuilder implements Builder {

    // 🚀 ADD THIS
    public static BrochureBuilder create() { return new BrochureBuilder(); }

    // ... rest of the class ...
}

3. Inside Bicycle (for the fluent style)
Standard Java libraries (like Lombok or Jackson) handle fluent builders without new by adding a static builder() method directly on the target class:
class Bicycle {

    // ... existing fields and methods ...

    // 🚀 ADD THIS
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        // ... rest of the nested Builder class remains untouched ...
    }
}

Step 2: The Fully Cleaned main Method
With those static methods added, your main method now has zero new keywords:
public static void main(String[] args) {

    // 1. Director created without 'new'
    Director director = Director.create();

    System.out.println("===== Model 1, built by the director =====");
    HolidayPackageBuilder builder1 = HolidayPackageBuilder.create();
    director.constructRelaxationPackage(builder1);
    HolidayPackage relaxation = builder1.getResult();
    System.out.println(relaxation);

    System.out.println();
    System.out.println("===== Model 2, SAME construction process =====");
    HolidayPackageBuilder builder2 = HolidayPackageBuilder.create();
    director.constructAdventurePackage(builder2);
    System.out.println(builder2.getResult());

    System.out.println();
    System.out.println("===== Different REPRESENTATION, same director call =====");
    BrochureBuilder builder3 = BrochureBuilder.create();
    director.constructRelaxationPackage(builder3);
    System.out.println(builder3.getResult());

    System.out.println();
    System.out.println("===== Custom object, no director (client drives the steps) =====");
    HolidayPackageBuilder custom = HolidayPackageBuilder.create();
    custom.setFlight("Economy Flight");
    custom.setHotel("City Hostel");
    System.out.println(custom.getResult());

    System.out.println();
    System.out.println("===== OPTIONAL: fluent / method-chaining style =====");
    // 2. Fluent builder called directly on Bicycle class
    Bicycle bike = Bicycle.builder()
            .frame("Carbon Fiber Frame")
            .gears("12-Speed Gear")
            .tires("Off-road Grip Tires")
            .build();
    System.out.println(bike);
}

Cheat Sheet: Eliminating new Across All Creational Patterns
| Pattern | Where new goes | How main gets the object |
|---|---|---|
| Simple Factory | Hidden inside Factory.create(...) | Transport t = TransportFactory.create("Road"); |
| Factory Method | Hidden inside Creator.createProduct() | Logistics road = LogisticsFactory.getLogistics("Road"); |
| Abstract Factory | Hidden inside ThemeFactoryProvider.getFactory(...) | Application app = Application.create("Dark"); |
| Builder | Hidden inside Builder.create() & Class.builder() | HolidayPackageBuilder b = HolidayPackageBuilder.create();
Bicycle b = Bicycle.builder()...build(); |


