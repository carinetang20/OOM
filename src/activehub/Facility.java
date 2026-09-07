package activehub;

/**
 * Represents a sports facility or court that can be booked.
 */
public class Facility {
    private String facilityId;
    private String facilityName;
    private String facilityType;

    public Facility(String facilityId, String facilityName, String facilityType) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void displayFacility() {
        ConsoleUI.tableRow("%-8s %-28s %-15s", facilityId, facilityName, facilityType);
    }

    @Override
    public String toString() {
        return facilityId + " - " + facilityName + " (" + facilityType + ")";
    }
}
