package com.jk51.model.concession.result;

/**
 * Created by ztq on 2018/1/6
 * Description:
 */
public class GoodsData {

    private Integer goodsId;

    private Integer num;

    /**
     * 商店价格，单位为分
     */
    private Integer shopPrice;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
    }

    @Override
    public String toString() {
        return "GoodsData{" +
            "goodsId=" + goodsId +
            ", num=" + num +
            ", shopPrice=" + shopPrice +
            '}';
    }
}
