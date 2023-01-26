package com.alok.alok.models;

public class Products {
    private String pname;
    private String pid;
    private String image;
    private String price;
    private String date;
    private String time;
    private String description;
    private String productState;
    private String sid;
    private String lattitude;
    private String longitude;
    private String category;
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    public String getLongitude() {
        return longitude;
    }
    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
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



    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    private String sellerAddress;

    public Products() {

    }

    public Products(String pname, String pid, String image, String price, String date, String time, String description, String productState, String sellerAddress,String sid,String lattitude
    , String longitude, String category) {
        this.pname = pname;
        this.pid = pid;
        this.image = image;
        this.price = price;
        this.date = date;
        this.time = time;
        this.description = description;
        this.productState = productState;
        this.sellerAddress = sellerAddress;
        this.sid=sid;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.category = category;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }
}