package com.jk51.modules.coupon.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.CouponRuleActivity;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.modules.coupon.mapper.CouponRuleActivityMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.merchant.service.UserStoresDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.coupon.service.
 * author   :zw
 * date     :2017/5/14
 * Update   :
 */
@Service
public class CouponActivityProcessService {

    @Autowired
    private CouponRuleService couponRuleService;

    @Autowired
    private CouponRuleMapper couponRuleMapper;

    @Autowired
    private CouponRuleActivityMapper activityMapper;
    @Autowired
    private UserStoresDistanceService userStoresDistanceService;

    public List<CouponDetail> getCoupon(List<CouponRule> rules, Integer userId, String source, String manageId) {
        // Optional.ofNullable(userId).orElseThrow(() -> new RuntimeException("用户为空"));
        // Optional.ofNullable(rules).orElseThrow(() -> new RuntimeException("优惠券规则为空"));

        return rules.stream()
                .map(couponRule -> {
                    return findDistanceResult(couponRule, userId,
                        CouponDetail.build(couponRule, userId, source, manageId,
                            couponRuleService.getCouponDownDetailNum(couponRule.getRuleId(), couponRule.getSiteId())));
                })

                .collect(Collectors.toList());
    }

    public CouponDetail getCoupon(CouponRule rules, Integer userId, String source, String manageId) {
        return findDistanceResult(rules, userId,
            CouponDetail.build(rules, userId, source, manageId,
                couponRuleService.getCouponDownDetailNum(rules.getRuleId(), rules.getSiteId()))
        );
    }

    public List<CouponDetail> getCoupon(List<CouponRule> rules, Integer userId, String source,
                                        String manageId,String tradeId) {

        Optional.ofNullable(userId).orElseThrow(() -> new RuntimeException("用户为空"));

        Optional.ofNullable(rules).orElseThrow(() -> new RuntimeException("优惠券规则为空"));

        List<CouponDetail> details = new ArrayList<>();

        rules.forEach(couponRule -> {
            details.add(
                findDistanceResult(couponRule,userId,
                    CouponDetail.build(couponRule, userId, source, manageId
                    ,  couponRuleService.getCouponDownDetailNum(couponRule.getRuleId(),couponRule.getSiteId()),tradeId)));
        });

        return details;
    }

    public List<CouponDetail> getCoupon(List<CouponRule> rules, String source, Integer userId, String orderId) {

        List<CouponDetail> details = new ArrayList<>();

        rules.forEach(couponRule -> {
            //findDistanceResult 距离优惠券
            details.add(findDistanceResult(couponRule,userId,
                CouponDetail.build(couponRule, source, userId, couponRuleService.getCouponDownDetailNum(
                    couponRule.getRuleId(),couponRule.getSiteId()), orderId)));
        });

        return details;
    }
    public void updateCouponCommon(Integer siteId, Integer ruleId,
                                   Integer actId, Integer sendNum, Integer useNum, Integer receiveNum) {
        CouponRuleActivity couponRuleActivity = new CouponRuleActivity();
        couponRuleActivity.setSiteId(siteId);
        couponRuleActivity.setRuleId(ruleId);
        couponRuleActivity.setActiveId(actId);
        couponRuleActivity.setSendNum(sendNum);
        couponRuleActivity.setUseNum(useNum);
        couponRuleActivity.setReceiveNum(receiveNum);
        activityMapper.updateCouponCommon(couponRuleActivity);
    }

    /**
     * 距离优惠券，最终优惠结果计算
     * @param couponRuleById
     * @param userId
     * @param build
     * @return
     */
    public CouponDetail findDistanceResult(CouponRule couponRuleById,Integer userId,CouponDetail build){
//        CouponRule couponRuleById = couponRuleMapper.findCouponRuleById(couponRule.getRuleId(), couponRule.getSiteId());
        if(couponRuleById!=null&&couponRuleById.getCouponType()==100||couponRuleById.getCouponType()==200){
            try {
                GoodsRule goodsRule = JacksonUtils.json2pojo(couponRuleById.getGoodsRule(), GoodsRule.class);
                if(goodsRule!=null&&goodsRule.getRule_type()==6){
                    List<Map> ruleList = (List<Map>) goodsRule.getRule();
                    Map<String,Object> params =new HashMap<String,Object>();
                    params.put("siteId",couponRuleById.getSiteId());
                    params.put("memberId",userId);
                    //接口，用户距离
                    Integer distance = userStoresDistanceService.getDistanceByMember(params);
                    //没有查询到距离 返回最小优惠的优惠券
/*                    if(distance==-1){return calculateCouponByMinDistanceRule(couponRuleById,ruleList,build);}*/
                    //正常优惠券
                    calculateCouponByDistanceRule(couponRuleById, userId, ruleList,build,distance);
                }
            } catch (Exception e) {
                e.printStackTrace();
                build=null;
            }
        }
        return build;
    }



    private void calculateCouponByDistanceRule(CouponRule couponRuleById,Integer userId,List<Map> ruleList,CouponDetail build,Integer distance) throws Exception {
        switch (couponRuleById.getCouponType()){
            case 100: {
                 // 查询 如果用户距离超出最大距离范围,照最大优惠
                 if(isOutOfDistanceRule(ruleList,distance,couponRuleById.getCouponType(),build)){return ;};
                    for(Map temp:ruleList){
                        Integer distance_meter = (Integer) temp.get("distance_meter");
                        if (distance <= distance_meter) {
                            Integer reduce_price = (Integer) temp.get("reduce_price");
                            build.setDistanceReduce(reduce_price);
                            return ;
                        }
                    }
                    break;
             }
            case 200: {
                // 查询 如果用户距离超出最大距离范围,照最大优惠
                if(isOutOfDistanceRule(ruleList,distance,couponRuleById.getCouponType(),build)){return ;};
                for(Map temp:ruleList){
                    Integer distance_meter = (Integer) temp.get("distance_meter");
                    if (distance <= distance_meter) {
                        Integer reduce_price = (Integer) temp.get("discount");
                        build.setDistanceDiscount(reduce_price);
                        return ;
                    }
                }
                break;
            }
        }
    }


    /**
     * 判断用户距离是否超出规则最大距离
     * @param ruleList
     * @param userDistance
     * @return
     */
    public boolean isOutOfDistanceRule(List<Map> ruleList,Integer userDistance,Integer couponType,CouponDetail build){
        if(ruleList==null||userDistance==null){
            return false;
        }
        Map lastMap = ruleList.get(ruleList.size() - 1);
        Integer maxDistance= lastMap.get("distance_meter")==null?0:Integer.parseInt(lastMap.get("distance_meter").toString());
        if(userDistance>maxDistance){
            switch (couponType){
                case 100:{
                    Integer distance_reduce = lastMap.get("reduce_price")==null?0:Integer.parseInt(lastMap.get("reduce_price").toString());
                    build.setDistanceReduce(distance_reduce);
                    break;
                }
                case 200:{
                    Integer distance_discount = lastMap.get("discount")==null?100:Integer.parseInt(lastMap.get("discount").toString());
                    build.setDistanceDiscount(distance_discount);
                    break;
                }
            }
        }
        return userDistance>maxDistance;
    }


}
