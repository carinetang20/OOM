package activehub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End-of-day summary report for the manager.
 */
public class DailyReport {
    public void generate(List<Booking> bookings, List<Transaction> transactions) {
        System.out.println("\n========== DAILY SUMMARY REPORT ==========");

        int activeBookings = 0;
        for (Booking b : bookings) {
            if (b.isActive()) {
                activeBookings++;
            }
        }
        System.out.println("Total facility bookings (active) : " + activeBookings);

        int completedCount = 0;
        double totalRevenue = 0.0;
        Map<String, Integer> itemFrequency = new HashMap<>();
        Map<String, Integer> paymentMethods = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.isCompleted()) {
                completedCount++;
                totalRevenue += t.getFinalAmount();

                if (t.getPayment() != null) {
                    String method = t.getPayment().getPaymentMethod();
                    paymentMethods.put(method, paymentMethods.getOrDefault(method, 0) + 1);
                }

                for (TransactionItem ti : t.getItems()) {
                    String key = ti.getItem().getItemCode() + " - " + ti.getItem().getItemName();
                    itemFrequency.put(key, itemFrequency.getOrDefault(key, 0) + ti.getQuantity());
                }
            }
        }

        System.out.println("Total completed transactions     : " + completedCount);
        System.out.printf("Total daily revenue              : RM%.2f%n", totalRevenue);

        String topItem = "N/A";
        int topQty = 0;
        for (Map.Entry<String, Integer> e : itemFrequency.entrySet()) {
            if (e.getValue() > topQty) {
                topQty = e.getValue();
                topItem = e.getKey();
            }
        }
        System.out.println("Most frequently rented item      : " + topItem
                + (topQty > 0 ? " (qty " + topQty + ")" : ""));

        System.out.println("Payment methods summary:");
        if (paymentMethods.isEmpty()) {
            System.out.println("  (no completed payments yet)");
        } else {
            for (Map.Entry<String, Integer> e : paymentMethods.entrySet()) {
                System.out.println("  - " + e.getKey() + ": " + e.getValue());
            }
        }
        System.out.println("==========================================");
    }
}
