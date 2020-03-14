package com.example.madassessment.dataEntities;

public class PointOfInterestEntity {

    private String name;
    private String type;
    private Double price;
    private Double latitude;
    private Double longitude;

    public PointOfInterestEntity(String[] values){
        this(values[0], values[1], Double.parseDouble(values[2]), Double.parseDouble(values[3]), Double.parseDouble(values[4]));
    }

    public PointOfInterestEntity(String name, String type, Double price, Double latitude, Double longitude) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Double getPrice() {
        return price;
    }

    public Double getLatitude() { return latitude; }

    public Double getLongitude() { return longitude; }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String toString() {
        String magicString = this.name + "," + this.type + "," + this.price + "," + this.latitude + "," + this.longitude;
        return magicString;
    }

}
