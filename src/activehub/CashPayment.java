package activehub;

public class CashPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        System.out.printf("Cash payment of RM%.2f received. Thank you!%n", amount);
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }
}
