package com.jk51.model.order;

import java.util.Date;

public class SGroupMember {
    private Integer id;

    private String unique_id;

    private Integer group_id;

    private String store_admin_id;

    private Integer site_id;

    private Date create_at;

    private Date update_at;

    private Integer is_close;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id == null ? null : unique_id.trim();
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getStore_admin_id() {
        return store_admin_id;
    }

    public void setStore_admin_id(String store_admin_id) {
        this.store_admin_id = store_admin_id == null ? null : store_admin_id.trim();
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

    public Integer getIs_close() {
        return is_close;
    }

    public void setIs_close(Integer is_close) {
        this.is_close = is_close;
    }
}
