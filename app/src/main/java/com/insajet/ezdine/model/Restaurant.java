package com.insajet.ezdine.model;

public class Restaurant {
    public String name;
    public String phoneNumber;
    public String email;
    public String uid;

    public  Restaurant(){

    }

    public Restaurant(String name, String phoneNumber, String email, String uid){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.uid = uid;
    }
}