package com.jk51.model.coupon;

import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.Member;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 优惠券活动的实体类，带有参加活动的数据统计 对应表名 b_coupon_activity
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司 <br/>
 * 作者: linwang                         <br/>
 * 创建日期: 2017-03-06                   <br/>
 * 修改记录:
 */
public class CouponActivity {

    public static final String SEND_DIVISION = "&&";

    private Integer siteId;
    private Integer id;
    private String title;
    private String content;
    private String image;
    private Integer sendObj;

    /**
     * 类{@link SignMembers}
     */
    private String signMermbers;//指定会员标签信息
    private Integer sendType;
    private Integer sendConditionType; // 发放条件类型 1满元 2满件 3满元并满件 4不满元也不满件
    private String sendCondition;//多少件或多少元 &&non&&商品ID集合 或者 多少件或多少元&&商品ID集合
    private Integer sendWay;
    private String sendWayValue; //门店或店员ID&&门店或店员ID
    /**
     * 当 sendWay == 2 sendLimit表示每个会员最多可领多少次，0代表无限
     */
    private Integer sendLimit;
    /**
     * 旧规则状态: 0发布中 1待发布(未到达发布时间) 2已过期(该活动的优惠券已经全部过期) 3已停止(该活动的优惠券已经发完)
     * 新规则状态:
     * 0发布中(已开始)
     * 1定时发布(未到达发布时间，定时任务使用)
     * 2过期结束(根据活动定义的时间结束)
     * 3已发完结束(该活动下的所有优惠券已经发完)
     * 4手动结束(手动停止发布优惠券活动)
     * 10待发布(活动可编辑，草稿状态)
     * 11发布中(未开始)
     */
    private Integer status;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<String> labelList;

