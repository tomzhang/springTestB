package com.jk51.model.role;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-10-20
 * 修改记录:
 */
public class StorePermission {
    private Integer id;//permissionTypeId
    private Integer platform;
    private String name;//TypeName
    List<Permission> permisionList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permission> getPermisionList() {
        return permisionList;
    }

    public void setPermisionList(List<Permission> permisionList) {
        this.permisionList = permisionList;
    }
}
