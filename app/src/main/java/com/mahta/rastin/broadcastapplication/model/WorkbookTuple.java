package com.mahta.rastin.broadcastapplication.model;

public class WorkbookTuple {
    private String lesson;
    private String grade;
    private int scale;

    public WorkbookTuple(){}

    public WorkbookTuple(String lesson, String grade, int scale) {
        this.lesson = lesson;
        this.grade = grade;
        this.scale = scale;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
