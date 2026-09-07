package activehub;

public class EquipmentBundleDiscount implements Promotion {
    @Override
    public String getCode(){
        return "C";
    }
    @Override
    public String getName(){
        return "Equipment Bundle Discount";
    }
    @Override
    public boolean isEligible(Transaction transaction){
        return transaction.hasFacility() && transaction.getTotalEquipmentQuantity() => 3;
    }
    @Override
    public double calculateSaving(Transaction transaction){
        if (isEligible(transaction)){
            return 20.00;
        }
        return 0.00;
    }
}
