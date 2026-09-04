package activehub;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class OffPeakSaver implements Promotion {
    @Override
    public String getCode(){
        return "A";
    }
    @Override
    public String getName(){
        return "Off-Peak Saver";
    }
    @Override
    public boolean isEligible(Transaction transaction){
        Booking booking = transaction.getBooking();

        if (booking == null){
            return false;
        }
        LocalDate date = booking.getDate();
        LocalTime time = booking.getTime();

        DayOfWeek day = date.getDayOfWeek();

        boolean weekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
        boolean eligibleTime = !time.isBefore(LocalTime.of(9,0)) && time.isBefore(LocalTime.of(16,0));

        return weekday && eligibleTime;
    }
    @Override
    public double calculateSaving(Transaction transaction){
        if (isEligible((transaction)){
            return transaction.getFacilityCharge() * 0.15;
        }
        return 0.0;
    }



}
