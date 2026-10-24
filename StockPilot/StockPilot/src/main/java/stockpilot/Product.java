package stockpilot;

/** A stock-keeping unit. Quantity is encapsulated: it only changes via validated methods. */
public class Product {
    private final String sku, name;
    private final Category category;
    private final Supplier supplier;
    private final double costPrice, sellPrice;
    private final int reorderLevel;
    private int quantity;

    public Product(String sku, String name, Category category, Supplier supplier,
                   double costPrice, double sellPrice, int quantity, int reorderLevel) {
        if (sku == null || sku.isBlank() || name == null || name.isBlank())
            throw new IllegalArgumentException("SKU and name are required.");
        if (costPrice < 0 || sellPrice < 0 || quantity < 0 || reorderLevel < 0)
            throw new IllegalArgumentException("Prices and quantities cannot be negative.");
        this.sku = sku.trim().toUpperCase(); this.name = name.trim();
        this.category = category; this.supplier = supplier;
        this.costPrice = costPrice; this.sellPrice = sellPrice;
        this.quantity = quantity; this.reorderLevel = reorderLevel;
    }

    public void addStock(int n) {
        if (n <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        quantity += n;
    }

    public void removeStock(int n) throws InsufficientStockException {
        if (n <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        if (n > quantity)
            throw new InsufficientStockException(name + ": only " + quantity + " in stock, requested " + n);
        quantity -= n;
    }

    public boolean isLowStock() { return quantity <= reorderLevel; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public Supplier getSupplier() { return supplier; }
    public double getCostPrice() { return costPrice; }
    public double getSellPrice() { return sellPrice; }
    public int getReorderLevel() { return reorderLevel; }
    public int getQuantity() { return quantity; }
}
