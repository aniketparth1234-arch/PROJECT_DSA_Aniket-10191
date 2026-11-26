import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class CouponService {
    private final Map<String, Coupon> store = new LinkedHashMap<>();

    public boolean addCoupon(Coupon c) {
        if (store.containsKey(c.getCode())) return false;   // codes must be unique
        store.put(c.getCode(), c);
        return true;
    }

    public Coupon find(String code) { return store.get(code.trim().toUpperCase()); }
    public Iterable<Coupon> all() { return store.values(); }

    public boolean deactivate(String code) {
        Coupon c = find(code);
        if (c == null) return false;
        c.deactivate();
        return true;
    }

    /** Runs every rule in order and returns the first failure. */
    public ValidationResult validate(String code, Cart cart, String userId) {
        Coupon c = find(code);
        if (c == null)                              return ValidationResult.fail("Coupon code does not exist");
        if (!c.isActive())                          return ValidationResult.fail("Coupon is deactivated");
        if (LocalDate.now().isAfter(c.getExpiry())) return ValidationResult.fail("Coupon has expired");
        if (c.getUsedCount() >= c.getUsageLimit())  return ValidationResult.fail("Coupon usage limit reached");
        if (c.usesBy(userId) >= c.getPerUserLimit())return ValidationResult.fail("You have already used this coupon");
        if (cart.total() < c.getMinOrder())
            return ValidationResult.fail("Minimum order of Rs." + (int) c.getMinOrder() + " required");
        return ValidationResult.ok();
    }

    /** Validates, then applies the coupon and records the use. Returns the final payable amount. */
    public double apply(String code, Cart cart, String userId) {
        ValidationResult r = validate(code, cart, userId);
        if (!r.isValid()) throw new IllegalArgumentException(r.getMessage());
        Coupon c = find(code);
        double discount = c.calculateDiscount(cart.total());
        c.recordUse(userId);
        return cart.total() - discount;
    }
}
