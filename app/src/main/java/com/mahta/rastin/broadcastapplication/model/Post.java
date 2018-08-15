package com.mahta.rastin.broadcastapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject implements Parcelable {
    @PrimaryKey
    private int id;

    private String title;

    private String content;

    private String preview;

    private String date;

    private String category;

    public Post(){}

    public Post(FavoritePost post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.preview = post.getPreview();
        this.date = post.getDate();
        this.category = post.getCategory();
    }
    //parcelable part***********************************************************************
    protected Post(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        preview = in.readString();
        date = in.readString();
        category = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(preview);
        dest.writeString(date);
        dest.writeString(category);
    }
    //parcelable part***********************************************************************


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
