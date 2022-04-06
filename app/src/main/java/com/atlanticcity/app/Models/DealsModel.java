package com.atlanticcity.app.Models;

import com.google.gson.annotations.SerializedName;

public class DealsModel {



 /*   public ArrayList<BusinessModel> getBusinessModels() {
        return businessModels;
    }

    public void setBusinessModels(ArrayList<BusinessModel> businessModels) {
        this.businessModels = businessModels;
    }*/
/*
    @SerializedName("business")
    ArrayList<BusinessModel> businessModels;*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDeal_expire_at() {
        return deal_expire_at;
    }

    public void setDeal_expire_at(String deal_expire_at) {
        this.deal_expire_at = deal_expire_at;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDealtype_id() {
        return dealtype_id;
    }

    public void setDealtype_id(String dealtype_id) {
        this.dealtype_id = dealtype_id;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getIs_favorited_count() {
        return is_favorited_count;
    }

    public void setIs_favorited_count(String is_favorited_count) {
        this.is_favorited_count = is_favorited_count;
    }


    String id;
    String item_id;
    String business_user_id;
    String title;
    String description;
    String price;
    String discount;
    String deal_expire_at;
    String avatar;
    String dealtype_id;
    String approved;
    String is_favorited_count;
    String deal_views_count;
    String add_id;

    public String getAdd_views_count() {
        return add_views_count;
    }

    public void setAdd_views_count(String add_views_count) {
        this.add_views_count = add_views_count;
    }

    String add_views_count;

    public String getAdd_id() {
        return add_id;
    }

    public void setAdd_id(String add_id) {
        this.add_id = add_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    String date;
    String url;
    String image;
    String type;
    String status;
    String created_at;
    String updated_at;



    @SerializedName("business")
    BusinessModel businessModel;


   /* public DealsModel(String is_favorited_count) {
        this.is_favorited_count = is_favorited_count;
    }*/



    public String getDeal_views_count() {
        return deal_views_count;
    }

    public void setDeal_views_count(String deal_views_count) {
        this.deal_views_count = deal_views_count;
    }



    public BusinessModel getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(BusinessModel businessModel) {
        this.businessModel = businessModel;
    }


}

