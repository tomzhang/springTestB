package com.jk51.model.treat;

import com.jk51.model.role.Role;

import java.sql.Timestamp;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:51后台用户
 * 作者: dumingliang
 * 创建日期: 2017-03-03
 * 修改记录:
 */
public class YBManager {
    private Integer id;
    private String username;
    private String password;
    private String realname;
    private Integer sex;
    private String cellphone;
    private String email;
    private String qq;
    private Integer isActive;
    private Integer isAdministrator;
    private String remark;
    private Timestamp lastLogintime;
    private Integer loginCount;
    private Timestamp createtime;
    private Timestamp updatetime;
    private List<Role> roleList;

    public YBManager() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsAdministrator() {
        return isAdministrator;
    }

    public void setIsAdministrator(Integer isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getLastLogintime() {
        return lastLogintime;
    }

    public void setLastLogintime(Timestamp lastLogintime) {
        this.lastLogintime = lastLogintime;
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
