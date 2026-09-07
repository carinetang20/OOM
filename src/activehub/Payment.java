package activehub;

/**
 * Common contract for payment methods (polymorphism).
 */
public interface Payment {
    void processPayment(double amount);

    String getPaymentMethod();
}
