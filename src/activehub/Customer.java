package activehub;

public class Customer {
    private String name;
    private String contactNumber;

    public Customer(String name, String contactNumber){
        this.name = name;
        this.contactNumber = contactNumber;
    }
    public String getName(){
        return name;
    }
    public String getContactNumber(){
        return contactNumber;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setContactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }
    public void displayCustomer(){
        System.out.println("Customer Name: " + name);
        System.out.println("Contact Number: " + contactNumber);
    }
}
