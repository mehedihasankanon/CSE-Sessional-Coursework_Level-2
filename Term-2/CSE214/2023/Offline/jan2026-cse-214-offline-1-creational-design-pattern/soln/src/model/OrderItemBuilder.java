package model;

public class OrderItemBuilder {
    
    private MenuItem menuItem;
    private int quantity;

    private Size size;
    private boolean extraCheese;
    private boolean spicy;
    private String note;

    public OrderItemBuilder(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.size = Size.MEDIUM; 
        this.extraCheese = false; 
        this.spicy = false; 
        this.note = ""; 
    }

    public OrderItemBuilder withSize(Size size) {
        this.size = size;
        return this;
    }

    public OrderItemBuilder withExtraCheese(boolean extraCheese) {
        this.extraCheese = extraCheese;
        return this;
    }

    public OrderItemBuilder withSpicy(boolean spicy) {
        this.spicy = spicy;
        return this;
    }

    public OrderItemBuilder withNote(String note) {
        this.note = note;
        return this;
    }

    public OrderItem build() {
        return new OrderItem(menuItem, quantity, size, extraCheese, spicy, note);
    }
}