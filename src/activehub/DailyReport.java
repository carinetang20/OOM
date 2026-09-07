package activehub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End-of-day summary report for the manager.
 */
public class DailyReport {
    public void generate(List<Booking> bookings, List<Transaction> transactions) {
        ConsoleUI.section("DAILY SUMMARY REPORT");

        int activeBookings = 0;
        for (Booking b : bookings) {
            if (b.isActive()) {
                activeBookings++;
            }
        }

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
                    String key = ti.getItem().getItemCode() + " — " + ti.getItem().getItemName();
                    itemFrequency.put(key, itemFrequency.getOrDefault(key, 0) + ti.getQuantity());
                }
            }
        }

        String topItem = "N/A";
        int topQty = 0;
        for (Map.Entry<String, Integer> e : itemFrequency.entrySet()) {
            if (e.getValue() > topQty) {
                topQty = e.getValue();
                topItem = e.getKey();
            }
        }

        ConsoleUI.blank();
        ConsoleUI.boxTop();
        ConsoleUI.boxCenter(ConsoleUI.bold("Today at ActiveHub"));
        ConsoleUI.boxBlank();
        ConsoleUI.boxRow("  Active facility bookings     "
                + ConsoleUI.bold(String.valueOf(activeBookings)));
        ConsoleUI.boxRow("  Completed transactions       "
                + ConsoleUI.bold(String.valueOf(completedCount)));
        ConsoleUI.boxRow("  Total daily revenue          "
                + ConsoleUI.bold(ConsoleUI.green(ConsoleUI.money(totalRevenue))));
        ConsoleUI.boxBlank();
        ConsoleUI.boxRow("  Most rented item");
        ConsoleUI.boxRow("    " + topItem
                + (topQty > 0 ? ConsoleUI.dim("  × " + topQty) : ""));
        ConsoleUI.boxBlank();
        ConsoleUI.boxRow("  Payment methods");
        if (paymentMethods.isEmpty()) {
            ConsoleUI.boxRow("    " + ConsoleUI.dim("(no completed payments yet)"));
        } else {
            for (Map.Entry<String, Integer> e : paymentMethods.entrySet()) {
                ConsoleUI.boxRow("    • " + e.getKey() + "  "
                        + ConsoleUI.bold(String.valueOf(e.getValue())));
            }
        }
        ConsoleUI.boxBlank();
        ConsoleUI.boxBottom();
    }
}
