package com.jk51.model.goods;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class YbCategory implements Serializable{
    private Integer cate_id;

    private Integer parent_id;

    private String cate_code;

    private String cate_name;

    private Integer cate_sort;

    private Integer cate_ishow;

    private Date create_time;

    private Date update_time;

    private List<YbCategory> subYbCategory;

    public List<YbCategory> getSubYbCategory() {
        return subYbCategory;
    }

    public void setSubYbCategory(List<YbCategory> subYbCategory) {
        this.subYbCategory = subYbCategory;
    }

    public Integer getCate_id() {
        return cate_id;
    }

    public void setCate_id(Integer cate_id) {
        this.cate_id = cate_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getCate_code() {
        return cate_code;
    }

    public void setCate_code(String cate_code) {
        this.cate_code = cate_code;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public Integer getCate_sort() {
        return cate_sort;
    }

    public void setCate_sort(Integer cate_sort) {
        this.cate_sort = cate_sort;
    }

    public Integer getCate_ishow() {
        return cate_ishow;
    }

    public void setCate_ishow(Integer cate_ishow) {
        this.cate_ishow = cate_ishow;
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
}