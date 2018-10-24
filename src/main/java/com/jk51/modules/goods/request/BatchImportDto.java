package com.jk51.modules.goods.request;

import javax.validation.constraints.NotNull;

public class BatchImportDto {
    @NotNull
    Integer siteId;

    /**
     * 文件名
     */
    @NotNull
    String fileurl;
    /**
     * 文件大小
     */
    String filesize;

    /**
     * 商品模板
     */
    @NotNull
    Integer detailTpl;

    /**
     * 导入类型 add/update
     */
    @NotNull
    String option;

    /**
     * 使用51jk数据
     */
    Boolean use51;

    /**
     * 回收站 0 不查找回收站 1 更新回收站 2 保留回收站
     */
    Integer recy;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public Integer getDetailTpl() {
        return detailTpl;
    }

    public void setDetailTpl(Integer detailTpl) {
        this.detailTpl = detailTpl;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean getUse51() {
        return use51;
    }

    public void setUse51(Boolean use51) {
        this.use51 = use51;
    }

    public Integer getRecy() {
        return recy;
    }

    public void setRecy(Integer recy) {
        this.recy = recy;
    }
}
