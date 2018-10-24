package com.jk51.model.promotions;

/**
 * Created by javen on 2017/11/20.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * 一眼看清优惠类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EasyToSee {
    //商品ID
    private String goodsId;
    @JsonIgnore
    private ProCouponEasyToSee proCouponEasyToSee = new ProCouponEasyToSee();
    private List<String> tags = new ArrayList<String>();
    //限价、拼团
    private Integer better_price;
    //50/60
    private Integer better_type;

    // 商品标题显示  包邮、用券
    private List<String> titles = new ArrayList<String>();
    //优惠券标签
    private List<EasyToSeeCoupon> couponTags = new ArrayList<EasyToSeeCoupon>();

    public EasyToSee() {
    }

    public EasyToSee(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public ProCouponEasyToSee getProCouponEasyToSee() {
        return proCouponEasyToSee;
    }

    public void setProCouponEasyToSee(ProCouponEasyToSee proCouponEasyToSee) {
        this.proCouponEasyToSee = proCouponEasyToSee;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getBetter_price() {
        return better_price;
    }

    public void setBetter_price(Integer better_price) {
        this.better_price = better_price;
    }

    public Integer getBetter_type() {
        return better_type;
    }

    public void setBetter_type(Integer better_type) {
        this.better_type = better_type;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<EasyToSeeCoupon> getCouponTags() {
        return couponTags;
    }

    public void setCouponTags(List<EasyToSeeCoupon> couponTags) {
        this.couponTags = couponTags;
    }

    @Override
    public String toString() {
        return "EasyToSee{" +
            "goodsId='" + goodsId + '\'' +
            ", proCouponEasyToSee=" + proCouponEasyToSee +
            '}';
    }
}


