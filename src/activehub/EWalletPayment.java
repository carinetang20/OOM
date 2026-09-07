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
        ConsoleUI.blank();
        ConsoleUI.success(walletName + " payment of " + ConsoleUI.money(amount) + " successful.");
    }

    @Override
    public String getPaymentMethod() {
        return "E-Wallet";
    }
}
