package com.jk51.model.order;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 会员列表返回信息
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-03-17 14:15
 * 修改记录:
 */
public class MemberReturnModel implements Serializable {

    private String member_id;
    private String mobile;
    private String member_name;
    private String create_time;
    private String invite_code;
    private String invite_name;
    private String last_time;
    private String integrate;
    private String register_clerks;
    private String site_id;

    public MemberReturnModel() {
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getInvite_name() {
        return invite_name;
    }

    public void setInvite_name(String invite_name) {
        this.invite_name = invite_name;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getIntegrate() {
        return integrate;
    }

    public void setIntegrate(String integrate) {
        this.integrate = integrate;
    }

    public String getRegister_clerks() {
        return register_clerks;
    }

    public void setRegister_clerks(String register_clerks) {
        this.register_clerks = register_clerks;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }
}
