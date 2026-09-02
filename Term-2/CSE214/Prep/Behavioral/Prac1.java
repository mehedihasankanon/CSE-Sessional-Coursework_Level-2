
/*

You are thinking exactly like a seasoned exam-taker now. Process of elimination is a highly effective study strategy. If the professors just tested State and Strategy heavily, it is statistically very likely they will pivot to **Mediator, Observer, or Template Method** to ensure they cover the full curriculum.

I cannot predict the *exact* question, but I can perfectly replicate the "wall of text" style of your professors. They love wrapping simple patterns in heavy domain logic to test your architectural vision.

Here are three highly probable, exam-style problem statements. Treat these as your final boss battles.

---

### Prediction 1: The Template Method Pattern

**Duration: 30 Minutes**
**Problem: Smart City Data Pipeline**

A newly developed Smart City platform must continuously process telemetry data from thousands of IoT devices. Currently, the city processes data from **Traffic Cameras** and **Weather Sensors**. Every time data is ingested, it must pass through a strict 4-step Data Pipeline:

1. **Authentication:** The system verifies the incoming API key. (This process is identical for all devices).
2. **Extraction:** The raw data is read. Traffic cameras upload heavy Binary Image Streams, while Weather Sensors upload lightweight JSON text.
3. **Transformation:** The data is analyzed. The Traffic pipeline runs an AI model to count cars. The Weather pipeline calculates the average temperature over the last hour.
4. **Logging:** The system writes a standard success log to the central database with a timestamp. (Identical for all devices).

**Important Behaviour:**
To meet strict government compliance, the order of these four steps (Authenticate $\rightarrow$ Extract $\rightarrow$ Transform $\rightarrow$ Log) is absolute. Junior developers writing new pipelines (e.g., a future `PollutionSensorPipeline`) must be completely blocked from altering the execution sequence or bypassing the Authentication and Logging steps. They should only be allowed to provide the specific Extraction and Transformation logic.

**Required Operations:**
Design the pipeline architecture. Provide a `processTelemetry()` method that executes the workflow, and demonstrate how the `TrafficCameraPipeline` and `WeatherSensorPipeline` integrate into this framework.

---

### Prediction 2: The Observer Pattern

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

---

### Prediction 3: The Mediator Pattern

**Duration: 30 Minutes**
**Problem: Automated Warehouse Robot Fleet**

An Amazon-style fulfillment center uses three types of automated robots to process customer orders: `PickerBot`, `PackerBot`, and `LoaderBot`.

Processing an order follows a specific chain reaction:

1. When an order arrives, the `PickerBot` retrieves the item from the shelf.
2. Once retrieved, the `PackerBot` places the item into a cardboard box.
3. Once boxed, the `LoaderBot` drives the box into the delivery truck.

**Important Behaviour:**
To prevent a chaotic, tangled web of software dependencies, **robots are strictly forbidden from communicating with one another.** A `PickerBot` does not know what a `PackerBot` is.

Instead, all robots report exclusively to a central `WarehouseController`. When a robot finishes its task, it simply sends a generic message (e.g., "TASK_COMPLETE") to the Controller. The Controller holds the business logic to determine which robot should be activated next.

**Required Operations:**
Design the system ensuring absolute decoupling between the robot classes. Implement the `WarehouseController` and the robots. Provide operations for a robot to report its status and for the Controller to orchestrate a full sequence from picking to loading.

---

Which of these three predicted problems do you want to attempt to write the skeleton code for first?
*/

/*

**Problem: Smart City Data Pipeline**

A newly developed Smart City platform must continuously process telemetry data 
from thousands of IoT devices. Currently, the city processes data from **Traffic Cameras** 
and **Weather Sensors**. Every time data is ingested, it must pass through a strict 4-step Data Pipeline:

1. **Authentication:** The system verifies the incoming API key. (This process is identical for all devices).
2. **Extraction:** The raw data is read. Traffic cameras upload heavy 
Binary Image Streams, while Weather Sensors upload lightweight JSON text.
3. **Transformation:** The data is analyzed. The Traffic pipeline runs an 
AI model to count cars. The Weather pipeline calculates the average temperature over the last hour.
4. **Logging:** The system writes a standard success log to the central 
database with a timestamp. (Identical for all devices).

**Important Behaviour:**
To meet strict government compliance, the order of these four steps (Authenticate 
$\rightarrow$ Extract $\rightarrow$ Transform $\rightarrow$ Log) is absolute. Junior developers writing new pipelines 
(e.g., a future `PollutionSensorPipeline`) must be completely blocked from altering the execution sequence or 
bypassing the Authentication and Logging steps. They should only be allowed to provide the specific Extraction and Transformation logic.

**Required Operations:**
Design the pipeline architecture. Provide a `processTelemetry()` method that executes 
the workflow, and demonstrate how the `TrafficCameraPipeline` and `WeatherSensorPipeline` integrate into this framework.



*/

