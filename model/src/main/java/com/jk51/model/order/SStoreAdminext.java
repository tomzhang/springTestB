package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class SStoreAdminext {
    private Integer id;

    private Integer site_id;

    private Integer store_id;

    private Integer storeadmin_id;

    private String mobile;

    private String name;

    private Integer sex;

    private String idcard_number;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date birthday;

    private Short age;

    private String email;

    private String qq;

    private String clerk_job;

    private String clerk_invitation_code;

    private String employee_number;

    private Integer storeadmin_status;

    private Integer is_del;

    private Date create_time;

    private Date update_time;

    private String memo;

    private List<SRole> roleList;

    public List<SRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SRole> roleList) {
        this.roleList = roleList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public Integer getStoreadmin_id() {
        return storeadmin_id;
    }

    public void setStoreadmin_id(Integer storeadmin_id) {
        this.storeadmin_id = storeadmin_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getIdcard_number() {
        return idcard_number;
    }

    public void setIdcard_number(String idcard_number) {
        this.idcard_number = idcard_number == null ? null : idcard_number.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getClerk_job() {
        return clerk_job;
    }

    public void setClerk_job(String clerk_job) {
        this.clerk_job = clerk_job == null ? null : clerk_job.trim();
    }

    public String getClerk_invitation_code() {
        return clerk_invitation_code;
    }

    public void setClerk_invitation_code(String clerk_invitation_code) {
        this.clerk_invitation_code = clerk_invitation_code == null ? null : clerk_invitation_code.trim();
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number == null ? null : employee_number.trim();
    }

    public Integer getStoreadmin_status() {
        return storeadmin_status;
    }

    public void setStoreadmin_status(Integer storeadmin_status) {
        this.storeadmin_status = storeadmin_status;
    }

    public Integer getIs_del() {
        return is_del;
    }

    public void setIs_del(Integer is_del) {
        this.is_del = is_del;
    }
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    @Override
    public String toString() {
        return "StoreAdminext{" +
            "id=" + id +
            ", site_id=" + site_id +
            ", store_id=" + store_id +
            ", storeadmin_id=" + storeadmin_id +
            ", mobile='" + mobile + '\'' +
            ", name='" + name + '\'' +
            ", sex=" + sex +
            ", idcard_number='" + idcard_number + '\'' +
            ", birthday=" + birthday +
            ", age=" + age +
            ", email='" + email + '\'' +
            ", qq='" + qq + '\'' +
            ", clerk_job='" + clerk_job + '\'' +
            ", clerk_invitation_code='" + clerk_invitation_code + '\'' +
            ", employee_number='" + employee_number + '\'' +
            ", storeadmin_status=" + storeadmin_status +
            ", is_del=" + is_del +
            ", create_time=" + create_time +
            ", update_time=" + update_time +
            ", memo='" + memo + '\'' +
            '}';
    }
}
