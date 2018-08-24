package com.mahta.rastin.broadcastapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Workbook extends RealmObject implements Parcelable {
    @PrimaryKey
    private int id;

    private int scale;

    private String year;

    private String month;

    private String lessons;

    private String grades;

    private String created_at;

    private String updated_at;

    public Workbook(){}

    //parcelable part***********************************************************************
    protected Workbook(Parcel in) {
        id = in.readInt();
        scale = in.readInt();
        year = in.readString();
        month = in.readString();
        lessons = in.readString();
        grades = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Workbook> CREATOR = new Creator<Workbook>() {
        @Override
        public Workbook createFromParcel(Parcel in) {
            return new Workbook(in);
        }

        @Override
        public Workbook[] newArray(int size) {
            return new Workbook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(scale);
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(lessons);
        dest.writeString(grades);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }
    //parcelable part***********************************************************************
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getLessons() {
        return lessons;
    }

    public void setLessons(String lessons) {
        this.lessons = lessons;
    }

    public String getGrades() {
        return grades;
    }

    public void setGrades(String grades) {
        this.grades = grades;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
