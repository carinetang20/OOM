package activehub;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Lightweight, dependency-free test harness (plain Java, no JUnit needed) that
 * verifies the ActiveHub business rules against the DIT2123 assignment brief:
 * the three promotions, the best-of promotion selection with alphabetical
 * tie-break, the 10% service charge / final-amount maths, and booking-conflict
 * detection.
 *
 * Run:  javac -d out/test -cp out/classes test/ActiveHubTests.java
 *       java  -cp out/classes:out/test activehub.ActiveHubTests
 */
public class ActiveHubTests {
    private static int passed = 0;
    private static int failed = 0;

    // ---- catalogue fixtures ----
    private static final RentalItem C01 = new RentalItem("C01", "Badminton Court Package (1hr)", "Facility", 40.00);
    private static final RentalItem C02 = new RentalItem("C02", "Futsal Court Package (1hr)", "Facility", 80.00);
    private static final RentalItem CX20 = new RentalItem("CX", "Mini Court Package", "Facility", 20.00);
    private static final RentalItem E01 = new RentalItem("E01", "Badminton Racquet", "Equipment", 8.00);
    private static final RentalItem E02 = new RentalItem("E02", "Shuttlecock Set", "Equipment", 5.00);
    private static final RentalItem A01 = new RentalItem("A01", "Towel Set", "Accessory", 3.00);
    private static final Facility F01 = new Facility("F01", "Badminton Court 1", "Badminton");

