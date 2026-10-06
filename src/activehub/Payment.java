package activehub;

/**
 * Abstract payment method. Cash, card and e-wallet inherit this class
 * and override processPayment (inheritance + polymorphism).
 */
public abstract class Payment {
    protected double amountPaid;

    public double getAmountPaid() {
        return amountPaid;
    }

    public abstract void processPayment(double amount);

    public abstract String getPaymentMethod();
}
