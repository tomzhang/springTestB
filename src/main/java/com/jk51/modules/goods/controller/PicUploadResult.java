package com.jk51.modules.goods.controller;

public class PicUploadResult {
    
    private Integer error;
    
    private String hash;
    
    private String width;
    
    private String height;

    private Long size;

    private Integer type;

    private String host_id;

    private Boolean flag;

    private Integer good_id;

    private Boolean is_default;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Integer getGood_id() {
        return good_id;
    }

    public void setGood_id(Integer good_id) {
        this.good_id = good_id;
    }

    public Boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(Boolean is_default) {
        this.is_default = is_default;
    }
}
