package com.jk51.model.promotions.sequence;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by javen73 on 2018/5/8.
 */
public class SequenceParam {
    private Integer siteId;//站点id
    private Integer memberId; //会员id
    private Integer promotionType; //活动类型
    private Integer page; //页码
    private Integer pageSize; //页面大小
    private Integer generalSize; //普通（除拼团和限价）的活动显示数量


    public Integer getGeneralSize() {
        return generalSize;
    }

    public void setGeneralSize(Integer generalSize) {
        this.generalSize = generalSize;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public SequenceParam() {
    }

    public SequenceParam(Integer siteId, Integer memberId, Integer page, Integer pageSize) {
        this.siteId = checkNotNull(siteId);
        this.memberId = checkNotNull(memberId);
        this.page = page;
        this.pageSize = pageSize;
    }
    public SequenceParam(Integer siteId, Integer memberId, Integer page, Integer pageSize,Integer promotionType) {
        this.siteId = checkNotNull(siteId);
//        this.memberId = checkNotNull(memberId);
        this.memberId = memberId;
        this.page = page;
        this.pageSize = pageSize;
        this.promotionType = checkNotNull(promotionType);
    }
}
