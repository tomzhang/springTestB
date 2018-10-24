package com.jk51.model.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.util.Date;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class YbGoodsSyncDraftExt implements Serializable {
    /**
     * 自增ID
     */
    private Integer id;

    /**
     * FK yb_goods_sync_draft id
     */
    private Integer syncDraftId;

    /**
     * 药品通用名
     */
    private String comName;

    /**
     * 生产企业
     */
    private String goodsCompany;

    /**
     * 类别: 110(甲类非处方药)，120(已类非处方药)，130(处方药)，140(双轨药)，150(非方剂)，160(方剂)，170(一类)，180(二类)，190(三类)
     */
    private Integer drugCategory;

    /**
     * 药品属性: 110(化学药制剂 ), 120(中成药), 130(生物制品), 140(抗生素) , 150(中药材) , 160(中药饮片) , 170(复方制剂) , 9999(其他),180(根茎类), 190(茎木类), 200(皮类), 210(叶类), 220(花类), 230(全草类), 240(果实种子类), 250(矿物类), 260(动物类)
     */
    private Integer goodsProperty;

    /**
     * 剂型: 110(片剂), 120(胶囊), 130(丸剂), 140(颗粒), 150(液体), 160(膏剂), 170(贴剂), 180(糖浆), 190(散剂), 200(栓剂), 210(喷雾), 9999(其他)
     */
    private Integer goodsForts;

    /**
     * 有效期(月)
     */
    private Integer goodsValidity;

    /**
     * 使用方法: 110(口服), 120(外用), 130(注射), 140(含服), 9999(其他)
     */
    private String goodsUse;

    /**
     * 适用人群：110(不限)， 120(成人)，130(婴幼儿)，140(儿童)，150(男性)，160(妇女) ，170 (中老年)
     */
    private String goodsForpeople;

    /**
     * 是否医保 1=非医保, 2=甲类医保, 3=乙类医保
     */
    private Boolean isMedicare;

    /**
     * 医保编码
     */
    private String medicareCode;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 重量(克)
     */
    private Integer goodsWeight;

    /**
     * 商品标题（用户自定义）,字数限制在60-80
     */
    private String goodsTitle;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 用户自定义分类（前期固定)
     */
    private String userCateid;

    /**
     * 允许更新的字段主键 多个用逗号隔开如:approval_number,specif_cation
     */
    private String fieldsPk;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSyncDraftId() {
        return syncDraftId;
    }

    public void setSyncDraftId(Integer syncDraftId) {
        this.syncDraftId = syncDraftId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getGoodsCompany() {
        return goodsCompany;
    }

    public void setGoodsCompany(String goodsCompany) {
        this.goodsCompany = goodsCompany;
    }

    public Integer getDrugCategory() {
        return drugCategory;
    }

    public void setDrugCategory(Integer drugCategory) {
        this.drugCategory = drugCategory;
    }

    public Integer getGoodsProperty() {
        return goodsProperty;
    }

    public void setGoodsProperty(Integer goodsProperty) {
        this.goodsProperty = goodsProperty;
    }

    public Integer getGoodsForts() {
        return goodsForts;
    }

    public void setGoodsForts(Integer goodsForts) {
        this.goodsForts = goodsForts;
    }

    public Integer getGoodsValidity() {
        return goodsValidity;
    }

    public void setGoodsValidity(Integer goodsValidity) {
        this.goodsValidity = goodsValidity;
    }

    public String getGoodsUse() {
        return goodsUse;
    }

    public void setGoodsUse(String goodsUse) {
        this.goodsUse = goodsUse;
    }

    public String getGoodsForpeople() {
        return goodsForpeople;
    }

    public void setGoodsForpeople(String goodsForpeople) {
        this.goodsForpeople = goodsForpeople;
    }

    public Boolean getIsMedicare() {
        return isMedicare;
    }

    public void setIsMedicare(Boolean isMedicare) {
        this.isMedicare = isMedicare;
    }

    public String getMedicareCode() {
        return medicareCode;
    }

    public void setMedicareCode(String medicareCode) {
        this.medicareCode = medicareCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(Integer goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getUserCateid() {
        return userCateid;
    }

    public void setUserCateid(String userCateid) {
        this.userCateid = userCateid;
    }

    public String getFieldsPk() {
        return fieldsPk;
    }

    public void setFieldsPk(String fieldsPk) {
        this.fieldsPk = fieldsPk;
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
}