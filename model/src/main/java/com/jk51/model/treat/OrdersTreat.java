package com.jk51.model.treat;

import java.util.Date;

public class OrdersTreat {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.site_id
     *
     * @mbg.generated
     */
    private Integer siteId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.order_id
     *
     * @mbg.generated
     */
    private String orderId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_id
     *
     * @mbg.generated
     */
    private Integer goodsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_title
     *
     * @mbg.generated
     */
    private String goodsTitle;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_price
     *
     * @mbg.generated
     */
    private Integer goodsPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_num
     *
     * @mbg.generated
     */
    private Integer goodsNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_gifts
     *
     * @mbg.generated
     */
    private Integer goodsGifts;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.approval_number
     *
     * @mbg.generated
     */
    private String approvalNumber;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.specif_cation
     *
     * @mbg.generated
     */
    private String specifCation;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_category
     *
     * @mbg.generated
     */
    private Integer goodsCategory;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.trades_id
     *
     * @mbg.generated
     */
    private String tradesId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_imgurl
     *
     * @mbg.generated
     */
    private String goodsImgurl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.orders_status
     *
     * @mbg.generated
     */
    private Integer ordersStatus;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_code
     *
     * @mbg.generated
     */
    private String goodsCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.yb_goods_id
     *
     * @mbg.generated
     */
    private Integer ybGoodsId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.goods_batch_no
     *
     * @mbg.generated
     */
    private String goodsBatchNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_orders.trades_snapshot
     *
     * @mbg.generated
     */
    private Integer tradesSnapshot;

    private String refundGoodsCode;

    public String getRefundGoodsCode() {
        return refundGoodsCode;
    }

