package com.insajet.ezdine.model;

public class ProfileModel {

    private String label,value;
    private int imageId;
    private String bg = null;
    private int type;

    public ProfileModel(String label, String value,int imageId,int type){
        this.label = label;
        this.value = value;
        this.imageId = imageId;
        this.type  = type;
    }
    public ProfileModel(String label, String value,int imageId, String bg,int type){
        this.label = label;
        this.value = value;
        this.imageId = imageId;
        this.bg = bg;
        this.type = type;
    }

    public String getBg(){
        return bg;
    }
    public String getLabel() {
        return label;
    }

    public int getType() {
        return type;
    }

    public String getValue(){
        return value;
    }
    public int getImageId(){
        return imageId;
    }
}