    public static void main(String[] args) {
        testOffPeakTimeBoundaries();
        testOffPeakWeekend();
        testOffPeakRequiresBooking();
        testTeamRewardCaps();
        testTeamRewardBelowEight();
        testBundleDiscountEligibility();
        testBestPromotionSelection();
        testTieBreakLowerCodeWins();
        testServiceChargeAndFinalAmount();
        testAddItemMergesQuantity();
        testBookingConflict();

        System.out.println();
        System.out.println("==================================================");
        System.out.printf("RESULT: %d passed, %d failed%n", passed, failed);
        System.out.println("==================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // Promotion A — Off-Peak Saver: 15% of facility charge, Mon–Fri, >=09:00 and <16:00.
    private static void testOffPeakTimeBoundaries() {
        LocalDate tue = LocalDate.of(2026, 11, 3); // Tuesday
        OffPeakSaver a = new OffPeakSaver();

        // 15:59 eligible -> 15% of 40 = 6.00
        Transaction t1 = txWithBooking(tue, LocalTime.of(15, 59), 4, C01, 1);
        check("A eligible at 15:59", a.isEligible(t1));
        checkMoney("A saving at 15:59 = 6.00", a.calculateSaving(t1), 6.00);

        // 16:00 NOT eligible
        Transaction t2 = txWithBooking(tue, LocalTime.of(16, 0), 4, C01, 1);
        check("A NOT eligible at 16:00", !a.isEligible(t2));

        // 09:00 eligible (boundary)
        Transaction t3 = txWithBooking(tue, LocalTime.of(9, 0), 4, C01, 1);
        check("A eligible at 09:00", a.isEligible(t3));

        // 08:59 NOT eligible
        Transaction t4 = txWithBooking(tue, LocalTime.of(8, 59), 4, C01, 1);
        check("A NOT eligible at 08:59", !a.isEligible(t4));
    }

    private static void testOffPeakWeekend() {
        LocalDate sat = LocalDate.of(2026, 11, 7); // Saturday
        OffPeakSaver a = new OffPeakSaver();
        Transaction t = txWithBooking(sat, LocalTime.of(10, 0), 4, C01, 1);
        check("A NOT eligible on weekend", !a.isEligible(t));
    }

    private static void testOffPeakRequiresBooking() {
        OffPeakSaver a = new OffPeakSaver();
        Transaction walkIn = new Transaction("T", new Customer("Walk", "0"), null);
        walkIn.addItem(C01, 1);
        check("A NOT eligible for walk-in (no booking)", !a.isEligible(walkIn));
    }

    // Promotion B — Team Booking Reward: RM5/participant, cap RM60, not below facility charge.
    private static void testTeamRewardCaps() {
        LocalDate tue = LocalDate.of(2026, 11, 3);
        TeamBookingReward b = new TeamBookingReward();

        // 10 pax * 5 = 50, but facility charge 40 -> capped to 40
        Transaction t1 = txWithBooking(tue, LocalTime.of(10, 0), 10, C01, 1);
        checkMoney("B: 10 pax capped at facility charge 40", b.calculateSaving(t1), 40.00);

        // 13 pax * 5 = 65, cap RM60, facility charge 80 -> 60
        Transaction t2 = txWithBooking(tue, LocalTime.of(10, 0), 13, C02, 1);
        checkMoney("B: 13 pax capped at RM60", b.calculateSaving(t2), 60.00);

        // 8 pax * 5 = 40, facility charge 80 -> 40
        Transaction t3 = txWithBooking(tue, LocalTime.of(10, 0), 8, C02, 1);
        checkMoney("B: 8 pax = 40", b.calculateSaving(t3), 40.00);
    }

    private static void testTeamRewardBelowEight() {
        LocalDate tue = LocalDate.of(2026, 11, 3);
        TeamBookingReward b = new TeamBookingReward();
        Transaction t = txWithBooking(tue, LocalTime.of(10, 0), 7, C01, 1);
        check("B NOT eligible with 7 participants", !b.isEligible(t));
    }

    // Promotion C — Equipment Bundle Discount: RM20 when >=1 facility item AND >=3 equipment units.
    private static void testBundleDiscountEligibility() {
        EquipmentBundleDiscount c = new EquipmentBundleDiscount();

        // Facility + 2 racquets + 1 shuttle set = 3 equipment units -> eligible
        Transaction t1 = new Transaction("T", cust(), null);
        t1.addItem(C01, 1);
        t1.addItem(E01, 2);
        t1.addItem(E02, 1);
        check("C eligible: facility + 3 equipment units", c.isEligible(t1));
        checkMoney("C saving = 20.00", c.calculateSaving(t1), 20.00);

        // Facility + only 2 equipment -> not eligible
        Transaction t2 = new Transaction("T", cust(), null);
        t2.addItem(C01, 1);
        t2.addItem(E01, 2);
        check("C NOT eligible: only 2 equipment units", !c.isEligible(t2));

        // 3 equipment but no facility -> not eligible
        Transaction t3 = new Transaction("T", cust(), null);
        t3.addItem(E01, 3);
        check("C NOT eligible: 3 equipment but no facility", !c.isEligible(t3));

        // Accessories do NOT count as equipment
        Transaction t4 = new Transaction("T", cust(), null);
        t4.addItem(C01, 1);
        t4.addItem(A01, 3);
        check("C NOT eligible: accessories are not equipment", !c.isEligible(t4));
    }

    // Best-of selection: README scenario -> A=6, B=40, C=20 -> B wins.
    private static void testBestPromotionSelection() {
        LocalDate tue = LocalDate.of(2026, 11, 3);
        Transaction t = txWithBooking(tue, LocalTime.of(10, 0), 10, C01, 1);
        t.addItem(E01, 2);
        t.addItem(E02, 1);
        new PromotionEngine().applyBestPromotion(t);
        check("Best promo selected is B", t.getSelectedPromotion() != null
                && t.getSelectedPromotion().getCode().equals("B"));
        checkMoney("Best saving = 40.00", t.getDiscountAmount(), 40.00);
        check("All 3 promotions listed as eligible", t.getEligiblePromotionSummary().size() == 3);
    }

    // Tie-break: B and C both save 20 -> lower alphabetical code (B) must win.
    private static void testTieBreakLowerCodeWins() {
        LocalDate tue = LocalDate.of(2026, 11, 3);
        // facility charge 20, 8 pax -> B = min(40,60,20) = 20 ; facility + 3 equipment -> C = 20
        Transaction t = txWithBooking(tue, LocalTime.of(10, 0), 8, CX20, 1);
        t.addItem(E01, 2);
        t.addItem(E02, 1);
        new PromotionEngine().applyBestPromotion(t);
        check("Tie B==C resolves to lower code B", t.getSelectedPromotion() != null
                && t.getSelectedPromotion().getCode().equals("B"));
        checkMoney("Tie saving = 20.00", t.getDiscountAmount(), 20.00);
    }

    // 10% service charge and final payable maths.
    private static void testServiceChargeAndFinalAmount() {
        Transaction t = new Transaction("T", cust(), null);
        t.addItem(C01, 1); // 40
        t.addItem(E01, 2); // 16
        t.addItem(E02, 1); // 5  -> subtotal 61
        checkMoney("Subtotal = 61.00", t.calculateSubtotal(), 61.00);
        checkMoney("Service charge (no promo) = 6.10", t.calculateServiceCharge(), 6.10);
        checkMoney("Final (no promo) = 67.10", t.calculateFinalAmount(), 67.10);

        t.setSelectedPromotion(new TeamBookingReward(), 40.00); // discount 40
        checkMoney("Service charge after discount = 2.10", t.calculateServiceCharge(), 2.10);
        checkMoney("Final after RM40 discount = 23.10", t.calculateFinalAmount(), 23.10);
    }

    private static void testAddItemMergesQuantity() {
        Transaction t = new Transaction("T", cust(), null);
        t.addItem(E01, 2);
        t.addItem(E01, 3);
        check("Duplicate item codes merge into one line", t.getItems().size() == 1);
        check("Merged quantity = 5", t.getItems().get(0).getQuantity() == 5);
    }

    private static void testBookingConflict() {
        LocalDate d = LocalDate.of(2026, 11, 3);
        Booking b = new Booking("B001", cust(), F01, d, LocalTime.of(10, 0), 4);
        check("Conflict: same facility/date/time", b.conflictsWith(F01, d, LocalTime.of(10, 0)));
        check("No conflict: different time", !b.conflictsWith(F01, d, LocalTime.of(11, 0)));
        b.cancel();
        check("No conflict once cancelled", !b.conflictsWith(F01, d, LocalTime.of(10, 0)));
    }

    // ---- helpers ----
    private static Customer cust() {
        return new Customer("Test", "0100000000");
    }

    private static Transaction txWithBooking(LocalDate date, LocalTime time, int pax,
                                             RentalItem facilityItem, int qty) {
        Booking booking = new Booking("B001", cust(), F01, date, time, pax);
        Transaction tx = new Transaction("T001", cust(), booking);
        tx.addItem(facilityItem, qty);
        return tx;
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + label);
        } else {
            failed++;
            System.out.println("  FAIL  " + label);
        }
    }

    private static void checkMoney(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (ok) {
            passed++;
            System.out.printf("  PASS  %s (=%.2f)%n", label, actual);
        } else {
            failed++;
            System.out.printf("  FAIL  %s (expected %.2f, got %.2f)%n", label, expected, actual);
        }
    }
}
