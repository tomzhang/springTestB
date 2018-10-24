package com.jk51.model.promotions.rule;

/**
 * Created by Administrator on 2017/8/17.
 */

/**
 * 根据活动Id查询目标商品下是否有赠品
 */
public class SelectGiftByGoodsIdParms {

    private Integer id; //活动id

    private String goodsInfo;//商品类别信息

    private Integer goodsId; //商品Id

    private String hash; //图片地址哈希值

    private Integer siteId; //门店ID

    public Integer getSiteId () {
        return siteId;
    }

    public void setSiteId (Integer siteId) {
        this.siteId = siteId;
    }

    public String getHash () {

        return hash;
    }

    public void setHash (String hash) {
        this.hash = hash;
    }

    public Integer getGoodsId () {

        return goodsId;
    }

    public void setGoodsId (Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getId () {

        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getGoodsInfo () {
        return goodsInfo;
    }

    public void setGoodsInfo (String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }
}
