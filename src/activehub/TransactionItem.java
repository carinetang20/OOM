package activehub;

public class TransactionItem {
    private RentalItem item;
    private int quantity;

    public TransactionItem(RentalItem item, int quantity){
        this.item = item;
        this.quantity = quantity;
    }
    public RentalItem getItem(){
        return item;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public double getSubtotal(){
        return item.getRentalPrice() * quantity;
    }
}
