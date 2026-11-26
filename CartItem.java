public class CartItem {
    private final String name;
    private final double price;
    private final int qty;

    public CartItem(String name, double price, int qty) {
        this.name = name; this.price = price; this.qty = qty;
    }
    public double subtotal() { return price * qty; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQty() { return qty; }
}
