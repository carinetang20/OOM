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
        System.out.printf("Card payment of RM%.2f approved (card ending %s).%n",
                amount, lastFourDigits);
    }

    @Override
    public String getPaymentMethod() {
        return "Credit/Debit Card";
    }
}
