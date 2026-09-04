package activehub;

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
        System.out.println(itemCode + " | " + itemName + " | " + category + " | RM" + rentalPrice);
    }

}
