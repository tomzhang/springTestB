package com.jk51.model.promotions.rule;

/**
 * Created by Administrator on 2017/8/17.
 */

/**
 * 赠品实体类
 */
public class GiftReturn {
    private  Integer goodsId ;

    private  Integer siteId;

    private  String goodsTitle;

    private  String  hash;

    private  Integer sendNum;

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getGoodsTitle() {

        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
}
