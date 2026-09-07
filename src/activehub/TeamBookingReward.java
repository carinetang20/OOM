package activehub;

/**
 * Promotion B - Team Booking Reward
 * For 8+ participants: RM5 per participant, capped at RM60,
 * and cannot reduce facility charge below RM0.
 */
public class TeamBookingReward implements Promotion {
    @Override
    public String getCode() {
        return "B";
    }

    @Override
    public String getName() {
        return "Team Booking Reward";
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        Booking booking = transaction.getBooking();
        if (booking == null || !transaction.hasFacility()) {
            return false;
        }
        return booking.getParticipants() >= 8;
    }

    @Override
    public double calculateSaving(Transaction transaction) {
        if (!isEligible(transaction)) {
            return 0.0;
        }
        int participants = transaction.getBooking().getParticipants();
        double saving = participants * 5.00;
        saving = Math.min(saving, 60.00);
        saving = Math.min(saving, transaction.getFacilityCharge());
        return saving;
    }
}
