package com.sathya.mobileotpauth.helper;

public class RideModel {

    public static String PRICE = "PRICE";
    public static String TYPE = "TYPE";
    public static String FROM = "FROM";
    public static String TO = "TO";

    private String price;
    private String type;
    private String from;
    private String to;
    private int imgid;

    public RideModel(String price, String type, String from, String to, int imgid) {
        this.price = price;
        this.type = type;
        this.from = from;
        this.to = to;
        this.imgid = imgid;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getImgid() {
        return imgid;
    }
}