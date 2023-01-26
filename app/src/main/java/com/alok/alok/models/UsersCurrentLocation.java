package com.alok.alok.models;

public class UsersCurrentLocation {
    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    private String add;
    private Double lattitude;
    private  Double longitude;

    public UsersCurrentLocation(String add, Double lattitude, Double longitude) {
        this.add = add;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }


}
