package com.jk51.modules.appInterface.controller;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.treat.MerchantTreat;
import com.jk51.modules.appInterface.service.AppClerkVisitService;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.appInterface.service.UsersService;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.appInterface.util.MemberInfo;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.offline.service.OfflineMemberService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.treat.mapper.MerchantMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: app 会员、优惠券相关接口
 * 作者: yeah
 * 创建日期: 2017-03-07
 * 修改记录:
 */

@RequestMapping("/member")
@ResponseBody
@Controller
public class AppMemberController {

    @Autowired
    AppMemberService memberService;
    @Autowired
    UsersService usersService;
    @Autowired
    CouponDetailService couponDetailService;

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    OfflineMemberService offlineMemberService;

    @Autowired
    MemberMapper memberMapper;

    private static final Logger logger = LoggerFactory.getLogger(AppMemberController.class);

    /**
     * 获取会员信息
     *
     * @param phone
     * @return
     */

    @RequestMapping("/{phone}")
    public Map<String, Object> getMemberInfo(@PathVariable(required = true, name = "phone") String phone, @RequestBody Map<String, Object> body) {
        //解析token
        Integer site_id = (Integer) body.get("site_id");
        return memberService.queryMemberInfoByPhoneNum(phone, site_id);
    }

    /**
     * 修改、更新会员信息
     *
     * @param
     * @return
     */

