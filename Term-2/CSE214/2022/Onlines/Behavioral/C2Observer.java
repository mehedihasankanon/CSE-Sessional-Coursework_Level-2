
/* 

Subsection: C2 Time: 25 minutes
You are building a financial dashboard for a day-trading firm. The traders need to see
real-time changes in stock prices across various distinct widgets on their screen.
The core of the system is a StockData feed. Whenever the price of a specific stock
changes, several unrelated components need to update immediately:
4. A Ticker Tape widget needs to scroll the new price.
5. A Graph widget needs to plot the new data point.
6. A Buy/Sell Bot needs to evaluate if the new price triggers an automated trade.
You need a design where the StockData feed doesn’t know the specifics of these widgets
but can notify all of them automatically whenever a price update occurs. Also there
should be provision of adding or removing widgets.
Task: Choose the appropriate design pattern to solve this problem and demonstrate with
minimal code.

*/

//  observer pattern.

// + mediator flavour?

/*

nope. pure observer.

Mediators are for many-to-many communication where components talk back and forth to 
each other through the hub. Here, the widgets don't care about each other, and they 
don't talk back to the StockData. It is a strict one-way broadcast from a single source 
of truth to many listeners. That is pure Observer.

*/


import java.util.ArrayList;
import java.util.List;

// 1. The Observer Interface (The Listeners)
interface WidgetObserver {
    void update(String stockSymbol, double newPrice);
}

// 2. Concrete Observers (The Widgets)
class TickerTape implements WidgetObserver {
    @Override
    public void update(String stockSymbol, double newPrice) {
        System.out.println("Ticker Tape: Scrolling -> " + stockSymbol + " @ $" + newPrice);
    }
}

class GraphWidget implements WidgetObserver {
    @Override
    public void update(String stockSymbol, double newPrice) {
        System.out.println("Graph Widget: Plotting new point for " + stockSymbol + " at $" + newPrice);
    }
}

class BuySellBot implements WidgetObserver {
    @Override
    public void update(String stockSymbol, double newPrice) {
        if (newPrice < 100.0) {
            System.out.println("Buy/Sell Bot: Price is low! Executing BUY order for " + stockSymbol);
        } else {
            System.out.println("Buy/Sell Bot: Monitoring " + stockSymbol + "...");
        }
    }
}

// 3. The Subject (The Publisher)
class StockData {
    private List<WidgetObserver> observers = new ArrayList<>();

    // Provision to add widgets dynamically
    public void subscribe(WidgetObserver observer) {
        observers.add(observer);
        System.out.println("[System] A new widget connected to the feed.");
    }

    // Provision to remove widgets dynamically
    public void unsubscribe(WidgetObserver observer) {
        observers.remove(observer);
        System.out.println("[System] A widget disconnected from the feed.");
    }

    // The core notification engine
    private void notifyWidgets(String stockSymbol, double newPrice) {
        for (WidgetObserver observer : observers) {
            observer.update(stockSymbol, newPrice);
        }
    }

    // Called when the real-world stock price changes
    public void updatePrice(String stockSymbol, double newPrice) {
        System.out.println("\n=== STOCK UPDATE: " + stockSymbol + " is now $" + newPrice + " ===");
        notifyWidgets(stockSymbol, newPrice);
    }
}

// 4. Demonstration
public class C2Observer {
    public static void main(String[] args) {
        StockData feed = new StockData();

        // Create the widgets
        WidgetObserver ticker = new TickerTape();
        WidgetObserver graph = new GraphWidget();
        WidgetObserver bot = new BuySellBot();

        // Wire them up (Dynamic Subscription)
        feed.subscribe(ticker);
        feed.subscribe(graph);
        feed.subscribe(bot);

        // Simulate stock price changes
        feed.updatePrice("AAPL", 150.50);
        feed.updatePrice("TSLA", 98.20); // This will trigger the bot!

        // Simulate a widget being removed (e.g., user closes the Graph window)
        feed.unsubscribe(graph);

        // Next update won't go to the Graph
        feed.updatePrice("AAPL", 152.00);
    }
}