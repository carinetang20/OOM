package activehub;

public class CashPayment implements Payment{
    @Override
    public void processPayment(double amount){
        System.out.println("Payment of RM" + amount + " made by cash.");
    }
    @Override
    public String getPaymentMethod(){
        return "Cash";
    }


}
