package com.jk51.model.coupon.requestParams;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/7/25.
 */
public class CouponActiveParams {

    private Integer siteId;
    private Integer id;
    private String title;
    private String content;
    private String image;
    private Integer sendObj;
    private SignMembers  signMermbers;//指定会员标签信息
    private Integer sendType;
    private Integer sendConditionType;
    private String sendCondition;//多少件或多少元 &&non&&商品ID集合 或者 多少件或多少元&&商品ID集合
    private Integer sendWay;
    private String sendWayValue; //门店或店员ID&&门店或店员ID

    private Integer sendLimit;

    private Integer status;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createTime;
    private Timestamp updateTime;

    private String sendRules;
    private Integer createdTotal;//生成优惠券总数
    private Integer usedTotal;//使用优惠券总数

    private Integer sendStatus;//活动发放状态
    private Integer sendConditionValue; //多少件或多少元

    private String timeRule;//领取有效时间

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

    public Integer getSendConditionType() {
        return sendConditionType;
    }

    public void setSendConditionType(Integer sendConditionType) {
        this.sendConditionType = sendConditionType;
    }

    public Integer getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(Integer sendLimit) {
        this.sendLimit = sendLimit;
    }

    public Integer getCreatedTotal() {
        return createdTotal;
    }

    public void setCreatedTotal(Integer createdTotal) {
        this.createdTotal = createdTotal;
    }

    public Integer getSendConditionValue() {
        return sendConditionValue;
    }

    public void setSendConditionValue(Integer sendConditionValue) {
        this.sendConditionValue = sendConditionValue;
    }

    public Integer getSendObj() {
        return sendObj;
    }

    public void setSendObj(Integer sendObj) {
        this.sendObj = sendObj;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getSendWay() {
        return sendWay;
    }

    public void setSendWay(Integer sendWay) {
        this.sendWay = sendWay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getUsedTotal() {
        return usedTotal;
    }

    public void setUsedTotal(Integer usedTotal) {
        this.usedTotal = usedTotal;
    }

    public SignMembers getSignMermbers() {
        return signMermbers;
    }

    public void setSignMermbers(SignMembers signMermbers) {
        this.signMermbers = signMermbers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSendRules() {
        return sendRules;
    }

    public void setSendRules(String sendRules) {
        this.sendRules = sendRules;
    }

    public String getSendCondition() {
        return sendCondition;
    }

    public void setSendCondition(String sendCondition) {
        this.sendCondition = sendCondition;
    }

    public String getSendWayValue() {
        return sendWayValue;
    }

    public void setSendWayValue(String sendWayValue) {
        this.sendWayValue = sendWayValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getTimeRule() {
        return timeRule;
    }

    public void setTimeRule(String timeRule) {
        this.timeRule = timeRule;
    }
}
