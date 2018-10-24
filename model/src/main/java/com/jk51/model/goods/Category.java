package com.jk51.model.goods;

import java.sql.Timestamp;

/**
 * 商品类目
 *
 */
public class Category {
    private Integer siteId;
    private Integer cateId;
    private Integer parentId;
    private String cateCode;
    private String cateName;
   /* private Integer cateSort;*/
    private String imgHash;
    /*private Integer cateIshow;
    private Integer ybCateid;*/
    /*private Integer delTag;
    private Timestamp createTime;
    private Timestamp updateTime;*/

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCateCode() {
        return cateCode;
    }

    public void setCateCode(String cateCode) {
        this.cateCode = cateCode;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    /*public Integer getCateSort() {
        return cateSort;
    }

    public void setCateSort(Integer cateSort) {
        this.cateSort = cateSort;
    }*/

    public String getImgHash() {
        return imgHash;
    }

    public void setImgHash(String imgHash) {
        this.imgHash = imgHash;
    }

    /*public Integer getCateIshow() {
        return cateIshow;
    }

    public void setCateIshow(Integer cateIshow) {
        this.cateIshow = cateIshow;
    }

    public Integer getYbCateid() {
        return ybCateid;
    }

    public void setYbCateid(Integer ybCateid) {
        this.ybCateid = ybCateid;
    }*/

   /* public Integer getDelTag() {
        return delTag;
    }

    public void setDelTag(Integer delTag) {
        this.delTag = delTag;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }*/
}
