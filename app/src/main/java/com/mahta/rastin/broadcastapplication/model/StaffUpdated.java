package com.mahta.rastin.broadcastapplication.model;

import java.util.Date;

public class StaffUpdated {
    private int id;
    private Date updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
