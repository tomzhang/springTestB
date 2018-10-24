package com.jk51.model.concession.result;

import java.util.*;

/**
 * Created by ztq on 2018/1/6
 * Description:
 */
@SuppressWarnings("unused")
public class Result extends BaseResult {

    /**
     * 基本可以无视，该值代表该类是否为空
     */
    private boolean isEmpty = false;

    private Integer siteId;

    private Integer memberId;


    /* -- 优惠券计算结果 -- */

    /**
     * 是否使用了优惠券
     */
    private boolean useCoupon = false;

    /**
     * 优惠券优惠结果标记，1代表优惠商品金额，2表示赠送礼品, 3计算不出优惠
     */
    private Integer couponConcessionRemark = 3;

    /**
     * 使用的优惠券的id
     */
    private Integer couponDetailId;

    /**
     * 优惠券优惠总金额
     */
    private int couponDiscount = 0;


    /* -- 活动计算结果 -- */

    /**
     * 该订单计算过程中是否使用了活动
     */
    private boolean usePromotions = false;

    /**
     * 活动优惠总金额
     */
    private int promotionsDiscount = 0;

    /**
     * 生效的活动ID，用逗号分隔
     */
    private String efficientPromotionsActivityId = "";


    /* -- 商品计算结果 -- */

    /**
     * 统计单类商品的优惠信息，key为商品id，value为对应商品的优惠信息
     */
    private Map<Integer, GoodsDataForResult> goodsConcession = new HashMap<>();


    /* -- 赠品计算结果（包括优惠券赠品和活动赠品） -- */

    private List<GiftResult> giftResults = new ArrayList<>();


    /* -- 非赠品计算结果 -- */

    private List<MoneyResultForPromotions> moneyResultForPromotionsList = new ArrayList<>();


    /* -- 邮费计算结果 -- */

    /**
     * 邮费优惠
     */
    private int postageDiscount = 0;


    /* -- getter & setter -- */

    public List<MoneyResultForPromotions> getMoneyResultForPromotionsList() {
        return moneyResultForPromotionsList;
    }

    public void setMoneyResultForPromotionsList(List<MoneyResultForPromotions> moneyResultForPromotionsList) {
        this.moneyResultForPromotionsList = moneyResultForPromotionsList;
    }

    public String getEfficientPromotionsActivityId() {
        return efficientPromotionsActivityId;
    }

    public void setEfficientPromotionsActivityId(String efficientPromotionsActivityId) {
        this.efficientPromotionsActivityId = efficientPromotionsActivityId;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
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

    public int getPostageDiscount() {
        return postageDiscount;
    }

    public void setPostageDiscount(int postageDiscount) {
        this.postageDiscount = postageDiscount;
    }

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public Integer getCouponConcessionRemark() {
        return couponConcessionRemark;
    }

    public void setCouponConcessionRemark(Integer couponConcessionRemark) {
        this.couponConcessionRemark = couponConcessionRemark;
    }

    public Integer getCouponDetailId() {
        return couponDetailId;
    }

    public void setCouponDetailId(Integer couponDetailId) {
        this.couponDetailId = couponDetailId;
    }

    public int getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(int couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public boolean isUsePromotions() {
        return usePromotions;
    }

    public void setUsePromotions(boolean usePromotions) {
        this.usePromotions = usePromotions;
    }

    public int getPromotionsDiscount() {
        return promotionsDiscount;
    }

    public void setPromotionsDiscount(int promotionsDiscount) {
        this.promotionsDiscount = promotionsDiscount;
    }

    public Map<Integer, GoodsDataForResult> getGoodsConcession() {
        return goodsConcession;
    }

    public void setGoodsConcession(Map<Integer, GoodsDataForResult> goodsConcession) {
        this.goodsConcession = goodsConcession;
    }

    public List<GiftResult> getGiftResults() {
        return giftResults;
    }

    public void setGiftResults(List<GiftResult> giftResults) {
        this.giftResults = giftResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;
        Result result = (Result) o;
        return isEmpty() == result.isEmpty() &&
            isUseCoupon() == result.isUseCoupon() &&
            getCouponDiscount() == result.getCouponDiscount() &&
            isUsePromotions() == result.isUsePromotions() &&
            getPromotionsDiscount() == result.getPromotionsDiscount() &&
            getPostageDiscount() == result.getPostageDiscount() &&
            Objects.equals(getSiteId(), result.getSiteId()) &&
            Objects.equals(getMemberId(), result.getMemberId()) &&
            Objects.equals(getCouponConcessionRemark(), result.getCouponConcessionRemark()) &&
            Objects.equals(getCouponDetailId(), result.getCouponDetailId()) &&
            Objects.equals(getEfficientPromotionsActivityId(), result.getEfficientPromotionsActivityId()) &&
            Objects.equals(getGoodsConcession(), result.getGoodsConcession()) &&
            Objects.equals(getGiftResults(), result.getGiftResults());
    }

    @Override
    public int hashCode() {

        return Objects.hash(isEmpty(), getSiteId(), getMemberId(), isUseCoupon(), getCouponConcessionRemark(), getCouponDetailId(), getCouponDiscount(), isUsePromotions(), getPromotionsDiscount(), getEfficientPromotionsActivityId(), getGoodsConcession(), getGiftResults(), getPostageDiscount());
    }

    @Override
    public String toString() {
        return "Result{" +
            "isEmpty=" + isEmpty +
            ", siteId=" + siteId +
            ", memberId=" + memberId +
            ", useCoupon=" + useCoupon +
            ", couponConcessionRemark=" + couponConcessionRemark +
            ", couponDetailId=" + couponDetailId +
            ", couponDiscount=" + couponDiscount +
            ", usePromotions=" + usePromotions +
            ", promotionsDiscount=" + promotionsDiscount +
            ", efficientPromotionsActivityId='" + efficientPromotionsActivityId + '\'' +
            ", goodsConcession=" + goodsConcession +
            ", giftResults=" + giftResults +
            ", postageDiscount=" + postageDiscount +
            '}';
    }
}
