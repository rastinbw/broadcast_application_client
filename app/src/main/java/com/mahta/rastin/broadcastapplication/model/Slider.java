package com.mahta.rastin.broadcastapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Slider extends RealmObject {
    @PrimaryKey
    private int id;

    private String image_1;

    private String image_2;

    private String image_3;

    private String image_4;

    private String updated_at;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_1() {
        return image_1;
    }

    public void setImage_1(String image_1) {
        this.image_1 = image_1;
    }

    public String getImage_2() {
        return image_2;
    }

    public void setImage_2(String image_2) {
        this.image_2 = image_2;
    }

    public String getImage_3() {
        return image_3;
    }

    public void setImage_3(String image_3) {
        this.image_3 = image_3;
    }

    public String getImage_4() {
        return image_4;
    }

    public void setImage_4(String image_4) {
        this.image_4 = image_4;
    }


    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}