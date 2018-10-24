package com.jk51.model.account.requestParams;

/**
 * filename :com.jk51.model.account.requestParams.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 */
public class CommissionParam {
    private Integer site_id;
    private Double wx_collection_fee; //微信代收手续费
    private Double ali_collection_fee;//支付宝代收手续费
    private Double cash_collection_fee;//现金代收手续费
    private Double health_insurance_collection_fee;//医保卡代收手续费
    private Double unionPay_collection_fee;//银联代收手续费
    private Double direct_purchase_fee;  //直购订单佣金比率
    private Double distributor_fee;      //分销订单佣金比率
    private Double delivery_fee;         //配送费佣金比率

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Double getWx_collection_fee() {
        return wx_collection_fee;
    }

    public void setWx_collection_fee(Double wx_collection_fee) {
        this.wx_collection_fee = wx_collection_fee;
    }

    public Double getAli_collection_fee() {
        return ali_collection_fee;
    }

    public void setAli_collection_fee(Double ali_collection_fee) {
        this.ali_collection_fee = ali_collection_fee;
    }

    public Double getCash_collection_fee() {
        return cash_collection_fee;
    }

    public void setCash_collection_fee(Double cash_collection_fee) {
        this.cash_collection_fee = cash_collection_fee;
    }

    public Double getHealth_insurance_collection_fee() {
        return health_insurance_collection_fee;
    }

    public void setHealth_insurance_collection_fee(Double health_insurance_collection_fee) {
        this.health_insurance_collection_fee = health_insurance_collection_fee;
    }

    public Double getUnionPay_collection_fee() {
        return unionPay_collection_fee;
    }

    public void setUnionPay_collection_fee(Double unionPay_collection_fee) {
        this.unionPay_collection_fee = unionPay_collection_fee;
    }

    public Double getDirect_purchase_fee() {
        return direct_purchase_fee;
    }

    public void setDirect_purchase_fee(Double direct_purchase_fee) {
        this.direct_purchase_fee = direct_purchase_fee;
    }

    public Double getDistributor_fee() {
        return distributor_fee;
    }

    public void setDistributor_fee(Double distributor_fee) {
        this.distributor_fee = distributor_fee;
    }

    public Double getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(Double delivery_fee) {
        this.delivery_fee = delivery_fee;
    }
}
