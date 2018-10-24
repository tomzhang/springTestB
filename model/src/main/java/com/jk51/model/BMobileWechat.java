package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
public class BMobileWechat {

    @JsonProperty("site_id")
    private int site_id;
    @JsonProperty("id")
    private int id;
    @JsonProperty("buyer_id")
    private int buyer_id;
    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("open_id")
    private String open_id;
    @JsonProperty("is_del")
    private int is_del;
    @JsonProperty("create_time")
    private Timestamp create_time;
    @JsonProperty("update_time")
    private Timestamp update_time;
    @JsonProperty("user_headimgurl")
    private String user_headimgurl;
    @JsonProperty("user_is_sync")
    private int user_is_sync;

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(int buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
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

    public String getUser_headimgurl() {
        return user_headimgurl;
    }

    public void setUser_headimgurl(String user_headimgurl) {
        this.user_headimgurl = user_headimgurl;
    }

    public int getUser_is_sync() {
        return user_is_sync;
    }

    public void setUser_is_sync(int user_is_sync) {
        this.user_is_sync = user_is_sync;
    }
}
