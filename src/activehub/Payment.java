package activehub;

public interface Payment {
    void processPayment(double amount);
    String getPaymentMethod();
}
