package com.jk51.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Goods implements Serializable {

    /**
     * 商品ID（商品主表）
     */
    protected Integer goodsId;

    protected Integer siteId;

    /**
     * 批准文号
     */
    protected String approvalNumber;

    /**
     * 药品名/商品名（非药类商品）/器械名(非药类)/
     */
    protected String drugName;

    /**
     * 药品通用名
     */
    protected String comName;

    /**
     * 规格(10mg*30片)，每一个规格都是一种药品
     */
    protected String specifCation;

    /**
     * 生产企业
     */
    protected String goodsCompany;

    /**
     * 品牌id（用户也可以自己填写品牌）
     */
    protected Integer barndId;

    /**
     * 类别: 110(甲类非处方药)，120(已类非处方药)，130(处方药)，140(双轨药)，150(非方剂)，160(方剂)，170(一类)，180(二类)，190(三类)
     */
    protected Integer drugCategory;

    /**
     * 药品属性:110(化学药制剂),120(中成药),130(生物制品),140(抗生素),150(中药材),160(中药饮片),170(复方制剂),9999(其他),180(根茎类),190(茎木类),200(皮类),210(叶类),220(花类),230(全草类),240(果实种子类),250(矿物类),260(动物类)
     */
    protected Integer goodsProperty;

    /**
     * 使用方法: 110(口服), 120(外用), 130(注射), 140(含服), 9999(其他)
     */
    protected String goodsUse;

    /**
     * 剂型:110片剂120胶囊130丸剂140颗粒150液体160膏剂170贴剂180糖浆190散剂200栓剂210喷雾220溶液剂230乳剂240混悬剂250气雾剂260粉雾剂270洗剂280搽剂290糊剂300凝胶剂310滴眼剂320滴鼻剂330滴耳剂340眼膏剂350含漱剂360舌下片剂370粘贴片380贴膜剂390滴剂400滴丸剂410芳香水剂420甘油剂430醑剂440注射剂450涂膜剂460合剂470酊剂480膜剂9999其他
     */
    protected Integer goodsForts;

    /**
     * 有效期(月)
     */
    protected Integer goodsValidity;

    /**
     * 适用人群：110(不限)， 120(成人)，130(婴幼儿)，140(儿童)，150(男性)，160(妇女) ，170 (中老年)
     */
    protected String goodsForpeople;

    /**
     * 用户自定义分类（前期固定）
     */
    @JsonProperty("user_cate_id")
    protected String userCateid;

    /**
     * 商品标题（用户自定义）,字数限制在60-80
     */
    protected String goodsTitle;

    /**
     * 标签id(用,号隔开),对表标签表（b_tags）
     */
    protected String goodsTagsid;

    /**
     * 市场价格（以分为单位）
     */
    protected Integer marketPrice;

    /**
     * 药房价格（以分为单位）
     */
    protected Integer shopPrice;

    /**
     * 成本价（以分为单位）
     */
    protected Integer costPrice;
    /**
     * erp价格（以分为单位）
     */
    protected Integer erpPrice;

    public Integer getErpPrice() {
        return erpPrice;
    }

    public void setErpPrice(Integer erpPrice) {
        this.erpPrice = erpPrice;
    }

    /**
     * 折扣价格/会员价格/用户手动改价（以分为单位）
     */
    protected Integer discountPrice;

    /**
     * 库存(件)
     */
    protected Integer inStock;

    /**
     * 重量(克)
     */
    protected Integer goodsWeight;

    /**
     * 限购（件）, 0为不限购
     */
    protected Integer controlNum;

    /**
     * 产品状态. 1(出售中),2(库存中), 3（违规）,4(软删除)
     */
    protected Integer goodsStatus;
    /**
     * 产品状态. 1(出售中),2(库存中), 3（违规）,4(软删除)
     */
    protected Integer appGoodsStatus;

    public Integer getAppGoodsStatus() {
        return appGoodsStatus;
    }

    public void setAppGoodsStatus(Integer appGoodsStatus) {
        this.appGoodsStatus = appGoodsStatus;
    }

    /**
     * 运费承担方式: 1(商家), 2(买家)
     */
    protected Integer freightPayer;

    /**
     * 上架时间
     */
    protected Date listTime;

    /**
     * 下架时间
     */
    protected Date delistTime;

    /**
     * 运费模版id
     */
    protected Integer postageId;

    /**
     * 商品录入模板标识：10（药品类模板），20（其他类模板），30（器械类模板），40（保健品模板），50（2.0版本废弃），60（化妆品模板），70 (中药材模板) ，80 (消毒类模板)
     */
    protected Integer detailTpl;

    /**
     * 是否医保 1=非医保, 2=甲类医保, 3=乙类医保
     */
    protected Integer isMedicare;

    /**
     * 医保编码
     */
    protected String medicareCode;

    /**
     * 医保最高价格 单位：分
     */
    protected Integer medicareTopPrice;

    /**
     * 条形码
     */
    protected String barCode;

    /**
     * 助记码
     */
    protected String mnemonicCode;

    /**
     * 购买方式：
     * 110=仅支持在电脑购买
     * 120=仅支持在手机购买
     * 130=支持手机和电脑购买
     * 140=仅展示，不支持购买（默认）
     */
    protected Integer purchaseWay;
    protected Integer appPurchaseWay;

    public Integer getAppPurchaseWay() {
        return appPurchaseWay;
    }

    public void setAppPurchaseWay(Integer appPurchaseWay) {
        this.appPurchaseWay = appPurchaseWay;
    }

    /**
     * 购买方式：110=立即购买，购物车 120=商品仅供展示   130=可拨打订购电话+可预留订购信息   140=可拨打订购电话   150=可预留订购信息
     */
    protected Integer wxPurchaseWay;

    /**
     * 商品编码
     */
    protected String goodsCode;

    /**
     * 对应yb_goods.goods_id
     */
    protected Integer ybGoodsId;

    /**
     * 商品创建时间
     */
    protected Date createTime;

    /**
     * 产品修改时间
     */
    protected Date updateTime;

    /**
     * 新增服务类商品有效期 1预约当日使用（可扩展）
     */
    protected Integer scheduleTime;

    /**
     * 新增服务次数（可扩展）
     */
    protected Integer servceNum;

    /**
     * 新增说明服务类商品
     */
    protected String remark;

    //图片大小
    private Integer size;


    private String picSize;

    /**
     * 1高毛利 2中毛利 3低毛利 4负毛利
     */
    private Integer grossProfit;

    /**
     * 0不主推 1主推
     */
    private Integer isMainPush;

    /**
     * 门店id
     */
    protected String stores_id;

    public Integer getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(Integer grossProfit) {
        this.grossProfit = grossProfit;
    }

    public Integer getIsMainPush() {
        return isMainPush;
    }

    public void setIsMainPush(Integer isMainPush) {
        this.isMainPush = isMainPush;
    }

    public String getStores_id() {
        return stores_id;
    }

    public void setStores_id(String stores_id) {
        this.stores_id = stores_id;
    }
    public String getPicSize() {
        return picSize;
    }

    public void setPicSize(String picSize) {
        this.picSize = picSize;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    private String hash;
    private String imgHash;
    private Integer isDefault;

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getImgHash() {
        return imgHash;
    }

    public void setImgHash(String imgHash) {
        this.imgHash = imgHash;
    }

    private Integer goodsNum;  //hulan

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
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

    public Integer getBarndId() {
        return barndId;
    }

    public void setBarndId(Integer barndId) {
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

    public Integer getPurchaseWay() {
        return purchaseWay;
    }

    public void setPurchaseWay(Integer purchaseWay) {
        this.purchaseWay = purchaseWay;
    }

    public Integer getWxPurchaseWay() {
        return wxPurchaseWay;
    }

    public void setWxPurchaseWay(Integer wxPurchaseWay) {
        this.wxPurchaseWay = wxPurchaseWay;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public Integer getYbGoodsId() {
        return ybGoodsId;
    }

    public void setYbGoodsId(Integer ybGoodsId) {
        this.ybGoodsId = ybGoodsId;
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

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Integer scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Integer getServceNum() {
        return servceNum;
    }

    public void setServceNum(Integer servceNum) {
        this.servceNum = servceNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
