package com.jk51.model.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class YbGoodsSyncDraftExtWithBLOBs extends YbGoodsSyncDraftExt implements Serializable {
    /**
     * 药品主要成分/产品参数(非药品类)
     */
    private String mainIngredient;

    /**
     * 功能主治/功能介绍（非药品类)
     */
    private String goodsIndications;

    /**
     * 用法用量
     */
    private String goodsUseMethod;

    /**
     * 适用人群（非药品类特有字段）
     */
    private String forpeopleDesc;

    /**
     * 药理作用/产品特色(非药品类)
     */
    private String goodsAction;

    /**
     * 产品详细描述
     */
    private String goodsDesc;

    /**
     * 不良反应
     */
    private String adverseReactioins;

    /**
     * 注意事项
     */
    private String goodsNote;

    /**
     * 禁忌
     */
    private String goodsContd;

    /**
     * 存放（非药品类特有字段）
     */
    private String goodsDeposit;

    /**
     * 商品说明书
     */
    private String goodsDescription;

    /**
     * 商品图片
     */
    private String goodsImgs;

    /**
     * 是否更新字段列表多个用逗号隔开如:approval_number,specif_cation,goods_company,drug_category
     */
    private String fields;

    private static final long serialVersionUID = 1L;

    public String getMainIngredient() {
        return mainIngredient;
    }

    public void setMainIngredient(String mainIngredient) {
        this.mainIngredient = mainIngredient;
    }

    public String getGoodsIndications() {
        return goodsIndications;
    }

    public void setGoodsIndications(String goodsIndications) {
        this.goodsIndications = goodsIndications;
    }

    public String getGoodsUseMethod() {
        return goodsUseMethod;
    }

    public void setGoodsUseMethod(String goodsUseMethod) {
        this.goodsUseMethod = goodsUseMethod;
    }

    public String getForpeopleDesc() {
        return forpeopleDesc;
    }

    public void setForpeopleDesc(String forpeopleDesc) {
        this.forpeopleDesc = forpeopleDesc;
    }

    public String getGoodsAction() {
        return goodsAction;
    }

    public void setGoodsAction(String goodsAction) {
        this.goodsAction = goodsAction;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getAdverseReactioins() {
        return adverseReactioins;
    }

    public void setAdverseReactioins(String adverseReactioins) {
        this.adverseReactioins = adverseReactioins;
    }

    public String getGoodsNote() {
        return goodsNote;
    }

    public void setGoodsNote(String goodsNote) {
        this.goodsNote = goodsNote;
    }

    public String getGoodsContd() {
        return goodsContd;
    }

    public void setGoodsContd(String goodsContd) {
        this.goodsContd = goodsContd;
    }

    public String getGoodsDeposit() {
        return goodsDeposit;
    }

    public void setGoodsDeposit(String goodsDeposit) {
        this.goodsDeposit = goodsDeposit;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public String getGoodsImgs() {
        return goodsImgs;
    }

    public void setGoodsImgs(String goodsImgs) {
        this.goodsImgs = goodsImgs;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}