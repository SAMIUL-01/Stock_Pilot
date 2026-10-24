package stockpilot;

import java.time.LocalDateTime;
import java.util.*;

/** Business logic for products, sales, stock audit trail. */
public class InventoryService {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final List<Sale> sales = new ArrayList<>();
    private final List<String> auditLog = new ArrayList<>();
    private int nextSaleId = 1001;

    public void addProduct(Product p) {
        if (products.containsKey(p.getSku()))
            throw new IllegalArgumentException("SKU already exists: " + p.getSku());
        products.put(p.getSku(), p);
        log("STOCK-IN", p, p.getQuantity(), "Opening stock");
    }

    public Product find(String sku) { return products.get(sku.trim().toUpperCase()); }
    public List<Product> getProducts() { return new ArrayList<>(products.values()); }
    public List<Sale> getSales() { return sales; }
    public List<String> getAuditLog() { return auditLog; }

    public void adjustStock(String sku, int delta, String reason) throws InsufficientStockException {
        Product p = find(sku);
        if (p == null) throw new IllegalArgumentException("Unknown SKU: " + sku);
        if (delta > 0) p.addStock(delta); else p.removeStock(-delta);
        log("ADJUST", p, delta, reason);
    }

    /** All-or-nothing: every line is validated before any stock is deducted. */
    public Sale sell(Map<String, Integer> cart) throws InsufficientStockException {
        if (cart.isEmpty()) throw new IllegalArgumentException("Cart is empty.");
        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            Product p = find(e.getKey());
            if (p == null) throw new IllegalArgumentException("Unknown SKU: " + e.getKey());
            if (e.getValue() > p.getQuantity())
                throw new InsufficientStockException(p.getName() + ": only " + p.getQuantity() + " in stock.");
        }
        Sale sale = new Sale(nextSaleId++, LocalDateTime.now());
        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            Product p = find(e.getKey());
            p.removeStock(e.getValue());
            sale.addItem(new SaleItem(p, e.getValue()));
            log("SALE #" + sale.getId(), p, -e.getValue(), "Invoice");
        }
        sales.add(sale);
        return sale;
    }

    public List<Product> lowStock() { return products.values().stream().filter(Product::isLowStock).toList(); }
    public double todaySales() { return sales.stream().mapToDouble(Sale::total).sum(); }
    public double grossProfit() { return sales.stream().mapToDouble(Sale::profit).sum(); }

    public int unitsSold(String sku) {
        return sales.stream().flatMap(s -> s.getItems().stream())
                .filter(i -> i.getProduct().getSku().equals(sku)).mapToInt(SaleItem::getQuantity).sum();
    }

    private void log(String type, Product p, int qty, String reason) {
        auditLog.add(LocalDateTime.now().withNano(0) + " | " + type + " | " + p.getSku() + " | "
                + (qty > 0 ? "+" : "") + qty + " | " + reason);
    }
}
