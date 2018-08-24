package com.mahta.rastin.broadcastapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Student extends RealmObject {

    @PrimaryKey
    private int id;

    private String first_name;

    private String last_name;

    private String national_code;

    private int group_id;

    private boolean isStudent;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getNational_code() {
        return national_code;
    }

    public void setNational_code(String national_code) {
        this.national_code = national_code;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }
}
