package stockpilot;

/** One line of a sale; keeps a price/cost snapshot so old profit stays correct. */
public class SaleItem {
    private final Product product;
    private final int quantity;
    private final double unitPrice, unitCost;

    public SaleItem(Product product, int quantity) {
        this.product = product; this.quantity = quantity;
        this.unitPrice = product.getSellPrice(); this.unitCost = product.getCostPrice();
    }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double lineTotal() { return unitPrice * quantity; }
    public double lineProfit() { return (unitPrice - unitCost) * quantity; }
}
