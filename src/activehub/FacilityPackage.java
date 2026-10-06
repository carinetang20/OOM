package activehub;

/**
 * Facility / court package. Counts as facility charges for Promotions A, B and C.
 */
public class FacilityPackage extends RentalItem {
    public FacilityPackage(String itemCode, String itemName, double rentalPrice) {
        super(itemCode, itemName, rentalPrice);
    }

    @Override
    public String getCategory() {
        return "Facility";
    }

    @Override
    public boolean isFacility() {
        return true;
    }
}
