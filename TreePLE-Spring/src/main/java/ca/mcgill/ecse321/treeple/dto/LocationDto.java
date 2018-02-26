package ca.mcgill.ecse321.treeple.dto;

public class LocationDto {

    private int locationId;
    private double latitude;
    private double longitude;

    public LocationDto() {
    }

    public LocationDto(int locationId, double latitude, double longitude) {
        this.locationId = locationId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLocationId() {
        return locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
