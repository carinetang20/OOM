package activehub;

/**
 * Accessory or add-on service. Not discounted by Off-Peak Saver
 * and not counted as equipment units for Promotion C.
 */
public class AccessoryItem extends RentalItem {
    public AccessoryItem(String itemCode, String itemName, double rentalPrice) {
        super(itemCode, itemName, rentalPrice);
    }

    @Override
    public String getCategory() {
        return "Accessory";
    }

    @Override
    public boolean isAccessory() {
        return true;
    }
}
