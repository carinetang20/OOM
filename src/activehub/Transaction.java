package activehub;

import java.util.ArrayList;
import java.util.List;

/**
 * A rental transaction that may link to a facility booking.
 * Calculates subtotal, service charge (10%), discount and final amount.
 */
public class Transaction {
    private String transactionId;
    private Customer customer;
    private Booking booking; // may be null for pure walk-in rentals
    private ArrayList<TransactionItem> items;
    private Promotion selectedPromotion;
    private double discountAmount;
    private double finalAmount;
    private Payment payment;
    private boolean completed;
    private List<String> eligiblePromotionSummary;

    public Transaction(String transactionId, Customer customer, Booking booking) {
        this.transactionId = transactionId;
        this.customer = customer;
        this.booking = booking;
        this.items = new ArrayList<>();
        this.discountAmount = 0.0;
        this.finalAmount = 0.0;
        this.completed = false;
        this.eligiblePromotionSummary = new ArrayList<>();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Booking getBooking() {
        return booking;
    }

    public ArrayList<TransactionItem> getItems() {
        return items;
    }

    public Promotion getSelectedPromotion() {
        return selectedPromotion;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public Payment getPayment() {
        return payment;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<String> getEligiblePromotionSummary() {
        return eligiblePromotionSummary;
    }

    /**
     * Adds a catalogue line. If the same item code already exists,
     * quantity is increased (composition: the line is owned by this bill).
     */
    public void addItem(RentalItem item, int quantity) {
        for (TransactionItem existing : items) {
            if (existing.getItem().getItemCode().equalsIgnoreCase(item.getItemCode())) {
                existing.addQuantity(quantity);
                return;
            }
        }
        items.add(new TransactionItem(item, quantity));
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (TransactionItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public boolean hasFacility() {
        for (TransactionItem item : items) {
            if (item.getItem().isFacility()) {
                return true;
            }
        }
        return false;
    }

    /** Promotion C: a court booking on file, or a facility/court package on the bill. */
    public boolean hasFacilityOrCourtBooking() {
        if (hasFacility()) {
            return true;
        }
        return booking != null && booking.isActive();
    }

    public int getTotalEquipmentQuantity() {
        int total = 0;
        for (TransactionItem item : items) {
            if (item.getItem().isEquipment()) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    public double getFacilityCharge() {
        double total = 0.0;
        for (TransactionItem item : items) {
            if (item.getItem().isFacility()) {
                total += item.getSubtotal();
            }
        }
        return roundMoney(total);
    }

    /** 10% of (subtotal minus discount). Service charge is never discounted by A/B/C. */
    public double calculateServiceCharge() {
        return roundMoney(amountAfterDiscount() * 0.10);
    }

    public double calculateFinalAmount() {
        finalAmount = roundMoney(amountAfterDiscount() + calculateServiceCharge());
        return finalAmount;
    }

    private double amountAfterDiscount() {
        return Math.max(0.0, calculateSubtotal() - discountAmount);
    }

    static double roundMoney(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public void setSelectedPromotion(Promotion promotion, double saving) {
        this.selectedPromotion = promotion;
        this.discountAmount = roundMoney(Math.min(Math.max(0.0, saving), calculateSubtotal()));
        calculateFinalAmount();
    }

    public void clearPromotion() {
        this.selectedPromotion = null;
        this.discountAmount = 0.0;
        this.eligiblePromotionSummary.clear();
        calculateFinalAmount();
    }

    public void setEligiblePromotionSummary(List<String> summary) {
        this.eligiblePromotionSummary = summary;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void markCompleted() {
        this.completed = true;
        calculateFinalAmount();
    }

    public void displayBill() {
        ConsoleUI.blank();
        ConsoleUI.boxTop();
        ConsoleUI.boxCenter(ConsoleUI.bold("TRANSACTION BILL"));
        ConsoleUI.boxBottom();

        ConsoleUI.kv("Transaction ID", ConsoleUI.bold(transactionId));
        ConsoleUI.kv("Customer", customer.toString());
        if (booking != null) {
            ConsoleUI.kv("Linked Booking", booking.getBookingId()
                    + " · " + booking.getDate() + " " + booking.getTime()
                    + " · " + booking.getParticipants() + " pax");
        } else {
            ConsoleUI.kv("Linked Booking", ConsoleUI.dim("walk-in / none"));
        }

        ConsoleUI.dividerSoft();
        List<String[]> itemRows = new ArrayList<>();
        for (TransactionItem item : items) {
            itemRows.add(item.toTableRow());
        }
        ConsoleUI.boxedTable("Line items",
                new String[]{"Code", "Item", "Qty", "Amount"},
                new boolean[]{false, false, true, true},
                itemRows);

        ConsoleUI.kv("Subtotal", ConsoleUI.money(calculateSubtotal()));
        ConsoleUI.kv("Facility charge", ConsoleUI.money(getFacilityCharge()));

        ConsoleUI.dividerSoft();
        ConsoleUI.info("Eligible promotions (only one will be applied)");
        if (eligiblePromotionSummary.isEmpty()) {
            ConsoleUI.kv("Eligible", ConsoleUI.dim("None"));
        } else {
            for (String line : eligiblePromotionSummary) {
                System.out.println("    " + ConsoleUI.cyan("• ") + line);
            }
        }

        if (selectedPromotion != null) {
            ConsoleUI.kv("Selected promo", ConsoleUI.green(
                    selectedPromotion.getCode() + " — " + selectedPromotion.getName()));
            ConsoleUI.kv("Discount", ConsoleUI.yellow("− " + ConsoleUI.money(discountAmount)));
        } else {
            ConsoleUI.kv("Selected promo", ConsoleUI.dim("None (promotions not combined)"));
            ConsoleUI.kv("Discount", ConsoleUI.money(0.0));
        }

        ConsoleUI.kv("Service charge 10%", ConsoleUI.money(calculateServiceCharge()));
        ConsoleUI.blank();
        ConsoleUI.boxTop();
        ConsoleUI.boxRow("  " + ConsoleUI.bold("FINAL PAYABLE")
                + "                    "
                + ConsoleUI.bold(ConsoleUI.green(ConsoleUI.money(calculateFinalAmount()))));
        if (payment != null) {
            ConsoleUI.boxRow("  Payment method : " + payment.getPaymentMethod());
        }
        ConsoleUI.boxRow("  Status         : "
                + (completed ? ConsoleUI.green("PAID") : ConsoleUI.yellow("PENDING")));
        ConsoleUI.boxBottom();
        ConsoleUI.blank();
        ConsoleUI.dotBarrier();
        ConsoleUI.blank();
    }

    public String toFileLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(transactionId).append("|");
        sb.append(customer.getName()).append("|");
        sb.append(customer.getContactNumber()).append("|");
        sb.append(booking == null ? "NONE" : booking.getBookingId()).append("|");
        sb.append(String.format("%.2f", calculateSubtotal())).append("|");
        sb.append(selectedPromotion == null ? "NONE" : selectedPromotion.getCode()).append("|");
        sb.append(String.format("%.2f", discountAmount)).append("|");
        sb.append(String.format("%.2f", calculateServiceCharge())).append("|");
        sb.append(String.format("%.2f", calculateFinalAmount())).append("|");
        sb.append(payment == null ? "NONE" : payment.getPaymentMethod()).append("|");
        sb.append(completed ? "PAID" : "PENDING").append("|");

        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(items.get(i).toFilePart());
        }
        return sb.toString();
    }
}
