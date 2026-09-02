/*

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


*/

interface Mediator {



}

interface Robot {

}

class PickerBot

public class Prac3 {
    
}
