package com.jk51.model.order;

import java.sql.Timestamp;
import com.jk51.model.concession.ConcessionDesc;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class Orders {
    private Integer siteId;
    private Integer id;
    private long orderId;
    private Integer goodsId;

    private String goodsTitle;
    private Integer goodsPrice;
    private Integer goodsNum;

    // 赠品相关
    private Integer goodsGifts;  //是否赠品
    /**
     * {@link ConcessionDesc}的json串
     */
    private String concessionDesc;

    private String approvalNumber;
    private String specifCation;
    private Integer goodsCategory;
    private long tradesId;
    private String goodsImgurl;
    private Integer ordersStatus;
    private String goodsCode;
    private Integer ybGoodsId;
    private String goodsBatchNo;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer tradesSnapshot;
    private String goodsCompany;   //生产企业
    private String barndName;
    private String goodsStatus;  //产品状态. 1(出售中),2(库存中), 3（违规）,4(软删除)
    private String hash;
    private Integer goodsFinalPrice;

    public Integer getGoodsFinalPrice() {
        return goodsFinalPrice;
    }

    public void setGoodsFinalPrice(Integer goodsFinalPrice) {
        this.goodsFinalPrice = goodsFinalPrice;
    }

    public String getConcessionDesc() {
        return concessionDesc;
    }

    public void setConcessionDesc(String concessionDesc) {
        this.concessionDesc = concessionDesc;
    }

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Integer getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Integer goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getGoodsGifts() {
        return goodsGifts;
    }

    public void setGoodsGifts(Integer goodsGifts) {
        this.goodsGifts = goodsGifts;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation;
    }

    public Integer getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(Integer goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public long getTradesId() {
        return tradesId;
    }

    public void setTradesId(long tradesId) {
        this.tradesId = tradesId;
    }

    public String getGoodsImgurl() {
        return goodsImgurl;
    }

    public void setGoodsImgurl(String goodsImgurl) {
        this.goodsImgurl = goodsImgurl;
    }

    public Integer getOrdersStatus() {
        return ordersStatus;
    }

    public void setOrdersStatus(Integer ordersStatus) {
        this.ordersStatus = ordersStatus;
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

    public String getGoodsBatchNo() {
        return goodsBatchNo;
    }

    public void setGoodsBatchNo(String goodsBatchNo) {
        this.goodsBatchNo = goodsBatchNo;
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

    public Integer getTradesSnapshot() {
        return tradesSnapshot;
    }

    public void setTradesSnapshot(Integer tradesSnapshot) {
        this.tradesSnapshot = tradesSnapshot;
    }

    public String getGoodsCompany() {
        return goodsCompany;
    }

    public void setGoodsCompany(String goodsCompany) {
        this.goodsCompany = goodsCompany;
    }

    public String getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(String goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", orderId=" + orderId +
                ", goodsId=" + goodsId +
                ", goodsTitle='" + goodsTitle + '\'' +
                ", goodsPrice=" + goodsPrice +
                ", goodsNum=" + goodsNum +
                ", goodsGifts=" + goodsGifts +
                ", approvalNumber='" + approvalNumber + '\'' +
                ", specifCation='" + specifCation + '\'' +
                ", goodsCategory=" + goodsCategory +
                ", tradesId=" + tradesId +
                ", goodsImgurl='" + goodsImgurl + '\'' +
                ", ordersStatus=" + ordersStatus +
                ", goodsCode='" + goodsCode + '\'' +
                ", ybGoodsId=" + ybGoodsId +
                ", goodsBatchNo='" + goodsBatchNo + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", tradesSnapshot=" + tradesSnapshot +
                '}';
    }
}
