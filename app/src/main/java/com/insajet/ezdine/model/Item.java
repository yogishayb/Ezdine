package com.insajet.ezdine.model;

public class Item {
    public String itemId;
    public String itemName;
    public String itemPrice;
    public String itemDescri;

    public Item(){

    }
    public Item(String itemId, String itemName, String itemPrice, String itemDescri){
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemId = itemId;
        this.itemDescri = itemDescri;
    }

    public String getItemDescri() {
        return itemDescri;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }
}
