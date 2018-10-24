package com.jk51.model.coupon.requestParams;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/3/7
 * Update   :
 */
public class LimitRule {
    private Integer is_first_order; //是否是首单 0不限 1仅首单
    private Integer register_auto_send; //注册后自动发放 0默认不发放 1发放
    private String order_type; //订单类型 100自提订单 200送货上门 300门店直购 400预购订单 多选用逗号隔开
    private String apply_channel;//适用渠道 100门店后台 101门店助手 102Pc站 103微信商城 104支付宝 105线下
    private Integer apply_store; //适用门店 -1全部门店 1具体门店 2指定区域内的门店
    private String use_stores; //门店id逗号隔开
    private Integer is_share;//是否可分享 0不可分享 1可分享
    private Integer can_get_num;//分享后可领取数量

    public Integer getIs_first_order() {
        return is_first_order;
    }

    public void setIs_first_order(Integer is_first_order) {
        this.is_first_order = is_first_order;
    }

    public Integer getRegister_auto_send() {
        return register_auto_send;
    }

    public void setRegister_auto_send(Integer register_auto_send) {
        this.register_auto_send = register_auto_send;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getApply_channel() {
        return apply_channel;
    }

    public void setApply_channel(String apply_channel) {
        this.apply_channel = apply_channel;
    }

    public Integer getApply_store() {
        return apply_store;
    }

    public void setApply_store(Integer apply_store) {
        this.apply_store = apply_store;
    }

    public String getUse_stores() {
        return use_stores;
    }

    public void setUse_stores(String use_stores) {
        this.use_stores = use_stores;
    }

    public Integer getIs_share() {
        return is_share;
    }

    public void setIs_share(Integer is_share) {
        this.is_share = is_share;
    }

    public Integer getCan_get_num() {
        return can_get_num;
    }

    public void setCan_get_num(Integer can_get_num) {
        this.can_get_num = can_get_num;
    }
}
