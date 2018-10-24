package com.jk51.modules.appInterface.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-22
 * 修改记录:
 */
public class GoodsInfo {

    private String com_name;
    private int shop_price;
    private int num;
    private int goods_id;

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getCom_name() {
        return com_name;
    }

    public void setCom_name(String com_name) {
        this.com_name = com_name;
    }

    public int getShop_price() {
        return shop_price;
    }

    public void setShop_price(int shop_price) {
        this.shop_price = shop_price;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
