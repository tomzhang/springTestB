package com.jk51.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by admin on 2017/2/24.
 */
public class Comments {
    private int siteId;
    private int commentId; //评价id
    private List<String> commentIds; //评价ids
    private int commentRank; //评分级别 1-5分
    private String commentContent; //评价内容
    private String buyerNick; //买家昵称
    private String tradesId; //交易ID由：seller_id+年(取后两位)月日+每天的交易流水号 组合成唯一的交易ID(seller_id:商家唯一id,id为交易流水号)
    private int goodsId; //商品ID
    private String drugName; //商品标题
    private int tradesRank; //订单评价等级
    private int isShow; //是否显示（0,不显示 1 显示）
    private Timestamp createTime; //评价创建时间
    private Timestamp updateTime; //评价修改时间
    private String goodsCode;//商品编码
    private String goodsTitle;//商品标题
    Goods goods;

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }



    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public List<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(List<String> commentIds) {
        this.commentIds = commentIds;
    }

    public int getCommentRank() {
        return commentRank;
    }

    public void setCommentRank(int commentRank) {
        this.commentRank = commentRank;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public int getTradesRank() {
        return tradesRank;
    }

    public void setTradesRank(int tradesRank) {
        this.tradesRank = tradesRank;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getGoodsTitle() {return goodsTitle;}

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }
}
