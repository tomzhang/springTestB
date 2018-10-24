package com.jk51.model.promotions.rule;

/**
 * {@link com.jk51.model.promotions.PromotionsRule}包邮活动的字段promotionRule对应的实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/10                                <br/>
 * 修改记录:                                         <br/>
 */
public class FreePostageRule {


    /**
     * 0全部商品参加 1指定商品参加 2指定商品不参加
     */
    private Integer goodsIdsType;

    /**
     * 商品id，用逗号分隔，all表示全部
     */
    private String goodsIds;

    /**
     * 包邮需满足的金额
     */
    private Integer meetMoney;

    /**
     * 免除的邮费上限
     */
    private Integer reducePostageLimit;

    /**
     * 1 表示 areaIds代表包邮地区Id
     * 2 表示 areaIds代表不包邮地区Id
     */
    private Integer areaIdsType;

    /**
     * 地区id集合，用逗号分隔，all表示全部
     */
    private String areaIds;

    public Integer getGoodsIdsType() {
        return goodsIdsType;
    }

    public void setGoodsIdsType(Integer goodsIdsType) {
        this.goodsIdsType = goodsIdsType;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getMeetMoney() {
        return meetMoney;
    }

    public void setMeetMoney(Integer meetMoney) {
        this.meetMoney = meetMoney;
    }

    public Integer getReducePostageLimit() {
        return reducePostageLimit;
    }

    public void setReducePostageLimit(Integer reducePostageLimit) {
        this.reducePostageLimit = reducePostageLimit;
    }

    public Integer getAreaIdsType() {
        return areaIdsType;
    }

    public void setAreaIdsType(Integer areaIdsType) {
        this.areaIdsType = areaIdsType;
    }

    public String getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(String areaIds) {
        this.areaIds = areaIds;
    }

    @Override
    public String toString() {
        return "FreePostageRule{" +
            "goodsIdsType=" + goodsIdsType +
            ", goodsIds='" + goodsIds + '\'' +
            ", meetMoney=" + meetMoney +
            ", reducePostageLimit=" + reducePostageLimit +
            ", areaIdsType=" + areaIdsType +
            ", areaIds='" + areaIds + '\'' +
            '}';
    }
}
