package com.jk51.model.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:b_goods和b_goodsextd中数据，yb表通用
 * 作者: ChengShanyunduo
 * 创建日期: 2017-07-25
 * 修改记录:
 */

// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsUpdateInfo {

    //b_goods字段
    private Integer siteId;

    private Integer goodsId;

    private String approvalNumber;

    private String drugName;

    private String comName;

    private String specifCation;

    private String goodsCompany;

    private String barndId;

    private Integer drugCategory;

    private Integer goodsProperty;

    private String goodsUse;

    private Integer goodsForts;

    private Integer goodsValidity;

    private String goodsForpeople;

    private String userCateid;

    private String goodsTitle;

    private String goodsTagsid;

    private Integer marketPrice;

    private Integer shopPrice;

    private Integer costPrice;

    private Integer discountPrice;

    private Integer inStock;

    private Integer goodsWeight;

    private Integer controlNum;

    private Integer goodsStatus;

    private Integer freightPayer;

    private Date listTime;

    private Date delistTime;

    private Integer postageId;

    private Integer detailTpl;

    private Integer isMedicare;

    private String medicareCode;

    private Integer medicareTopPrice;

    private String barCode;

    private String mnemonicCode;

    private String goodsCode;

    private Integer updateStatus;

    private Integer updateImg;

    private Date syncdateTime;

    private Integer merchantId;

    private Integer ybGoodsId;


    //b_goodsextd字段
    private String mainIngredient; //主要成分

    private String goodsIndication; //功能主治

    private String goodsAction;

    private String adverseReactions;

    private String goodsNote;

    private String goodsUseMethod;

    private String goodsContd;

    private String goodsDesc;

    private String goodsDescription;

    private Integer browseNumber;

    private Integer transMumber;

    private Integer shoppingNumber;

    private Date productDate;

    private String goodsUsage;

    private String goodsDeposit;

    private String forpeopleDesc;

    private String qualification;

    private String barndName;

    public String getBarndName() {
        return barndName;
    }

    public void setBarndName(String barndName) {
        this.barndName = barndName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation;
    }

    public String getGoodsCompany() {
        return goodsCompany;
    }

    public void setGoodsCompany(String goodsCompany) {
        this.goodsCompany = goodsCompany;
    }

    public String getBarndId() {
        return barndId;
    }

    public void setBarndId(String barndId) {
        this.barndId = barndId;
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

    public String getGoodsUse() {
        return goodsUse;
    }

    public void setGoodsUse(String goodsUse) {
        this.goodsUse = goodsUse;
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

    public String getGoodsForpeople() {
        return goodsForpeople;
    }

    public void setGoodsForpeople(String goodsForpeople) {
        this.goodsForpeople = goodsForpeople;
    }

    public String getUserCateid() {
        return userCateid;
    }

    public void setUserCateid(String userCateid) {
        this.userCateid = userCateid;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsTagsid() {
        return goodsTagsid;
    }

    public void setGoodsTagsid(String goodsTagsid) {
        this.goodsTagsid = goodsTagsid;
    }

    public Integer getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Integer marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
    }

    public Integer getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Integer costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public Integer getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(Integer goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public Integer getControlNum() {
        return controlNum;
    }

    public void setControlNum(Integer controlNum) {
        this.controlNum = controlNum;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public Integer getFreightPayer() {
        return freightPayer;
    }

    public void setFreightPayer(Integer freightPayer) {
        this.freightPayer = freightPayer;
    }

    public Date getListTime() {
        return listTime;
    }

    public void setListTime(Date listTime) {
        this.listTime = listTime;
    }

    public Date getDelistTime() {
        return delistTime;
    }

    public void setDelistTime(Date delistTime) {
        this.delistTime = delistTime;
    }

    public Integer getPostageId() {
        return postageId;
    }

    public void setPostageId(Integer postageId) {
        this.postageId = postageId;
    }

    public Integer getDetailTpl() {
        return detailTpl;
    }

    public void setDetailTpl(Integer detailTpl) {
        this.detailTpl = detailTpl;
    }

    public Integer getIsMedicare() {
        return isMedicare;
    }

    public void setIsMedicare(Integer isMedicare) {
        this.isMedicare = isMedicare;
    }

    public String getMedicareCode() {
        return medicareCode;
    }

    public void setMedicareCode(String medicareCode) {
        this.medicareCode = medicareCode;
    }

    public Integer getMedicareTopPrice() {
        return medicareTopPrice;
    }

    public void setMedicareTopPrice(Integer medicareTopPrice) {
        this.medicareTopPrice = medicareTopPrice;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMnemonicCode() {
        return mnemonicCode;
    }

    public void setMnemonicCode(String mnemonicCode) {
        this.mnemonicCode = mnemonicCode;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public Integer getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Integer updateStatus) {
        this.updateStatus = updateStatus;
    }

    public Integer getUpdateImg() {
        return updateImg;
    }

    public void setUpdateImg(Integer updateImg) {
        this.updateImg = updateImg;
    }

    public Date getSyncdateTime() {
        return syncdateTime;
    }

    public void setSyncdateTime(Date syncdateTime) {
        this.syncdateTime = syncdateTime;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMainIngredient() {
        return mainIngredient;
    }

    public void setMainIngredient(String mainIngredient) {
        this.mainIngredient = mainIngredient;
    }

    public String getGoodsIndication() {
        return goodsIndication;
    }

    public void setGoodsIndication(String goodsIndication) {
        this.goodsIndication = goodsIndication;
    }

    public String getGoodsAction() {
        return goodsAction;
    }

    public void setGoodsAction(String goodsAction) {
        this.goodsAction = goodsAction;
    }

    public String getAdverseReactions() {
        return adverseReactions;
    }

    public void setAdverseReactions(String adverseReactions) {
        this.adverseReactions = adverseReactions;
    }

    public String getGoodsNote() {
        return goodsNote;
    }

    public void setGoodsNote(String goodsNote) {
        this.goodsNote = goodsNote;
    }

    public String getGoodsUseMethod() {
        return goodsUseMethod;
    }

    public void setGoodsUseMethod(String goodsUseMethod) {
        this.goodsUseMethod = goodsUseMethod;
    }

    public String getGoodsContd() {
        return goodsContd;
    }

    public void setGoodsContd(String goodsContd) {
        this.goodsContd = goodsContd;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public Integer getBrowseNumber() {
        return browseNumber;
    }

    public void setBrowseNumber(Integer browseNumber) {
        this.browseNumber = browseNumber;
    }

    public Integer getTransMumber() {
        return transMumber;
    }

    public void setTransMumber(Integer transMumber) {
        this.transMumber = transMumber;
    }

    public Integer getShoppingNumber() {
        return shoppingNumber;
    }

    public void setShoppingNumber(Integer shoppingNumber) {
        this.shoppingNumber = shoppingNumber;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public String getGoodsUsage() {
        return goodsUsage;
    }

    public void setGoodsUsage(String goodsUsage) {
        this.goodsUsage = goodsUsage;
    }

    public String getGoodsDeposit() {
        return goodsDeposit;
    }

    public void setGoodsDeposit(String goodsDeposit) {
        this.goodsDeposit = goodsDeposit;
    }

    public String getForpeopleDesc() {
        return forpeopleDesc;
    }

    public void setForpeopleDesc(String forpeopleDesc) {
        this.forpeopleDesc = forpeopleDesc;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getYbGoodsId() {
        return ybGoodsId;
    }

    public void setYbGoodsId(Integer ybGoodsId) {
        this.ybGoodsId = ybGoodsId;
    }
}
