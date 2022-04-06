package com.atlanticcity.app.Models;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

public class ContactModel {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Uri getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }



    public ContactModel() {
    }

    public ContactModel(int id, String name, String mobileNumber) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }

    public int id;
    public String name;
    public String mobileNumber;

    public int getIs_sent() {
        return is_sent;
    }

    public void setIs_sent(int is_sent) {
        this.is_sent = is_sent;
    }

    public int is_sent;
    public boolean isChecked;
    public String photo;
    public Uri photoURI;


    public ContactModel (Cursor cursor){

        this.id= cursor.getInt(cursor.getColumnIndex("id"));
        this.name= cursor.getString(cursor.getColumnIndex("name"));
        this.mobileNumber= cursor.getString(cursor.getColumnIndex("mobile_number"));
        this.photo= cursor.getString(cursor.getColumnIndex("photo"));
        this.is_sent= cursor.getInt(cursor.getColumnIndex("is_sent"));



    }
}
