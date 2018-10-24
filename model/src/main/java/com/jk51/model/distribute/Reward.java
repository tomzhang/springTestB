package com.jk51.model.distribute;

import java.sql.Timestamp;

/**
 * Created by admin on 2017/2/10.
 */
public class Reward {

    /**
     * 分佣(价格单位统一为分)
     */
    private long id;

    /**
     * 订单ID
     */
    private long order_id;

    /**
     * 门店site_id
     */
    private long owner;

    /**
     * 分销商id
     */
    private long distributor_id;

    private Integer distributor_father_id;

    private Integer distributor_grandfather_id;

    /**
     * 订单总额
     */
    private long order_price;

    /**
     * 实际支付金额
     */
    private long real_pay;

    /**
     * 下单推荐分佣
     */
    private long level_1_award;

    /**
     * 上级推荐分佣
     */
    private long level_2_award;

    /**
     * 上上级推荐分佣
     */
    private long level_3_award;

    /**
     * 总分佣
     */
    private long total_award;

    /**
     * 订单状态 0：交易失败（退款） 1：交易成功  2：交易结束
     */
    private int order_status;

    /**
     * 奖励状态：0-待确认 1-已确认
     */
    private int reward_status;

    /**
     *
     */
    private long buyer_id;

    /**
     * 对应ybzf退款表主键
     */
    private long refund_id;

    /**
     * 下单时间
     */
    private Timestamp create_time;

    /**
     * 更新时间
     */
    private Timestamp update_time;

    public Integer getDistributor_father_id() {
        return distributor_father_id;
    }

    public void setDistributor_father_id(Integer distributor_father_id) {
        this.distributor_father_id = distributor_father_id;
    }

    public Integer getDistributor_grandfather_id() {
        return distributor_grandfather_id;
    }

    public void setDistributor_grandfather_id(Integer distributor_grandfather_id) {
        this.distributor_grandfather_id = distributor_grandfather_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public long getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(long distributor_id) {
        this.distributor_id = distributor_id;
    }

    public long getOrder_price() {
        return order_price;
    }

    public void setOrder_price(long order_price) {
        this.order_price = order_price;
    }

    public long getReal_pay() {
        return real_pay;
    }

    public void setReal_pay(long real_pay) {
        this.real_pay = real_pay;
    }

    public long getLevel_1_award() {
        return level_1_award;
    }

    public void setLevel_1_award(long level_1_award) {
        this.level_1_award = level_1_award;
    }

    public long getLevel_2_award() {
        return level_2_award;
    }

    public void setLevel_2_award(long level_2_award) {
        this.level_2_award = level_2_award;
    }

    public long getLevel_3_award() {
        return level_3_award;
    }

    public void setLevel_3_award(long level_3_award) {
        this.level_3_award = level_3_award;
    }

    public long getTotal_award() {
        return total_award;
    }

    public void setTotal_award(long total_award) {
        this.total_award = total_award;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public int getReward_status() {
        return reward_status;
    }

    public void setReward_status(int reward_status) {
        this.reward_status = reward_status;
    }

    public long getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(long buyer_id) {
        this.buyer_id = buyer_id;
    }

    public long getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(long refund_id) {
        this.refund_id = refund_id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
