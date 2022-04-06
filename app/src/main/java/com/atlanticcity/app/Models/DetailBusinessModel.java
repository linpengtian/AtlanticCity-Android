package com.atlanticcity.app.Models;

import com.google.gson.annotations.SerializedName;

public class DetailBusinessModel {

    String id;
    String fav_business_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFav_business_id() {
        return fav_business_id;
    }

    public void setFav_business_id(String fav_business_id) {
        this.fav_business_id = fav_business_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public BusinessModel getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(BusinessModel businessModel) {
        this.businessModel = businessModel;
    }

    String user_id;
    String business_id;

    @SerializedName("businesses")
    BusinessModel businessModel;
}
