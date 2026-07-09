package model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * 
 * OrderBuilder interface. 
 * 
 * This interface will be implemented by concrete builders that will make the
 * product based on the client(in this case, OrderService class)'s specification.
 * 
 */
public interface OrderBuilder {
    void reset(String orderId, String customerName, String phone, List<OrderItem> items);
    
    void buildDeliveryType(DeliveryType deliveryType);
    void buildDeliveryAddress(String deliveryAddress);
    void buildPaymentMethod(PaymentMethod paymentMethod);
    void buildScheduledTime(LocalDateTime scheduledTime);
    void buildCouponCode(String couponCode);
    void buildGiftWrap(boolean giftWrap);
    void buildCutleryRequired(boolean cutleryRequired);
    void buildLoyaltyPointsToRedeem(int loyaltyPointsToRedeem);
    void buildRushOrder(boolean rushOrder);
    void buildSpecialInstructions(String specialInstructions);
}