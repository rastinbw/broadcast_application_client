package com.mahta.rastin.broadcastapplication.model;

public class WorkbookTuple {
    private String lesson;
    private String grade;

    public WorkbookTuple(){}

    public WorkbookTuple(String lesson, String grade) {
        this.lesson = lesson;
        this.grade = grade;
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
}
