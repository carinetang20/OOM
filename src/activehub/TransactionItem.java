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

    public String[] toTableRow() {
        return new String[] {
                item.getItemCode(),
                item.getItemName(),
                String.valueOf(quantity),
                ConsoleUI.money(getSubtotal())
        };
    }

    public String toFilePart() {
        return item.getItemCode() + ":" + quantity;
    }
}
