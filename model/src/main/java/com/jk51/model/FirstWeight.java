package com.jk51.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/2/13.
 */
public class FirstWeight implements Serializable{

    private int first_weight_id;
    private int owner;
    private String weight_name;
    private double weight_value;
    private Timestamp create_time;
    private Timestamp update_time;

    public int getFirst_weight_id() {
        return first_weight_id;
    }

    public void setFirst_weight_id(int first_weight_id) {
        this.first_weight_id = first_weight_id;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getWeight_name() {
        return weight_name;
    }

    public void setWeight_name(String weight_name) {
        this.weight_name = weight_name;
    }

    public double getWeight_value() {
        return weight_value;
    }

    public void setWeight_value(double weight_value) {
        this.weight_value = weight_value;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
