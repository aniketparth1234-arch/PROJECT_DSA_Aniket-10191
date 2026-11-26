import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    static final CouponService service = new CouponService();
    static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        seed();
        while (true) {
            System.out.println("\n===== COUPON & DISCOUNT MANAGER =====");
            System.out.println("1. Admin: Create coupon");
            System.out.println("2. Admin: List coupons");
            System.out.println("3. Admin: Deactivate coupon");
            System.out.println("4. Customer: Checkout with coupon");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            switch (in.nextLine().trim()) {
                case "1": create(); break;
                case "2": service.all().forEach(System.out::println); break;
                case "3": System.out.print("Code: ");
                          System.out.println(service.deactivate(in.nextLine()) ? "Deactivated." : "Not found."); break;
                case "4": checkout(); break;
                case "0": System.out.println("Bye!"); return;
                default:  System.out.println("Invalid option.");
            }
        }
    }

    static void seed() {
        service.addCoupon(new Coupon("SAVE10", DiscountType.PERCENT, 10, 500, 200, LocalDate.now().plusDays(30), 100, 1));
        service.addCoupon(new Coupon("FLAT100", DiscountType.FLAT, 100, 1000, 0, LocalDate.now().plusDays(10), 50, 2));
        service.addCoupon(new Coupon("OLD50", DiscountType.PERCENT, 50, 0, 0, LocalDate.now().minusDays(1), 10, 1));
    }

    static void create() {
        try {
            System.out.print("Code: ");              String code = in.nextLine();
            System.out.print("Type (PERCENT/FLAT): "); DiscountType t = DiscountType.valueOf(in.nextLine().trim().toUpperCase());
            System.out.print("Value: ");             double v = Double.parseDouble(in.nextLine());
            System.out.print("Min order: ");         double min = Double.parseDouble(in.nextLine());
            System.out.print("Max discount (0=none): "); double max = Double.parseDouble(in.nextLine());
            System.out.print("Valid for days: ");    int days = Integer.parseInt(in.nextLine());
            System.out.print("Total usage limit: "); int lim = Integer.parseInt(in.nextLine());
            System.out.print("Per-user limit: ");    int per = Integer.parseInt(in.nextLine());
            boolean ok = service.addCoupon(new Coupon(code, t, v, min, max, LocalDate.now().plusDays(days), lim, per));
            System.out.println(ok ? "Coupon created." : "Code already exists.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input, coupon not created.");
        }
    }

    static void checkout() {
        Cart cart = new Cart();
        cart.add("Headphones", 1200, 1);
        cart.add("Phone Case", 300, 2);
        System.out.printf("Cart total: Rs.%.2f%n", cart.total());
        System.out.print("User ID: ");     String user = in.nextLine().trim();
        System.out.print("Coupon code: "); String code = in.nextLine();

        ValidationResult r = service.validate(code, cart, user);
        if (!r.isValid()) { System.out.println("Rejected: " + r.getMessage()); return; }
        double discount = service.find(code).calculateDiscount(cart.total());
        double payable = service.apply(code, cart, user);
        System.out.printf("Discount: Rs.%.2f%nPayable : Rs.%.2f%n", discount, payable);
    }
}
