package model;

import java.time.LocalDateTime;
import java.util.List;

public class StandardOrderBuilder implements OrderBuilder {
    private String orderId;
    private String customerName;
    private String phone;
    private List<OrderItem> items;
    
    private DeliveryType deliveryType;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
    private LocalDateTime scheduledTime;
    private String couponCode;
    private boolean giftWrap;
    private boolean cutleryRequired;
    private int loyaltyPointsToRedeem;
    private boolean rushOrder;
    private String specialInstructions;

    @Override
    public void reset(String orderId, String customerName, String phone, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.phone = phone;
        this.items = items;
        
        this.deliveryType = DeliveryType.PICKUP;
        this.deliveryAddress = "";
        this.paymentMethod = PaymentMethod.CASH;
        this.scheduledTime = null;
        this.couponCode = "";
        this.giftWrap = false;
        this.cutleryRequired = true;
        this.loyaltyPointsToRedeem = 0;
        this.rushOrder = false;
        this.specialInstructions = "";
    }

    @Override
    public void buildDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    @Override
    public void buildDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    @Override
    public void buildPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    @Override
    public void buildScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    @Override
    public void buildCouponCode(String couponCode) { this.couponCode = couponCode; }
    @Override
    public void buildGiftWrap(boolean giftWrap) { this.giftWrap = giftWrap; }
    @Override
    public void buildCutleryRequired(boolean cutleryRequired) { this.cutleryRequired = cutleryRequired; }
    @Override
    public void buildLoyaltyPointsToRedeem(int points) { this.loyaltyPointsToRedeem = points; }
    @Override
    public void buildRushOrder(boolean rushOrder) { this.rushOrder = rushOrder; }
    @Override
    public void buildSpecialInstructions(String instructions) { this.specialInstructions = instructions; }

    public Order getResult() {
        return new Order(this);
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public List<OrderItem> getItems() { return items; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public String getCouponCode() { return couponCode; }
    public boolean isGiftWrap() { return giftWrap; }
    public boolean isCutleryRequired() { return cutleryRequired; }
    public int getLoyaltyPointsToRedeem() { return loyaltyPointsToRedeem; }
    public boolean isRushOrder() { return rushOrder; }
    public String getSpecialInstructions() { return specialInstructions; }
}