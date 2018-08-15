package com.mahta.rastin.broadcastapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavoriteProgram extends RealmObject {
    @PrimaryKey
    private int id;

    private String title;

    private String content;

    private String preview;

    private String date;

    private String category;

    private int group_id;

    public FavoriteProgram(){ }

    public FavoriteProgram(Program program){
        this.id = program.getId();
        this.title = program.getTitle();
        this.content = program.getContent();
        this.preview = program.getPreview();
        this.date = program.getDate();
        this.category = program.getCategory();
        this.group_id = program.getGroup_id();
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

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}