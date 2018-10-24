package com.jk51.model.coupon.tags;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by javen on 2018/3/23.
 */
public class TagsParam {
    private Integer siteId;
    private Integer memberId;
    private String goodsIds;
    private Integer tagsNum;


    public TagsParam(Integer siteId, Integer memberId, String goodsIds, Integer tagsNum) {
        this.siteId = checkNotNull(siteId);
        this.memberId = memberId;
        this.goodsIds = checkNotNull(goodsIds);
        this.tagsNum = checkNotNull(tagsNum);
    }

    public TagsParam() {
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getTagsNum() {
        return tagsNum;
    }

    public void setTagsNum(Integer tagsNum) {
        this.tagsNum = tagsNum;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }
}
