package com.jk51.modules.persistence.mapper;

import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.CouponRuleActivity;
import com.jk51.model.merchant.MemberLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/8.
 */
@Mapper
public interface UserSpecificDiscountMapper {
    /*优惠券*/
    List<CouponActivity> getCouponActivityBySiteId(@Param(value = "siteId") Integer siteId);
    //1.根据 activeId 和 siteId 获取 CouponRuleActivity 对象 集合
    List<CouponRuleActivity> getCouponRuleActivityBySiteIdAndActiveId(@Param(value = "siteId") Integer siteId, @Param(value = "activeId") Integer activeId);
    //根据 siteId 和ruleIds 获取CouponRule集合对象 并过滤
    List<CouponRule> getCouponRuleBySiteIdAndRuleIds(@Param(value = "siteId") Integer siteId, @Param(value = "ruleIdList") Set<Integer> ruleIdList);
    //根据 site_id 和指定标签组 获取MemberLabel会员标签对象集合
    List<MemberLabel> getMemberLabelBySiteIdAndLabelGroup(@Param(value = "siteId") Integer siteId, @Param(value = "memberIdOrLabelGroupList") List<String> memberIdOrLabelGroupList);
    //根据site_id 和指定标签 获取指定标签
    List<String> getMemberLabelBySiteIdAndLabels(@Param(value = "siteId") Integer siteId, @Param(value = "memberIdOrLabelList") List<String> memberIdOrLabelList);
    List<CouponDetail> getCouponDetailBySiteIdAndMemberIdAndActiveIdAndRuleId(@Param(value = "siteId") Integer siteId, @Param(value = "memberId") Integer memberId, @Param(value = "activeId") Integer activeId, @Param(value = "ruleId") Integer ruleId);

}
