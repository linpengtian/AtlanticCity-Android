package com.atlanticcity.app.Models;

public class BusinessDeals {
    String id;

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

    public String getDeal_views_count() {
        return deal_views_count;
    }

    public void setDeal_views_count(String deal_views_count) {
        this.deal_views_count = deal_views_count;
    }

    String deal_views_count;
}
