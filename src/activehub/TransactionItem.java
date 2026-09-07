package activehub;

/**
 * One line item inside a rental transaction (composition).
 */
public class TransactionItem {
    private RentalItem item;
    private int quantity;

    public TransactionItem(RentalItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public RentalItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int extra) {
        this.quantity += extra;
    }

    public double getSubtotal() {
        return item.getRentalPrice() * quantity;
    }

    public void display() {
        System.out.printf("  %-8s %-28s x%-3d RM%8.2f%n",
                item.getItemCode(), item.getItemName(), quantity, getSubtotal());
    }

    public String toFilePart() {
        return item.getItemCode() + ":" + quantity;
    }
}
