package com.jk51.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class Barnd implements Serializable {

    private Integer siteId;

    private Integer barndId;


    /**
     * 品牌名
     */
    private String barndName;

    /**
     * 品牌描述
     */
    private String barndDesc;

    /**
     * 公司网址
     */
    private String siteUrl;

    /**
     * 品牌logo
     */
    private String barndLogo;

    /**
     * 是否显示: 0(显示) ，1（不显示）
     */
    private Integer isShow;

    /**
     * 排序字段（数字越小越靠前，非负数），默认为：99999
     */
    private Integer barndSort;

    private Date createTime;

    private Date updateTime;

    /**
     * 对应  yb_barnd.id
     */
    private Integer ybBarndid;

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getBarndId() {
        return barndId;
    }

    public void setBarndId(Integer barndId) {
        this.barndId = barndId;
    }

    public String getBarndName() {
        return barndName;
    }

    public void setBarndName(String barndName) {
        this.barndName = barndName;
    }

    public String getBarndDesc() {
        return barndDesc;
    }

    public void setBarndDesc(String barndDesc) {
        this.barndDesc = barndDesc;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getBarndLogo() {
        return barndLogo;
    }

    public void setBarndLogo(String barndLogo) {
        this.barndLogo = barndLogo;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Integer getBarndSort() {
        return barndSort;
    }

    public void setBarndSort(Integer barndSort) {
        this.barndSort = barndSort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYbBarndid() {
        return ybBarndid;
    }

    public void setYbBarndid(Integer ybBarndid) {
        this.ybBarndid = ybBarndid;
    }
}