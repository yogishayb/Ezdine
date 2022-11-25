package com.insajet.ezdine.model;

import android.widget.ImageView;

public class CategoryModel {
    String id;
    String categoryName;
    String itemCount;

    public CategoryModel(){

    }

    public CategoryModel(String id, String categoryName, String itemCount){
        this.id = id;
        this.categoryName = categoryName;
        this.itemCount = itemCount;
    }

    public String getCategoryName() { return categoryName; }

    public String getId() { return id; }
    public  String getItemCount(){ return itemCount;}
}
