package activehub;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A facility booking made by a customer.
 * Uses java.time for date/time (not strings) for scheduling decisions.
 */
public class Booking {
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private String bookingId;
    private Customer customer;
    private Facility facility;
    private LocalDate date;
    private LocalTime time;
    private int participants;
    private String status; // ACTIVE or CANCELLED

    public Booking(String bookingId, Customer customer, Facility facility,
                   LocalDate date, LocalTime time, int participants) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.facility = facility;
        this.date = date;
        this.time = time;
        this.participants = participants;
        this.status = "ACTIVE";
    }

    public String getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getParticipants() {
        return participants;
    }

    public String getStatus() {
        return status;
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    /**
     * Conflict check: same facility, same date, same start time, and still active.
     */
    public boolean conflictsWith(Facility otherFacility, LocalDate otherDate, LocalTime otherTime) {
        if (!isActive()) {
            return false;
        }
        return facility.getFacilityId().equalsIgnoreCase(otherFacility.getFacilityId())
                && date.equals(otherDate)
                && time.equals(otherTime);
    }

    public void displayBooking() {
        String statusLabel = isActive()
                ? ConsoleUI.green(status)
                : ConsoleUI.red(status);
        String name = customer.getName();
        if (name.length() > 16) {
            name = name.substring(0, 15) + "…";
        }
        ConsoleUI.tableRow("%-8s %-16s %-8s %-12s %-6s %-4d %-10s",
                bookingId,
                name,
                facility.getFacilityId(),
                date.format(DATE_FMT),
                time.format(TIME_FMT),
                participants,
                statusLabel);
    }

    public String toFileLine() {
        return bookingId + "|"
                + customer.getName() + "|"
                + customer.getContactNumber() + "|"
                + facility.getFacilityId() + "|"
                + date.format(DATE_FMT) + "|"
                + time.format(TIME_FMT) + "|"
                + participants + "|"
                + status;
    }

    @Override
    public String toString() {
        return bookingId + " | " + customer.getName() + " | " + facility.getFacilityName()
                + " | " + date.format(DATE_FMT) + " " + time.format(TIME_FMT)
                + " | pax=" + participants + " | " + status;
    }
}
