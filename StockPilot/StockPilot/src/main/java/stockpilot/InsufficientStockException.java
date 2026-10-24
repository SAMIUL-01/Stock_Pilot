package stockpilot;

/** Thrown when a sale or adjustment would make stock negative. */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) { super(message); }
}
