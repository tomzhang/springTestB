package com.jk51.modules.im.controller.request;

import javax.validation.constraints.NotNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-27
 * 修改记录:
 */
public class IMRelationRequest {

    @NotNull
    private Integer site_id;
    private Integer store_admin_id;

    //会员手机号
    private String mobile;
    private String start_time;
    private String end_time;

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getStore_admin_id() {
        return store_admin_id;
    }

    public void setStore_admin_id(Integer store_admin_id) {
        this.store_admin_id = store_admin_id;
    }



    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "IMRelationRequest{" +
                "site_id=" + site_id +
                ", store_admin_id=" + store_admin_id +
                ", mobile='" + mobile + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                '}';
    }
}
