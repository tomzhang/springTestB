package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:权限列表用户信息
 * 作者: chen
 * 创建日期: 2017-03-14
 * 修改记录:
 */
public class ClerkInfo {

    private Integer id;
    private Integer siteId;
    private String employee_number;
    private String user_name;
    private String name;
    private String clerk_job;
//    private String role_name;
//    private String group;
    private List<String> role_name;
    private List<String> group_name;
    private String roleName;
    private String groupName;
    private String mobile;
    private String email;
    private Date last_login_time;
    private Integer login_count;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public List<String> getRole_name() {
        return role_name;
    }

    public void setRole_name(List<String> role_name) {
        this.role_name = role_name;
    }

    public List<String> getGroup_name() {
        return group_name;
    }

    public void setGroup_name(List<String> group_name) {
        this.group_name = group_name;
    }

    public String getRoleName() {
        return role_name.toString();
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getGroupName() {
        return group_name.toString();
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClerk_job() {
        return clerk_job;
    }

    public void setClerk_job(String clerk_job) {
        this.clerk_job = clerk_job;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public Integer getLogin_count() {
        return login_count;
    }

    public void setLogin_count(Integer login_count) {
        this.login_count = login_count;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }




}
