package activehub;

/**
 * Represents a customer who books facilities or rents equipment.
 */
public class Customer {
    private String name;
    private String contactNumber;

    public Customer(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void displayCustomer() {
        System.out.println("Customer Name  : " + name);
        System.out.println("Contact Number : " + contactNumber);
    }

    /**
     * Removes spaces, dashes and brackets. Converts +60 / 60 to a local 0-prefix.
     */
    public static String normaliseContact(String raw) {
        if (raw == null) {
            return "";
        }
        String digits = raw.trim().replaceAll("[\\s\\-()]", "");
        if (digits.startsWith("+60")) {
            digits = "0" + digits.substring(3);
        } else if (digits.startsWith("60") && digits.length() >= 11) {
            digits = "0" + digits.substring(2);
        }
        return digits;
    }

    /**
     * Malaysian contact: 10 or 11 digits starting with 0
     * (mobile 01x… or landline 03…).
     */
    public static boolean isValidContact(String contact) {
        return normaliseContact(contact).matches("0\\d{9,10}");
    }

    @Override
    public String toString() {
        return name + " (" + contactNumber + ")";
    }
}
