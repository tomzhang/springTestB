package com.jk51.model.promotions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jk51.commons.json.LocalDateTimeDeserializerForLongFormatter;
import com.jk51.commons.json.LocalDateTimeSerializerForLongFormatter;
import com.jk51.model.merchant.MemberLabel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动发放的实体类，是活动的入口
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
public class PromotionsActivity {
    private Integer id;
    private Integer siteId;

    /**
     * 发放标题
     */
    private String title;

    /**
     * 规则id {@link PromotionsRule#id}
     */
    private Integer promotionsId;

    /**
     * 活动开始时间
     */
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime endTime;

    /**
     * 活动海报
     */
    private String templatePic;

    /**
     * 活动描叙
     */
    private String intro;

    /**
     * 弹窗图片
     */
    private String posterPic;

    /**
     * 默认值为10 状态：
     * 0发布中(已开始)
     * 2过期结束(根据活动定义的时间结束)
     * 3已发完结束(该活动下的所有优惠券已经发完)
     * 4手动结束(手动停止发布优惠券活动)
     * 10待发布(活动可编辑，草稿状态)
     * 11发布中(未开始)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime createTime;

    /**
     * 该活动是否是独立活动
     * 独立活动：参加了该活动就不能参加其他活动
     * 1代表是，2代表不是，null则根据活动类型判断
     * null:
     */
    private Integer isIndependent;

    /**
     * 参加了该活动的情况下是否可以使用优惠券
     * 1代表可以，2代表不可以，null则根据活动类型判断
     * null:
     */
    private Integer canUseCoupon;

    /**
     * 是否展示在APP下单完成后的广告展示中（活动的商品）
     * 1，展示，0，不展示
     */
    private Integer showAd;

    /**
     * 修改时间
     */
    @JsonSerialize(using = LocalDateTimeSerializerForLongFormatter.class)
    @JsonDeserialize(using = LocalDateTimeDeserializerForLongFormatter.class)
    private LocalDateTime updateTime;

    /**
     * 是否显示在活动中心 对应实体类{@link com.jk51.model.promotions.activity.ShowRule}
     */
    private String showRule;

    /**
     * 针对对象，json串 {@link com.jk51.model.coupon.requestParams.SignMembers}
     */
    private String useObject;

    private String memberPhones;//接收多个会员手机号

    private List<MemberLabel> memberLabels;

    private PromotionsRule promotionsRule;

    private String active_link;

    private List<String> labelList;

    public List<String> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<String> labelList) {
        this.labelList = labelList;
    }

    public String getActive_link() {
        return active_link;
    }

    public void setActive_link(String active_link) {
        this.active_link = active_link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPromotionsId() {
        return promotionsId;
    }

    public void setPromotionsId(Integer promotionsId) {
        this.promotionsId = promotionsId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getTemplatePic() {
        return templatePic;
    }

    public void setTemplatePic(String templatePic) {
        this.templatePic = templatePic;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPosterPic() {
        return posterPic;
    }

    public void setPosterPic(String posterPic) {
        this.posterPic = posterPic;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getShowRule() {
        return showRule;
    }

    public void setShowRule(String showRule) {
        this.showRule = showRule;
    }

    public String getUseObject() {
        return useObject;
    }

    public void setUseObject(String useObject) {
        this.useObject = useObject;
    }

    public List<MemberLabel> getMemberLabels() {
        return memberLabels;
    }

    public void setMemberLabels(List<MemberLabel> memberLabels) {
        this.memberLabels = memberLabels;
    }

    public PromotionsRule getPromotionsRule() {
        return promotionsRule;
    }

    public void setPromotionsRule(PromotionsRule promotionsRule) {
        this.promotionsRule = promotionsRule;
    }

    public String getMemberPhones() {
        return memberPhones;
    }

    public void setMemberPhones(String memberPhones) {
        this.memberPhones = memberPhones;
    }

    public Integer getIsIndependent() {
        return isIndependent;
    }

    public void setIsIndependent(Integer isIndependent) {
        this.isIndependent = isIndependent;
    }

    public Integer getCanUseCoupon() {
        return canUseCoupon;
    }

    public void setCanUseCoupon(Integer canUseCoupon) {
        this.canUseCoupon = canUseCoupon;
    }

    public Integer getShowAd() {
        return showAd;
    }

    public void setShowAd(Integer showAd) {
        this.showAd = showAd;
    }

    @Override
    public String toString() {
        return "PromotionsActivity{" +
            "id=" + id +
            ", siteId=" + siteId +
            ", title='" + title + '\'' +
            ", promotionsId=" + promotionsId +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", templatePic='" + templatePic + '\'' +
            ", intro='" + intro + '\'' +
            ", posterPic='" + posterPic + '\'' +
            ", status=" + status +
            ", createTime=" + createTime +
            ", isIndependent=" + isIndependent +
            ", canUseCoupon=" + canUseCoupon +
            ", updateTime=" + updateTime +
            ", showRule='" + showRule + '\'' +
            ", useObject='" + useObject + '\'' +
            ", memberPhones='" + memberPhones + '\'' +
            ", memberLabels=" + memberLabels +
            ", promotionsRule=" + promotionsRule +
            ", active_link='" + active_link + '\'' +
            ", labelList=" + labelList +
            '}';
    }
}
