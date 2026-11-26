import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Coupon {
    private final String code;
    private final DiscountType type;
    private final double value;          // % or flat amount
    private final double minOrder;       // minimum cart total
    private final double maxDiscount;    // cap for PERCENT coupons (0 = no cap)
    private final LocalDate expiry;
    private final int usageLimit;        // total uses allowed
    private final int perUserLimit;      // uses allowed per customer
    private int usedCount = 0;
    private boolean active = true;
    private final Map<String, Integer> userUsage = new HashMap<>();

    public Coupon(String code, DiscountType type, double value, double minOrder,
                  double maxDiscount, LocalDate expiry, int usageLimit, int perUserLimit) {
        this.code = code.toUpperCase();
        this.type = type;
        this.value = value;
        this.minOrder = minOrder;
        this.maxDiscount = maxDiscount;
        this.expiry = expiry;
        this.usageLimit = usageLimit;
        this.perUserLimit = perUserLimit;
    }

    public double calculateDiscount(double cartTotal) {
        double d = (type == DiscountType.PERCENT) ? cartTotal * value / 100.0 : value;
        if (type == DiscountType.PERCENT && maxDiscount > 0) d = Math.min(d, maxDiscount);
        return Math.min(d, cartTotal);   // discount never exceeds the cart total
    }

    public void recordUse(String userId) {
        usedCount++;
        userUsage.merge(userId, 1, Integer::sum);
    }

    public int usesBy(String userId) { return userUsage.getOrDefault(userId, 0); }
    public void deactivate() { active = false; }

    public String getCode() { return code; }
    public DiscountType getType() { return type; }
    public double getValue() { return value; }
    public double getMinOrder() { return minOrder; }
    public LocalDate getExpiry() { return expiry; }
    public int getUsageLimit() { return usageLimit; }
    public int getPerUserLimit() { return perUserLimit; }
    public int getUsedCount() { return usedCount; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        String v = (type == DiscountType.PERCENT) ? (int) value + "% off" : "Rs." + (int) value + " off";
        return String.format("%-10s %-10s min Rs.%-6.0f expires %s used %d/%d %s",
                code, v, minOrder, expiry, usedCount, usageLimit, active ? "[ACTIVE]" : "[OFF]");
    }
}
