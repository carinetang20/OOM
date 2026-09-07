package activehub;

public class EWalletPayment implements Payment{
    @Override
    public void processPayment(double amount){
        System.out.println("Payment of RM" + amount + " made by e-wallet");
    }
    @Override
    public String getPaymentMethod(){
        return "E-Wallet";
    }
}
