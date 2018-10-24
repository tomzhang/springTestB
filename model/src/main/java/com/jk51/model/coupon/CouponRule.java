package com.jk51.model.coupon;

import com.jk51.model.coupon.requestParams.CouponView;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.LimitRule;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * 优惠券规则实体类 对应表名 b_coupon_rule
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司        <br/>
 * 作者: chenpeng                               <br/>
 * 创建日期: 2017-03-02                         <br/>
 * 修改记录:                                    <br/>
 */
public class CouponRule implements Serializable {

    private Integer siteId;
    private Integer ruleId;
    private String ruleName;
    private String markedWords;
    /**
     * 100现金券 200打折券 300限价券 400包邮券 500满赠券
     */
    private int couponType;
    private int amount;
    private String timeRule;
    /**
     * limitRule的内容为{@link LimitRule}的json转换
     */
    private String limitRule;
    private String limitState;
    private String limitRemark;
    private int aimAt;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    /**
     * 新规则状态 0正常(可发放) 1已过期 2手动停发 3已发完 4手动作废 10待发放
     */
    private int status;
    private String orderRule;
    private String areaRule;

    /**
     * 该字段为json字段，对应{@link GoodsRule}
     */
    private String goodsRule;
    private int version;

    private Integer total;
    private Integer shareNum;
    private Integer orderPrice;
    private Integer goodsNum;

    private Integer sendMemberNum; // 发放会员数
    private Integer memberNum; // 使用会员数
    /**
     * 不推荐使用的原因是发放优惠券时不保证更新数据库中的该字段
     */
    @Deprecated
    private Integer sendNum;
    /**
     * 不推荐使用的原因是使用优惠券时不保证更新数据库中的该字段
     */
    @Deprecated
    private Integer useAmount;

    /**
     * 不推荐使用的原因是使用优惠券时不保证更新数据库中的该字段
     */
    @Deprecated
    private Integer receiveNum;
    private Integer oldCouponId;

    private CouponView couponView;
    private String effectiveTime; //计算有效时间
    private Integer effectiveTimeType;//解析时间类型1普通时间即倒计时时间2显示会员券时间

    private List storeList;

    private Integer isLine;

    private Integer useScope;

    private Integer isShare;
    private Integer isCanGet;

    public CouponRule() {
    }

    public CouponRule(Integer siteId, Integer ruleId, int amount) {
        this.siteId = siteId;
        this.ruleId = ruleId;
        this.amount = amount;
    }

    public Integer getIsCanGet() {
        return isCanGet;
    }

    public void setIsCanGet(Integer isCanGet) {
        this.isCanGet = isCanGet;
    }

    public Integer getSendMemberNum() {
        return sendMemberNum;
    }

    public void setSendMemberNum(Integer sendMemberNum) {
        this.sendMemberNum = sendMemberNum;
    }

    public Integer getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getMarkedWords() {
        return markedWords;
    }

    public void setMarkedWords(String markedWords) {
        this.markedWords = markedWords;
    }

    public int getCouponType() {
        return couponType;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTimeRule() {
        return timeRule;
    }

    public void setTimeRule(String timeRule) {
        this.timeRule = timeRule;
    }

    public String getLimitRule() {
        return limitRule;
    }

    public void setLimitRule(String limitRule) {
        this.limitRule = limitRule;
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

    public int getAimAt() {
        return aimAt;
    }

    public void setAimAt(int aimAt) {
        this.aimAt = aimAt;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderRule() {
        return orderRule;
    }

    public void setOrderRule(String orderRule) {
        this.orderRule = orderRule;
    }

    public String getAreaRule() {
        return areaRule;
    }

    public void setAreaRule(String areaRule) {
        this.areaRule = areaRule;
    }

    public String getGoodsRule() {
        return goodsRule;
    }

    public void setGoodsRule(String goodsRule) {
        this.goodsRule = goodsRule;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Integer getUseAmount() {
        return useAmount;
    }

    public void setUseAmount(Integer useAmount) {
        this.useAmount = useAmount;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getShareNum() {
        return shareNum;
    }

    public void setShareNum(Integer shareNum) {
        this.shareNum = shareNum;
    }

    public Integer getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Integer orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public CouponView getCouponView() {
        return couponView;
    }

    public void setCouponView(CouponView couponView) {
        this.couponView = couponView;
    }

    public Integer getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(Integer receiveNum) {
        this.receiveNum = receiveNum;
    }

    public Integer getOldCouponId() {
        return oldCouponId;
    }

    public void setOldCouponId(Integer oldCouponId) {
        this.oldCouponId = oldCouponId;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getEffectiveTimeType() {
        return effectiveTimeType;
    }

    public void setEffectiveTimeType(Integer effectiveTimeType) {
        this.effectiveTimeType = effectiveTimeType;
    }

    public List getStoreList() {
        return storeList;
    }

    public void setStoreList(List storeList) {
        this.storeList = storeList;
    }

    public Integer getIsLine() {
        return isLine;
    }

    public void setIsLine(Integer isLine) {
        this.isLine = isLine;
    }

    public Integer getUseScope() {
        return useScope;
    }

    public void setUseScope(Integer useScope) {
        this.useScope = useScope;
    }

    public Integer getIsShare() {
        return isShare;
    }

    public void setIsShare(Integer isShare) {
        this.isShare = isShare;
    }

    @Override
    public String toString() {
        return "CouponRule{" +
                "siteId=" + siteId +
                ", ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", markedWords='" + markedWords + '\'' +
                ", couponType=" + couponType +
                ", amount=" + amount +
                ", timeRule='" + timeRule + '\'' +
                ", limitRule='" + limitRule + '\'' +
                ", limitState='" + limitState + '\'' +
                ", limitRemark='" + limitRemark + '\'' +
                ", aimAt=" + aimAt +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", status=" + status +
                ", orderRule='" + orderRule + '\'' +
                ", areaRule='" + areaRule + '\'' +
                ", goodsRule='" + goodsRule + '\'' +
                ", version=" + version +
                ", useAmount=" + useAmount +
                ", total=" + total +
                ", sendNum=" + sendNum +
                ", shareNum=" + shareNum +
                ", isLine=" + isLine +
                ", useScope=" + useScope +
                ", isShare=" + isShare +
                ", orderPrice=" + orderPrice +
                ", goodsNum=" + goodsNum +
                ", receiveNum=" + receiveNum +
                ", oldCouponId=" + oldCouponId +
                ", effectiveTime='" + effectiveTime + '\'' +
                ", effectiveTimeType=" + effectiveTimeType +
                '}';
    }
}
