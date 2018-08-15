package com.mahta.rastin.broadcastapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Media extends RealmObject {
    @PrimaryKey
    private int id;

    private String title;

    private String description;

    private String path;

    private String date;

    public Media(){}

    public Media(FavoriteMedia favoriteMedia){
        this.id = favoriteMedia.getId();
        this.title = favoriteMedia.getTitle();
        this.path = favoriteMedia.getPath();
        this.date = favoriteMedia.getDate();
        this.description = favoriteMedia.getDescription();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}