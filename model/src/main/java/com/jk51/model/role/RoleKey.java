package com.jk51.model.role;

public class RoleKey {
    private Integer id;

    private Integer siteId;

    public RoleKey(){}
    public RoleKey(Integer id, Integer siteId) {
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