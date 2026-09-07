package activehub;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ActiveHub Sports Centre console application.
 * Facility booking, rental catalogue, transactions, promotions, payment, reports.
 */
public class ActiveHubSystem {
    private final Scanner scanner;
    private final FileManager fileManager;
    private final PromotionEngine promotionEngine;
    private final DailyReport dailyReport;

    private final List<Facility> facilities;
    private final List<RentalItem> catalogue;
    private final List<Booking> bookings;
    private final List<Transaction> transactions;

    private int bookingCounter;
    private int transactionCounter;

    public ActiveHubSystem() {
        scanner = new Scanner(System.in);
        fileManager = new FileManager("data");
        promotionEngine = new PromotionEngine();
        dailyReport = new DailyReport();

        facilities = new ArrayList<>();
        catalogue = new ArrayList<>();
        bookings = new ArrayList<>();
        transactions = new ArrayList<>();

        initialiseData();
    }

    private void initialiseData() {
        List<Facility> loadedFacilities = fileManager.loadFacilities();
        if (loadedFacilities.isEmpty()) {
            seedFacilities();
            fileManager.saveFacilities(facilities);
        } else {
            facilities.addAll(loadedFacilities);
        }

        List<RentalItem> loadedCatalogue = fileManager.loadCatalogue();
        if (loadedCatalogue.isEmpty()) {
            seedCatalogue();
            fileManager.saveCatalogue(catalogue);
        } else {
            catalogue.addAll(loadedCatalogue);
        }

        bookings.addAll(fileManager.loadBookings(facilities));
        bookingCounter = bookings.size();
        transactionCounter = 0;
    }

    private void seedFacilities() {
        facilities.add(new Facility("F01", "Badminton Court 1", "Badminton"));
        facilities.add(new Facility("F02", "Badminton Court 2", "Badminton"));
        facilities.add(new Facility("F03", "Futsal Court A", "Futsal"));
        facilities.add(new Facility("F04", "Basketball Court", "Basketball"));
        facilities.add(new Facility("F05", "Table Tennis Room", "Table Tennis"));
    }

    /**
     * Eight rental items: 4 Equipment, 2 Facility packages, 2 Accessories.
     */
    private void seedCatalogue() {
        // Equipment (4)
        catalogue.add(new RentalItem("E01", "Badminton Racquet", "Equipment", 8.00));
        catalogue.add(new RentalItem("E02", "Shuttlecock Set", "Equipment", 5.00));
        catalogue.add(new RentalItem("E03", "Basketball", "Equipment", 6.00));
        catalogue.add(new RentalItem("E04", "Futsal Ball", "Equipment", 6.00));
        // Facility packages (2)
        catalogue.add(new RentalItem("C01", "Badminton Court Package (1hr)", "Facility", 40.00));
        catalogue.add(new RentalItem("C02", "Futsal Court Package (1hr)", "Facility", 80.00));
        // Accessories / add-on services (2)
        catalogue.add(new RentalItem("A01", "Towel Set", "Accessory", 3.00));
        catalogue.add(new RentalItem("A02", "Locker Service", "Accessory", 4.00));
    }

