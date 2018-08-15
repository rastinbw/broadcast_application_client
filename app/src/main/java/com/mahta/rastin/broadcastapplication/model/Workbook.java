package com.mahta.rastin.broadcastapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Workbook extends RealmObject implements Parcelable {
    @PrimaryKey
    private int id;

    private String year;

    private String month;

    private String lessons;

    private String grades;

    public Workbook(){}

    //parcelable part***********************************************************************
    protected Workbook(Parcel in) {
        id = in.readInt();
        year = in.readString();
        month = in.readString();
        lessons = in.readString();
        grades = in.readString();
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
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(lessons);
        dest.writeString(grades);
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
}
