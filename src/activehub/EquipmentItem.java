package activehub;

/**
 * Equipment rental (racquets, balls, shuttlecocks). Counts toward Promotion C.
 */
public class EquipmentItem extends RentalItem {
    public EquipmentItem(String itemCode, String itemName, double rentalPrice) {
        super(itemCode, itemName, rentalPrice);
    }

    @Override
    public String getCategory() {
        return "Equipment";
    }

    @Override
    public boolean isEquipment() {
        return true;
    }
}
