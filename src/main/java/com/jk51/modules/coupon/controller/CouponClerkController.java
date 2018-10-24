package com.jk51.modules.coupon.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.coupon.CouponClerk;
import com.jk51.model.coupon.requestParams.GetAddClerkParams;
import com.jk51.model.order.BMemberInfo;
import com.jk51.model.order.Member;
import com.jk51.model.order.YBMember;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.mapper.CouponClerkMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.member.mapper.MemberInfoMapper;
import com.jk51.modules.member.service.MemberService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

/**
 * filename :com.jk51.modules.coupon.controller.
 * author   :zw
 * date     :2017/3/26
 * Update   :
 */
@RestController
@RequestMapping("coupon/clerk")
public class CouponClerkController {
    @Autowired
    private CouponClerkMapper couponClerkMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponActivityMapper couponActivityMapper;
    @Autowired
    private MemberInfoMapper memberInfoMapper;
    @Autowired
    CouponActiveForMemberService couponActiveForMemberService;

    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;

    private static final Logger logger = LoggerFactory.getLogger(CouponClerkController.class);

    /**
     * 增加店员可分享的优惠券
     *
     * @param couponClerk
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/addClerk")
    public ReturnDto createCouponRule(CouponClerk couponClerk) {
        if (couponClerk.getSiteId() == null)
            return ReturnDto.buildFailedReturnDto("siteId为空");
        if (couponClerk.getManagerId() == null)
            return ReturnDto.buildFailedReturnDto("店员手机号不能为空");
        if (couponClerk.getRuleId() == null)
            return ReturnDto.buildFailedReturnDto("规则id不能为空");

        // 校验店员是否合法
        Map<String, Object> map = couponClerkMapper.findClerkIsExist(couponClerk.getSiteId(), couponClerk.getManagerId());
        if (map == null)
            return ReturnDto.buildFailedReturnDto("该手机号没有找到店员");

        try {
            couponClerkMapper.addCouponClerk(couponClerk);
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto("领取失败，请求异常");
        }
        return ReturnDto.buildSuccessReturnDto("领取成功");
    }

    /**
     * 领活动 领单张券 领多个活动  分享优惠券（店员and其他用户）
     *
     * @param getAddClerkParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getCouponByShare")
    public ReturnDto getCouponByShare(@RequestBody GetAddClerkParams getAddClerkParams) {
        logger.info("coupon ============getCouponByShare{}", ParameterUtil.ObjectConvertJson(getAddClerkParams));
        if (StringUtils.isBlank(getAddClerkParams.getPhone()))
            return ReturnDto.buildFailedReturnDto("领取手机号不能为空");
        if (getAddClerkParams.getSiteId() == null)
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (StringUtils.isBlank(getAddClerkParams.getActivityIds()))
            return ReturnDto.buildFailedReturnDto("活动id不能为空");

        String phone = getAddClerkParams.getPhone();
        Integer siteId = getAddClerkParams.getSiteId();
        Integer ruleId = getAddClerkParams.getRuleId() == null ? 0 : getAddClerkParams.getRuleId();
        String activityIds = getAddClerkParams.getActivityIds();
        Integer storeId = getAddClerkParams.getStoreId() == null ? 0 : getAddClerkParams.getStoreId();
        Integer managerId = getAddClerkParams.getManagerId() == null ? 0 : getAddClerkParams.getManagerId();


        Member member = memberIsNull(phone, siteId, storeId, managerId);
        if (member == null || member.getMemberId() == null) {
            return ReturnDto.buildFailedReturnDto("领取失败,用户数据异常");
        }
        String[] split = activityIds.split(",");

        boolean ifg = false;
        boolean isCan = false;
        for (String p : split) {
            // 活动是否在发布中
            Integer activityId = Integer.parseInt(p);
            //fixme 只查询一次 CouponActivity
            CouponActivity activity = couponActivityMapper.getCouponActivity(siteId, activityId);
            boolean isGet = couponActiveForMemberService.checkUserForCouponActive(member.getMemberId(), activityId, siteId,activity);
            if (!isGet) //非指定会员不可领取
                continue;
            isCan = true;
            ReturnDto returnDto = IsActivityReleasing(getAddClerkParams.getSiteId(), activityId);
            if (returnDto != null) return returnDto;
            Integer counter = 0;
            if (ruleId > 0) { //领单张券
                counter = couponDetailMapper.countCouponByUserRuleAndActivity(member.getMemberId().toString(), ruleId,
                    siteId, activityId);
            }
            // 查询该活动可以参加几次
            Integer limitTimes = couponActivityService.selectActivityLimitTimes(activity);
            Integer max = limitTimes != null ? limitTimes : 0;


            // 参加次数小于限制次数
            if (max.equals(0) || counter < max) {
                logger.info("coupon ============123123{}");
                try {
                    //fixme 优化
                    Integer count = couponActivityService.sendCouponByActive(activityId, siteId, ruleId, String.valueOf(member.getMemberId()), storeId, managerId,activity);
                    if (count > 0) {
                        ifg = true;
                    }
                } catch (Exception e) {
                    logger.error("getCouponByActive error exception:[{}] ", e);
                    return ReturnDto.buildFailedReturnDto("领取失败");
                }
            }

        }
        logger.info("coupon ============isCan{}", isCan);
        logger.info("coupon ============ifg{}", ifg);
        if (!isCan) {
            return ReturnDto.buildFailedReturnDto("该优惠券指定会员才可领取");
        } else if (ifg) {
            return ReturnDto.buildSuccessReturnDtoByMsg("领取成功");
        } else {
            return ReturnDto.buildFailedReturnDto("您已领取过此优惠券");
        }

    }

    /**
     * 活动是否在发布中
     *
     * @return 如果在发布中，返回null
     */
    private ReturnDto IsActivityReleasing(Integer siteId, Integer activityId) {
        CouponActivity couponActivity = couponActivityMapper.getCouponActivity(siteId, activityId);

        // 判断活动是否是正在发布中
        if (couponActivity == null || couponActivity.getStatus() != 0)
            return ReturnDto.buildFailedReturnDto("该优惠券活动不在发布中，领取失败");

        return null;
    }

