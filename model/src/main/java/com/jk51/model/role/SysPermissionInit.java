package com.jk51.model.role;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: bluesx
 * 创建日期: 2017-04-20
 * 修改记录:
 */
public class SysPermissionInit {
    private Integer id;
    private String url;
    private String permissionInit;
    private Integer sort;
    private Date createtime;
    private Date updatetime;

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    private Integer isDel;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermissionInit() {
        return permissionInit;
    }

    public void setPermissionInit(String permissionInit) {
        this.permissionInit = permissionInit;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
