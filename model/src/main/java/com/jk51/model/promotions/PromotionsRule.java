package com.jk51.model.promotions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jk51.commons.json.LocalDateTimeDeserializerForLongFormatter;
import com.jk51.commons.json.LocalDateTimeSerializerForLongFormatter;
import com.jk51.model.promotions.rule.*;

import java.time.LocalDateTime;

/**
 * 活动规则对应的实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
public class PromotionsRule {
    private Integer id;
    private Integer siteId;

    /**
     * 类型 10满赠活动 20打折活动 30包邮活动 40满减活动 50限价活动 60拼团活动
     */
    private Integer promotionsType;

    /**
     * 优惠名称
     */
    private String promotionsName;

    /**
     * 优惠标签
     */
    private String label;

    /**
     * 规则状态, 默认值是10，定义：0正常(可发放) 1已过期 2手动停发 3已发完 10待发放
     */
    private Integer status;

    /**
     * 优惠规则 json
     * <ul>
     * <li>10满赠活动对应实体类{@link GiftRule}</li>
     * <li>20打折活动对应实体类{@link DiscountRule}</li>
     * <li>30包邮活动对应实体类{@link FreePostageRule}</li>
     * <li>40满减活动对应实体类{@link ReduceMoneyRule}</li>
     * <li>50限价活动对应实体类{@link FixedPriceRule}</li>
     * <li>60拼团活动对应实体类{@link GroupBookingRule}</li>
     * </ul>
     */
    private String promotionsRule;

    /**
     * 库存 -1表示不限量
     */
    private Integer amount;

    /**
     * 时间规则 json 实体类为{@link TimeRuleForPromotionsRule} <br/>
     */
    private String timeRule;

    /**
     * 是否首单 0不限  1首单
     */
    private Integer isFirstOrder;

    /**
     * 订单类型 -1表示全部 100门店自提 200送货上门 300门店直购（app下单） 多选逗号隔开
     */
    private String orderType;

    /**
     * 使用门店 -1表示全部门店 1表示具体门店 2表示指定区域
     */
    private String useStore;

    /**
     * 以逗号分隔的地区或门店Id
     */
    private String useArea;

    /**
     * 用作锁机制
     */
    private Integer version;

    /**
     * 总量-1表示不限量
     */
    private Integer total;

    /**
     * 已使用数量
     */
    private Integer useAmount;

    /**
     * 生成数量
     */
    private Integer sendAmount;

    /**
     * {@link PromotionsActivity#id}
     */
    private Integer activityId;

    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime createTime;

    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime updateTime;

    /**
     * 优惠说明
     */
    private String limitState;

    /**
     * 商家备注
     */
    private String limitRemark;

    private ProCouponRuleView proCouponRuleView;

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

    public Integer getPromotionsType() {
        return promotionsType;
    }

    public void setPromotionsType(Integer promotionsType) {
        this.promotionsType = promotionsType;
    }

    public String getPromotionsName() {
        return promotionsName;
    }

    public void setPromotionsName(String promotionsName) {
        this.promotionsName = promotionsName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getIsFirstOrder() {
        return isFirstOrder;
    }

    public void setIsFirstOrder(Integer isFirstOrder) {
        this.isFirstOrder = isFirstOrder;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getUseStore() {
        return useStore;
    }

    public void setUseStore(String useStore) {
        this.useStore = useStore;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getUseAmount() {
        return useAmount;
    }

    public void setUseAmount(Integer useAmount) {
        this.useAmount = useAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getPromotionsRule() {
        return promotionsRule;
    }

    public void setPromotionsRule(String promotionsRule) {
        this.promotionsRule = promotionsRule;
    }

    public String getTimeRule() {
        return timeRule;
    }

    public void setTimeRule(String timeRule) {
        this.timeRule = timeRule;
    }

    public String getLimitState() {
        return limitState;
    }

    public void setLimitState(String limitState) {
        this.limitState = limitState;
    }

    public String getLimitRemark() {
        return limitRemark;
    }

    public void setLimitRemark(String limitRemark) {
        this.limitRemark = limitRemark;
    }

    public ProCouponRuleView getProCouponRuleView() {
        return proCouponRuleView;
    }

    public void setProCouponRuleView(ProCouponRuleView proCouponRuleView) {
        this.proCouponRuleView = proCouponRuleView;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(Integer sendAmount) {
        this.sendAmount = sendAmount;
    }

    public String getUseArea() {
        return useArea;
    }

    public void setUseArea(String useArea) {
        this.useArea = useArea;
    }

    @Override
    public String toString() {
        return "PromotionsRule{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", promotionsType=" + promotionsType +
            ", promotionsName='" + promotionsName + '\'' +
            ", label='" + label + '\'' +
            ", status=" + status +
            ", promotionsRule='" + promotionsRule + '\'' +
            ", amount=" + amount +
            ", timeRule='" + timeRule + '\'' +
            ", isFirstOrder=" + isFirstOrder +
            ", orderType='" + orderType + '\'' +
            ", useStore='" + useStore + '\'' +
            ", version=" + version +
            ", total=" + total +
            ", useAmount=" + useAmount +
            ", useArea=" + useArea +
            ", sendAmount=" + sendAmount +
            ", activityId=" + activityId +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", limitState='" + limitState + '\'' +
            ", limitRemark='" + limitRemark + '\'' +
            ", proCouponRuleView=" + proCouponRuleView +
            '}';
    }
}
