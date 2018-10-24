package com.jk51.model.coupon.requestParams;

/**
 * Created by Administrator on 2017/7/12.
 */
public class SignMembers {

    private  Integer type; //0全部会员  1指定标签组会员 2 指定会员 3 指定标签会员
    private String promotion_members;

    public Integer getType() {
        return type;
    }

    public void setPromotion_members(String promotion_members) {
        this.promotion_members = promotion_members;
    }

    public String getPromotion_members() {
        return promotion_members;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
