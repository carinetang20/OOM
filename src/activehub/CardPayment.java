package activehub;

public class CardPayment implements Payment {
    private String lastFourDigits;

    public CardPayment() {
        this.lastFourDigits = "****";
    }

    public CardPayment(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    @Override
    public void processPayment(double amount) {
        ConsoleUI.blank();
        ConsoleUI.success("Card payment of " + ConsoleUI.money(amount)
                + " approved (card ending " + lastFourDigits + ").");
    }

    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}
