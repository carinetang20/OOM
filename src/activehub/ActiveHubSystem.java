package activehub;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ActiveHub Sports Centre console application.
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

    private void seedCatalogue() {
        // Equipment (4)
        catalogue.add(new RentalItem("E01", "Badminton Racquet", "Equipment", 8.00));

        catalogue.add(new RentalItem("E02", "Shuttlecock Set", "Equipment", 5.00));

        catalogue.add(new RentalItem("E03", "Basketball", "Equipment", 6.00));

        catalogue.add(new RentalItem("E04", "Futsal Ball", "Equipment", 6.00));

        // Facility packages (2)
        catalogue.add(new RentalItem("C01", "Badminton Court Package (1hr)", "Facility", 40.00));

        catalogue.add(new RentalItem("C02", "Futsal Court Package (1hr)", "Facility", 80.00));

        // Accessories (2)
        catalogue.add(new RentalItem("A01", "Towel Set", "Accessory", 3.00));

        catalogue.add(new RentalItem("A02", "Locker Service", "Accessory", 4.00));
    }

    public void run() {
        ConsoleUI.welcomeSplash();
        int choice;
        do {
            printMainMenu();
            choice = readInt("Select option [1-7]: ");
            switch (choice) {
                case 1 -> facilityBookingMenu();
                case 2 -> viewCatalogue();
                case 3 -> createOrUpdateTransaction();
                case 4 -> applyPromotion();
                case 5 -> makePayment();
                case 6 -> dailyReport.generate(bookings, transactions);
                case 7 -> {
                    persistAll();
                    ConsoleUI.goodbye();
                }
                default -> ConsoleUI.error("Invalid choice. Please enter 1 to 7.");
            }
        } while (choice != 7);
        scanner.close();
    }

    private void printMainMenu() {
        ConsoleUI.section("ACTIVEHUB SYSTEM");
        ConsoleUI.blank();
        ConsoleUI.menuItem(1, "Facility Booking", "create / view / cancel");
        ConsoleUI.menuItem(2, "View Rental Catalogue", "8 rental items");
        ConsoleUI.menuItem(3, "Create Rental Transaction", "new / update / list");
        ConsoleUI.menuItem(4, "Apply Promotion", "best of A / B / C");
        ConsoleUI.menuItem(5, "Make Payment", "cash / card / e-wallet");
        ConsoleUI.menuItem(6, "Daily Report", "manager summary");
        ConsoleUI.menuItem(7, "Exit", "save & quit");
        ConsoleUI.tip("Dates: yyyy-MM-dd   ·   Times: HH:mm (24-hour)");
    }

    // -------------------- Module 1 --------------------

    private void facilityBookingMenu() {
        int choice;
        do {
            ConsoleUI.section("FACILITY BOOKING");
            ConsoleUI.blank();
            ConsoleUI.menuItem(1, "Create Booking");
            ConsoleUI.menuItem(2, "View All Bookings");
            ConsoleUI.menuItem(3, "Cancel Booking");
            ConsoleUI.menuItem(4, "Back to Main Menu");
            ConsoleUI.blank();
            choice = readInt("Select option [1-4]: ");
            switch (choice) {
                case 1 -> createBooking();
                case 2 -> viewBookings();
                case 3 -> cancelBooking();
                case 4 -> { }
                default -> ConsoleUI.error("Invalid choice.");
            }
        } while (choice != 4);
    }

    private void createBooking() {
        ConsoleUI.section("Create Booking");
        String name = readNonEmpty("Customer name: ");
        String contact = readNonEmpty("Contact number: ");
        LocalDate date = readDate("Booking date (yyyy-MM-dd): ");
        LocalTime time = readTime("Booking time (HH:mm): ");
        int participants = readPositiveInt("Number of participants: ");

        ConsoleUI.blank();
        ConsoleUI.info("Available facilities");
        ConsoleUI.tableHeader("%-8s %-28s %-15s", "ID", "Name", "Type");
        for (Facility f : facilities) {
            f.displayFacility();
        }

        Facility facility = null;
        while (facility == null) {
            String facilityId = readNonEmpty("Preferred facility ID: ");
            facility = findFacility(facilityId);
            if (facility == null) {
                ConsoleUI.error("Facility not found. Try again (e.g. F01).");
            }
        }

        if (hasConflict(facility, date, time)) {
            ConsoleUI.error("This facility is already booked for "
                    + date.format(Booking.DATE_FMT) + " at "
                    + time.format(Booking.TIME_FMT) + ".");
            ConsoleUI.warn("Please choose another facility, date, or time.");
            return;
        }

        bookingCounter++;
        String bookingId = String.format("B%03d", bookingCounter);
        Customer customer = new Customer(name, contact);
        Booking booking = new Booking(bookingId, customer, facility, date, time, participants);
        bookings.add(booking);
        fileManager.saveBookings(bookings);

        ConsoleUI.blank();
        ConsoleUI.success("Booking created successfully!");
        ConsoleUI.boxTop();
        ConsoleUI.boxRow("  ID           : " + ConsoleUI.bold(bookingId));
        ConsoleUI.boxRow("  Customer     : " + name);
        ConsoleUI.boxRow("  Facility     : " + facility.getFacilityName());
        ConsoleUI.boxRow("  Date / Time  : " + date.format(Booking.DATE_FMT)
                + "  " + time.format(Booking.TIME_FMT));
        ConsoleUI.boxRow("  Participants : " + participants);
        ConsoleUI.boxRow("  Status       : " + ConsoleUI.green("ACTIVE"));
        ConsoleUI.boxBottom();
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
        ConsoleUI.section("All Bookings");
        if (bookings.isEmpty()) {
            ConsoleUI.warn("No bookings found.");
            return;
        }
        ConsoleUI.tableHeader("%-8s %-16s %-8s %-12s %-6s %-4s %-10s",
                "ID", "Customer", "Court", "Date", "Time", "Pax", "Status");
        for (Booking b : bookings) {
            b.displayBooking();
        }
        ConsoleUI.tip("Total: " + bookings.size());
    }

    private void cancelBooking() {
        viewBookings();
        if (bookings.isEmpty()) {
            return;
        }
        String id = readNonEmpty("Enter booking ID to cancel: ");
        Booking booking = findBooking(id);
        if (booking == null) {
            ConsoleUI.error("Booking not found.");
            return;
        }
        if (!booking.isActive()) {
            ConsoleUI.warn("Booking is already cancelled.");
            return;
        }
        booking.cancel();
        fileManager.saveBookings(bookings);
        ConsoleUI.success("Booking " + id + " cancelled.");
    }

    // -------------------- Module 2 --------------------

    private void viewCatalogue() {
        ConsoleUI.section("RENTAL CATALOGUE");
        ConsoleUI.tableHeader("%-8s %-30s %-12s %8s", "Code", "Item Name", "Category", "Price");
        for (RentalItem item : catalogue) {
            item.displayItem();
        }
        ConsoleUI.tip("Equipment · Facility · Accessory  |  8 items");
    }

    // -------------------- Module 3 --------------------

    private void createOrUpdateTransaction() {
        ConsoleUI.section("RENTAL TRANSACTION");
        ConsoleUI.blank();
        ConsoleUI.menuItem(1, "Create New Transaction");
        ConsoleUI.menuItem(2, "Update Existing Transaction", "add items");
        ConsoleUI.menuItem(3, "View All Transactions");
        ConsoleUI.blank();
        int choice = readInt("Select option [1-3]: ");
        switch (choice) {
            case 1 -> createTransaction();
            case 2 -> updateTransaction();
            case 3 -> viewTransactions();
            default -> ConsoleUI.error("Invalid choice.");
        }
    }

    private void createTransaction() {
        ConsoleUI.section("New Transaction");
        String name = readNonEmpty("Customer name: ");
        String contact = readNonEmpty("Contact number: ");
        Customer customer = new Customer(name, contact);

        Booking linkedBooking = null;
        String link = readNonEmpty("Link to booking ID? (ID or NONE): ");
        if (!"NONE".equalsIgnoreCase(link)) {
            linkedBooking = findBooking(link);
            if (linkedBooking == null || !linkedBooking.isActive()) {
                ConsoleUI.warn("Active booking not found. Creating walk-in transaction.");
                linkedBooking = null;
            } else {
                customer = linkedBooking.getCustomer();
                ConsoleUI.success("Linked to booking " + linkedBooking.getBookingId());
            }
        }

        transactionCounter++;
        String txId = String.format("T%03d", transactionCounter);
        Transaction tx = new Transaction(txId, customer, linkedBooking);

        addItemsInteractively(tx);

        if (tx.getItems().isEmpty()) {
            ConsoleUI.error("No items added. Transaction cancelled.");
            return;
        }

        transactions.add(tx);
        tx.displayBill();
        ConsoleUI.success("Transaction " + txId + " created.");
        ConsoleUI.tip("Next: use menu [4] to apply promotion.");
    }

    private void updateTransaction() {
        Transaction tx = selectPendingTransaction("update");
        if (tx == null) {
            return;
        }
        addItemsInteractively(tx);
        tx.clearPromotion();
        tx.displayBill();
        ConsoleUI.success("Transaction updated.");
        ConsoleUI.tip("Re-apply promotion from menu [4] if needed.");
    }

    private void addItemsInteractively(Transaction tx) {
        viewCatalogue();
        ConsoleUI.blank();
        ConsoleUI.info("Add items to the bill. Type DONE when finished.");
        boolean more = true;
        while (more) {
            String code = readNonEmpty("Item code (or DONE): ");
            if ("DONE".equalsIgnoreCase(code)) {
                more = false;
                continue;
            }
            RentalItem item = findCatalogueItem(code);
            if (item == null) {
                ConsoleUI.error("Item not found.");
                continue;
            }
            int qty = readPositiveInt("Quantity: ");
            tx.addItem(item, qty);
            ConsoleUI.success("Added " + item.getItemName() + " × " + qty);
        }
    }

    private void viewTransactions() {
        ConsoleUI.section("All Transactions");
        if (transactions.isEmpty()) {
            ConsoleUI.warn("No transactions yet.");
            return;
        }
        ConsoleUI.tableHeader("%-8s %-18s %-8s %12s %-10s",
                "ID", "Customer", "Items", "Final", "Status");
        for (Transaction t : transactions) {
            String status = t.isCompleted()
                    ? ConsoleUI.green("PAID")
                    : ConsoleUI.yellow("PENDING");
            ConsoleUI.tableRow("%-8s %-18s %-8d %12s %-10s",
                    t.getTransactionId(),
                    truncate(t.getCustomer().getName(), 18),
                    t.getItems().size(),
                    ConsoleUI.money(t.calculateFinalAmount()),
                    status);
        }
    }

    // -------------------- Module 4 --------------------

    private void applyPromotion() {
        ConsoleUI.section("APPLY PROMOTION");
        ConsoleUI.tip("System picks the single best saving from A / B / C.");
        Transaction tx = selectPendingTransaction("apply promotion");
        if (tx == null) {
            return;
        }
        promotionEngine.applyBestPromotion(tx);
        tx.displayBill();
        if (tx.getSelectedPromotion() != null) {
            ConsoleUI.success("Applied " + tx.getSelectedPromotion().getCode()
                    + " — " + tx.getSelectedPromotion().getName());
        } else {
            ConsoleUI.warn("No eligible promotion for this transaction.");
        }
    }

    // -------------------- Module 5 --------------------

    private void makePayment() {
        ConsoleUI.section("MAKE PAYMENT");
        Transaction tx = selectPendingTransaction("pay");
        if (tx == null) {
            return;
        }

        if (tx.getSelectedPromotion() == null) {
            ConsoleUI.info("No promotion applied yet — evaluating now...");
            promotionEngine.applyBestPromotion(tx);
        }

        tx.displayBill();
        ConsoleUI.blank();
        ConsoleUI.info("Select payment method");
        ConsoleUI.menuItem(1, "Cash");
        ConsoleUI.menuItem(2, "Credit / Debit Card");
        ConsoleUI.menuItem(3, "E-Wallet");
        ConsoleUI.blank();
        int method = readInt("Choice [1-3]: ");

        Payment payment;
        switch (method) {
            case 1 -> payment = new CashPayment();
            case 2 -> {
                String digits = readNonEmpty("Last 4 digits of card: ");
                payment = new CardPayment(digits);
            }
            case 3 -> {
                String wallet = readNonEmpty("E-Wallet name (e.g. TouchNGo): ");
                payment = new EWalletPayment(wallet);
            }
            default -> {
                ConsoleUI.error("Invalid method. Payment cancelled.");
                return;
            }
        }

        double amount = tx.calculateFinalAmount();
        payment.processPayment(amount);
        tx.setPayment(payment);
        tx.markCompleted();
        fileManager.saveTransactions(transactions);
        ConsoleUI.success("Transaction " + tx.getTransactionId() + " marked as PAID.");
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
            ConsoleUI.warn("No pending transactions to " + action + ".");
            return null;
        }
        ConsoleUI.blank();
        ConsoleUI.info("Pending transactions");
        ConsoleUI.tableHeader("%-8s %-20s %12s", "ID", "Customer", "Subtotal");
        for (Transaction t : pending) {
            ConsoleUI.tableRow("%-8s %-20s %12s",
                    t.getTransactionId(),
                    truncate(t.getCustomer().getName(), 20),
                    ConsoleUI.money(t.calculateSubtotal()));
        }
        String id = readNonEmpty("Enter transaction ID: ");
        Transaction tx = findTransaction(id);
        if (tx == null) {
            ConsoleUI.error("Transaction not found.");
            return null;
        }
        if (tx.isCompleted()) {
            ConsoleUI.warn("Transaction already paid.");
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

    private String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 1) + "…";
    }

    private String readNonEmpty(String prompt) {
        String value;
        do {
            ConsoleUI.prompt(prompt);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                ConsoleUI.error("Input cannot be empty.");
            }
        } while (value.isEmpty());
        return value;
    }

    private int readInt(String prompt) {
        while (true) {
            ConsoleUI.prompt(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                ConsoleUI.error("Please enter a valid number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        int value;
        do {
            value = readInt(prompt);
            if (value <= 0) {
                ConsoleUI.error("Please enter a positive number.");
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
                ConsoleUI.error("Invalid date. Use yyyy-MM-dd (e.g. 2026-11-03).");
            }
        }
    }

    private LocalTime readTime(String prompt) {
        while (true) {
            String text = readNonEmpty(prompt);
            try {
                return LocalTime.parse(text, Booking.TIME_FMT);
            } catch (DateTimeParseException e) {
                ConsoleUI.error("Invalid time. Use HH:mm (e.g. 14:30).");
            }
        }
    }
}
