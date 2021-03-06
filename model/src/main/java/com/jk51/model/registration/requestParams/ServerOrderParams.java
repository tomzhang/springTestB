package com.jk51.model.registration.requestParams;

/**
 * Created by Administrator on 2017/4/7.
 */
public class ServerOrderParams {
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_servce_order.site_id
     *
     * @mbg.generated
     */
    private Integer siteId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_servce_order.store_id
     *
     * @mbg.generated
     */
    private Integer storeId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_servce_order.trades_id
     *
     * @mbg.generated
     */
    private String tradesId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column b_servce_order.use_detail_id
     *
     * @mbg.generated
     */
    private Integer useDetailId;

    private Integer goodsId;

    private Integer memberId;

    private String type; // 1下架，2停诊 3会员取消 4 签到

    private Integer serveStatus;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getUseDetailId() {
        return useDetailId;
    }

    public void setUseDetailId(Integer useDetailId) {
        this.useDetailId = useDetailId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getServeStatus() {
        return serveStatus;
    }

    public void setServeStatus(Integer serveStatus) {
        this.serveStatus = serveStatus;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