    public List<String> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<String> labelList) {
        this.labelList = labelList;
    }

    /**
     * 对应实体类{@link CouponActivityRulesForJson}
     */
    private String sendRules;

    /**
     * {@link CouponActivityTimeRuleForJson}
     */
    private String timeRule;
    private Integer createdTotal;//生成优惠券总数
    private Integer usedTotal;//使用优惠券总数

    private Integer sendStatus;//活动发放状态
    private Integer sendConditionValue; //多少件或多少元
    private String sendConditionProducts;//1.all 2.100001:100002 3.non&&100001:100002

    private List<CouponRule> couponRules;
    private List<Map<String, Object>> map;

    private int sendNum;
    private String url;
    private YbMerchant ybMerchant;

    private List<MemberLabel> memberLabels;

    private List<Member> memberList;

    private int isReissUre;//是否可以补发实际操作要结合活动状态

    public void valid() {
        Optional.ofNullable(sendObj).orElseThrow(() -> new RuntimeException("sendObj为空"));
        Optional.ofNullable(sendWay).orElseThrow(() -> new RuntimeException("sendWay为空"));
        Optional.ofNullable(sendRules).orElseThrow(() -> new RuntimeException("sendRules为空"));

        if (sendType != null) {
            switch (sendType) {
                case -1:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 6:
                    break;
                case 3:
                    Optional.ofNullable(sendConditionType).orElseThrow(() -> new RuntimeException("sendConditionType为空"));
                    Optional.ofNullable(sendCondition).orElseThrow(() -> new RuntimeException("sendCondition为空"));
                    Optional.ofNullable(startTime).orElseThrow(() -> new RuntimeException("startTime为空"));
                    Optional.ofNullable(endTime).orElseThrow(() -> new RuntimeException("endTime为空"));
                    break;
                default:
                    Optional.ofNullable(sendConditionType).orElseThrow(() -> new RuntimeException("sendConditionType为空"));
                    break;
            }
        }

        switch (sendWay) {
            case 2:
                Optional.ofNullable(sendLimit).orElseThrow(() -> new RuntimeException("sendLimit为空"));
                break;
            case 4:
                Optional.ofNullable(sendWayValue).orElseThrow(() -> new RuntimeException("sendWayValue为空"));
                break;
            case 5:
                Optional.ofNullable(sendWayValue).orElseThrow(() -> new RuntimeException("sendWayValue为空"));
                break;
        }
    }



    public YbMerchant getYbMerchant() {
        return ybMerchant;
    }

    public void setYbMerchant(YbMerchant ybMerchant) {
        this.ybMerchant = ybMerchant;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public static String getSendDivision() {
        return SEND_DIVISION;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSendObj() {
        return sendObj;
    }

    public void setSendObj(Integer sendObj) {
        this.sendObj = sendObj;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getSendConditionType() {
        return sendConditionType;
    }

    public void setSendConditionType(Integer sendConditionType) {
        this.sendConditionType = sendConditionType;
    }

    public String getSendCondition() {
        return sendCondition;
    }

    public void setSendCondition(String sendCondition) {
        if (sendCondition == null) {
        } else if (sendCondition.length() > 0) {
            String[] sendConditionParams = sendCondition.split(SEND_DIVISION);
            if (sendConditionParams[0].indexOf(",") == -1)
                this.setSendConditionValue(Integer.parseInt(sendConditionParams[0]));
            if (sendConditionParams.length > 2)
                this.setSendConditionProducts(sendConditionParams[1] + SEND_DIVISION + sendConditionParams[2]);
            else
                this.setSendConditionProducts(sendConditionParams[1]);
        }
        this.sendCondition = sendCondition;
    }

    public Integer getSendWay() {
        return sendWay;
    }

    public void setSendWay(Integer sendWay) {
        this.sendWay = sendWay;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
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

    public Integer getSendConditionValue() {
        return sendConditionValue;
    }

    public void setSendConditionValue(Integer sendConditionValue) {
        this.sendConditionValue = sendConditionValue;
    }

    public String getSendConditionProducts() {
        return sendConditionProducts;
    }

    public void setSendConditionProducts(String sendConditionProducts) {
        this.sendConditionProducts = sendConditionProducts;
    }

    public String getSendWayValue() {
        return sendWayValue;
    }

    public void setSendWayValue(String sendWayValue) {
        this.sendWayValue = sendWayValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSendRules() {
        return sendRules;
    }

    public void setSendRules(String sendRules) {
        this.sendRules = sendRules;
    }

    public List<CouponRule> getCouponRules() {
        return couponRules;
    }

    public void setCouponRules(List<CouponRule> couponRules) {
        this.couponRules = couponRules;
    }

    public List<Map<String, Object>> getMap() {
        return map;
    }

    public void setMap(List<Map<String, Object>> map) {
        this.map = map;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public Integer getCreatedTotal() {
        return createdTotal;
    }

    public void setCreatedTotal(Integer createdTotal) {
        this.createdTotal = createdTotal;
    }

    public Integer getUsedTotal() {
        return usedTotal;
    }

    public void setUsedTotal(Integer usedTotal) {
        this.usedTotal = usedTotal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(Integer sendLimit) {
        this.sendLimit = sendLimit;
    }

    public String getSignMermbers() {
        return signMermbers;
    }

    public void setSignMermbers(String signMermbers) {
        this.signMermbers = signMermbers;
    }

    public List<MemberLabel> getMemberLabels() {
        return memberLabels;
    }

    public void setMemberLabels(List<MemberLabel> memberLabels) {
        this.memberLabels = memberLabels;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public int getIsReissUre() {
        return isReissUre;
    }

    public void setIsReissUre(int isReissUre) {
        this.isReissUre = isReissUre;
    }

    public String getTimeRule() {
        return timeRule;
    }

    public void setTimeRule(String timeRule) {
        this.timeRule = timeRule;
    }

    @Override
    public String toString() {
        return "CouponActivity{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", sendObj=" + sendObj +
                ", signMermbers='" + signMermbers + '\'' +
                ", sendType=" + sendType +
                ", sendConditionType=" + sendConditionType +
                ", sendCondition='" + sendCondition + '\'' +
                ", sendWay=" + sendWay +
                ", sendWayValue='" + sendWayValue + '\'' +
                ", sendLimit=" + sendLimit +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", sendRules='" + sendRules + '\'' +
                ", createdTotal=" + createdTotal +
                ", usedTotal=" + usedTotal +
                ", sendStatus=" + sendStatus +
                ", isReissUre=" + isReissUre +
                ", sendConditionValue=" + sendConditionValue +
                ", sendConditionProducts='" + sendConditionProducts + '\'' +
                ", couponRules=" + couponRules +
                ", map=" + map +
                ", sendNum=" + sendNum +
                ", url='" + url + '\'' +
                ", ybMerchant=" + ybMerchant +
                ", memberLabels=" + memberLabels +
                '}';
    }
}
