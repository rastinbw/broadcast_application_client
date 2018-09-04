package com.mahta.rastin.broadcastapplication.model;

import io.realm.RealmObject;

public class ReadPost extends RealmObject {
    private int id;
    private int type;

    public ReadPost(){}

    public ReadPost(int id, int type) {
        this.id = id;
        this.type = type;
    }

}
