package com.mahta.rastin.broadcastapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavoritePost extends RealmObject {
    @PrimaryKey
    private int id;

    private String title;

    private String content;

    private String preview;

    private String date;

    private String category;

    public FavoritePost(){ }

    public FavoritePost(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.preview = post.getPreview();
        this.date = post.getDate();
        this.category = post.getCategory();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

