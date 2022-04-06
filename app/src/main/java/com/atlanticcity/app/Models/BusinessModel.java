package com.atlanticcity.app.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BusinessModel {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_detail() {
        return business_detail;
    }

    public void setBusiness_detail(String business_detail) {
        this.business_detail = business_detail;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIs_favorited_count() {
        return is_favorited_count;
    }

    public void setIs_favorited_count(String is_favorited_count) {
        this.is_favorited_count = is_favorited_count;
    }

    public String getDeals_count() {
        return deals_count;
    }

    public void setDeals_count(String deals_count) {
        this.deals_count = deals_count;
    }

    public ArrayList<BusinessDeals> getBusinessDeals() {
        return businessDeals;
    }

    public void setBusinessDeals(ArrayList<BusinessDeals> businessDeals) {
        this.businessDeals = businessDeals;
    }

    @SerializedName("deals")
    ArrayList<BusinessDeals> businessDeals;

    String id;
    String business_user_id;
    String email;
    String business_name;
    String business_detail;
    String logo;
    String color;
    String cover;
    String address;
    String state;
    String city;
    String zipcode;
    String phone_number;
    String website;
    String open_time;
    String close_time;
    String expire_date;
    String category_id;
    String sub_category_id;
    String qr_code;
    String lat;
    String lng;
    String status;
    String is_favorited_count;
    String deals_count;
}
