package de.dfki.feedback_service.feedback_webservice.models;

public class Location {
    private String name;
    private double lat;
    private double lng;

    public Location() {
    }

    public Location(Location location) {
        this.name = location.getName();
        this.lat = location.getLat();
        this.lng = location.getLng();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void cleanUp() {
        this.name = "";
        this.lat = 0;
        this.lng = 0;
    }
}