import java.time.LocalDateTime;

class Data {
    String api;
    String rawData;
    public Data(String api, String rawData) {
        this.api = api;
        this.rawData = rawData;
    }

    
}

abstract class Template {

    public final void execute(Data d) {

        authenticate(d);
        extract(d);
        transform(d);
        log(d);        

    }

    private void authenticate(Data d) {
        System.out.println("OK: "+ d.api);
    }
    protected abstract void extract(Data d);
    protected abstract void transform(Data d);
    private void log(Data d) {
        System.out.print(LocalDateTime.now().toString() + ": " + d.toString());
    }

}

class PollutionSensorPipeline extends Template {

    protected void extract(Data d) {

        System.out.println("Data read from: " + d.toString());

    }

    protected void transform(Data d) {

        System.out.println("Data transformed from " + d.rawData + " and cars counted.");

    }

}




public class Prac1 {
    
    Data d1 = new Data("aonfaifj2984qkfwj", "JSON");
    Data d2 = new Data("aofh0e98rq3rjnaij", "BinaryImageStream");

    Template te = new PollutionSensorPipeline();

    


}


/*

import java.time.LocalDateTime;

// 1. Data DTO
class Data {
    String api;
    String rawData;
    String sourceName;

    public Data(String api, String rawData, String sourceName) {
        this.api = api;
        this.rawData = rawData;
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "[" + sourceName + " Data]";
    }
}

// 2. The Abstract Template
abstract class TelemetryPipeline {

    // THE CRITICAL FIX: 'final' blocks juniors from overriding the pipeline sequence
    public final void processTelemetry(Data d) {
        System.out.println("--- Starting pipeline for " + d.sourceName + " ---");
        authenticate(d);
        extract(d);
        transform(d);
        log(d);        
        System.out.println();
    }

    private void authenticate(Data d) {
        System.out.println("1. Auth: Verified API Key -> " + d.api);
    }

    // Hooks for subclasses
    protected abstract void extract(Data d);
    protected abstract void transform(Data d);

    private void log(Data d) {
        System.out.println("4. Log: " + LocalDateTime.now().toString() + " - Successfully processed " + d.toString());
    }
}

// 3. Concrete Pipeline 1
class TrafficCameraPipeline extends TelemetryPipeline {
    @Override
    protected void extract(Data d) {
        System.out.println("2. Extract: Reading heavy " + d.rawData + "...");
    }

    @Override
    protected void transform(Data d) {
        System.out.println("3. Transform: AI Model ran. Counted 42 cars on the highway.");
    }
}

// 4. Concrete Pipeline 2
class WeatherSensorPipeline extends TelemetryPipeline {
    @Override
    protected void extract(Data d) {
        System.out.println("2. Extract: Parsing lightweight " + d.rawData + "...");
    }

    @Override
    protected void transform(Data d) {
        System.out.println("3. Transform: Calculated average temperature: 32°C.");
    }
}

// 5. Demonstration
public class SmartCityApp {
    public static void main(String[] args) {
        Data trafficData = new Data("API_TRAF_99X", "BinaryImageStream", "TrafficCam-01");
        Data weatherData = new Data("API_WEAT_77Y", "JSON", "WeatherSensor-North");

        TelemetryPipeline trafficPipeline = new TrafficCameraPipeline();
        TelemetryPipeline weatherPipeline = new WeatherSensorPipeline();

        trafficPipeline.processTelemetry(trafficData);
        weatherPipeline.processTelemetry(weatherData);
    }
}

*/