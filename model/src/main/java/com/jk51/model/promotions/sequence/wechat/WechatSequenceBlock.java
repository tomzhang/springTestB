package com.jk51.model.promotions.sequence.wechat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jk51.model.promotions.sequence.SequenceBlock;
import com.jk51.model.promotions.sequence.SequenceGoods;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by javen73 on 2018/5/12.
 */
public class WechatSequenceBlock extends SequenceBlock{
    //活动下的商品
    private List<WechatSequenceGoods> goods = new ArrayList<>();

    private String tag;
    private Integer promotionsActivityId;

    @JsonIgnore
    public Set<Integer> goodsIds = new HashSet<Integer>();
    public Long startTime;
    public Long endTime;

    public Integer page;
    public Integer pages;
    public Long totals;

    public Integer getPromotionsActivityId() {
        return promotionsActivityId;
    }

    public void setPromotionsActivityId(Integer promotionsActivityId) {
        this.promotionsActivityId = promotionsActivityId;
    }

    public Set<Integer> getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(Set<Integer> goodsIds) {
        this.goodsIds = goodsIds;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Long getTotals() {
        return totals;
    }

    public void setTotals(Long totals) {
        this.totals = totals;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<WechatSequenceGoods> getGoods() {
        return goods;
    }

    public void setGoods(List<WechatSequenceGoods> goods) {
        this.goods = goods;
    }
}
