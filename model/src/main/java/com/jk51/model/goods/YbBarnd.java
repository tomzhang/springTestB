package com.jk51.model.goods;

import java.util.Date;

public class YbBarnd {
    private Integer barnd_id;

    private String barnd_name;

    private String barnd_desc;

    private String site_url;

    private String barnd_logo;

    private Integer is_show;

    private Integer barnd_sort;

    private Date create_time;

    private Date update_time;

    private Integer add_status;

    public Integer getBarnd_id() {
        return barnd_id;
    }

    public void setBarnd_id(Integer barnd_id) {
        this.barnd_id = barnd_id;
    }

    public String getBarnd_name() {
        return barnd_name;
    }

    public void setBarnd_name(String barnd_name) {
        this.barnd_name = barnd_name;
    }

    public String getBarnd_desc() {
        return barnd_desc;
    }

    public void setBarnd_desc(String barnd_desc) {
        this.barnd_desc = barnd_desc;
    }

    public String getSite_url() {
        return site_url;
    }

    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    public String getBarnd_logo() {
        return barnd_logo;
    }

    public void setBarnd_logo(String barnd_logo) {
        this.barnd_logo = barnd_logo;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public Integer getBarnd_sort() {
        return barnd_sort;
    }

    public void setBarnd_sort(Integer barnd_sort) {
        this.barnd_sort = barnd_sort;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getAdd_status() {
        return add_status;
    }

    public void setAdd_status(Integer add_status) {
        this.add_status = add_status;
    }
}