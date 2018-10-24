package com.jk51.model;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 * 店员指标分数记录表
 */
public class TargetRecode {

    private int target_record_id;
    private int site_id;
    private int salesclerk_id;
    private String target_record;
    private Timestamp create_time;
    private Timestamp update_time;

    public int getTarget_record_id() {
        return target_record_id;
    }

    public void setTarget_record_id(int target_record_id) {
        this.target_record_id = target_record_id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getSalesclerk_id() {
        return salesclerk_id;
    }

    public void setSalesclerk_id(int salesclerk_id) {
        this.salesclerk_id = salesclerk_id;
    }

    public String getTarget_record() {
        return target_record;
    }

    public void setTarget_record(String target_record) {
        this.target_record = target_record;
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
