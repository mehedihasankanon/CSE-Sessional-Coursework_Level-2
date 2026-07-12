package service;

import model.DeliveryType;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.PaymentMethod;
import model.Size;

import model.StandardOrderBuilder;
import model.OrderItemBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates order creation.
 *
 * Several methods below repeat long Order constructor calls with many optional
 * parameters. That is intentional assignment material for refactoring.
 */
public class OrderService {
    private int nextNumber = 1001;

    public OrderItem createOrderItem(MenuItem item, int quantity, Size size, boolean extraCheese, boolean spicy, String note) {
        OrderItemBuilder builder = new OrderItemBuilder(item, quantity);

        return builder.withSize(size)
                      .withExtraCheese(extraCheese)
                      .withSpicy(spicy)
                      .withNote(note)
                      .build();

    }

    public Order createDeliveryOrder(String customerName,
                                     String phone,
                                     String address,
                                     List<OrderItem> items,
                                     String couponCode,
                                     boolean rushOrder,
                                     String specialInstructions) { 
        // return new Order(nextOrderId(), customerName, phone,
        //         DeliveryType.DELIVERY,
        //         address,
        //         PaymentMethod.CASH,
        //         null,
        //         couponCode,
        //         false,
        //         true,
        //         0,
        //         rushOrder,
        //         items,
        //         specialInstructions);

        StandardOrderBuilder builder = new StandardOrderBuilder();
        OrderDirector director = new OrderDirector(builder);

        director.makeDeliveryOrder(nextOrderId(), customerName, phone, address, items, couponCode, rushOrder, specialInstructions);
        return builder.getResult();
    }

    public Order createPickupOrder(String customerName, String phone, List<OrderItem> items) {
        // return new Order(nextOrderId(), customerName, phone,
        //         DeliveryType.PICKUP,
        //         "",
        //         PaymentMethod.CASH,
        //         null,
        //         "",
        //         false,
        //         true,
        //         0,
        //         false,
        //         items,
        //         "");

        StandardOrderBuilder builder = new StandardOrderBuilder();
        OrderDirector director = new OrderDirector(builder);

        director.makePickupOrder(nextOrderId(), customerName, phone, items);
        return builder.getResult();
    }

    public Order createScheduledGiftOrder(String customerName,
                                          String phone,
                                          String address,
                                          List<OrderItem> items,
                                          LocalDateTime scheduledTime) {
        // return new Order(nextOrderId(), customerName, phone,
        //         DeliveryType.DELIVERY,
        //         address,
        //         PaymentMethod.CARD,
        //         scheduledTime,
        //         "WELCOME10",
        //         true,
        //         false,
        //         25,
        //         false,
        //         items,
        //         "Please call before delivery");

        StandardOrderBuilder builder = new StandardOrderBuilder();
        OrderDirector director = new OrderDirector(builder);

        director.makeScheduledGiftOrder(nextOrderId(), customerName, phone, address, items, scheduledTime);
        return builder.getResult();
    }

    public Order createSampleFamilyOrder(MenuCatalog catalog) {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(catalog.findByCode("P01"), 2, Size.LARGE, true, false, "half spicy"));
        items.add(new OrderItem(catalog.findByCode("B02"), 3, Size.MEDIUM, true, true, ""));
        items.add(new OrderItem(catalog.findByCode("D02"), 4, Size.MEDIUM, false, false, "less sugar"));
        items.add(new OrderItem(catalog.findByCode("S02"), 2, Size.LARGE, false, true, ""));

        // return new Order(nextOrderId(),
        //         "Sample Family",
        //         "01711111111",
        //         DeliveryType.DELIVERY,
        //         "House 25, Road 4, Dhanmondi",
        //         PaymentMethod.MOBILE_BANKING,
        //         null,
        //         "FAMILY15",
        //         false,
        //         true,
        //         50,
        //         true,
        //         items,
        //         "Deliver together");

        StandardOrderBuilder builder = new StandardOrderBuilder();
        OrderDirector director = new OrderDirector(builder);

        director.makeSampleFamilyOrder(nextOrderId(), items);
        return builder.getResult();
    }

    private String nextOrderId() {
        return "FF-" + nextNumber++;
    }
}

