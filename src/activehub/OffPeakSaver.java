package activehub;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Promotion A - Off-Peak Saver
 * 15% off facility/court charges only (not equipment, accessories, or service charge).
 * Eligible: Monday–Friday, start time at or after 09:00 and before 16:00
 * (15:59 yes, 16:00 no). Uses LocalDate, LocalTime and DayOfWeek.
 */
public class OffPeakSaver extends Promotion {
    public OffPeakSaver() {
        super("A", "Off-Peak Saver");
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        Booking booking = transaction.getBooking();
        if (booking == null || !booking.isActive()) {
            return false;
        }
        if (!transaction.hasFacility() || transaction.getFacilityCharge() <= 0) {
            return false;
        }

        LocalDate date = booking.getDate();
        DayOfWeek day = date.getDayOfWeek();
        LocalTime startTime = booking.getTime();

        boolean mondayToFriday = day.getValue() >= DayOfWeek.MONDAY.getValue()
                && day.getValue() <= DayOfWeek.FRIDAY.getValue();
        boolean offPeak = !startTime.isBefore(LocalTime.of(9, 0))
                && startTime.isBefore(LocalTime.of(16, 0));

        return mondayToFriday && offPeak;
    }

    @Override
    public double calculateSaving(Transaction transaction) {
        if (!isEligible(transaction)) {
            return 0.0;
        }
        return Transaction.roundMoney(transaction.getFacilityCharge() * 0.15);
    }
}
