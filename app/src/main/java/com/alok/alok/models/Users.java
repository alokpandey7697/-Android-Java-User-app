package com.alok.alok.models;

public class Users {
    private String name;
    private String phone;
    private String password;
    private String image;
    private String email;



    private String lattitude;
    private String longitude;
    private String uid;
    private String address;
private String delAddress;

    public String getDelAddress() {
        return delAddress;
    }

    public void setDelAddress(String delAddress) {
        this.delAddress = delAddress;
    }

    public String getDelLongitude() {
        return delLongitude;
    }

    public void setDelLongitude(String delLongitude) {
        this.delLongitude = delLongitude;
    }

    public String getDelLattitude() {
        return delLattitude;
    }

    public void setDelLattitude(String delLattitude) {
        this.delLattitude = delLattitude;
    }

    private String delLongitude;
private String delLattitude;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Users(){

    }
/*
    public Users(String name,String phone, String password,String image,String email) {
        this.name=name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.email = email;
    }*/

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public Users(String name, String phone, String password, String image, String email, String lattitude, String longitude, String uid, String address, String delAddress, String delLattitude, String delLongitude) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.email = email;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.uid = uid;
        this.address = address;
        this.delAddress = delAddress;
        this.delLongitude = delLongitude;
        this.delLattitude = delLattitude;
    }



}
