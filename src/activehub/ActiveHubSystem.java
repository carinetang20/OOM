package activehub;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ActiveHub Sports Centre console application.
 * Orchestrates booking, catalogue, transaction, promotion, payment and report
 * modules. Collections are aggregated here and persisted through FileManager.
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
        bookingCounter = 0;
        for (Booking b : bookings) {
            bookingCounter = Math.max(bookingCounter, parseNumericId(b.getBookingId()));
        }

        transactions.addAll(fileManager.loadTransactions(catalogue, bookings, promotionEngine));
        transactionCounter = 0;
        for (Transaction t : transactions) {
            transactionCounter = Math.max(transactionCounter, parseNumericId(t.getTransactionId()));
        }
    }

    private int parseNumericId(String id) {
        if (id == null || id.length() < 2) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
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
        catalogue.add(new EquipmentItem("E01", "Badminton Racquet", 8.00));
        catalogue.add(new EquipmentItem("E02", "Shuttlecock Set", 5.00));
        catalogue.add(new EquipmentItem("E03", "Basketball", 6.00));
        catalogue.add(new EquipmentItem("E04", "Futsal Ball", 6.00));

        // Facility packages (2)
        catalogue.add(new FacilityPackage("C01", "Badminton Court Package (1hr)", 40.00));
        catalogue.add(new FacilityPackage("C02", "Futsal Court Package (1hr)", 80.00));

        // Accessories / add-on services (2)
        catalogue.add(new AccessoryItem("A01", "Towel Set", 3.00));
        catalogue.add(new AccessoryItem("A02", "Locker Service", 4.00));
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
        ConsoleUI.menu("ACTIVEHUB SYSTEM",
                "Facility Booking",
                "View Rental Catalogue",
                "Create Rental Transaction",
                "Apply Promotion",
                "Make Payment",
                "Daily Report",
                "Exit");
    }

    // -------------------- Module 1 --------------------

    private void facilityBookingMenu() {
        int choice;
        do {
            ConsoleUI.menu("FACILITY BOOKING",
                    "Create Booking",
                    "View All Bookings",
                    "Cancel Booking",
                    "Back to Main Menu");
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
        String contact = readContactNumber();
        LocalDate date = readDate("Booking date (yyyy-MM-dd): ");
        LocalTime time = readTime("Booking time (HH:mm): ");
        int participants = readPositiveInt("Number of participants: ");

        ConsoleUI.blank();
        List<String[]> facilityRows = new ArrayList<>();
        for (Facility f : facilities) {
            facilityRows.add(f.toTableRow());
        }
        ConsoleUI.boxedTable("Available facilities",
                new String[]{"ID", "Name", "Type"},
                new boolean[]{false, false, false},
                facilityRows);

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
        if (bookings.isEmpty()) {
            ConsoleUI.warn("No bookings found.");
            return;
        }
        List<String[]> rows = new ArrayList<>();
        for (Booking b : bookings) {
            rows.add(b.toTableRow());
        }
        ConsoleUI.boxedTable("All Bookings",
                new String[]{"ID", "Customer", "Court", "Date", "Time", "Pax", "Status"},
                new boolean[]{false, false, false, false, false, true, false},
                rows,
                "Total: " + bookings.size());
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
        List<String[]> rows = new ArrayList<>();
        for (RentalItem item : catalogue) {
            rows.add(item.toTableRow());
        }
        ConsoleUI.boxedTable("RENTAL CATALOGUE",
                new String[]{"Code", "Item Name", "Category", "Price"},
                new boolean[]{false, false, false, true},
                rows,
                "Equipment · Facility · Accessory  |  8 items");
    }

    // -------------------- Module 3 --------------------

    private void createOrUpdateTransaction() {
        ConsoleUI.menu("RENTAL TRANSACTION",
                "Create New Transaction",
                "Update Existing Transaction  — add items",
                "View All Transactions");
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
        String contact = readContactNumber();
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
        promotionEngine.applyBestPromotion(tx);
        fileManager.saveTransactions(transactions);
        tx.displayBill();
        ConsoleUI.success("Transaction " + txId + " created.");
        if (tx.getSelectedPromotion() != null) {
            ConsoleUI.tip("Best promotion applied: " + tx.getSelectedPromotion().getCode()
                    + " — " + tx.getSelectedPromotion().getName());
        }
        ConsoleUI.tip("Menu [4] re-evaluates promotions if the bill changes.");
    }

    private void updateTransaction() {
        Transaction tx = selectPendingTransaction("update");
        if (tx == null) {
            return;
        }
        addItemsInteractively(tx);
        promotionEngine.applyBestPromotion(tx);
        fileManager.saveTransactions(transactions);
        tx.displayBill();
        ConsoleUI.success("Transaction updated.");
        if (tx.getSelectedPromotion() != null) {
            ConsoleUI.tip("Best promotion re-applied: " + tx.getSelectedPromotion().getCode());
        }
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
        if (transactions.isEmpty()) {
            ConsoleUI.warn("No transactions yet.");
            return;
        }
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : transactions) {
            String status = t.isCompleted()
                    ? ConsoleUI.green("PAID")
                    : ConsoleUI.yellow("PENDING");
            rows.add(new String[] {
                    t.getTransactionId(),
                    t.getCustomer().getName(),
                    String.valueOf(t.getItems().size()),
                    ConsoleUI.money(t.calculateFinalAmount()),
                    status
            });
        }
        ConsoleUI.boxedTable("All Transactions",
                new String[]{"ID", "Customer", "Items", "Final", "Status"},
                new boolean[]{false, false, true, true, false},
                rows);
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
        fileManager.saveTransactions(transactions);
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
        ConsoleUI.menu("SELECT PAYMENT METHOD",
                "Cash",
                "Credit / Debit Card",
                "E-Wallet");
        int method = readInt("Choice [1-3]: ");

        Payment payment;
        switch (method) {
            case 1 -> payment = new CashPayment();
            case 2 -> {
                String digits = readCardLastFour();
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
        List<String[]> rows = new ArrayList<>();
        for (Transaction t : pending) {
            rows.add(new String[] {
                    t.getTransactionId(),
                    t.getCustomer().getName(),
                    ConsoleUI.money(t.calculateSubtotal())
            });
        }
        ConsoleUI.boxedTable("Pending transactions",
                new String[]{"ID", "Customer", "Subtotal"},
                new boolean[]{false, false, true},
                rows);
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

    private String readCardLastFour() {
        while (true) {
            String raw = readNonEmpty("Last 4 digits of card: ");
            String digits = raw.trim().replaceAll("\\s+", "");
            if (CardPayment.isValidLastFour(digits)) {
                return digits;
            }
            ConsoleUI.error("Card digits must be numbers only — exactly 4 digits. Letters are not allowed.");
            ConsoleUI.tip("Example: 1234");
        }
    }

    private String readContactNumber() {
        while (true) {
            String raw = readNonEmpty("Contact number: ");
            if (Customer.isValidContact(raw)) {
                return Customer.normaliseContact(raw);
            }
            ConsoleUI.error("Invalid contact. Use 10–11 digits starting with 0.");
            ConsoleUI.tip("Examples: 0123456789  |  012-345 6789  |  +60123456789");
        }
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
