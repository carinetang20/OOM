package activehub;

/**
 * A catalogue item available for rental.
 * Categories: Equipment, Facility, Accessory
 */
public class RentalItem {
    private String itemCode;
    private String itemName;
    private String category;
    private double rentalPrice;

    public RentalItem(String itemCode, String itemName, String category, double rentalPrice) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.category = category;
        this.rentalPrice = rentalPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public boolean isEquipment() {
        return "Equipment".equalsIgnoreCase(category);
    }

    public boolean isFacility() {
        return "Facility".equalsIgnoreCase(category);
    }

    public boolean isAccessory() {
        return "Accessory".equalsIgnoreCase(category);
    }

    public void displayItem() {
        String catLabel;
        if (isEquipment()) {
            catLabel = ConsoleUI.blue(String.format("%-12s", category));
        } else if (isFacility()) {
            catLabel = ConsoleUI.magenta(String.format("%-12s", category));
        } else {
            catLabel = ConsoleUI.cyan(String.format("%-12s", category));
        }
        System.out.printf("  %-8s %-30s ", itemCode, itemName);
        System.out.print(catLabel);
        System.out.printf(" %8s%n", ConsoleUI.money(rentalPrice));
        ConsoleUI.line();
    }

    /** Pipe-separated line for text-file storage. */
    public String toFileLine() {
        return itemCode + "|" + itemName + "|" + category + "|" + rentalPrice;
    }

    public static RentalItem fromFileLine(String line) {
        String[] p = line.split("\\|");
        return new RentalItem(p[0], p[1], p[2], Double.parseDouble(p[3]));
    }

    @Override
    public String toString() {
        return itemCode + " " + itemName + " [" + category + "] RM" + String.format("%.2f", rentalPrice);
    }
}
