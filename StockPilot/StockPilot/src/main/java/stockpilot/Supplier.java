package stockpilot;

/** Supplier with a delivery lead time used by the restock advisor. */
public class Supplier {
    private final String name;
    private final int leadTimeDays;
    public Supplier(String name, int leadTimeDays) { this.name = name; this.leadTimeDays = leadTimeDays; }
    public String getName() { return name; }
    public int getLeadTimeDays() { return leadTimeDays; }
    @Override public String toString() { return name; }
}
