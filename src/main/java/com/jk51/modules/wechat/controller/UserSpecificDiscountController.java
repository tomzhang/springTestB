package com.jk51.modules.wechat.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.coupon.CouponActivity;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceResult;
import com.jk51.modules.promotions.sequence.wechat.WechatSequenceHandlerImpl;
import com.jk51.modules.promotions.sequence.wechat.WechatSequenceImpl;
import com.jk51.modules.wechat.service.UserSpecificDiscountService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/5/8.
 */
@RestController
@RequestMapping("userSpecificDiscount")
public class UserSpecificDiscountController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserSpecificDiscountService userSpecificDiscountService;
    @Autowired
    private ServletContext servletContext;

    //获取优惠券
    @ResponseBody
    @PostMapping(value = "/getCouponBySiteId")
    public ReturnDto getCouponBySiteId(HttpServletRequest request) throws Exception {
        Map<String,Object> requestParam = ParameterUtil.getParameterMap(request);
        Object siteId = requestParam.get("siteId");
        Object memberId = requestParam.get("memberId");
        logger.info("获取用户专属优惠券信息");
        if (Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        /*if (Objects.isNull(memberId)) {
            return ReturnDto.buildFailedReturnDto("memberId不能为空");
        }*/
        Integer memberid = null;
        Integer siteid = Integer.parseInt(siteId.toString());
        if (!Objects.isNull(memberId))
            Integer.parseInt(memberId.toString());
        //获取优惠券 优惠券规则为：指定商户、库存大于0 或不限量、针对对象为商品、状态为可发放状态的
        List<CouponActivity> couponActivityList = userSpecificDiscountService.getCouponBySiteId(siteid, memberid);
        if (CollectionUtils.isEmpty(couponActivityList))
            return ReturnDto.buildFailedReturnDto("暂无优惠券信息！");
        else
            if (couponActivityList.size() > 3)
                return ReturnDto.buildSuccessReturnDto(couponActivityList.subList(0, 3));
            else
                return ReturnDto.buildSuccessReturnDto(couponActivityList);

    }

    /*
*  会员专属活动
**/
    @RequestMapping("getExclusiveActivities")
    @ResponseBody
    public ReturnDto getExclusiveActivities(HttpServletRequest request) {
        Map<String,Object> requestParam = ParameterUtil.getParameterMap(request);
        Object siteId = requestParam.get("siteId");
        Object memberId = requestParam.get("memberId");
        Object promotionTypeObj = requestParam.get("promotiontype");
        if (Objects.isNull(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        if (Objects.isNull(promotionTypeObj)) {
            return ReturnDto.buildFailedReturnDto("promotionType不能为空");
        }
        logger.info("会员专属活动信息");
        Object pageNumObj = requestParam.get("pageNum");
        Object pageSizeObj = requestParam.get("pageSize");
        Integer pageNum = null;
        Integer pageSize = null;

        Integer siteid = Integer.parseInt(siteId.toString());
        Integer memberid = null;
        if (Objects.nonNull(memberId))
            memberid = Integer.parseInt(memberId.toString());

        Integer promotionType = Integer.parseInt(promotionTypeObj.toString());

        if (!Objects.isNull(pageNumObj)) {
            pageNum = Integer.parseInt(pageNumObj.toString());
        }
        else {
           pageNum = 1;
        }
        if (!Objects.isNull(pageSizeObj)) {
            pageSize = Integer.parseInt(pageSizeObj.toString());
        }else {
            if (60 == promotionType || 50 == promotionType)
                pageSize = 3;
            else if (10 == promotionType || 20 == promotionType || 30 == promotionType || 40 == promotionType)
                pageSize = 6;
        }
        SequenceParam param = new SequenceParam(siteid,memberid,pageNum,pageSize,promotionType);
        WechatSequenceImpl ws = new WechatSequenceImpl(servletContext,param);
        try {
            ws.collection();
            ws.processGoods();
            ws.processSequence(new WechatSequenceHandlerImpl());
            ws.processTags();
            WechatSequenceResult sequenceResult =(WechatSequenceResult) ws.result.get();
            return ReturnDto.buildSuccessReturnDto(sequenceResult);
        } catch (Exception e) {
            logger.error("会员专属活动异常");
            e.printStackTrace();
        }
        return ReturnDto.buildFailedReturnDto("暂无会员专属活动信息");
    }

}
