package com.jk51.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class StoreAdminExt {

    //商家ID
    private int site_id;
    //自增id
    private int id;
    //门店ID
    private int store_id;
    //店员Id
    private int storeadmin_id;
    private int sex;
    private String mobile;
    private String name;
    private String idcard_number;
    private Date birthday;
    private int age;
    private String email;
    private String qq;
    private String clerk_job;
    private String memo;
    private String clerk_invitation_code;
    private String employee_number;
    private int storeadmin_status;
    private int is_del;
    private Timestamp create_time;
    private Timestamp update_time;

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

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

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getStoreadmin_id() {
        return storeadmin_id;
    }

    public void setStoreadmin_id(int storeadmin_id) {
        this.storeadmin_id = storeadmin_id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard_number() {
        return idcard_number;
    }

    public void setIdcard_number(String idcard_number) {
        this.idcard_number = idcard_number;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getClerk_job() {
        return clerk_job;
    }

    public void setClerk_job(String clerk_job) {
        this.clerk_job = clerk_job;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getClerk_invitation_code() {
        return clerk_invitation_code;
    }

    public void setClerk_invitation_code(String clerk_invitation_code) {
        this.clerk_invitation_code = clerk_invitation_code;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public int getStoreadmin_status() {
        return storeadmin_status;
    }

    public void setStoreadmin_status(int storeadmin_status) {
        this.storeadmin_status = storeadmin_status;
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


}
