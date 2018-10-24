package com.jk51.model.role;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-03-07
 * 修改记录:
 */
public class ManagerKey {
    private Integer id;
    private Integer siteId;

    public ManagerKey() {
    }

    public  ManagerKey(Integer id, Integer siteId) {
        this.id = id;
        this.siteId = siteId;
    }

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
}
