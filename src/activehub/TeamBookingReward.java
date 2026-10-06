package activehub;

/**
 * Promotion B - Team Booking Reward
 * Booking with 8 or more participants: RM5 per participant.
 * Cap RM60 per transaction. Cannot reduce facility charge below RM0.
 */
public class TeamBookingReward extends Promotion {
    public TeamBookingReward() {
        super("B", "Team Booking Reward");
    }

    @Override
    public boolean isEligible(Transaction transaction) {
        Booking booking = transaction.getBooking();
        if (booking == null || !booking.isActive()) {
            return false;
        }
        if (transaction.getFacilityCharge() <= 0) {
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
        return Transaction.roundMoney(saving);
    }
}
