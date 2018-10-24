package com.jk51.model.concession;

import java.util.Objects;

public class GiftMsg {
    private Integer goodsId;

    /**
     * 该赠品最多赠送多少
     */
    private Integer sendNum;

    /**
     * 药物名称
     */
    private String drugName;

    /**
     * 药物规格
     */
    private String specifCation;

    /**
     * 单位：分
     */
    private Integer shopPrice;

    /**
     * 图片hash值
     */
    private String hash;


    /* -- setter & getter -- */

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getSpecifCation() {
        return specifCation;
    }

    public void setSpecifCation(String specifCation) {
        this.specifCation = specifCation;
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftMsg)) return false;
        GiftMsg giftMsg = (GiftMsg) o;
        return Objects.equals(getGoodsId(), giftMsg.getGoodsId()) &&
            Objects.equals(getSendNum(), giftMsg.getSendNum()) &&
            Objects.equals(getDrugName(), giftMsg.getDrugName()) &&
            Objects.equals(getSpecifCation(), giftMsg.getSpecifCation()) &&
            Objects.equals(getShopPrice(), giftMsg.getShopPrice()) &&
            Objects.equals(getHash(), giftMsg.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGoodsId(), getSendNum(), getDrugName(), getSpecifCation(), getShopPrice(), getHash());
    }

    @Override
    public String toString() {
        return "GiftMsg{" +
            "goodsId=" + goodsId +
            ", sendNum=" + sendNum +
            ", drugName='" + drugName + '\'' +
            ", specifCation='" + specifCation + '\'' +
            ", shopPrice=" + shopPrice +
            ", hash='" + hash + '\'' +
            '}';
    }
}
