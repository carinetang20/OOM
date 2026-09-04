package activehub;

public class CardPayment implements Payment{
    @Override
    public void processPayment(double amount){
        System.out.println("Payment of RM" + amount + " made by card.");
    }

    @Override
    public String getPaymentMethod(){
        return "Credit/Debit Card";
    }

}
