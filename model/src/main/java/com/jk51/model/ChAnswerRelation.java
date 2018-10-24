package com.jk51.model;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
public class ChAnswerRelation {

    private int id;
    private String user_openid;
    private String pharmacist_userid;
    private Timestamp create_time;
    private Timestamp update_time;
    private int disable;
    private String remark;
    private Integer im_service_id;
    private Integer im_recode_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_openid() {
        return user_openid;
    }

    public void setUser_openid(String user_openid) {
        this.user_openid = user_openid;
    }

    public String getPharmacist_userid() {
        return pharmacist_userid;
    }

    public void setPharmacist_userid(String pharmacist_userid) {
        this.pharmacist_userid = pharmacist_userid;
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

    public int getDisable() {
        return disable;
    }

    public void setDisable(int disable) {
        this.disable = disable;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIm_service_id() {
        return im_service_id;
    }

    public void setIm_service_id(Integer im_service_id) {
        this.im_service_id = im_service_id;
    }

    public Integer getIm_recode_id() {
        return im_recode_id;
    }

    public void setIm_recode_id(Integer im_recode_id) {
        this.im_recode_id = im_recode_id;
    }
}
