package activehub;

/**
 * Promotion C - Equipment Bundle Discount
 * Fixed RM20 when transaction has at least one facility item
 * AND total equipment quantity >= 3.
 */
public class EquipmentBundleDiscount implements Promotion {
    @Override
    public String getCode() {
        return "C";
    }

    @Override
    public String getName() {
        return "Equipment Bundle Discount";
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        return transaction.hasFacility() && transaction.getTotalEquipmentQuantity() >= 3;
    }

    @Override
    public double calculateSaving(Transaction transaction) {
        if (!isEligible(transaction)) {
            return 0.0;
        }
        return 20.00;
    }
}
