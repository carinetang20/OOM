package activehub;

/**
 * Promotion C - Equipment Bundle Discount
 * Fixed RM20 when the transaction has at least one facility/court booking
 * AND total equipment quantity of 3 or more (same or different items).
 * Accessories, facility packages and add-on services are not equipment units.
 */
public class EquipmentBundleDiscount extends Promotion {
    public EquipmentBundleDiscount() {
        super("C", "Equipment Bundle Discount");
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        if (!transaction.hasFacilityOrCourtBooking()) {
            return false;
        }
        return transaction.getTotalEquipmentQuantity() >= 3;
    }

    @Override
    public double calculateSaving(Transaction transaction) {
        if (!isEligible(transaction)) {
            return 0.0;
        }
        return Transaction.roundMoney(Math.min(20.00, transaction.calculateSubtotal()));
    }
}
