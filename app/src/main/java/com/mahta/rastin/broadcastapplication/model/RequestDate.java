package com.mahta.rastin.broadcastapplication.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RequestDate extends RealmObject {
    @PrimaryKey
    private int id;

    private String date;

    public RequestDate() { }

    public RequestDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
