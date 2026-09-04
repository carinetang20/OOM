package activehub;

import java.util.ArrayList;

public class Transaction {
    private String transactionId;
    private Customer customer;
    private Booking booking;
    private ArrayList<TransactionItem> items;

    private Promotion selectedPromotion;
    private double discountAmount;
    private double finalAmount;
    private Payment payment;
    private boolean completed;

    public Transaction(String transactionId, Customer customer, Booking booking){
        this.transactionId = transactionId;
        this.customer = customer;
        this.booking = booking;
        this.items = new ArrayList<>();
        this.completed = false;
    }
    public void addItem(RentalItem item, int quantity){
        TransactionItem transactionItem = new TransactionItem(item, quantity);

        items.add(transactionItem);
    }
    public double calculateSubtotal(){
        double subtotal = 0;

        for (TransactionItem : items){
            if (items.getItem().getCategory().equalsIgnoreCase("Facility")){
                return true;
            }
        }
        return false;
    }
    public int getTotalEquipmentQuantity(){
        int total = 0;

        for (TransactionItem item : items){
            if (item.getItem().getCategory().equalsIgnoreCase("Equipment")){
                total += item.getQuantity();
            }
        }
        return total;
    }
    public double getFacilityCharge(){
        double total = 0;
        for (TransactionItem item : items){
            if (item.getItem().getCategory().equalsIgnoreCaase("Facility")){
                total += item.getSubtotal();
            }
        }
        return total;
    }
    public double calculateServiceCharge(){
        double amountAfterDiscount = calculateSubtotal() - discountAmount;
        serviceCharge = amountAfterDiscount * 0.10;

        return serviceCharge;

    }
    public double calculateFinalAmount(){
        finalAmount = calculateSubtotal() - discountAmount + calculateServiceCharge();
        return finalAmount;
    }
    public void setSelectedPromotion(Promotion promotion){

    }



}
