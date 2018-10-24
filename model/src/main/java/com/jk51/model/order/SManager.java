package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jk51.model.role.Role;

import java.sql.Timestamp;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-28
 * 修改记录:
 */
public class SManager {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("realname")
    private String realname;
    @JsonProperty("sex")
    private Byte sex;
    @JsonProperty("cellphone")
    private String cellphone;
    @JsonProperty("email")
    private String email;
    @JsonProperty("qq")
    private String qq;
    @JsonProperty("isActive")
    private Byte isActive;
    @JsonProperty("remark")
    private String remark;
    @JsonProperty("lastLoginTime")
    private Timestamp lastLoginTime;
    @JsonProperty("loginCount")
    private Integer loginCount;

    private Timestamp createtime;

    private Timestamp updatetime;
    private List<Role> roleList;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
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

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getLastlogintime() {
        return lastLoginTime;
    }

    public void setLastlogintime(Timestamp lastlogintime) {
        this.lastLoginTime = lastlogintime;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }

    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
