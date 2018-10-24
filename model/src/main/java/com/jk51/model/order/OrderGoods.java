package com.jk51.model.order;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单分单时，根据商品ID来计算运费
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
public class OrderGoods {

    private int goodsId;//商品ID

    private int goodsNum;//购买的商品数量

    private int goodsPrice;

    private String drugName;

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(int goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    @Override
    public String toString() {
        return "OrderGoods{" +
                "goodsId=" + goodsId +
                ", goodsNum=" + goodsNum +
                '}';
    }
}
