package stockpilot;

import java.util.ArrayList;
import java.util.List;

/** Rule-based reorder advice: reorder point = avg daily sales x lead time + safety stock. */
public class RestockAdvisor {
    public record Suggestion(Product product, double avgDaily, int reorderPoint, int orderQty, String reason) {}

    private final InventoryService service;
    private final int windowDays;

    public RestockAdvisor(InventoryService service, int windowDays) {
        this.service = service; this.windowDays = windowDays;
    }

    public List<Suggestion> advise() {
        List<Suggestion> out = new ArrayList<>();
        for (Product p : service.getProducts()) {
            double avg = service.unitsSold(p.getSku()) / (double) windowDays;
            int rp = (int) Math.ceil(avg * p.getSupplier().getLeadTimeDays()) + p.getReorderLevel();
            if (p.getQuantity() < rp) {
                int target = rp + (int) Math.ceil(avg * 7);
                String why = String.format("Stock %d < reorder point %d (%.1f/day x %d days lead + %d safety)",
                        p.getQuantity(), rp, avg, p.getSupplier().getLeadTimeDays(), p.getReorderLevel());
                out.add(new Suggestion(p, avg, rp, target - p.getQuantity(), why));
            }
        }
        return out;
    }
}
