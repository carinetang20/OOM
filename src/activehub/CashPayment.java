package activehub;

public class CashPayment extends Payment {
    @Override
    public void processPayment(double amount) {
        this.amountPaid = amount;
        ConsoleUI.blank();
        ConsoleUI.success("Cash payment of " + ConsoleUI.money(amount) + " received.");
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }
}
