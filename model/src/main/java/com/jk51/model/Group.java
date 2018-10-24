package com.jk51.model;

import java.util.Date;

public class Group {
    private Integer id;

    private String group_name;

    private Integer site_id;

    private Date create_at;

    private Date update_at;

    private Boolean is_close;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name == null ? null : group_name.trim();
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Date update_at) {
        this.update_at = update_at;
    }

    public Boolean getIs_close() {
        return is_close;
    }

    public void setIs_close(Boolean is_close) {
        this.is_close = is_close;
    }
}