    @RequestMapping("/edit")
    @ResponseBody
    public Map<String, Object> editMemberInfo(@RequestBody Map<String, Object> parameterMap) {
        Map<String, Object> result = memberService.updateMemberInfo(parameterMap);
        if (result.get("status").toString().equals("OK")) {
            try {
                parameterMap.put("siteId", parameterMap.get("site_id"));
                offlineMemberService.updateMemberinfo(parameterMap);
            } catch (Exception e) {
                logger.info("修改线下会员信息失败" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 发送验证码到手机
     * 验证码是用phone做键存在redis中
     *
     * @param
     * @return
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public Map<String, Object> getSignCode(@RequestBody Map<String, Object> paramsmap) {

        Integer siteId = (Integer) paramsmap.get("siteId");
        String type = (String) paramsmap.get("type");
        String phone = (String) paramsmap.get("phone");

        Integer smsType = 0;
        if(paramsmap.containsKey("smsType")){
            smsType = Integer.parseInt(paramsmap.get("smsType").toString());
        }
        ResultMap vali = this.memberService.ValidatePhoneNum(phone, siteId);
        if (vali.get("status").toString().equals("ERROR")) {
            return vali;
        }
        if (StringUtil.equals(type, "1")) {
            //sms
            return this.memberService.sendSignCode(siteId, phone,smsType);
        } else if (StringUtil.equals(type, "2")) {
            //call
            return this.memberService.callSignCode(phone);
        }
        return ResultMap.errorResult("参数错误");
    }

    /**
     * 会员注册
     *
     * @param parameterMap
     * @return
     */

    @RequestMapping("/register")
    @ResponseBody
    public Map<String, Object> registerMember(@RequestBody Map<String, Object> parameterMap) {
        Map<String, Object> result = memberService.registerMember(parameterMap);
        if (result.get("status").toString().equals("OK")) {
            try {
                Map<String, Object> memberInfo = memberMapper.queryMemberInfoByPhoneNum(parameterMap.get("mobile").toString(),
                    Integer.parseInt(parameterMap.get("site_id").toString()));
                String invite_code = "";
                if (StringUtil.isEmpty(memberInfo.get("invite_code"))) {
                    invite_code = memberInfo.get("invite_code").toString();
                }
                offlineMemberService.getuser(Integer.parseInt(parameterMap.get("site_id").toString()),
                    parameterMap.get("mobile").toString(), invite_code);
            } catch (Exception e) {
                logger.info("app后台更新erp会员有误" + e.getMessage());
            }
        }
        return result;

    }

    /**
     * 查询门店优惠券
     *
     * @return
     */

    @RequestMapping("/appstorecoupon")
    @ResponseBody
    public Map<String, Object> getAppCoupon(@RequestBody Map<String, Object> body) {
        //解析token
        String accessToken = (String) body.get("AuthToken");
        AuthToken authToken = usersService.parseAccessToken(accessToken);
        return this.memberService.getAppCoupon(authToken);
    }

    /**
     * 获取店员优惠券
     *
     * @param
     * @return
     */
    @RequestMapping("/getCustomerCoupon")
    @ResponseBody
    public Map<String, Object> getCustomerCoupon(@RequestBody Map<String, Object> param) {

        logger.info("获取优惠券列表！");

        Map<String, Object> result = new HashMap();
        try {
            String accessToken = (String) param.get("AuthToken");
            AuthToken authToken = usersService.parseAccessToken(accessToken);

            MerchantTreat merchant = merchantMapper.getMerchant(authToken.getSiteId() + "");
            List<Map<String, Object>> customerCoupons = memberService.getCustomerCoupon(Integer.parseInt(authToken.getSiteId() + ""), Integer.parseInt(authToken.getStoreUserId() + ""),
                authToken.getStoreAdminId() + "");
            if (null == customerCoupons || customerCoupons.isEmpty() || customerCoupons.size() == 0) {
                return ResultMap.errorResult("此店员没有可用优惠券!");
            }
            customerCoupons.stream().forEach(p -> {
                p.put("company_name", merchant.getCompany_name());
                try {
                    GoodsRule goodsRule = JacksonUtils.json2pojo(p.get("goods_rule") + "", GoodsRule.class);
                    p.put("goods_rules", converGoodsRule(goodsRule));
                } catch (Exception e) {
                    logger.error("获取店员优惠券列表失败：", e);
                }

                String time_rule = couponDetailService.getEffectiveTimeForGoodsDetail(p.get("time_rule") + "",
                    DateUtils.parseDate(p.get("create_time") + "", "yyyy-MM-dd hh:mm:dd"));
                p.put("time_rules", time_rule);
            });
            result.put("list", customerCoupons);
            return ResultMap.successResult(result);
        } catch (Exception e) {
            logger.error("获取店员优惠券列表失败：", e);
        }
        return ResultMap.errorResult("请求数据失败！");
    }

    /**
     * @return
     */
    public String converGoodsRule(GoodsRule goodsRule) {
        String result = "";
        switch (goodsRule.getType()) {
            case 0:
                result = "全部商品";
                break;
            case 1:
                result = "限购买指定类目";
                break;
            case 2:
                result = "限购买指定商品";
                break;
            case 3:
                result = "指定商品不参加";
                break;
        }

        return result;
    }

    /**
     * 获取门店下顾客的优惠券
     * @param param
     * @return
     */
   /* @RequestMapping("/getStoreCustomerCoupon")
    @ResponseBody
    public Map<String, Object> getStoreCustomerCoupon(@RequestBody Map<String, Object> param) {
        Map<String,Object> result=new HashMap();
        List<CouponDetail> couponDetails = couponDetailService.getStoreCustomerCoupon(param);
        result.put("list",couponDetails);
        return result;
    }*/


    /**
     * 生成关注微信公众号关注店铺二维码
     *
     * @return
     */

    @RequestMapping("/grcode")
    @ResponseBody
    public Map<String, Object> generateBarCode(@RequestBody Map<String, Object> body) {
        //解析token
        Integer siteId = (Integer) body.get("siteId");
        return memberService.generateBarCode(siteId);
    }

    /**
     * 获取订单可用优惠券
     *
     * @return {
     * "results": {
     * "list": [
     * {
     * "isUse": 1,                         是否可用：1代表不可用 0代表可用
     * "userCouponAmount": 10,             1.优惠券类型为100或300时表示使用优惠券可以减去的金额  2.优惠券类型为200表示可以打的折扣  3.优惠券类型为400 无用
     * "effectiveTime": "10小时13分5秒",     优惠价剩余有效时间
     * "couponType": 400,                  优惠券类型：100现金券 200打折券 300 限价券 400包邮券'
     * "userCouponId": 104235,             优惠券ID
     * "couponLimitType": 0,               优惠券限制类型：0 全品 1 限品
     * "couponLimitName": "满100减10元 "    前台提示语
     * },
     * {
     * "isUse": 0,
     * "userCouponAmount": 10,
     * "effectiveTime": "16天",
     * "couponType": 100,
     * "userCouponId": 88755,
     * "couponLimitType": 0,
     * "couponLimitName": "1000元"
     * }
     * ]
     * },
     * "status": "OK"
     * }
     */

  /*  @RequestMapping("/coupon")
    @ResponseBody
    public Map<String, Object> getUsableCoupon(String area_id, @RequestParam("product_ids") String[] product_ids,
                                               @RequestParam("product_nums") String[] product_nums, @RequestParam("order_type") String order_type,
                                               @RequestParam("order_amount") String order_amount, @RequestParam("mobile") String phone,
                                                @RequestParam("product_prices") String[] product_prices, @RequestParam("authToken") String accessToken) {


        AuthToken authToken = usersService.parseAccessToken(accessToken);
        return this.memberService.getUsableCoupon(area_id, authToken, product_ids, product_nums, order_amount, phone, order_type, product_prices);
    }*/
    @RequestMapping("/coupon")
    @ResponseBody
    public Map<String, Object> getUsableCoupon(@RequestBody Map<String, Object> body) {

        return this.memberService.getUsableCoupon(body);
    }


    @RequestMapping("/memberFirst")
    @ResponseBody
    public Result memberFirst(Integer siteId, String mobile, String tradesId) {
        if (StringUtils.isBlank(mobile) && StringUtils.isNotBlank(tradesId)) mobile = memberService.getMobileByTradesId(siteId, tradesId);
        if (!StringUtil.isMobileNO(mobile)) return Result.fail("手机号格式不正确");
        return memberService.memberFirst(siteId, mobile);
    }

    @Autowired
    AppClerkVisitService appClerkVisitService;

    @RequestMapping("/memberSummary")
    @ResponseBody
    public Result memberSummary(Integer siteId, Integer storeId, String mobile) {
        Result result = memberService.memberFirst(siteId, mobile);
        if (result.getStatus() == Result.SUCCESS_STATUS) {
            if (result.getData() == null) {
                result = Result.fail("会员未注册");
            } else {
                MemberInfo memberInfo = (MemberInfo) result.getData();
                Map<String, Object> customerInfoMap = null;
                Map<String, Object> dealAnalyzeMap = null;
                List<Map<String, Object>> data = null;//会员可使用优惠券列表
                try {
                    customerInfoMap = appClerkVisitService.queryCustomerInfo(memberInfo.getBuyerId(), siteId, storeId);//用户信息
                } catch (Exception e) {
                    logger.error("queryCustomerInfo 异常 {} ", e);
                }
                try {
                    dealAnalyzeMap = appClerkVisitService.queryDealAnalyze(siteId, memberInfo.getBuyerId());//交易分析
                } catch (Exception e) {
                    logger.error("queryDealAnalyze 异常 {} ", e);
                }
                //查询会员可用优惠券
                try {
                    if (Objects.nonNull(customerInfoMap)) {
                        String status = "0";//0待使用1已使用2已过期
                        String userId = customerInfoMap.get("member_id").toString();
                        if (!org.apache.commons.lang.StringUtils.isNotBlank(status))
                            status = "0";
                        logger.info("查询用户优惠券:入口，参数:siteId:" + siteId + "userId:" + userId + "status:" + status);
                        data = couponDetailService.findUserCouponList(siteId, Integer.parseInt(userId), Integer.parseInt(status));
                    }
                }catch (Exception e) {
                    logger.error("findUserCouponList 异常 {}", e);
                }
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("customerInfoMap", customerInfoMap);
                resultMap.put("dealAnalyzeMap", dealAnalyzeMap);
                resultMap.put("memberCoupon", data);
                result = Result.success(resultMap);
            }
        }
        return result;
    }


}
