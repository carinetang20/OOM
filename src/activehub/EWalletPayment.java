package activehub;

public class EWalletPayment implements Payment {
    private String walletName;

    public EWalletPayment() {
        this.walletName = "E-Wallet";
    }

    public EWalletPayment(String walletName) {
        this.walletName = walletName;
    }

    @Override
    public void processPayment(double amount) {
        System.out.printf("%s payment of RM%.2f successful.%n", walletName, amount);
    }

    @Override
    public String getPaymentMethod() {
        return "E-Wallet";
    }
}
