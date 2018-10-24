package com.jk51.model.grouppurchase;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by mqq on 2017/11/17.
 */
public class GroupPurchase {
    private Integer siteId;

    private Integer id;

    /**
     * 如果parentId为null，表示该团是团长的团
     * 如果parentId存在，则表示该团的参团的
     */
    private Integer parentId;

    /**
     * promotionsActivity 的 Id
     */
    private Integer proActivityId;

    /**
     * trades 的 tradesId
     */
    private String tradesId;

    /**
     * 不一定存在，表示的是参团者关注微信公众号后生成的id，用来给顾客发消息用
     */
    private String openId;

    private String aliUserId;

    /**
     * 对应b_member表的id
     */
    private Integer memberId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime groupbeginTime;

    /**
     * 团员参团付款即为2 未付款为0
     * 团长开团未付款是0 付款是1 开团成功是2
     * 0 无效拼团(开团或参团失败) (未付款的)
     * 1 进行中 (只有团长的团才会有这个参数)
     * 2 拼团成功/参团成功
     * 3 拼团失败
     * 4 拼团取消
     */
    private Integer status;

    private Integer goodsId;

    private String wxInfo;

    private String buyerNick;

    private String mobile;

    private long lastTime;

    private String aftergroupLiveTime;

    public String getAliUserId() {
        return aliUserId;
    }

    public void setAliUserId(String aliUserId) {
        this.aliUserId = aliUserId;
    }

    public String getAftergroupLiveTime() {
        return aftergroupLiveTime;
    }

    public void setAftergroupLiveTime(String aftergroupLiveTime) {
        this.aftergroupLiveTime = aftergroupLiveTime;
    }

    private List<GroupPurchase> childrenList;

    public Integer getSiteId () {
        return siteId;
    }

    public void setSiteId (Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public Integer getMemberId () {
        return memberId;
    }

    public void setMemberId (Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getParentId () {
        return parentId;
    }

    public void setParentId (Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getProActivityId () {
        return proActivityId;
    }

    public void setProActivityId (Integer proActivityId) {
        this.proActivityId = proActivityId;
    }

    public LocalDateTime getCreateTime () {
        return createTime;
    }

    public void setCreateTime (LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime () {
        return updateTime;
    }

    public void setUpdateTime (LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getOpenId () {
        return openId;
    }

    public void setOpenId (String openId) {
        this.openId = openId;
    }

    public String getTradesId () {
        return tradesId;
    }

    public void setTradesId (String tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getStatus () {
        return status;
    }

    public void setStatus (Integer status) {
        this.status = status;
    }

    public List<GroupPurchase> getChildrenList () {
        return childrenList;
    }

    public void setChildrenList (List<GroupPurchase> childrenList) {
        this.childrenList = childrenList;
    }

    public Integer getGoodsId () {
        return goodsId;
    }

    public void setGoodsId (Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getWxInfo () {
        return wxInfo;
    }

    public void setWxInfo (String wxInfo) {
        this.wxInfo = wxInfo;
    }

    public long getLastTime () {
        return lastTime;
    }

    public void setLastTime (long lastTime) {
        this.lastTime = lastTime;
    }

    public LocalDateTime getGroupbeginTime () {
        return groupbeginTime;
    }

    public void setGroupbeginTime (LocalDateTime groupbeginTime) {
        this.groupbeginTime = groupbeginTime;
    }

    public String getMobile () {
        return mobile;
    }

    public void setMobile (String mobile) {
        this.mobile = mobile;
    }

    public String getBuyerNick () {
        return buyerNick;
    }

    public void setBuyerNick (String buyerNick) {
        this.buyerNick = buyerNick;
    }

    
}
