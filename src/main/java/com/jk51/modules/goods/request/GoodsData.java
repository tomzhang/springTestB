package com.jk51.modules.goods.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jk51.model.Goods;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 没有多重继承  很尴尬.. 只能重新写一遍字段了
 */

/**
 * 添加商品数据
 */
// 忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsData extends Goods {

    protected String barndName;

    protected String cateName;

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getBarndName() {
        return barndName;
    }

    public void setBarndName(String barndName) {
        this.barndName = barndName;
    }
    /**
     * ============================================================================================
     *
     * 这下面是b_goodsextd的字段
     *
     * ============================================================================================
     */

    /**
     * 对应yb_goods.goods_id
     */
    protected Integer ybGoodsId;

    protected Integer goodsextdId;

    protected Integer browseNumber;

    protected Integer transMumber;

    protected Integer shoppingNumber;

    protected Date productDate;

    @Size(max = 32, message = "商品批次号不能超过32个字符")
    protected String goodsBatchNo;

    protected String mainIngredient;

    protected String goodsIndications;

    protected String goodsAction;

    protected String adverseReactioins;

    protected String goodsNote;

    protected String goodsUseMethod;

    protected String goodsContd;

    protected String goodsDesc;

    protected String goodsMobileDesc;

    protected String goodsDescription;

    protected String goodsUsage;

    protected String goodsDeposit;

    protected String forpeopleDesc;

    protected String qualification;

    protected String netWt;

    /**
     * ============================================================================================
     * <p>
     * 这下面是b_integral_goods的字段
     * <p>
     * ============================================================================================
     */

    private Long integralGoodsId;

    private Integer integralExchanges;

    private String integralGoodsStoreIds;

    private Integer integralGoodsIsDel;

    private Integer integralGoodsNum;

    private Integer integralGoodsStatus;

    private Integer limitEach;

    private Integer limitCount;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date integralGoodsStartTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date integralGoodsEndTime;

    private Integer erpPrice;

    private Integer integralGoodsLimitEach;

    public Integer getIntegralGoodsLimitEach() {
        return integralGoodsLimitEach;
    }

    public void setIntegralGoodsLimitEach(Integer integralGoodsLimitEach) {
        this.integralGoodsLimitEach = integralGoodsLimitEach;
    }

    public void setErpPrice(Integer erpPrice) {
        this.erpPrice = erpPrice;
    }

    public Integer getErpPrice() {
        return erpPrice;
    }

    public Integer getYbGoodsId() {
        return ybGoodsId;
    }

    public void setYbGoodsId(Integer ybGoodsId) {
        this.ybGoodsId = ybGoodsId;
    }

    public Integer getGoodsextdId() {
        return goodsextdId;
    }

    public void setGoodsextdId(Integer goodsextdId) {
        this.goodsextdId = goodsextdId;
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

    public String getGoodsBatchNo() {
        return goodsBatchNo;
    }

    public void setGoodsBatchNo(String goodsBatchNo) {
        this.goodsBatchNo = goodsBatchNo;
    }

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

    public String getGoodsAction() {
        return goodsAction;
    }

    public void setGoodsAction(String goodsAction) {
        this.goodsAction = goodsAction;
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

    public String getGoodsMobileDesc() {
        return goodsMobileDesc;
    }

    public void setGoodsMobileDesc(String goodsMobileDesc) {
        this.goodsMobileDesc = goodsMobileDesc;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
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

    public Long getIntegralGoodsId() {
        return integralGoodsId;
    }

    public void setIntegralGoodsId(Long integralGoodsId) {
        this.integralGoodsId = integralGoodsId;
    }

    public String getIntegralGoodsStoreIds() {
        return integralGoodsStoreIds;
    }

    public void setIntegralGoodsStoreIds(String integralGoodsStoreIds) {
        this.integralGoodsStoreIds = integralGoodsStoreIds;
    }

    public Date getIntegralGoodsStartTime() {
        return integralGoodsStartTime;
    }

    public void setIntegralGoodsStartTime(Date integralGoodsStartTime) {
        this.integralGoodsStartTime = integralGoodsStartTime;
    }

    public Date getIntegralGoodsEndTime() {
        return integralGoodsEndTime;
    }

    public void setIntegralGoodsEndTime(Date integralGoodsEndTime) {
        this.integralGoodsEndTime = integralGoodsEndTime;
    }

    public Integer getIntegralExchanges() {
        return integralExchanges;
    }

    public void setIntegralExchanges(Integer integralExchanges) {
        this.integralExchanges = integralExchanges;
    }

    public Integer getIntegralGoodsIsDel() {
        return integralGoodsIsDel;
    }

    public void setIntegralGoodsIsDel(Integer integralGoodsIsDel) {
        this.integralGoodsIsDel = integralGoodsIsDel;
    }

    public Integer getIntegralGoodsNum() {
        return integralGoodsNum;
    }

    public void setIntegralGoodsNum(Integer integralGoodsNum) {
        this.integralGoodsNum = integralGoodsNum;
    }

    public Integer getIntegralGoodsStatus() {
        return integralGoodsStatus;
    }

    public void setIntegralGoodsStatus(Integer integralGoodsStatus) {
        this.integralGoodsStatus = integralGoodsStatus;
    }

    public Integer getLimitEach() {return limitEach;}

    public void setLimitEach(Integer limitEach) {this.limitEach = limitEach;}

    public Integer getLimitCount() {return limitCount;}

    public void setLimitCount(Integer limitCount) {this.limitCount = limitCount;}

    public String getNetWt() {
        return netWt;
    }

    public void setNetWt(String netWt) {
        this.netWt = netWt;
    }
}
