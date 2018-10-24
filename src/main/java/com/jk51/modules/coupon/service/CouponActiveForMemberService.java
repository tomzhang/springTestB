package com.jk51.modules.coupon.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.persistence.mapper.MemberLabelMapper;
import com.jk51.modules.persistence.mapper.RelationLabelMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/7/12.
 */
@Service
public class CouponActiveForMemberService {

    @Autowired
    MemberLabelMapper memberLabelMapper;


    @Autowired
    MemberMapper memberMapper;

    @Autowired
    private CouponProcessUtils couponProcessUtils;

    @Autowired
    private CouponActivityMapper couponActivityMapper;

    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;

    @Autowired
    private RelationLabelMapper relationLabelMapper;


    private static final int TAG_GROUP_MEMBER = 1;  //指定标签组会员

    private static final int MEMBER = 2;  //指定会员

    private static final int TAG_MEMBER = 3;  //指定标签会员

    private static final int ALL_MEMBER = 1;  //全部会员

    private static final int SPECIFY_MEMBER = 2;  //指定会员

    private static final int SPECIFY_TAG_MEMBER = 3;  //指定标签会员
    /**
     * 查询全部的指定会员
     *
     * @param activity
     * @return
     */
    public List<Member> queryAllMemberForCouponActive(CouponActivity activity) {
        SignMembers members= JSON.parseObject(activity.getSignMermbers(), SignMembers.class);
        Set<String> result = new HashSet<String>();
        if(members.getType()==TAG_GROUP_MEMBER) {
            String[] sign_members = members.getPromotion_members().split(",");
            List<MemberLabel> memberLabelList = memberLabelMapper.getLabelAllForCouponActive(activity.getSiteId(), sign_members).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getScene())).collect(Collectors.toList());


            Map<String, Object> stringMap = null;

