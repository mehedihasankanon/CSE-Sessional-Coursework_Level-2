/*

King’s Landing has a giant message board where ravens deliver scrolls like:
• “Enemy spotted near the river”
• “Winter supplies running low”
• “Ships seen in the east”
Your task is to build a RavenBoard system with appropriate design pattern where mul-
tiple groups can receive new messages: Commander, Scouts, Supply Team etc. and act
based on it. (example: Scouts print “Dispatch riders!”, Supply Team prints “Update
inventory!”). However, groups can subscribe/unsubscribe at runtime (like Scouts leaving
the board room).
Demonstrate your implementation using 3 messages, some subscribe/unsubscribe and
some response prints.

*/

// dafuq? subscriber or mediator? -_-


import java.util.ArrayList;
import java.util.List;

// 1. The Subscriber Interface
interface Subscriber {
    void update(String message);
}

// 2. Concrete Subscribers
class Commander implements Subscriber {
    @Override
    public void update(String message) {
        if (message.contains("Enemy") || message.contains("Ships")) {
            System.out.println("Commander: Prepare the defenses! Sound the alarm!");
        } else {
            System.out.println("Commander: Acknowledged.");
        }
    }
}

class Scouts implements Subscriber {
    @Override
    public void update(String message) {
        if (message.contains("Enemy")) {
            System.out.println("Scouts: Dispatch riders to track their movements!");
        } else {
            System.out.println("Scouts: Keep an eye on the perimeter.");
        }
    }
}

class SupplyTeam implements Subscriber {
    @Override
    public void update(String message) {
        if (message.contains("Winter") || message.contains("supplies")) {
            System.out.println("Supply Team: Update inventory! Ration the grain!");
        } else {
            System.out.println("Supply Team: Logged raven message.");
        }
    }
}

// 3. The Subject / Publisher
class RavenBoard {
    private List<Subscriber> subscribers = new ArrayList<>();

    public void subscribe(Subscriber sub) {
        subscribers.add(sub);
        System.out.println("[Log] A group has joined the board room.");
    }

    public void unsubscribe(Subscriber sub) {
        subscribers.remove(sub);
        System.out.println("[Log] A group has left the board room.");
    }

    public void receiveScroll(String message) {
        System.out.println("\n=== RAVEN ARRIVES: \"" + message + "\" ===");
        for (Subscriber sub : subscribers) {
            sub.update(message);
        }
    }
}

// 4. Demonstration
public class A2Subscriber {
    public static void main(String[] args) {
        RavenBoard board = new RavenBoard();

        Subscriber commander = new Commander();
        Subscriber scouts = new Scouts();
        Subscriber supplyTeam = new SupplyTeam();

        // Subscribe groups
        board.subscribe(commander);
        board.subscribe(scouts);
        board.subscribe(supplyTeam);

        // Message 1
        board.receiveScroll("Enemy spotted near the river");

        // Message 2
        board.receiveScroll("Winter supplies running low");

        // Unsubscribe Scouts (they leave the room)
        board.unsubscribe(scouts);

        // Message 3 (Scouts won't react to this one)
        board.receiveScroll("Ships seen in the east");
    }
}