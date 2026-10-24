package stockpilot;

/** Product classification. */
public class Category {
    private final String name;
    public Category(String name) { this.name = name; }
    public String getName() { return name; }
    @Override public String toString() { return name; }
}
