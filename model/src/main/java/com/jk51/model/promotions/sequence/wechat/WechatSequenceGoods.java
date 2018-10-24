package com.jk51.model.promotions.sequence.wechat;

import com.jk51.model.promotions.sequence.SequenceGoods;

import java.util.Map;

/**
 * Created by javen73 on 2018/5/12.
 */
public class WechatSequenceGoods extends SequenceGoods{
    //成团人数限制
    private Integer limitGroup;
    //限价
    private Integer limitNum;
    //剩余拼团人数
    private Integer limitGroupMemberNum;
    //拼团和限价才有数据 拼团和限价中的价格
    private Integer limitPrice;


    public Integer getLimitGroupMemberNum() {
        return limitGroupMemberNum;
    }

    public void setLimitGroupMemberNum(Integer limitGroupMemberNum) {
        this.limitGroupMemberNum = limitGroupMemberNum;
    }

    public Integer getLimitGroup() {
        return limitGroup;
    }

    public void setLimitGroup(Integer limitGroup) {
        this.limitGroup = limitGroup;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public WechatSequenceGoods(Map goods, Integer sequence) {
        super(goods, sequence);
    }

    public Integer getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(Integer limitPrice) {
        this.limitPrice = limitPrice;
    }
}
