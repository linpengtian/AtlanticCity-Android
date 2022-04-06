package com.atlanticcity.app.Models;

import com.google.gson.annotations.SerializedName;

public class FavouriteAddsClass {

    String id;
    String fav_add_id;
    String user_id;
    String add_id;
    String created_at;
    String updated_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFav_add_id() {
        return fav_add_id;
    }

    public void setFav_add_id(String fav_add_id) {
        this.fav_add_id = fav_add_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAdd_id() {
        return add_id;
    }

    public void setAdd_id(String add_id) {
        this.add_id = add_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public SliderModel getSliderModel() {
        return sliderModel;
    }

    public void setSliderModel(SliderModel sliderModel) {
        this.sliderModel = sliderModel;
    }

    @SerializedName("adds")
    SliderModel sliderModel;
}
