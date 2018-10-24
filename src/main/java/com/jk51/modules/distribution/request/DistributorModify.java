package com.jk51.modules.distribution.request;

import javax.validation.constraints.NotNull;

public class DistributorModify {
    @NotNull(message = "siteId不能为空")
    private Integer siteId;
    @NotNull(message = "id不能为空")
    private Integer id;
    @NotNull(message = "status不能为空")
    private Integer status;
    private String note;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
