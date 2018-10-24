package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-03-10
 * 修改记录:
 */
public class ClerkDetail {
    private Integer storeadmin_id;
    private String storeName;
    private String mobile;
    private String clerkName;
    private Integer sex;
    private String employee_number;
    private String invocation_code;
    private String id_card_num;
    private Date birthday;
    private String email;
    private String qq;
    private String clerk_job;
    private Date create_time;
    private String memo;
    private Integer status;
    private Integer id;
    private String qrcode_url;

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClerkDetail(){}

    public Integer getStoreadmin_id() {
        return storeadmin_id;
    }

    public void setStoreadmin_id(Integer storeadmin_id) {
        this.storeadmin_id = storeadmin_id;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
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

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getInvocation_code() {
        try{
        return invocation_code.split("_")[1];

        }catch (Exception e){
            return invocation_code;
        }
    }

    public void setInvocation_code(String invocation_code) {
        this.invocation_code = invocation_code;
    }

    public String getId_card_num() {
        return id_card_num;
    }

    public void setId_card_num(String id_card_num) {
        this.id_card_num = id_card_num;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClerkDetail{" +
                "storeadmin_id=" + storeadmin_id +
                ", storeName='" + storeName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", clerkName='" + clerkName + '\'' +
                ", sex=" + sex +
                ", employee_number='" + employee_number + '\'' +
                ", invocation_code='" + invocation_code + '\'' +
                ", id_card_num='" + id_card_num + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", qq='" + qq + '\'' +
                ", clerk_job='" + clerk_job + '\'' +
                ", create_time=" + create_time +
                ", memo='" + memo + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", qrcode_url='" + qrcode_url + '\'' +
                '}';
    }
}
