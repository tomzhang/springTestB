package com.jk51.modules.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.concession.result.Result;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.CouponDetailView;
import com.jk51.model.order.BeforeCreateOrderReq;
import com.jk51.model.order.OrderGoods;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.request.CouponGoods;
import com.jk51.modules.coupon.request.OwnCouponParam;
import com.jk51.modules.coupon.service.*;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Controller
@RequestMapping("/coupon")
public class CouponDetailController {
    private static final Logger log = LoggerFactory.getLogger(CouponDetailController.class);

    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;
    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private CouponActivityProcessService couponActivityProcessService;

    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;


    @RequestMapping(name = "领券中心领券列表查询", value = "centerOfOwnCoupon")
    @ResponseBody
    public ReturnDto centerOfOwnCoupon(@RequestBody OwnCouponParam ownCouponParam) {
        log.info("领券中心领券列表查寻开始:ownCouponParam:" + ParameterUtil.ObjectConvertJson(ownCouponParam));
        if (null == ownCouponParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        if (null == ownCouponParam.getContentType())
            return ReturnDto.buildFailedReturnDto("contentType不能为空");

        if (null == ownCouponParam.getUserId())
            return ReturnDto.buildFailedReturnDto("userId不能为空");

        List<Map<String, Object>> data = couponDetailService.centerOfOwnCoupon(ownCouponParam);
        log.info("领券中心领券列表查寻返回结果:data:" + ParameterUtil.ObjectConvertJson(data));
        return ReturnDto.buildSuccessReturnDto(data);
    }

    @RequestMapping(name = "领券中心领券列表查询头部数据", value = "centerOfOwnCouponHeader")
    @ResponseBody
    public ReturnDto centerOfOwnCouponHeader(@RequestBody OwnCouponParam ownCouponParam) {
        log.info("领券中心领券列表查寻开始:ownCouponParam:" + ParameterUtil.ObjectConvertJson(ownCouponParam));
        if (null == ownCouponParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        if (null == ownCouponParam.getContentType())
            return ReturnDto.buildFailedReturnDto("contentType不能为空");

        if (null == ownCouponParam.getUserId())
            return ReturnDto.buildFailedReturnDto("userId不能为空");
        //fixme:后期优化策略，list查询不区分type
        List<Map<String, Object>> data = couponDetailService.centerOfOwnCoupon(ownCouponParam);
        log.info("领券中心领券列表查寻返回结果:data:" + ParameterUtil.ObjectConvertJson(data));
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", data);
        ownCouponParam.setContentType(null);
        List<Map<String, Object>>   groupData =couponDetailService.centerOfOwnCoupon(ownCouponParam);
        if(Objects.nonNull(groupData)){
            groupData=groupData.stream().filter(map -> Objects.nonNull(map.get("coupon_type"))).collect(Collectors.toList());
            Map<Integer, Long> groupCount = groupData.stream().collect(Collectors.groupingBy(o -> Integer.valueOf(o.get("coupon_type").toString()), Collectors.counting()));
//        ownCouponParam.setContentType(100);
//        resultMap.put("cash", CollectionUtils.isEmpty(couponDetailService.centerOfOwnCoupon(ownCouponParam)) ? 0 : couponDetailService.centerOfOwnCoupon(ownCouponParam).size());
//        ownCouponParam.setContentType(200);
//        resultMap.put("discount", CollectionUtils.isEmpty(couponDetailService.centerOfOwnCoupon(ownCouponParam)) ? 0 : couponDetailService.centerOfOwnCoupon(ownCouponParam).size());
//        ownCouponParam.setContentType(300);
//        resultMap.put("fixed", CollectionUtils.isEmpty(couponDetailService.centerOfOwnCoupon(ownCouponParam)) ? 0 : couponDetailService.centerOfOwnCoupon(ownCouponParam).size());
//        ownCouponParam.setContentType(400);
//        resultMap.put("other", CollectionUtils.isEmpty(couponDetailService.centerOfOwnCoupon(ownCouponParam)) ? 0 : couponDetailService.centerOfOwnCoupon(ownCouponParam).size());
//        ownCouponParam.setContentType(500);
//        resultMap.put("gift", CollectionUtils.isEmpty(couponDetailService.centerOfOwnCoupon(ownCouponParam)) ? 0 : couponDetailService.centerOfOwnCoupon(ownCouponParam).size());
            if(groupCount.size() > 0){
                for (Integer count : groupCount.keySet()) {
                    if(count == 100){
                        resultMap.put("cash", Integer.valueOf(groupCount.get(count).toString()));
                        continue;
                    }
                    if(count == 200){
                        resultMap.put("discount",Integer.valueOf(groupCount.get(count).toString()));
                        continue;
                    }
                    if(count == 300){
                        resultMap.put("fixed",Integer.valueOf(groupCount.get(count).toString()));
                        continue;
                    }
                    if(count == 400){
                        resultMap.put("other",Integer.valueOf(groupCount.get(count).toString()));
                        continue;
                    }
                    if(count == 500){
                        resultMap.put("gift",Integer.valueOf(groupCount.get(count).toString()));
                        continue;
                    }
                }
            }
            if(Objects.isNull(resultMap.get("cash"))){
                resultMap.put("cash",0);
            }
            if(Objects.isNull(resultMap.get("discount"))){
                resultMap.put("discount",0);
            }
            if(Objects.isNull(resultMap.get("fixed"))){
                resultMap.put("fixed",0);
            }
            if(Objects.isNull(resultMap.get("other"))){
                resultMap.put("other",0);
            }
            if(Objects.isNull(resultMap.get("gift"))){
                resultMap.put("gift",0);
            }
        }else{
            resultMap.put("cash",0);
            resultMap.put("discount",0);
            resultMap.put("fixed",0);
            resultMap.put("other",0);
            resultMap.put("gift",0);
        }
        return ReturnDto.buildSuccessReturnDto(resultMap);
    }

    @RequestMapping(name = "个人中心领券中心查询是否有可以领取的券", value = "centerOfOwnCouponCount")
    @ResponseBody
    public ReturnDto centerOfOwnCouponCount(@RequestBody OwnCouponParam ownCouponParam) {
        log.info("个人中心查询有无可以领取的优惠券可是:ownCouponParam:" + ParameterUtil.ObjectConvertJson(ownCouponParam));
        if (null == ownCouponParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");

        if (null == ownCouponParam.getUserId())
            return ReturnDto.buildFailedReturnDto("userId不能为空");

        List<Map<String, Object>> data = couponDetailService.centerOfOwnCoupon(ownCouponParam);
        log.info("领券中心领券列表查寻返回结果:data:" + ParameterUtil.ObjectConvertJson(data));
        return ReturnDto.buildSuccessReturnDto(CollectionUtils.isEmpty(data) ? 0 : data.size());
    }
    @RequestMapping(name="通过扫码获得优惠券详情",value="getCouponDetailByScanQr")
    @ResponseBody
    public ReturnDto getCouponDetailByScanQr(Integer siteId,Integer storeId,Integer managerId,String couponNo){
        ReturnDto returnDto = null;
        try {
            Map<String, Object> couponDetailByScanQr = couponDetailService.getCouponDetailByScanQr(siteId, storeId, managerId, couponNo);
            if(Objects.isNull(couponDetailByScanQr)){
                return ReturnDto.buildFailedReturnDto("抱歉，找不到该优惠券");
            }
            returnDto = ReturnDto.buildSuccessReturnDto(couponDetailByScanQr);
        } catch (Exception e) {
            log.error("通过优惠券编码获得优惠券详情失败:{}",e);
            ReturnDto.buildFailedReturnDto("通过优惠券编码获得优惠券详情失败");
        }
        return returnDto;
    }

    @RequestMapping(name="app核销优惠券",value="writeOffCoupons")
    @ResponseBody
    public ReturnDto writeOffCoupons(Integer siteId,Integer managerId,String couponNo){
        try {
            return couponDetailService.writeOffCoupons(siteId,managerId,couponNo);
        } catch (Exception e) {
            log.error("核销优惠券失败:{}",e);
            return ReturnDto.buildFailedReturnDto("核销优惠券异常");
        }
    }


    /**
     * 核销
     * @param ownCouponParam
     * @return
     */
    @RequestMapping(name = "领券中心优惠券详情", value = "centerOfOwnCouponDetail")
    @ResponseBody
    public ReturnDto centerOfOwnCouponDetail(@RequestBody OwnCouponParam ownCouponParam) {

        if (null == ownCouponParam.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");


        if (null == ownCouponParam.getRuleId())
            return ReturnDto.buildFailedReturnDto("ruleId不能为空");

        if (null == ownCouponParam.getActivityId())
            return ReturnDto.buildFailedReturnDto("activityId不能为空");

        Map<String, Object> data = couponDetailService.centerOfOwnCouponDetail(ownCouponParam);
        return ReturnDto.buildSuccessReturnDto(data);
    }

    /**
     * 查询用户优惠券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto findUserCouponList(HttpServletRequest request) throws ParseException {

        String siteId = request.getParameter("siteId");
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");//0待使用1已使用2已过期

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId为空");
        if (!StringUtils.isNotBlank(userId))
            return ReturnDto.buildFailedReturnDto("userId为空");
        if (!StringUtils.isNotBlank(status))
            status = "0";

        List<Map<String, Object>> data;
        try {
            log.info("查询用户优惠券:入口，参数:siteId:" + siteId + "userId:" + userId + "status:" + status);
            data = couponDetailService.findUserCouponList(Integer.parseInt(siteId), Integer.parseInt(userId),
                Integer.parseInt(status));
        } catch (Exception e) {
            return ReturnDto.buildSystemErrorReturnDto();
        }

        return ReturnDto.buildSuccessReturnDto(data);
    }

    /**
     * 查询可用优惠券
     *
     * @param request
     * @return
     */
    @RequestMapping("/usableList")
    @ResponseBody
    public ReturnDto findUsableCouponList(BeforeCreateOrderReq beforeCreateOrderReq, HttpServletRequest request) throws ParseException {
        try {
            String siteId = request.getParameter("siteId");
            String userId = request.getParameter("userId");
            String orderType = request.getParameter("orderType");
            String applyChannel = request.getParameter("applyChannel");
            String storeId = request.getParameter("storeId");
            String postFee = request.getParameter("postFee");
            String orderFee = request.getParameter("orderFee");
            String goodsInfo = request.getParameter("goodsInfo");
            String areaId = request.getParameter("areaId");
            String mobile = request.getParameter("mobile");
            String buyerId = request.getParameter("buyerId");
            Result result = JSON.parseObject(request.getParameter("concessionResultJSON"), Result.class);
            String orderGoodsJson = request.getParameter("orderGoodsJson");

            if (StringUtil.isNotEmpty(orderGoodsJson)) {
                List<OrderGoods> ordersGoodsList = JacksonUtils.json2list(orderGoodsJson, OrderGoods.class);
                beforeCreateOrderReq.setOrderGoods(ordersGoodsList);
                beforeCreateOrderReq.setMobile(mobile);
                beforeCreateOrderReq.setBuyerId(Integer.parseInt(buyerId));
                beforeCreateOrderReq.setSiteId(StringUtil.isEmpty(siteId) ? 0 : Integer.parseInt(siteId));
                beforeCreateOrderReq.setStoresId(StringUtil.isEmpty(storeId) ? 0 : Integer.parseInt(storeId));
            }

            if (StringUtil.isEmpty(siteId))
                return ReturnDto.buildFailedReturnDto("siteId为空");
            if (StringUtil.isEmpty(userId))
                return ReturnDto.buildFailedReturnDto("userId为空");
            if (StringUtil.isEmpty(orderType))
                return ReturnDto.buildFailedReturnDto("orderType为空");
            if (StringUtil.isEmpty(applyChannel))
                return ReturnDto.buildFailedReturnDto("applyChannel为空");
            if (StringUtil.isEmpty(orderFee))
                return ReturnDto.buildFailedReturnDto("orderFee为空");
            if (StringUtil.isEmpty(goodsInfo))
                return ReturnDto.buildFailedReturnDto("goodsInfo为空");


            return couponDetailService.usableCouponList(siteId, userId, orderType, applyChannel, storeId,
                orderFee, postFee, goodsInfo, areaId, result);
        } catch (Exception e) {
            log.error("异常发送，{}", e);
            return ReturnDto.buildFailedReturnDto("请求数据异常");
        }
    }




    /**
     * 查询可用优惠券数量
     *
     * @return
     */
    @RequestMapping("/count")
    @ResponseBody
    public ReturnDto count(HttpServletRequest request) throws ParseException {

        String siteId = request.getParameter("siteId");
        String userId = request.getParameter("userId");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId 为空");
        if (!StringUtils.isNotBlank(userId))
            return ReturnDto.buildFailedReturnDto("userId 为空");

        List<Map<String, Object>> data = couponDetailService.findUserCouponList(Integer.parseInt(siteId),
            Integer.parseInt(userId), 0);
        if (data.isEmpty())
            return ReturnDto.buildFailedReturnDto("该用户无可用优惠券");

        return ReturnDto.buildSuccessReturnDto(data.size());
    }

    /**
     * 查询微信优惠券详情
     *
     * @return
     */
    @RequestMapping("/wxCouponDetail")
    @ResponseBody
    public ReturnDto findWxCouponDetailById(HttpServletRequest request) {

        String siteId = request.getParameter("siteId");
        String id = request.getParameter("id");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId");
        if (!StringUtils.isNotBlank(id))
            return ReturnDto.buildFailedReturnDto("id为空");

        CouponDetailView wxCouponDetailById = couponDetailService.findWxCouponDetailById(Integer.parseInt(siteId), Integer.parseInt(id));
        return ReturnDto.buildSuccessReturnDto(wxCouponDetailById);
    }

    /**
     * 线下使用优惠券，改变状态
     *
     * @return
     */
    @RequestMapping("/wxUpdate")
    @ResponseBody
    public ReturnDto update(HttpServletRequest request) {

        String siteId = request.getParameter("siteId");
        String id = request.getParameter("id");
        String userId = request.getParameter("userId");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId 为空");
        if (!StringUtils.isNotBlank(userId))
            return ReturnDto.buildFailedReturnDto("userId 为空");
        if (!StringUtils.isNotBlank(id))
            return ReturnDto.buildFailedReturnDto("id 为空");

        int i = couponDetailMapper.updateStatusToOffLine(Integer.parseInt(siteId), Integer.parseInt(id), Integer.parseInt(userId));
        if (i > 0) {
            CouponDetail couponDetail = couponDetailMapper.getCouponDetailByUserId(Integer.parseInt(siteId), Integer.parseInt(id));
            CouponRule couponRule = couponRuleMapper.findCouponRuleById(couponDetail.getRuleId(), Integer.parseInt(siteId));
            couponRuleService.updateRuleStatus(Integer.parseInt(siteId), couponDetail.getRuleId(), null, couponRule.getUseAmount() + 1, null, null, null);
            if (StringUtils.isNotBlank(couponDetail.getSource())) {
//                couponActivityService.updateCouponActivityByUse(Integer.parseInt(siteId),Integer.parseInt(couponDetail.getSource()),null,0);
                couponActivityProcessService.updateCouponCommon(Integer.parseInt(siteId), couponDetail.getRuleId(), Integer.parseInt(couponDetail.getSource()), null, 0, null);
            }
            return ReturnDto.buildSuccessReturnDto("优惠券线下使用成功");
        }

        return ReturnDto.buildFailedReturnDto("优惠券使用失败");
    }

    @RequestMapping("/sendByRegister")
    @ResponseBody
    public ReturnDto sendCouponRegister(HttpServletRequest request) {

        String siteId = request.getParameter("siteId");
        String userId = request.getParameter("userId");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId 为空");
        if (!StringUtils.isNotBlank(userId))
            return ReturnDto.buildFailedReturnDto("userId 为空");

        return ReturnDto.buildSuccessReturnDto(
            couponSendService.sendCoupon(Integer.parseInt(siteId), Integer.parseInt(userId)));
    }

    /**
     * 更新分享次数为0 ？不确定
     *
     * @param request
     * @return
     */
    @RequestMapping("/shareNum")
    @ResponseBody
    public ReturnDto shareNum(HttpServletRequest request) {

        String siteId = request.getParameter("siteId");
        String ruleId = request.getParameter("ruleId");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId 为空");
        if (!StringUtils.isNotBlank(ruleId))
            return ReturnDto.buildFailedReturnDto("ruleId 为空");

        try {
            couponRuleService.updateRuleStatus(Integer.parseInt(siteId), Integer.parseInt(ruleId), 0);
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto("分享次数更新失败");
        }

        return ReturnDto.buildSuccessReturnDto("分享次数更新成功");
    }

    /**
     * 查询用户优惠券
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/goodsCoupon", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto goodsCoupon(HttpServletRequest request) throws ParseException {

        String siteId = request.getParameter("siteId");
        String userId = request.getParameter("userId");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId为空");
        if (!StringUtils.isNotBlank(userId))
            return ReturnDto.buildFailedReturnDto("userId为空");

        Set<Integer> goodsId = new HashSet<>();
        try {
            List<Map<String, Object>> data =
                couponDetailService.findUserCouponList(Integer.parseInt(siteId), Integer.parseInt(userId), 0);
            if (data.size() > 0) {
                goodsId = couponDetailService.goodsCoupon(data);
            }
        } catch (Exception e) {
            return ReturnDto.buildSystemErrorReturnDto();
        }

        return ReturnDto.buildSuccessReturnDto(goodsId);
    }

    /**
     * 优惠券下面可以关联到的商品信息
     */
    @RequestMapping(value = "queryAllGoodsForCoupon", method = RequestMethod.POST)
    @ResponseBody
    private ReturnDto queryAllGoodsForCoupon(@RequestBody CouponGoods couponGoods) {

        if (StringUtils.isBlank(couponGoods.getSiteId()))
            return ReturnDto.buildFailedReturnDto("siteId为空");
        if (StringUtils.isBlank(couponGoods.getRuleId()))
            return ReturnDto.buildFailedReturnDto("ruleId为空");

        PageInfo<?> pageInfo;
        try {
            pageInfo = couponRuleService.queryCouponGoodsList(couponGoods);
        } catch (Exception e) {
            log.info("查询优惠券商品报错:" + e);
            return ReturnDto.buildFailedReturnDto("系统错误，请稍后再试");
        }

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping("/couponDetailListByStoreAdmin")
    @ResponseBody
    public Object couponDetailListByStoreAdmin(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        log.info("查询店员发放优惠券请求参数:{}", parameterMap);
        ReturnDto result = null;
        try {
            result = checkParamMap(parameterMap);
            if (result != null) return result;
            result = couponDetailService.findCouponListByStoreAdmin(parameterMap);
        } catch (Exception e) {
            log.error("查询店员发放优惠券失败：{}", e);
            result = ReturnDto.buildFailedReturnDto("查询店员发放优惠券失败");
        }
        return result;
    }

    /**
     * 检查map参数是否为空
     *
     * @param parameterMap
     * @return
     */
    private ReturnDto checkParamMap(Map<String, Object> parameterMap) {
        Set<String> keySet = parameterMap.keySet();
        for (String key : keySet) {
            Object temp = parameterMap.get(key);
            if (temp == null) {
                log.error("查询失败参数不能为空:{}", key);
                return ReturnDto.buildFailedReturnDto("参数不能为空:" + key);
            }
        }
        return null;
    }


    @RequestMapping(value = "/couponUseUnuse", method = RequestMethod.GET)
    @ResponseBody
    public Object couponUseUnuse(HttpServletRequest request) throws Exception {
        String siteId = request.getParameter("siteId");
        String ruleId = request.getParameter("ruleId");
        String status = request.getParameter("status");//0待使用1已使用
        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId为空");
        if (!StringUtils.isNotBlank(ruleId))
            return ReturnDto.buildFailedReturnDto("ruleId为空");
        if (!StringUtils.isNotBlank(status))
            return ReturnDto.buildFailedReturnDto("status为空");
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Object coupon_no = param.get("no");
        if (coupon_no != null) {
            if (!coupon_no.toString().contains("Q")) {
                String encryptionCouponNo = couponNoEncodingService.decryptionCouponNo(coupon_no.toString());
                param.put("no", encryptionCouponNo);
            }
        }
        int use = couponDetailService.couponUseUnuse(Integer.parseInt(siteId.toString()), Integer.parseInt(ruleId.toString()), Integer.parseInt(status.toString()), param);

        return ReturnDto.buildSuccessReturnDto(use);
    }

}

