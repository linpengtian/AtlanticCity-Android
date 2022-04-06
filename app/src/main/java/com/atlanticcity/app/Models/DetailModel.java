package com.atlanticcity.app.Models;

import com.google.gson.annotations.SerializedName;

public class DetailModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFav_deal_id() {
        return fav_deal_id;
    }

    public void setFav_deal_id(String fav_deal_id) {
        this.fav_deal_id = fav_deal_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }



    String id,fav_deal_id,user_id,item_id;

    public DealsModel getDealsModels() {
        return dealsModels;
    }

    public void setDealsModels(DealsModel dealsModels) {
        this.dealsModels = dealsModels;
    }

    @SerializedName("deals")
    DealsModel dealsModels;
}
