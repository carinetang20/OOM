package activehub;

public class TeamBookingReward implements Promotion {

    @Override
    public String getCode(){
        return "B";
    }
    @Override
    public String getName(){
        return "Team Booking Reward";
    }
    @Override
    public boolean isEligible(Transaction transaction){
        if (!isEligible((transaction))){
            return 0.0;
        }
        int participants = transaction.getBooking().getParticipants();

        double saving = participants * 5.00;

        //maximum discount is RM60
        saving = Math.min(saving,60.00);

        //cannot reduce facility charge below RM0
        saving = Math.min(saving, transaction.getFacilityCharge());

        return saving;

    }
}
