package com.insajet.ezdine.model;

public class RestInfo {

    public String restName;
    public String id;
    public String phoneNumber;
    public String email;
    public String address;
    public String website;
    public String ownerName;

    public RestInfo() {
    }

    public RestInfo(String id, String restName,String ownerName, String phoneNumber, String email, String website, String address) {
        this.restName = restName;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.website = website;
        this.ownerName = ownerName;
    }

    public String getRestName() {
        return restName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getWebsite() {
        return website;
    }
}
