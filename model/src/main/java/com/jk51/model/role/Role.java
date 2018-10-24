package com.jk51.model.role;

import java.util.Date;
import java.util.List;

public class Role extends RoleKey {
    private Integer platform;

    private Integer storeId;

    private String name;

    private String roleDesc;

    private Integer isActive;

    private Date deletedAt;

    private Date createtime;

    private Date updatetime;

    private String permissions;

    private List<ManagerKey> managerKeyList;

    public Role() {
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public List<ManagerKey> getManagerKeyList() {
        return managerKeyList;
    }

    public void setManagerKeyList(List<ManagerKey> managerKeyList) {
        this.managerKeyList = managerKeyList;
    }

    @Override
    public String toString() {
        return "Role{" +
                "platform=" + platform +
                ", store_id=" + storeId +
                ", name='" + name + '\'' +
                ", role_desc='" + roleDesc + '\'' +
                ", is_active=" + isActive +
                ", deleted_at=" + deletedAt +
                ", create_time=" + createtime +
                ", update_time=" + updatetime +
                ", permissions='" + permissions + '\'' +
                '}';
    }
}