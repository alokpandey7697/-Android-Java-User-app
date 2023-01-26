package com.alok.alok.models;

public class Cart {
    private String pid;
    private String pname;
    private String price;
    private String quantity;
    private String discount;
    private String sellerAddress;
    private String lattitude;
    private String longitude;
    private String sid;


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
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


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;




    public Cart(){}



    public Cart(String pid, String pname, String price, String quantity, String discount, String sellerAddress,String image) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.sellerAddress = sellerAddress;
        this.image = image;


    }


    public String getSellerAddress() {
        return sellerAddress; }

    public void setSellerAddress(String sellerAddress) { this.sellerAddress = sellerAddress; }
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
