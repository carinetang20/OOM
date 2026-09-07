package activehub;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Promotion A - Off-Peak Saver
 * 15% discount on facility/court charges for Mon–Fri bookings
 * starting at or after 09:00 and before 16:00.
 */
public class OffPeakSaver implements Promotion {
    @Override
    public String getCode() {
        return "A";
    }

    @Override
    public String getName() {
        return "Off-Peak Saver";
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        Booking booking = transaction.getBooking();
        if (booking == null || !transaction.hasFacility()) {
            return false;
        }

        DayOfWeek day = booking.getDayOfWeek();
        LocalTime time = booking.getTime();

        boolean weekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
        // Eligible: >= 09:00 and < 16:00 (3:59 PM yes, 4:00 PM no)
        boolean eligibleTime = !time.isBefore(LocalTime.of(9, 0))
                && time.isBefore(LocalTime.of(16, 0));

        return weekday && eligibleTime;
    }

    @Override
    public double calculateSaving(Transaction transaction) {
        if (!isEligible(transaction)) {
            return 0.0;
        }
        return transaction.getFacilityCharge() * 0.15;
    }
}