    /**
     * 如果用户为空，注册用户
     *
     * @param phone
     * @param siteId
     * @return
     */
    private Member memberIsNull(String phone, Integer siteId, Integer storeId, Integer managerId) {
        Member member = memberMapper.findMobileById(siteId, phone);
        // 如果用户为空，注册用户
        if (null == member) {
            Integer count;
            YBMember ybMembers = memberMapper.selectYbMemberByPhone(phone);
            StoreAdminExt storeAdmin = storeAdminExtMapper.getStoreAdminExtById(siteId + "", managerId + "");
            if (ybMembers == null) {
                YBMember ybmember = new YBMember();
                ybmember.setMobile(phone);
                ybmember.setIsActivated(0);
                ybmember.setReginSource(120);

                ybmember.setRegisterStores(storeId);
                memberService.insertYBMember(ybmember);
                ybMembers = ybmember;
            }
            member = new Member();
            member.setSiteId(siteId);
            member.setMobile(phone);
            member.setMemSource(120);
            member.setIsActivated(0);
            member.setBuyerId(ybMembers.getMemberId());

            member.setRegisterStores(storeId);//邀请门店id
            member.setRegisterClerks(BigInteger.valueOf(managerId));//邀请店员id
            count = memberService.insertMember(member);

            BMemberInfo bMemberInfo = new BMemberInfo();
            bMemberInfo.setMemberId(ybMembers.getMemberId());
            bMemberInfo.setStatus(0);
            bMemberInfo.setSiteId(siteId);

            if (storeAdmin != null) {
                bMemberInfo.setInviteCode(storeAdmin.getClerk_invitation_code());//邀请码
            }
            memberInfoMapper.insertByShare(bMemberInfo);

            if (null == count || !count.equals(1)) {
                return null; //用户创建失败返回领取失败
            }else{
                return member;
            }
        }else{
            return  member;
        }
        //可以注释
//        member = memberMapper.findMobileById(siteId, phone);
//        return member;
    }


}
