import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public void add(String name, double price, int qty) { items.add(new CartItem(name, price, qty)); }
    public double total() { return items.stream().mapToDouble(CartItem::subtotal).sum(); }
    public List<CartItem> getItems() { return items; }
    public boolean isEmpty() { return items.isEmpty(); }
}
