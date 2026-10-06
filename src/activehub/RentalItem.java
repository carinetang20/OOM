package activehub;

/**
 * Abstract catalogue item. Equipment, facility packages and accessories
 * inherit shared code/name/price and override category checks.
 */
public abstract class RentalItem {
    private String itemCode;
    private String itemName;
    private double rentalPrice;

    protected RentalItem(String itemCode, String itemName, double rentalPrice) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.rentalPrice = rentalPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public abstract String getCategory();

    public boolean isEquipment() {
        return false;
    }

    public boolean isFacility() {
        return false;
    }

    public boolean isAccessory() {
        return false;
    }

    public String[] toTableRow() {
        String catLabel;
        if (isEquipment()) {
            catLabel = ConsoleUI.blue(getCategory());
        } else if (isFacility()) {
            catLabel = ConsoleUI.magenta(getCategory());
        } else {
            catLabel = ConsoleUI.cyan(getCategory());
        }
        return new String[] { itemCode, itemName, catLabel, ConsoleUI.money(rentalPrice) };
    }

    public String toFileLine() {
        return itemCode + "|" + itemName + "|" + getCategory() + "|" + rentalPrice;
    }

    public static RentalItem fromFileLine(String line) {
        String[] p = line.split("\\|");
        String code = p[0];
        String name = p[1];
        String category = p[2];
        double price = Double.parseDouble(p[3]);
        if ("Equipment".equalsIgnoreCase(category)) {
            return new EquipmentItem(code, name, price);
        }
        if ("Facility".equalsIgnoreCase(category)) {
            return new FacilityPackage(code, name, price);
        }
        return new AccessoryItem(code, name, price);
    }

    @Override
    public String toString() {
        return itemCode + " " + itemName + " [" + getCategory() + "] RM"
                + String.format("%.2f", rentalPrice);
    }
}
