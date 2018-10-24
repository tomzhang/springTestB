package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:51后台用户
 * 作者: dumingliang
 * 创建日期: 2017-03-03
 * 修改记录:
 */

public class WxCart {
    private Integer goodsId;   //商品id
    private Integer quantity;  //手机号码
    private Integer status;    //状态 1有效 0无效
    private Integer siteId;    //商家ID
    private Integer userId;    //用户ID
    private Timestamp createDate;   //创建时间
    private Timestamp updateDate;   //更新时间
    public WxCart() {
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public Timestamp getCreateDate() {

        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Integer getUserId() {

        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStatus() {

        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getQuantity() {

        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getGoodsId() {

        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
}