            for (MemberLabel memberLabel : memberLabelList) {
                stringMap = couponProcessUtils.String2Map(memberLabel.getScene());
                if (null != stringMap.get("userIds") && StringUtil.isNotEmpty((String) stringMap.get("userIds"))) {
                    String userIds = (String) stringMap.get("userIds");
                    result.addAll(Arrays.asList(userIds.split(",")));
                }

            }
        }else if(members.getType()==MEMBER){
            result=new HashSet<String>(Arrays.asList(members.getPromotion_members().split(",")));
        }else if(members.getType()==TAG_MEMBER){
            String[] labelName = members.getPromotion_members().split(",");
            result= relationLabelMapper.getCustomMemberIdAll(activity.getSiteId(), labelName);
        }

        if (result.isEmpty()) return null;
        else return memberMapper.queryMemberListForCouponActive(activity.getSiteId(), result);

    }


    /**
     * 判断用户是否符合领券要求
     *
     * @param userId
     * @param activeId
     * @param siteId
     * @return
     */
    public boolean checkUserForCouponActive(Integer userId, Integer activeId, Integer siteId,CouponActivity activity) {

        if (null == userId || null == activeId) {
            return false;
        }
        Set<String> result = new HashSet<String>();
        if (null != activity && ALL_MEMBER == activity.getSendObj()) return true;

        if (null != activity ) {
            SignMembers members= JSON.parseObject(activity.getSignMermbers(), SignMembers.class);
            if(members.getType()==TAG_GROUP_MEMBER && SPECIFY_MEMBER == activity.getSendObj()){
                String[] sign_members = members.getPromotion_members().split(",");
                List<MemberLabel> memberLabelList = memberLabelMapper.getLabelAllForCouponActive(activity.getSiteId(), sign_members).stream()
                        .filter(d -> StringUtil.isNotEmpty(d.getScene())).collect(Collectors.toList());


                Map<String, Object> stringMap = null;

                for (MemberLabel memberLabel : memberLabelList) {
                    stringMap = couponProcessUtils.String2Map(memberLabel.getScene());
                    String userIds = (String) stringMap.get("userIds");
                    result.addAll(Arrays.asList(userIds.split(",")));
                }
            }else if(members.getType() == MEMBER && SPECIFY_TAG_MEMBER == activity.getSendObj()){
                result=new HashSet<String>(Arrays.asList(members.getPromotion_members().split(",")));
            }


            if (result.contains(userId.toString())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断活动是否符合使用要求
     */
    public boolean checkProActivity(Integer siteId,Integer proactiveId,Integer userId){
        if (null == userId || null == proactiveId) {
            return false;
        }
        Set<String> result = new HashSet<String>();
        PromotionsActivity proActivityDtoactivity = promotionsActivityMapper.getPromotionsActivityDetail(siteId,proactiveId);
        if(null==proActivityDtoactivity)
            return false;
        String user_Obj=proActivityDtoactivity.getUseObject();
        if (judgeMemberContains(siteId, userId, result, proActivityDtoactivity, user_Obj)) return true;

        return false;


    }

    public boolean judgeMemberContains(Integer siteId, Integer userId, Set<String> result, PromotionsActivity proActivityDtoactivity, String user_Obj) {
        SignMembers members= JSON.parseObject(user_Obj,SignMembers.class);
        if(members.getType()==0)
            return true;

        if(members.getType()==1){
            String[] sign_members = members.getPromotion_members().split(",");
            List<MemberLabel> memberLabelList = memberLabelMapper.getLabelAllForCouponActive(siteId, sign_members).stream()
                    .filter(d -> StringUtil.isNotEmpty(d.getScene())).collect(Collectors.toList());


            Map<String, Object> stringMap = null;

            for (MemberLabel memberLabel : memberLabelList) {
                stringMap = couponProcessUtils.String2Map(memberLabel.getScene());
                String userIds = (String) stringMap.get("userIds");
                result.addAll(Arrays.asList(userIds.split(",")));
            }

        }else if(members.getType()==2){
            result=new HashSet<String>(Arrays.asList(members.getPromotion_members().split(",")));
        }else  if(members.getType()==3) {
            String[]  labelNameArr = (JSON.parseObject(proActivityDtoactivity.getUseObject(), SignMembers.class).getPromotion_members().split(","));
            result= relationLabelMapper.getCustomMemberIdAll(siteId, labelNameArr);
        }
        if (result.contains(userId.toString())) {
            return true;
        }
        return false;
    }


    /**
     * 判断活动是否符合使用要求
     */
    public boolean checkProActivityCanUse(PromotionsActivity pa,Integer userId){
        if (null == userId && null == pa) {
            return false;
        }
        Set<String> result = new HashSet<String>();
        String user_Obj=pa.getUseObject();
        SignMembers members=JSON.parseObject(user_Obj,SignMembers.class);
        if(members.getType()==0)
            return true;

        if(members.getType()==1){
            String[] sign_members = members.getPromotion_members().split(",");
            List<MemberLabel> memberLabelList = memberLabelMapper.getLabelAllForCouponActive(pa.getSiteId(), sign_members).stream()
                .filter(d -> StringUtil.isNotEmpty(d.getScene())).collect(Collectors.toList());


            Map<String, Object> stringMap = null;

            for (MemberLabel memberLabel : memberLabelList) {
                stringMap = couponProcessUtils.String2Map(memberLabel.getScene());
                String userIds = (String) stringMap.get("userIds");
                result.addAll(Arrays.asList(userIds.split(",")));
            }

        }else if(members.getType()==2){
            result=new HashSet<String>(Arrays.asList(members.getPromotion_members().split(",")));
        }else  if(members.getType()==3) {
            String[]  labelNameArr = (JSON.parseObject(pa.getUseObject(), SignMembers.class).getPromotion_members().split(","));
            result= relationLabelMapper.getCustomMemberIdAll(pa.getSiteId(), labelNameArr);
        }
        if(userId==null){
            return false;
        }
        if (result.contains(userId.toString())) {
            return true;
        }

        return false;


    }


}
