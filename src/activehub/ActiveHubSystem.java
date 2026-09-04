package activehub;

import java.util.Scanner;

public class ActiveHubSystem {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("=====TASTEHUB SYSTEM====");
            System.out.println("1. Facility Booking Module");
            System.out.println("2. View Rental Catalogue");
            System.out.println("3. Create Rental Transaction");
            System.out.println("4. Apply Promotion");
            System.out.println("5. Make Payment");
            System.out.println("6. Daily Report");
            System.out.println("7. Exit");

            System.out.println("Please enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\n---FACILITY BOOKING MODULE---");
                    System.out.println("Please enter the following details:");
                    System.out.println("Name: " + getName());
                    System.out.println("Contact Number: ");
                    System.out.println("Booking date: ");
                    System.out.println("Booking time: ");
                    System.out.println("Number of participants: ");
                    System.out.println("Preferred facility type/Court Type: ");

                    break;

                case 2:
                    System.out.println("\n--- RENTAL CATALOGUE ---");

                    break;
                case 3:
                    System.out.println("\n---RENTAL TRANSACTION---");
                    break;
                case 4:
                    System.out.println("\n---PROMOTION MODULE---");
                    break;
                case 5:
                    System.out.println("\n---PAYMENT MODULE ---");
                    break;
                case 6:
                    System.out.println("\n---DAILY REPORT---");
                    break;
                case 7:
                    System.out.println("Thank you. Goodbye !");
                    break;
                default:
                    System.out.println("Invalid choice !");
            }
        }while(choice != 7);
        scanner.close();
    }
}
