package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商品详细信息
 * 作者: baixiongfei
 * 创建日期: 2017/2/17
 * 修改记录:
 */
public class GoodsInfo {

    private int	siteId;
    private int	goodsId;
    private String approvalNumber;
    private String drugName;
    private String comName;
    private String specifCation;
    private String goodsCompany;
    private String barndId;
    private int	drugCategory;
    private int	goodsProperty;
    private String goodsUse;
    private int	goodsForts;
    private int	goodsValidity;
    private String goodsForpeople;
    private String userCateid;
    private String goodsTitle;
    private String goodsTagsid;
    private int	marketPrice;
    private int	shopPrice;
    private int	costPrice;
    private int	discountPrice;
    private int	inStock;
    private int	goodsWeight;
    private int	controlNum;
    private int	goodsStatus;
    private int	appGoodsStatus;
    private int	freightPayer;
    private Timestamp listTime;
    private Timestamp delistTime;
    private int	postageId;
    private int	detailTpl;
    private int	isMedicare;
    private String medicareCode;
    private int	medicareTopPrice;
    private String barCode;
    private String mnemonicCode;
    private int	purchaseWay;
    private int	wxPurchaseWay;
    private String goodsCode;
    private int	ybGoodsId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private int	erpPrice;

    public void setErpPrice(int erpPrice) {
        this.erpPrice = erpPrice;
    }

    public int getErpPrice() {
        return erpPrice;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
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

    public int getDrugCategory() {
        return drugCategory;
    }

    public void setDrugCategory(int drugCategory) {
        this.drugCategory = drugCategory;
    }

    public int getGoodsProperty() {
        return goodsProperty;
    }

    public void setGoodsProperty(int goodsProperty) {
        this.goodsProperty = goodsProperty;
    }

    public String getGoodsUse() {
        return goodsUse;
    }

    public void setGoodsUse(String goodsUse) {
        this.goodsUse = goodsUse;
    }

    public int getGoodsForts() {
        return goodsForts;
    }

    public void setGoodsForts(int goodsForts) {
        this.goodsForts = goodsForts;
    }

    public int getGoodsValidity() {
        return goodsValidity;
    }

    public void setGoodsValidity(int goodsValidity) {
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

    public int getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }

    public int getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(int shopPrice) {
        this.shopPrice = shopPrice;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(int costPrice) {
        this.costPrice = costPrice;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public int getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(int goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public int getControlNum() {
        return controlNum;
    }

    public void setControlNum(int controlNum) {
        this.controlNum = controlNum;
    }

    public int getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(int goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public int getAppGoodsStatus() {
        return appGoodsStatus;
    }

    public void setAppGoodsStatus(int appGoodsStatus) {
        this.appGoodsStatus = appGoodsStatus;
    }

    public int getFreightPayer() {
        return freightPayer;
    }

    public void setFreightPayer(int freightPayer) {
        this.freightPayer = freightPayer;
    }

    public Timestamp getListTime() {
        return listTime;
    }

    public void setListTime(Timestamp listTime) {
        this.listTime = listTime;
    }

    public Timestamp getDelistTime() {
        return delistTime;
    }

    public void setDelistTime(Timestamp delistTime) {
        this.delistTime = delistTime;
    }

    public int getPostageId() {
        return postageId;
    }

    public void setPostageId(int postageId) {
        this.postageId = postageId;
    }

    public int getDetailTpl() {
        return detailTpl;
    }

    public void setDetailTpl(int detailTpl) {
        this.detailTpl = detailTpl;
    }

    public int getIsMedicare() {
        return isMedicare;
    }

    public void setIsMedicare(int isMedicare) {
        this.isMedicare = isMedicare;
    }

    public String getMedicareCode() {
        return medicareCode;
    }

    public void setMedicareCode(String medicareCode) {
        this.medicareCode = medicareCode;
    }

    public int getMedicareTopPrice() {
        return medicareTopPrice;
    }

    public void setMedicareTopPrice(int medicareTopPrice) {
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

    public int getPurchaseWay() {
        return purchaseWay;
    }

    public void setPurchaseWay(int purchaseWay) {
        this.purchaseWay = purchaseWay;
    }

    public int getWxPurchaseWay() {
        return wxPurchaseWay;
    }

    public void setWxPurchaseWay(int wxPurchaseWay) {
        this.wxPurchaseWay = wxPurchaseWay;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public int getYbGoodsId() {
        return ybGoodsId;
    }

    public void setYbGoodsId(int ybGoodsId) {
        this.ybGoodsId = ybGoodsId;
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
    }

    @Override
    public String toString() {
        return "GoodsInfo{" +
                "siteId=" + siteId +
                ", goodsId=" + goodsId +
                ", approvalNumber='" + approvalNumber + '\'' +
                ", drugName='" + drugName + '\'' +
                ", comName='" + comName + '\'' +
                ", specifCation='" + specifCation + '\'' +
                ", goodsCompany='" + goodsCompany + '\'' +
                ", barndId='" + barndId + '\'' +
                ", drugCategory=" + drugCategory +
                ", goodsProperty=" + goodsProperty +
                ", goodsUse='" + goodsUse + '\'' +
                ", goodsForts=" + goodsForts +
                ", goodsValidity=" + goodsValidity +
                ", goodsForpeople='" + goodsForpeople + '\'' +
                ", userCateid='" + userCateid + '\'' +
                ", goodsTitle='" + goodsTitle + '\'' +
                ", goodsTagsid='" + goodsTagsid + '\'' +
                ", marketPrice=" + marketPrice +
                ", shopPrice=" + shopPrice +
                ", costPrice=" + costPrice +
                ", discountPrice=" + discountPrice +
                ", inStock=" + inStock +
                ", goodsWeight=" + goodsWeight +
                ", controlNum=" + controlNum +
                ", goodsStatus=" + goodsStatus +
                ", appGoodsStatus=" + appGoodsStatus +
                ", freightPayer=" + freightPayer +
                ", listTime=" + listTime +
                ", delistTime=" + delistTime +
                ", postageId=" + postageId +
                ", detailTpl=" + detailTpl +
                ", isMedicare=" + isMedicare +
                ", medicareCode='" + medicareCode + '\'' +
                ", medicareTopPrice=" + medicareTopPrice +
                ", barCode='" + barCode + '\'' +
                ", mnemonicCode='" + mnemonicCode + '\'' +
                ", purchaseWay=" + purchaseWay +
                ", wxPurchaseWay=" + wxPurchaseWay +
                ", goodsCode='" + goodsCode + '\'' +
                ", ybGoodsId=" + ybGoodsId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", erpPrice=" + erpPrice +
                '}';
    }
}
