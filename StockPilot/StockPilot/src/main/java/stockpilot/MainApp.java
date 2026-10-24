package stockpilot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Simple Swing prototype: Dashboard, Products, Sales, Restock and Audit tabs. */
public class MainApp extends JFrame {
    private final InventoryService service = new InventoryService();
    private final RestockAdvisor advisor = new RestockAdvisor(service, 7);
    private final Map<String, Integer> cart = new LinkedHashMap<>();

    private final JLabel kProducts = card(), kSales = card(), kLow = card(), kProfit = card();
    private final DefaultTableModel productModel = new DefaultTableModel(
            new String[]{"SKU", "Name", "Category", "Supplier", "Cost", "Price", "Stock", "Status"}, 0);
    private final DefaultTableModel restockModel = new DefaultTableModel(
            new String[]{"SKU", "Product", "Stock", "Reorder pt", "Order qty", "Reason"}, 0);
    private final JTextArea cartArea = new JTextArea(6, 30), auditArea = new JTextArea();

    public MainApp() {
        super("StockPilot - Inventory Intelligence (Prototype)");
        seed();
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Dashboard", dashboardTab());
        tabs.add("Products", productsTab());
        tabs.add("Sales & Billing", salesTab());
        tabs.add("Smart Restock", new JScrollPane(new JTable(restockModel)));
        tabs.add("Audit Trail", new JScrollPane(auditArea));
        auditArea.setEditable(false); cartArea.setEditable(false);
        add(tabs);
        refresh();
        setSize(960, 560); setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
    }

    private static JLabel card() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 22f)); l.setOpaque(true);
        l.setBackground(new Color(225, 238, 250)); l.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        return l;
    }

    private JPanel dashboardTab() {
        JPanel p = new JPanel(new GridLayout(1, 4, 12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        p.add(kProducts); p.add(kSales); p.add(kLow); p.add(kProfit);
        return p;
    }

    private JPanel productsTab() {
        JTextField sku = new JTextField(6), name = new JTextField(10), cat = new JTextField(7),
                cost = new JTextField(5), price = new JTextField(5), qty = new JTextField(4), re = new JTextField(4);
        JButton add = new JButton("Add Product");
        add.addActionListener(e -> {
            try {
                Supplier s = service.getProducts().get(0).getSupplier();
                service.addProduct(new Product(sku.getText(), name.getText(), new Category(cat.getText()), s,
                        Double.parseDouble(cost.getText()), Double.parseDouble(price.getText()),
                        Integer.parseInt(qty.getText()), Integer.parseInt(re.getText())));
                refresh();
            } catch (NumberFormatException ex) { error("Enter valid numbers for cost, price, qty, reorder level.");
            } catch (IllegalArgumentException ex) { error(ex.getMessage()); }
        });
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Object[] f : new Object[][]{{"SKU", sku}, {"Name", name}, {"Category", cat}, {"Cost", cost},
                {"Price", price}, {"Qty", qty}, {"Reorder", re}}) {
            form.add(new JLabel((String) f[0])); form.add((Component) f[1]);
        }
        form.add(add);
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(new JTable(productModel)), BorderLayout.CENTER);
        p.add(form, BorderLayout.SOUTH);
        return p;
    }

    private JPanel salesTab() {
        JTextField sku = new JTextField(8), qty = new JTextField(4);
        JButton addBtn = new JButton("Add to cart"), payBtn = new JButton("Checkout");
        addBtn.addActionListener(e -> {
            try { cart.merge(sku.getText().trim().toUpperCase(), Integer.parseInt(qty.getText()), Integer::sum); showCart(); }
            catch (NumberFormatException ex) { error("Quantity must be a number."); }
        });
        payBtn.addActionListener(e -> {
            try {
                Sale s = service.sell(cart);
                JOptionPane.showMessageDialog(this, String.format("Invoice #%d\nTotal: %.2f\nProfit: %.2f",
                        s.getId(), s.total(), s.profit()));
                cart.clear(); showCart(); refresh();
            } catch (InsufficientStockException | IllegalArgumentException ex) { error(ex.getMessage()); }
        });
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("SKU")); top.add(sku); top.add(new JLabel("Qty")); top.add(qty); top.add(addBtn); top.add(payBtn);
        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH); p.add(new JScrollPane(cartArea), BorderLayout.CENTER);
        return p;
    }

    private void showCart() {
        StringBuilder sb = new StringBuilder();
        cart.forEach((k, v) -> sb.append(k).append(" x ").append(v).append('\n'));
        cartArea.setText(sb.toString());
    }

    private void refresh() {
        kProducts.setText("<html><center>Products<br>" + service.getProducts().size());
        kSales.setText(String.format("<html><center>Sales<br>%.0f", service.todaySales()));
        kLow.setText("<html><center>Low Stock<br>" + service.lowStock().size());
        kProfit.setText(String.format("<html><center>Gross Profit<br>%.0f", service.grossProfit()));
        productModel.setRowCount(0);
        for (Product p : service.getProducts())
            productModel.addRow(new Object[]{p.getSku(), p.getName(), p.getCategory(), p.getSupplier(),
                    p.getCostPrice(), p.getSellPrice(), p.getQuantity(), p.isLowStock() ? "LOW" : "OK"});
        restockModel.setRowCount(0);
        for (RestockAdvisor.Suggestion s : advisor.advise())
            restockModel.addRow(new Object[]{s.product().getSku(), s.product().getName(), s.product().getQuantity(),
                    s.reorderPoint(), s.orderQty(), s.reason()});
        auditArea.setText(String.join("\n", service.getAuditLog()));
    }

    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void seed() {
        Supplier s = new Supplier("TechSource Ltd", 4);
        Category acc = new Category("Accessories");
        service.addProduct(new Product("KB-001", "USB Keyboard", acc, s, 350, 500, 12, 8));
        service.addProduct(new Product("MS-002", "Wireless Mouse", acc, s, 250, 400, 40, 10));
        service.addProduct(new Product("HD-003", "Headphones", acc, s, 600, 950, 6, 5));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}
