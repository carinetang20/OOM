package activehub;

public class CardPayment extends Payment {
    private String lastFourDigits;

    public CardPayment() {
        this.lastFourDigits = "****";
    }

    public CardPayment(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    /** Last four of the card: digits only, exactly 4 characters. */
    public static boolean isValidLastFour(String input) {
        if (input == null) {
            return false;
        }
        return input.trim().matches("\\d{4}");
    }

    @Override
    public void processPayment(double amount) {
        this.amountPaid = amount;
        ConsoleUI.blank();
        ConsoleUI.success("Card payment of " + ConsoleUI.money(amount)
                + " approved (card ending " + lastFourDigits + ").");
    }

    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}
