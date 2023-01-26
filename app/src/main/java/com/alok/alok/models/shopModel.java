package com.alok.alok.models;

public class shopModel {
    private String name;
    private String phone;
    private String address;
    private String category;
    private String lattitude;
    private String longitude;
    private String sid;
    private String image;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


public shopModel() {

}

    public shopModel(String name, String phone, String address, String category, String lattitude, String longitude, String sid, String image, String email) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.category = category;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.sid = sid;
        this.image = image;
        this.email = email;
    }





}
