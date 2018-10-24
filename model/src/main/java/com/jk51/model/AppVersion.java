package com.jk51.model;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:app版本信息
 * 作者: gaojie
 * 创建日期: 2017-04-26
 * 修改记录:
 */
public class AppVersion {

    private Integer id;
    private Integer is_active;
    private Integer updateType;
    private String version;
    private String fileName;
    private String title;
    private String message;
    private String other;
    private String downloadUrl;
    private Date created_at;
    private Date updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIs_active() {
        return is_active;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
    }

    public Integer getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Integer updateType) {
        this.updateType = updateType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
