package service;

import model.DeliveryType;
import model.OrderBuilder;
import model.OrderItem;
import model.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDirector {
    private OrderBuilder builder;

    public OrderDirector(OrderBuilder builder) {
        this.builder = builder;
    }

    public void changeBuilder(OrderBuilder builder) {
        this.builder = builder;
    }

    public void makeDeliveryOrder(String orderId, String name, String phone, String address, List<OrderItem> items, String coupon, boolean rush, String instructions) {
        builder.reset(orderId, name, phone, items);
        builder.buildDeliveryType(DeliveryType.DELIVERY);
        builder.buildDeliveryAddress(address);
        builder.buildCouponCode(coupon);
        builder.buildRushOrder(rush);
        builder.buildSpecialInstructions(instructions);
    }

    public void makePickupOrder(String orderId, String name, String phone, List<OrderItem> items) {
        builder.reset(orderId, name, phone, items);
        builder.buildDeliveryType(DeliveryType.PICKUP);
    }

    public void makeScheduledGiftOrder(String orderId, String name, String phone, String address, List<OrderItem> items, LocalDateTime scheduledTime) {
        builder.reset(orderId, name, phone, items);
        builder.buildDeliveryType(DeliveryType.DELIVERY);
        builder.buildDeliveryAddress(address);
        builder.buildPaymentMethod(PaymentMethod.CARD);
        builder.buildScheduledTime(scheduledTime);
        builder.buildCouponCode("WELCOME10");
        builder.buildGiftWrap(true);
        builder.buildCutleryRequired(false);
        builder.buildLoyaltyPointsToRedeem(25);
        builder.buildSpecialInstructions("Please call before delivery");
    }

    public void makeSampleFamilyOrder(String orderId, List<OrderItem> items) {
        builder.reset(orderId, "Sample Family", "01711111111", items);
        builder.buildDeliveryType(DeliveryType.DELIVERY);
        builder.buildDeliveryAddress("House 25, Road 4, Dhanmondi");
        builder.buildPaymentMethod(PaymentMethod.MOBILE_BANKING);
        builder.buildCouponCode("FAMILY15");
        builder.buildLoyaltyPointsToRedeem(50);
        builder.buildRushOrder(true);
        builder.buildSpecialInstructions("Deliver together");
    }
}