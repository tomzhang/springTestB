package com.jk51.modules.wechat.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.*;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.persistence.mapper.UserSpecificDiscountMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/5/8.
 */
@Service
public class UserSpecificDiscountService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserSpecificDiscountMapper userSpecificDiscountMapper;
    @Autowired
    private CouponProcessUtils couponProcessUtils;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;

    //获取优惠券
    public List<CouponActivity> getCouponBySiteId(Integer siteId, Integer memberId) throws Exception {
        List<CouponActivity> couponActivityList = userSpecificDiscountMapper.getCouponActivityBySiteId(siteId);
        if (CollectionUtils.isEmpty(couponActivityList))
            return null;
        //获取处理后的CouponActivity集合对象
        List<CouponActivity> couponActivityResult = getCouponActivityResult(siteId, memberId, couponActivityList);
        return couponActivityResult;
    }

    //获取处理后的CouponActivity集合对象
    private List<CouponActivity> getCouponActivityResult(Integer siteId, Integer memberId, List<CouponActivity> couponActivityList) throws Exception {

        //用户专属优惠券集合
        List<CouponActivity> userSpeciCouponList = new ArrayList<>();
        //通用优惠券集合
        List<CouponActivity> userCommonCouponList = new ArrayList<>();
        for (CouponActivity ca : couponActivityList) {
            //1.根据 activeId 和 siteId 获取 CouponRuleActivity 对象 集合
            List<CouponRuleActivity> couponRuleActivityList = userSpecificDiscountMapper.getCouponRuleActivityBySiteIdAndActiveId(siteId, ca.getId());
            if (CollectionUtils.isEmpty(couponRuleActivityList))
                return null;
            Set<Integer> ruleIdList = getRuleIdList(couponRuleActivityList);
            //2.根据siteId 和CouponRuleActivity 对象中的 ruleId 获取 CouponRule 对象集合  并对 CouponRule对象中的时间规则进行过期时间的过滤
            //根据 siteId 和ruleIds 获取CouponRule集合对象 过滤条件 要是，针对商品的、可发放状态的、库存大于0或不限量的
            List<CouponRule> couponRuleList = userSpecificDiscountMapper.getCouponRuleBySiteIdAndRuleIds(siteId, ruleIdList);
            //2.1处理优惠券规则对象中的时间规则，判断其是否过期；/*并过滤会员已经领取过的优惠券*/
            List<CouponRule> couponRuleResult = getCouponRuleList(couponRuleList, siteId, memberId, ca.getId());
            if (CollectionUtils.isEmpty(couponRuleResult))
                continue;

            couponRuleResult.forEach(couponRule -> {
                //优惠券规则  有效时间处理 eg:14天  或者  8小时
                couponRule.setEffectiveTime(couponDetailService.getEffectiveTimeForGoodsDetail(couponRule.getTimeRule(),
                    couponRule.getCreateTime()));
                //优惠券规则  有效时间类型处理  1: 普通时间即倒计时时间  2: 显示会员券时间
                couponRule.setEffectiveTimeType(couponDetailService.getEffictiveTimeType(couponRule.getTimeRule()));
                couponRule.setCouponView(parsingCouponRuleService.accountCoupon(couponRule.getAimAt(),
                    couponRule.getCouponType(), couponRule.getOrderRule(), couponRule.getGoodsRule()));
                /*//优惠券 开始时间 和 结束时间处理
                couponOfStartTimeAndEndTimeHandle(couponRule);*/
            });
            //3.对CouponActivity 对象中的 sendRules 进行解析 解析成CouponActivityRulesForJson对象后 过滤掉sendNumTag 等于 2 或 4
            String sendRules = ca.getSendRules();
            if (StringUtils.isNotBlank(sendRules)){
                CouponActivityRulesForJson car = JacksonUtils.json2pojo(sendRules, CouponActivityRulesForJson.class);
                Integer sendNumTag = car.getSendNumTag();
                if (null == sendNumTag || sendNumTag == 2 || sendNumTag == 4){//2 ：选择多张优惠券，随机发放其中一张;4 ：大转盘
//                    ca.setCouponRules(null);
                    continue;//
                }else {
                    ca.setCouponRules(couponRuleResult);
                }
            }
            //4.对专属会员优惠券 和 通用优惠券进行处理 即解析signMermbers字段后，对type进行判断，进行是否是专属会员进行处理
            String signMermbers = ca.getSignMermbers();
            if (StringUtils.isNotBlank(signMermbers) && (!signMermbers.equals("null")) && (!signMermbers.equals(""))){
                SignMembers signMembers = JSON.parseObject(signMermbers, SignMembers.class);
                //会员标签类型  0全部会员  1指定标签组会员 2 指定会员 3 指定标签会员
                Integer type = signMembers.getType();
                String promotionMembers = signMembers.getPromotion_members();
                //存放 会员id 或 标签组  或标签
                List<String> memberIdOrLabelList = null;
                if (StringUtils.isNotBlank(promotionMembers)){
                    memberIdOrLabelList = Arrays.asList(promotionMembers.split(","));
                }
                if (0 == type){//0全部会员
                    userCommonCouponList.add(ca);
                }else if (1 == type){//1指定标签组会员 {"type":1,"promotion_members":"1386"} 1386为该标签组
                    if (CollectionUtils.isNotEmpty(memberIdOrLabelList)){
                        //根据 site_id 和指定标签组 获取MemberLabel会员标签对象集合
                        List<MemberLabel> memberLabelList = userSpecificDiscountMapper.getMemberLabelBySiteIdAndLabelGroup(siteId, memberIdOrLabelList).stream()
                            .filter(ml -> StringUtils.isNotBlank(ml.getScene())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(memberLabelList)){
                            for (MemberLabel ml : memberLabelList) {
                                String scene = ml.getScene();
                                if (StringUtils.isNotBlank(scene)){
                                    Map<String, Object> memberIdsMap = couponProcessUtils.String2Map(scene);
                                    //{"userIds":"15234420,15234436"}
                                    String memberIds = (String)memberIdsMap.get("userIds");
                                    if (StringUtils.isNotBlank(memberIds) && memberIds.equals("null")){
                                        List<String> memIdList = Arrays.asList(memberIds.split(","));
                                        if (CollectionUtils.isNotEmpty(memIdList)){
                                            for (String mId : memIdList) {
                                                if (memberId == Integer.parseInt(mId)){
                                                    userSpeciCouponList.add(ca);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else if (2 == type){//2 指定会员{"type":2,"promotion_members":"656784"}
                    if (CollectionUtils.isNotEmpty(memberIdOrLabelList)){
                        for (String memberid : memberIdOrLabelList) {
                            if (memberId == Integer.parseInt(memberid))
                                userSpeciCouponList.add(ca);
                        }
                    }
                }else if (3 == type){//3 指定标签会员{"type":3,"promotion_members":"标签库"}
                    if (CollectionUtils.isNotEmpty(memberIdOrLabelList)){
                        //根据site_id 和指定标签 获取指定标签
                        List<String> memberIdList = userSpecificDiscountMapper.getMemberLabelBySiteIdAndLabels(siteId, memberIdOrLabelList);
                        if (CollectionUtils.isNotEmpty(memberIdList)){
                            for (String mId: memberIdList) {
                                if (memberId == Integer.parseInt(mId))
                                    userSpeciCouponList.add(ca);
                            }
                        }
                    }
                }
            }else {
                userCommonCouponList.add(ca);
            }
        }
        userSpeciCouponList.addAll(userCommonCouponList);
        return userSpeciCouponList;
    }
    //优惠券 开始时间 和 结束时间处理
    private void couponOfStartTimeAndEndTimeHandle(CouponRule couponRule) {
        String timeRuleStr = couponRule.getTimeRule();
        if (StringUtils.isNotBlank(timeRuleStr)){
            TimeRule timeRuleObj = JSON.parseObject(timeRuleStr, TimeRule.class);
            if (timeRuleObj.getValidity_type() == 1){
                String startTime = timeRuleObj.getStartTime();
                String endTime = timeRuleObj.getEndTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (StringUtils.isNotBlank(startTime)){
                    try {
                        Date date = null;
                        date = sdf.parse(startTime);
                        String format = sdf2.format(date);
                        Timestamp timestamp = Timestamp.valueOf(format);
                        couponRule.setStartTime(timestamp);
                    } catch (Exception e) {
                        logger.info("顾客专属优惠券时间转换异常！");
                        e.printStackTrace();
                    }
                }
                if (StringUtils.isNotBlank(endTime)){
                    try {
                        Timestamp timestamp = Timestamp.valueOf(endTime + " 23:59:59");
                        couponRule.setEndTime(timestamp);
                    } catch (Exception e) {
                        logger.info("顾客专属优惠券时间转换异常！");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //处理优惠券规则对象中的时间规则，判断其是否过期；并过滤会员已经领取过的优惠券
    // （若b_coupon_detail表中有其存储的记录，则表明该会员已领取过该优惠券）
    private List<CouponRule> getCouponRuleList(List<CouponRule> couponRuleList,Integer siteId, Integer memberId, Integer activeId) {
        if (CollectionUtils.isEmpty(couponRuleList))
            return null;
        List<CouponRule> couponRules = couponRuleList.stream().filter(couponRule -> {
            String timeRule = couponRule.getTimeRule();
            if (StringUtils.isBlank(timeRule))
                return false;
            return couponRuleService.judgeLimitTime(timeRule);
        })/*.filter(couponRule -> {
            Integer ruleId = couponRule.getRuleId();
            if (null != ruleId && null != activeId){
                List<CouponDetail> couponDetailList = userSpecificDiscountMapper.getCouponDetailBySiteIdAndMemberIdAndActiveIdAndRuleId(siteId, memberId, activeId, ruleId);
                if (CollectionUtils.isNotEmpty(couponDetailList))
                    return false;
            }else
                return false;
            return true;
        })*/.collect(Collectors.toList());

        return couponRules;
    }

    //获取优惠券规则的ids
    private Set<Integer> getRuleIdList(List<CouponRuleActivity> couponRuleActivityList){
        Set<Integer> ruleIdList = new HashSet<>();
        for (CouponRuleActivity cra : couponRuleActivityList) {
            Integer ruleId = cra.getRuleId();
            if (null != ruleId)
                ruleIdList.add(ruleId);
        }
        return  ruleIdList;
    }
    //获取表b_coupon_activity的优惠券活动id
    private Set<Integer>  getActiveIdList(List<CouponActivity> couponActivityList) {
        Set<Integer> activeIdList = new HashSet<>();
        for (CouponActivity ca : couponActivityList) {
            Integer activityId = ca.getId();
            if (null != activityId)
                activeIdList.add(activityId);
        }
        return activeIdList;
    }

}
