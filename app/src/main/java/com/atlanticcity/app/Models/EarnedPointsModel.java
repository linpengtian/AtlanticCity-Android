package com.atlanticcity.app.Models;

import com.google.gson.annotations.SerializedName;

public class EarnedPointsModel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    String type;
    String points;
    String created_at;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
    @SerializedName("user")
    UserModel userModel;

    public DealsModel getDealsModel() {
        return dealsModel;
    }

    public void setDealsModel(DealsModel dealsModel) {
        this.dealsModel = dealsModel;
    }

    @SerializedName("item")
    DealsModel dealsModel;

}
