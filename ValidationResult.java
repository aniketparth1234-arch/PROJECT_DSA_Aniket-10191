public class ValidationResult {
    private final boolean valid;
    private final String message;

    private ValidationResult(boolean valid, String message) { this.valid = valid; this.message = message; }
    public static ValidationResult ok() { return new ValidationResult(true, "Coupon is valid"); }
    public static ValidationResult fail(String why) { return new ValidationResult(false, why); }
    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
}