    public void run() {
        int choice;
        do {
            printMainMenu();
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> facilityBookingMenu();
                case 2 -> viewCatalogue();
                case 3 -> createOrUpdateTransaction();
                case 4 -> applyPromotion();
                case 5 -> makePayment();
                case 6 -> dailyReport.generate(bookings, transactions);
                case 7 -> {
                    persistAll();
                    System.out.println("Data saved. Thank you. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("===== ACTIVEHUB SYSTEM =====");
        System.out.println("1. Facility Booking Module");
        System.out.println("2. View Rental Catalogue");
        System.out.println("3. Create Rental Transaction");
        System.out.println("4. Apply Promotion");
        System.out.println("5. Make Payment");
        System.out.println("6. Daily Report");
        System.out.println("7. Exit");
    }

    // -------------------- Module 1: Facility Booking --------------------

    private void facilityBookingMenu() {
        int choice;
        do {
            System.out.println("\n--- FACILITY BOOKING MODULE ---");
            System.out.println("1. Create Booking");
            System.out.println("2. View All Bookings");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Back to Main Menu");
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> createBooking();
                case 2 -> viewBookings();
                case 3 -> cancelBooking();
                case 4 -> { }
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    private void createBooking() {
        System.out.println("\n--- Create Facility Booking ---");
        String name = readNonEmpty("Customer name: ");
        String contact = readNonEmpty("Contact number: ");

        LocalDate date = readDate("Booking date (yyyy-MM-dd): ");
        LocalTime time = readTime("Booking time (HH:mm, 24-hour): ");
        int participants = readPositiveInt("Number of participants: ");

        System.out.println("\nAvailable facilities:");
        System.out.printf("%-8s %-28s %-15s%n", "ID", "Name", "Type");
        for (Facility f : facilities) {
            f.displayFacility();
        }

        Facility facility = null;
        while (facility == null) {
            String facilityId = readNonEmpty("Preferred facility ID: ");
            facility = findFacility(facilityId);
            if (facility == null) {
                System.out.println("Facility not found. Try again.");
            }
        }

        if (hasConflict(facility, date, time)) {
            System.out.println("ERROR: This facility is already booked for "
                    + date.format(Booking.DATE_FMT) + " at "
                    + time.format(Booking.TIME_FMT) + ".");
            System.out.println("Please choose another facility, date, or time.");
            return;
        }

        bookingCounter++;
        String bookingId = String.format("B%03d", bookingCounter);
        Customer customer = new Customer(name, contact);
        Booking booking = new Booking(bookingId, customer, facility, date, time, participants);
        bookings.add(booking);
        fileManager.saveBookings(bookings);

        System.out.println("Booking created successfully!");
        System.out.println(booking);
    }

    private boolean hasConflict(Facility facility, LocalDate date, LocalTime time) {
        for (Booking b : bookings) {
            if (b.conflictsWith(facility, date, time)) {
                return true;
            }
        }
        return false;
    }

    private void viewBookings() {
        System.out.println("\n--- All Bookings ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.printf("%-8s %-18s %-12s %-12s %-6s %-4s %-10s%n",
                "ID", "Customer", "Facility", "Date", "Time", "Pax", "Status");
        for (Booking b : bookings) {
            b.displayBooking();
        }
    }

    private void cancelBooking() {
        viewBookings();
        if (bookings.isEmpty()) {
            return;
        }
        String id = readNonEmpty("Enter booking ID to cancel: ");
        Booking booking = findBooking(id);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        if (!booking.isActive()) {
            System.out.println("Booking is already cancelled.");
            return;
        }
        booking.cancel();
        fileManager.saveBookings(bookings);
        System.out.println("Booking " + id + " cancelled.");
    }

    // -------------------- Module 2: Catalogue --------------------

    private void viewCatalogue() {
        System.out.println("\n--- RENTAL CATALOGUE ---");
        System.out.printf("%-8s %-30s %-12s %10s%n", "Code", "Item Name", "Category", "Price");
        System.out.println("------------------------------------------------------------------");
        for (RentalItem item : catalogue) {
            item.displayItem();
        }
        System.out.println("------------------------------------------------------------------");
        System.out.println("Categories: Equipment | Facility | Accessory");
    }

    // -------------------- Module 3: Transaction --------------------

    private void createOrUpdateTransaction() {
        System.out.println("\n--- RENTAL TRANSACTION ---");
        System.out.println("1. Create New Transaction");
        System.out.println("2. Update Existing Transaction (add items)");
        System.out.println("3. View All Transactions");
        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1 -> createTransaction();
            case 2 -> updateTransaction();
            case 3 -> viewTransactions();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void createTransaction() {
        String name = readNonEmpty("Customer name: ");
        String contact = readNonEmpty("Contact number: ");
        Customer customer = new Customer(name, contact);

        Booking linkedBooking = null;
        String link = readNonEmpty("Link to booking ID? (enter ID or NONE): ");
        if (!"NONE".equalsIgnoreCase(link)) {
            linkedBooking = findBooking(link);
            if (linkedBooking == null || !linkedBooking.isActive()) {
                System.out.println("Active booking not found. Creating walk-in transaction.");
                linkedBooking = null;
            } else {
                customer = linkedBooking.getCustomer();
                System.out.println("Linked to booking: " + linkedBooking);
            }
        }

        transactionCounter++;
        String txId = String.format("T%03d", transactionCounter);
        Transaction tx = new Transaction(txId, customer, linkedBooking);

        addItemsInteractively(tx);

        if (tx.getItems().isEmpty()) {
            System.out.println("No items added. Transaction cancelled.");
            return;
        }

        transactions.add(tx);
        tx.displayBill();
        System.out.println("Transaction " + txId + " created. Use menu 4 to apply promotion.");
    }

    private void updateTransaction() {
        Transaction tx = selectPendingTransaction("update");
        if (tx == null) {
            return;
        }
        addItemsInteractively(tx);
        tx.clearPromotion(); // items changed; promotion must be re-applied
        tx.displayBill();
        System.out.println("Transaction updated. Re-apply promotion from menu 4 if needed.");
    }

    private void addItemsInteractively(Transaction tx) {
        viewCatalogue();
        boolean more = true;
        while (more) {
            String code = readNonEmpty("Enter item code to add (or DONE): ");
            if ("DONE".equalsIgnoreCase(code)) {
                more = false;
                continue;
            }
            RentalItem item = findCatalogueItem(code);
            if (item == null) {
                System.out.println("Item not found.");
                continue;
            }
            int qty = readPositiveInt("Quantity: ");
            tx.addItem(item, qty);
            System.out.println("Added: " + item.getItemName() + " x" + qty);
        }
    }

    private void viewTransactions() {
        System.out.println("\n--- All Transactions ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : transactions) {
            System.out.printf("%s | %s | items=%d | final=RM%.2f | %s%n",
                    t.getTransactionId(),
                    t.getCustomer().getName(),
                    t.getItems().size(),
                    t.calculateFinalAmount(),
                    t.isCompleted() ? "PAID" : "PENDING");
        }
    }

    // -------------------- Module 4: Promotion --------------------

    private void applyPromotion() {
        System.out.println("\n--- APPLY PROMOTION ---");
        Transaction tx = selectPendingTransaction("apply promotion");
        if (tx == null) {
            return;
        }
        promotionEngine.applyBestPromotion(tx);
        tx.displayBill();
        System.out.println("Best eligible promotion applied automatically (promotions cannot be combined).");
    }

    // -------------------- Module 5: Payment --------------------

    private void makePayment() {
        System.out.println("\n--- MAKE PAYMENT ---");
        Transaction tx = selectPendingTransaction("pay");
        if (tx == null) {
            return;
        }

        // Ensure amounts are up to date; apply promo if not yet applied
        if (tx.getSelectedPromotion() == null) {
            System.out.println("No promotion applied yet. Evaluating promotions now...");
            promotionEngine.applyBestPromotion(tx);
        }

        tx.displayBill();
        System.out.println("Select payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Credit/Debit Card");
        System.out.println("3. E-Wallet");
        int method = readInt("Choice: ");

        Payment payment;
        switch (method) {
            case 1 -> payment = new CashPayment();
            case 2 -> {
                String digits = readNonEmpty("Last 4 digits of card: ");
                payment = new CardPayment(digits);
            }
            case 3 -> {
                String wallet = readNonEmpty("E-Wallet name (e.g. TouchNGo, GrabPay): ");
                payment = new EWalletPayment(wallet);
            }
            default -> {
                System.out.println("Invalid method. Payment cancelled.");
                return;
            }
        }

        double amount = tx.calculateFinalAmount();
        payment.processPayment(amount);
        tx.setPayment(payment);
        tx.markCompleted();
        fileManager.saveTransactions(transactions);
        System.out.println("Transaction " + tx.getTransactionId() + " marked as PAID.");
        tx.displayBill();
    }

    // -------------------- Helpers --------------------

    private Transaction selectPendingTransaction(String action) {
        List<Transaction> pending = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!t.isCompleted()) {
                pending.add(t);
            }
        }
        if (pending.isEmpty()) {
            System.out.println("No pending transactions to " + action + ".");
            return null;
        }
        System.out.println("Pending transactions:");
        for (Transaction t : pending) {
            System.out.printf("  %s | %s | subtotal RM%.2f%n",
                    t.getTransactionId(), t.getCustomer().getName(), t.calculateSubtotal());
        }
        String id = readNonEmpty("Enter transaction ID: ");
        Transaction tx = findTransaction(id);
        if (tx == null) {
            System.out.println("Transaction not found.");
            return null;
        }
        if (tx.isCompleted()) {
            System.out.println("Transaction already paid.");
            return null;
        }
        return tx;
    }

    private Facility findFacility(String id) {
        for (Facility f : facilities) {
            if (f.getFacilityId().equalsIgnoreCase(id)) {
                return f;
            }
        }
        return null;
    }

    private Booking findBooking(String id) {
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }

    private Transaction findTransaction(String id) {
        for (Transaction t : transactions) {
            if (t.getTransactionId().equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }

    private RentalItem findCatalogueItem(String code) {
        for (RentalItem item : catalogue) {
            if (item.getItemCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    private void persistAll() {
        fileManager.saveFacilities(facilities);
        fileManager.saveCatalogue(catalogue);
        fileManager.saveBookings(bookings);
        fileManager.saveTransactions(transactions);
    }

    private String readNonEmpty(String prompt) {
        String value;
        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("Input cannot be empty.");
            }
        } while (value.isEmpty());
        return value;
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        int value;
        do {
            value = readInt(prompt);
            if (value <= 0) {
                System.out.println("Please enter a positive number.");
            }
        } while (value <= 0);
        return value;
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String text = readNonEmpty(prompt);
            try {
                return LocalDate.parse(text, Booking.DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use format yyyy-MM-dd (e.g. 2026-11-03).");
            }
        }
    }

    private LocalTime readTime(String prompt) {
        while (true) {
            String text = readNonEmpty(prompt);
            try {
                return LocalTime.parse(text, Booking.TIME_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time. Use format HH:mm (e.g. 14:30).");
            }
        }
    }
}
