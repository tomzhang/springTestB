package com.jk51.model.account.models;


import java.sql.Timestamp;

public class MerchantConfig {

    private Long id;
    private String site_id;
    private int set_type;
    private String set_value;
    private Timestamp thelast_time;
    private Timestamp update_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public int getSet_type() {
        return set_type;
    }

    public void setSet_type(int set_type) {
        this.set_type = set_type;
    }

    public String getSet_value() {
        return set_value;
    }

    public void setSet_value(String set_value) {
        this.set_value = set_value;
    }

    public Timestamp getThelast_time() {
        return thelast_time;
    }

    public void setThelast_time(Timestamp thelast_time) {
        this.thelast_time = thelast_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return "MerchantConfig{" +
                "id=" + id +
                ", site_id='" + site_id + '\'' +
                ", set_type=" + set_type +
                ", set_value='" + set_value + '\'' +
                ", thelast_time=" + thelast_time +
                ", update_time=" + update_time +
                '}';
    }
}
