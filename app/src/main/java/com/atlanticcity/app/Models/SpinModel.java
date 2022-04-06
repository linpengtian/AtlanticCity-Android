package com.atlanticcity.app.Models;

public class SpinModel {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpinner_id() {
        return spinner_id;
    }

    public void setSpinner_id(String spinner_id) {
        this.spinner_id = spinner_id;
    }

    public String getPrize_title() {
        return prize_title;
    }

    public void setPrize_title(String prize_title) {
        this.prize_title = prize_title;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SpinModel(String id, String active_status) {
        this.id = id;
        this.active_status = active_status;
    }

    String id;
    String active_status;
    String spinner_id;
    String prize_title;
    String prize;
    String status;

    public String getActive_status() {
        return active_status;
    }

    public void setActive_status(String active_status) {
        this.active_status = active_status;
    }


}
