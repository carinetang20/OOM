package activehub;

public class CashPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        ConsoleUI.blank();
        ConsoleUI.success("Cash payment of " + ConsoleUI.money(amount) + " received.");
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }
}