    public void setRefundGoodsCode(String refundGoodsCode) {
        this.refundGoodsCode = refundGoodsCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.id
     *
     * @return the value of b_orders.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.id
     *
     * @param id the value for b_orders.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.site_id
     *
     * @return the value of b_orders.site_id
     *
     * @mbg.generated
     */
    public Integer getSiteId() {
        return siteId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.site_id
     *
     * @param siteId the value for b_orders.site_id
     *
     * @mbg.generated
     */
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.order_id
     *
     * @return the value of b_orders.order_id
     *
     * @mbg.generated
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.order_id
     *
     * @param orderId the value for b_orders.order_id
     *
     * @mbg.generated
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_id
     *
     * @return the value of b_orders.goods_id
     *
     * @mbg.generated
     */
    public Integer getGoodsId() {
        return goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_id
     *
     * @param goodsId the value for b_orders.goods_id
     *
     * @mbg.generated
     */
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_title
     *
     * @return the value of b_orders.goods_title
     *
     * @mbg.generated
     */
    public String getGoodsTitle() {
        return goodsTitle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_title
     *
     * @param goodsTitle the value for b_orders.goods_title
     *
     * @mbg.generated
     */
    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle == null ? null : goodsTitle.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_price
     *
     * @return the value of b_orders.goods_price
     *
     * @mbg.generated
     */
    public Integer getGoodsPrice() {
        return goodsPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_price
     *
     * @param goodsPrice the value for b_orders.goods_price
     *
     * @mbg.generated
     */
    public void setGoodsPrice(Integer goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_num
     *
     * @return the value of b_orders.goods_num
     *
     * @mbg.generated
     */
    public Integer getGoodsNum() {
        return goodsNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_num
     *
     * @param goodsNum the value for b_orders.goods_num
     *
     * @mbg.generated
     */
    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_gifts
     *
     * @return the value of b_orders.goods_gifts
     *
     * @mbg.generated
     */
    public Integer getGoodsGifts() {
        return goodsGifts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_gifts
     *
     * @param goodsGifts the value for b_orders.goods_gifts
     *
     * @mbg.generated
     */
    public void setGoodsGifts(Integer goodsGifts) {
        this.goodsGifts = goodsGifts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.approval_number
     *
     * @return the value of b_orders.approval_number
     *
     * @mbg.generated
     */
    public String getApprovalNumber() {
        return approvalNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.approval_number
     *
     * @param approvalNumber the value for b_orders.approval_number
     *
     * @mbg.generated
     */
    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber == null ? null : approvalNumber.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.specif_cation
     *
     * @return the value of b_orders.specif_cation
     *
     * @mbg.generated
     */
    public String getSpecifCation() {
        return specifCation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.specif_cation
     *
     * @param specifCation the value for b_orders.specif_cation
     *
     * @mbg.generated
     */
    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation == null ? null : specifCation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_category
     *
     * @return the value of b_orders.goods_category
     *
     * @mbg.generated
     */
    public Integer getGoodsCategory() {
        return goodsCategory;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_category
     *
     * @param goodsCategory the value for b_orders.goods_category
     *
     * @mbg.generated
     */
    public void setGoodsCategory(Integer goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.trades_id
     *
     * @return the value of b_orders.trades_id
     *
     * @mbg.generated
     */
    public String getTradesId() {
        return tradesId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.trades_id
     *
     * @param tradesId the value for b_orders.trades_id
     *
     * @mbg.generated
     */
    public void setTradesId(String tradesId) {
        this.tradesId = tradesId == null ? null : tradesId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_imgurl
     *
     * @return the value of b_orders.goods_imgurl
     *
     * @mbg.generated
     */
    public String getGoodsImgurl() {
        return goodsImgurl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_imgurl
     *
     * @param goodsImgurl the value for b_orders.goods_imgurl
     *
     * @mbg.generated
     */
    public void setGoodsImgurl(String goodsImgurl) {
        this.goodsImgurl = goodsImgurl == null ? null : goodsImgurl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.orders_status
     *
     * @return the value of b_orders.orders_status
     *
     * @mbg.generated
     */
    public Integer getOrdersStatus() {
        return ordersStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.orders_status
     *
     * @param ordersStatus the value for b_orders.orders_status
     *
     * @mbg.generated
     */
    public void setOrdersStatus(Integer ordersStatus) {
        this.ordersStatus = ordersStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_code
     *
     * @return the value of b_orders.goods_code
     *
     * @mbg.generated
     */
    public String getGoodsCode() {
        return goodsCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_code
     *
     * @param goodsCode the value for b_orders.goods_code
     *
     * @mbg.generated
     */
    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode == null ? null : goodsCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.yb_goods_id
     *
     * @return the value of b_orders.yb_goods_id
     *
     * @mbg.generated
     */
    public Integer getYbGoodsId() {
        return ybGoodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.yb_goods_id
     *
     * @param ybGoodsId the value for b_orders.yb_goods_id
     *
     * @mbg.generated
     */
    public void setYbGoodsId(Integer ybGoodsId) {
        this.ybGoodsId = ybGoodsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.goods_batch_no
     *
     * @return the value of b_orders.goods_batch_no
     *
     * @mbg.generated
     */
    public String getGoodsBatchNo() {
        return goodsBatchNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.goods_batch_no
     *
     * @param goodsBatchNo the value for b_orders.goods_batch_no
     *
     * @mbg.generated
     */
    public void setGoodsBatchNo(String goodsBatchNo) {
        this.goodsBatchNo = goodsBatchNo == null ? null : goodsBatchNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.create_time
     *
     * @return the value of b_orders.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.create_time
     *
     * @param createTime the value for b_orders.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.update_time
     *
     * @return the value of b_orders.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.update_time
     *
     * @param updateTime the value for b_orders.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column b_orders.trades_snapshot
     *
     * @return the value of b_orders.trades_snapshot
     *
     * @mbg.generated
     */
    public Integer getTradesSnapshot() {
        return tradesSnapshot;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column b_orders.trades_snapshot
     *
     * @param tradesSnapshot the value for b_orders.trades_snapshot
     *
     * @mbg.generated
     */
    public void setTradesSnapshot(Integer tradesSnapshot) {
        this.tradesSnapshot = tradesSnapshot;
    }
}
