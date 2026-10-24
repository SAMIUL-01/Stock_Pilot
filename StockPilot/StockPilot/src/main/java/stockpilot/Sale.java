package stockpilot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** An invoice composed of one or more SaleItem objects (composition). */
public class Sale {
    private final int id;
    private final LocalDateTime time;
    private final List<SaleItem> items = new ArrayList<>();

    public Sale(int id, LocalDateTime time) { this.id = id; this.time = time; }
    public void addItem(SaleItem item) { items.add(item); }
    public int getId() { return id; }
    public LocalDateTime getTime() { return time; }
    public List<SaleItem> getItems() { return items; }
    public double total() { return items.stream().mapToDouble(SaleItem::lineTotal).sum(); }
    public double profit() { return items.stream().mapToDouble(SaleItem::lineProfit).sum(); }
}
