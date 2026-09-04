package activehub;

public class Facility {
    private String facilityId;
    private String facilityName;
    private String facilityType;

    public Facility(String facilityId, String facilityName, String facilityType){
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
    }
    public String getFacilityId(){
        return facilityId;
    }
    public String getFacilityName(){
        return facilityName;
    }
    public String getFacilityType(){
        return facilityType;
    }

    public void displayFacility(){
        System.out.println("Facility ID: " + facilityId);
        System.out.println("Facility Name: " + facilityName);
        System.out.println("Facility Type: " + facilityType);
    }
}
