package com.jk51.modules.appInterface.service;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.clerkvisit.BVisitMessage;
import com.jk51.model.coupon.requestParams.CouponView;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.order.Member;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.model.orders.SYbMember;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMessageSettingMapper;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.appInterface.util.MemberInfo;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.appInterface.util.SendSMS;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.coupon.service.CouponDetailService;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.im.event.DelayedMessageProduce;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.persistence.mapper.SYbMemberMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-03-07
 * 修改记录:
 */
@Service
public class AppMemberService {

    public String getCouponUrl() {
        return couponUrl;
    }

    public void setCouponUrl(String couponUrl) {
        this.couponUrl = couponUrl;
    }
    @Autowired
    private WechatUtil wechatUtil;
    @Value("${coupon.triggerCouponUrl}")
    public String couponUrl;

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminextMapper;
    @Autowired
    private MerchantExtMapper merchantExtMapper;
    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private SYbMemberMapper ybMemberMapper;
    @Autowired
    private BMemberInfoMapper bMemberInfoMapper;
    @Autowired
    private IntegralService integral4Regist;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private CouponDetailService couponDetailService;
    @Autowired
    private SendSMS sendSMS;
    @Autowired
    UsersService usersService;
    @Autowired
    private DelayedMessageProduce delayedMessageProduce;
    @Autowired
    private BMessageSettingMapper bMessageSettingMapper;

    @Autowired
    private CouponSendService couponSendService;
    private static final Logger logger = LoggerFactory.getLogger(AppMemberService.class);

    public Map<String, Object> queryMemberInfoByPhoneNum(String phone, Integer site_id) {
        HashMap<String, Object> result = null;
        try {
            result = this.memberMapper.queryMemberInfoByPhoneNum(phone, site_id);
            if (result == null) {
                return ResultMap.errorResult("抱歉，会员信息不存在");
            }
            return ResultMap.successResult(result);
        } catch (Exception e) {
            logger.error("查询手机号为[{}]的会员信息失败！错误信息" + e, phone);
            return ResultMap.errorResult("查询会员信息失败");
        }
    }

    @Transactional
    public Map<String, Object> updateMemberInfo(Map<String, Object> params) {//  tag为null
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //更新b_member表
            if (params.get("name") != null || params.get("memo") != null || params.get("idcard_number") != null || params.get("email") != null || params.get("sex") != null) {
                Integer updateMemNum = this.memberMapper.updateMember(params);
            }
            //更新b_member_info表
            if (params.get("tag") != null || params.get("birthday") != null || params.get("age") != null || params.get("address") != null || params.get("province") != null || params.get("city") != null || params.get("area") != null || params.get("membershipNumber") != null) {
                Integer updateMemInfoNum = this.memberMapper.updateMemberInfo(params);
            }

            //修改yb_member.idcard_number
            if (params.get("idcard_number") != null) {
                ybMemberMapper.updateIdCardNumber(params);
            }

            return ResultMap.successResult(new HashedMap());
        } catch (Exception e) {
            logger.error("更新会员信息失败！错误信息" + e);
            return ResultMap.errorResult("更新会员信息失败");
        }
    }


    //发送短信验证码
    public Map<String, Object> sendSignCode(Integer siteId, String phone,Integer type) {

        if (StringUtil.isEmpty(phone)) {
            return ResultMap.errorResult("手机号码为空");
        }


        //获取验证码
        String svcode = String.valueOf(randomInt(1000, 9999));
        logger.info("注册会员，二维码{}", svcode);
        //将注册用的验证码放入redis
        template.opsForValue().set(phone, svcode, 1, TimeUnit.MINUTES);


        Integer status = sendSMS.sendSMS(siteId, phone, svcode,type);

        if (StringUtil.equals("0", status.toString())) {
            return ResultMap.successResult();
        } else {
            return ResultMap.errorResult("发送短信失败");
        }

    }

    //发送语音验证码
    public Map<String, Object> callSignCode(String phone) {
        String svcode = String.valueOf(randomInt(1000, 9999));
        logger.info("注册会员，二维码{}", svcode);
        //将注册用的验证码放入redis
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(phone, svcode, 1, TimeUnit.HOURS);
        String msg = "您的验证码是" + svcode;


        return sendSMS.webcall(phone, msg);

    }

    @Transactional
    public Map<String, Object> registerMember(Map<String, Object> parameterMap) {
        ValueOperations<String, String> ops = template.opsForValue();
        String redisKey = ops.get(parameterMap.get("mobile"));
        Map<String, Object> map = new HashMap();


        //校验验证码是否正确 vcode或微信注册
        if (StringUtils.equals(redisKey, parameterMap.get("vcode").toString())) {
            //密码加密保存
            try {
                // 生成随机密码
                String encryptPwd = EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(this.getRandomString(6)));
                parameterMap.put("passwd", encryptPwd);
            } catch (NoSuchAlgorithmException e) {
                logger.error("随机密码加密失败！错误信息" + e);
                return ResultMap.errorResult("随机密码加密失败");
            }
            String clerkInvitationCode = null;
            String storeId = null;
            //根据site_id和store_user_id查询b_store_adminext 获取邀请码clerk_invitation_code
            if (parameterMap.get("site_id") != null && parameterMap.get("store_user_id") != null) {
                Map bStoreAdminExt = this.memberMapper.queryBStoreAdminExt(Integer.parseInt(parameterMap.get("site_id").toString()), Integer.parseInt(parameterMap.get("store_user_id").toString()));
                if (!StringUtil.isEmpty(bStoreAdminExt) && !StringUtil.isEmpty(bStoreAdminExt.get("clerk_invitation_code")) && !StringUtil.isEmpty(bStoreAdminExt.get("store_id"))) {
                    clerkInvitationCode = bStoreAdminExt.get("clerk_invitation_code").toString();
                    storeId = bStoreAdminExt.get("store_id").toString();
                    if (!clerkInvitationCode.contains("_")) {
                        clerkInvitationCode = storeId + "_" + clerkInvitationCode;
                    }
                    parameterMap.put("invite_code", clerkInvitationCode);
                }
            }

            try {

                //判断是否以是会员
                Integer num = memberMapper.queryMemberExist((String) parameterMap.get("mobile"), (Integer) parameterMap.get("site_id"));
                if (num != 0) {
                    return ResultMap.errorResult(parameterMap.get("mobile") + " ：已经是会员!");
                }

                //新增会员总表数据
                SYbMember ybMember = ybMemberMapper.selectByMobile((String) parameterMap.get("mobile"));
                if (StringUtil.isEmpty(ybMember)) {

                    memberMapper.saveYbMember(parameterMap);

                } else {

                    String usersarr = ybMember.getbUsersarr();
                    ybMember.setbUsersarr(usersarr + ";" + parameterMap.get("site_id"));
                    ybMemberMapper.updateByPrimaryKey(ybMember);
                    parameterMap.put("buyer_id", ybMember.getMemberId());
                }

                //新增会员信息

                Integer saveMem = this.memberMapper.saveMember(parameterMap);
                try {
                    logger.info("APP注册送券----------------------");
                    Member member = memberMapper.queryMember((String) parameterMap.get("mobile"), (Integer) parameterMap.get("site_id"));
                    couponSendService.sendCoupon(member.getSiteId(), member.getMemberId());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("发放注册券出错");
                }

                //校验info会员扩展信息表是否存在
                SBMemberInfo bMemberInfo = bMemberInfoMapper.getMemberInfo(Integer.valueOf(parameterMap.get("buyer_id").toString()), Integer.valueOf(parameterMap.get("site_id").toString()));

                if (bMemberInfo == null) {
                    //新增扩展表信息
                    Integer saveMemInfo = this.memberMapper.saveMemberInfoMap(parameterMap);
                }

                Integer buyer_id = StringUtil.isEmpty(ybMember) ? ((Long) parameterMap.get("buyer_id")).intValue() : ybMember.getMemberId();

                //注册送积分
                integral4Regist((Integer) parameterMap.get("site_id"), buyer_id);

                HashMap<String, Object> results = new HashMap<>();
                results.put("msg", "注册成功");
                results.put("buyerId", buyer_id);
                return ResultMap.successResult(results);
            } catch (Exception e) {

                logger.error("会员注册失败！错误信息" + e);
                return ResultMap.errorResult("会员注册失败");
            }

        }
        return ResultMap.errorResult("验证码错误");

    }

    //注册会员送积分
    private void integral4Regist(Integer site_id, Integer buyer_id) {

        Map<String, Object> pramMap = new HashMap();
        //siteId, buyerId, buyerNick
        pramMap.put("siteId", site_id);
        pramMap.put("buyerId", buyer_id);
        pramMap.put("buyerNick", "");
        Map result = integral4Regist.integralAddForRegister(pramMap);
        if (StringUtil.isEmpty(result) || !result.get("status").equals("success")) {
            logger.error("注册会员送积分失败");
        }
    }

    /**
     * 生成随机验证码
     *
     * @param from
     * @param to
     * @return
     */
    public int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

    /**
     * 生成随机长度的密码
     *
     * @param length 表示生成字符串的长度
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 校验手机号码格式
     * 校验手机号码是否已经被注册
     *
     * @param phone
     * @return
     */
    public ResultMap ValidatePhoneNum(String phone, Integer siteId) {
        if (StringUtil.isEmpty(phone)) {
            return ResultMap.errorResult("手机号码为空");
        }
        //校验手机号码是否正确
        if (!StringUtil.isMobileNO(phone)) {
            return ResultMap.errorResult("手机号码格式不正确");
        }
        // 判断手机号码是否已经被注册
        Integer num = this.memberMapper.queryMemberExist(phone, siteId);
        if (num != 0) {
            // 手机号码已经被注册
            return ResultMap.errorResult("手机号码已经被注册");
        }
        return ResultMap.successResult(new HashMap());
    }

    @Autowired
    private YbMerchantMapper ybMerchantMapper;

    public Map<String, Object> getAppCoupon(AuthToken authToken) {

        if (authToken == null || authToken.getStoreUserId() == 0 || authToken.getSiteId() == 0) {
            return ResultMap.errorResult("token参数有误");
        }

        StoreAdminExt storeAdminext = this.storeAdminextMapper.selectByPrimaryKey(authToken.getStoreUserId(), authToken.getSiteId());
        if (storeAdminext == null) {
            return ResultMap.errorResult("获取店员信息失败,请传递正确的店员ID!");
        }

        List<Map> list = new ArrayList<>();
        try {

            String managerId = String.valueOf(authToken.getStoreAdminId());

            List<Map<String, Object>> couponList = couponActivityService.couponCenter(authToken.getSiteId(), managerId);

            if (null == couponList) {
                return ResultMap.errorResult("无可用优惠券");
            }
            for (Map active : couponList) { //遍历活动list
                if (active.get("amount") == null || active.get("rule_id") == null || active.get("marked_words") == null || active.get("time_rule") == null) {
                    continue;
                }
                StringBuffer triggerCouponUrl = new StringBuffer();
                Map singleCoupon = new HashMap();

                Map<String, Object> timeRule = JacksonUtils.json2map(active.get("time_rule").toString());
                Integer validity_type = (Integer) timeRule.get("validity_type");
                //1绝对时间，2相对时间，3指定时间
                if (validity_type == 1) {
                    if (timeRule.get("startTime") == null || timeRule.get("endTime") == null) {
                        continue;
                    }
                    singleCoupon.put("timeRule", timeRule.get("startTime").toString() + "到" + timeRule.get("endTime").toString());
                } else if (validity_type == 2) {
                    if (timeRule.get("draw_day") == null || timeRule.get("how_day") == null) {
                        continue;
                    }
                    singleCoupon.put("timeRule", "领取" + timeRule.get("draw_day").toString() + "天后可以使用，在" + timeRule.get("how_day").toString() + "天内使用！");
                } else if (validity_type == 3) {
                    if (timeRule.get("assign_type").toString().equals("1")) {//指定时间的类型 1,按月份日期，2按星期
                        singleCoupon.put("timeRule", "每月" + timeRule.get("assign_rule").toString() + "均可以使用");
                    } else if (timeRule.get("assign_type").toString().equals("2")) {
                        singleCoupon.put("timeRule", "每星期" + timeRule.get("assign_rule").toString() + "均可以使用");
                    }
                }

                String rule_id = active.get("rule_id").toString();
                singleCoupon.put("triggerId", rule_id);//规则id
                singleCoupon.put("triggerCouponNum", active.get("amount").toString());//优惠券数量
                singleCoupon.put("triggerTitle", active.get("marked_words").toString());//前台显示语

                YbMerchant ybMerchant = ybMerchantMapper.getMerchant(authToken.getSiteId() + "");

                switch (authToken.getSiteId()) {//根据site_id和优惠券id生成不同的url
                    case 100001:
                        triggerCouponUrl.append("http://newwx.test.51jk.com/");
                    case 1001:
                        triggerCouponUrl.append("http://1001.pre.51jk.com/");
                    default:
                        /*if (ybMerchant.getShopwx_url() == null) {
                            triggerCouponUrl.append("http://" + authToken.getSiteId() + ".weixin-run.51jk.com/");
                        } else {*/
                            if (ybMerchant.getShopwx_url().indexOf("http://") != -1) {
                                triggerCouponUrl.append(ybMerchant.getShopwx_url() + "/");
                            } else {
                                triggerCouponUrl.append("http://" + ybMerchant.getShopwx_url() + "/");
                            }

                        /*}*/

                }
                triggerCouponUrl.append("new/sendCoupons?activityId="
                    + active.get("active_id") + "&siteId=" + authToken.getSiteId() +
                    "&managerId=" + managerId + "&storeId=" + storeAdminext.getStore_id() + "&ruleId=" + active.get("rule_id") + "&from=timeline");

                singleCoupon.put("triggerCouponUrl", triggerCouponUrl);
                list.add(singleCoupon);
            }

        } catch (Exception e) {
            logger.error("请求优惠券接口错误{}", e);
            return ResultMap.errorResult("请求优惠券接口错误");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        return ResultMap.successResult(map);
    }


    /**
     * 生成商家公众号二维码
     *
     * @param siteId 商家id
     * @return
     */
    public Map<String, Object> generateBarCode(Integer siteId) {

        Map<String, Object> map = null;
        Map<String, Object> result = null;
        String createBarcode = "";
        try {

            String access_token= wechatUtil.getAccessToken(siteId);
            createBarcode = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=".concat(access_token);

            Map param = new HashMap();
            param.put("action_name", "QR_SCENE");
            param.put("action_info", new HashMap<>().put("scene", new HashMap<>().put("scene_id", 600)));

            //获取ticket
            result = JacksonUtils.json2map(HttpClient.doHttpPost(createBarcode, JacksonUtils.mapToJson(param)));
            String ticket = (String) result.get("ticket");//提醒：TICKET记得进行UrlEncode
            map.put("grcode", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=".concat(URLEncoder.encode(ticket)));
            return ResultMap.successResult(map);
        } catch (Exception e) {
            logger.error("获取微信公众号失败,报错信息:{},map:{},createBarcode:{},result:{}", e, map, createBarcode, result);
            return ResultMap.errorResult("获取微信公众号失败");

        }

    }

    public Map<String, Object> getUsableCoupon(Map<String, Object> body) {
        //Map requestParams = new HashMap();

        String phone = (String) body.get("mobile");
        String orderType = (String) body.get("order_type");
        ArrayList<String> product_ids = (ArrayList) body.get("product_ids");
        ArrayList<String> product_nums = (ArrayList) body.get("product_nums");
        ArrayList<String> product_prices = (ArrayList) body.get("product_prices");
        String accessToken = (String) body.get("authToken");
        String order_amount = (String) body.get("order_amount");
        String area_id = (String) body.get("area_id");
        String postFee = (String) body.get("post_fee");
        postFee = StringUtils.isBlank(postFee) ? "0" : postFee;
        AuthToken authToken = usersService.parseAccessToken(accessToken);


        String goodsInfo = "";
        if (product_ids.size() != product_nums.size()) {
            return ResultMap.errorResult("参数错误");
        }
        List<Map> relist = new ArrayList<>();
        for (int i = 0; i < product_ids.size(); i++) {
            Map<Object, Object> goodInfo = new HashMap<>();
            goodInfo.put("goodsId", product_ids.get(i));
            goodInfo.put("num", product_nums.get(i));
            //根据商品id和siteId查询商品
//            Map good = this.goodsMapper.queryGoodsDetailByGoodId(Integer.parseInt(product_ids[i]), authToken.getSiteId());
            goodInfo.put("goodsPrice", StringUtils.substringBefore(String.valueOf(Double.valueOf(product_prices.get(i)) * 100), "."));// 商品价格
            relist.add(goodInfo);
        }
        try {
            goodsInfo = JacksonUtils.obj2json(relist);
        } catch (Exception e) {

            logger.error("商品信息序列化错误:{}", e);
            return ResultMap.errorResult("商品信息序列化错误");
        }

        HashMap memberInfoByPhoneNum = this.memberMapper.queryMemberInfoByPhoneNum(phone, authToken.getSiteId());

        String siteId = String.valueOf(authToken.getSiteId());
        String userId = String.valueOf((Long) memberInfoByPhoneNum.get("memberId"));
        String storeId = String.valueOf(authToken.getStoreId());
        String orderFee = StringUtils.substringBefore(order_amount.toString(), ".");
        String areaId = !StringUtil.isEmpty(area_id) ? area_id : null;


        List<Map> dataList = new ArrayList<>();//用于存放返回值
        try {

            ReturnDto returnDto = null;
            try {
                returnDto = couponDetailService.usableCouponList(siteId, userId, orderType, "101", storeId, orderFee, postFee, goodsInfo, areaId, null);

            } catch (Exception e) {
                logger.error("获取可用优惠券列表异常,报错信息:{}", e);
            }

            if (returnDto == null || returnDto.getCode() == null || !StringUtil.equals(returnDto.getCode(), "000")
                || returnDto.getValue() == null) {
                return ResultMap.errorResult("返回值错误");
            }
            //获取并解析数据
            Map<String, Object> value = (Map<String, Object>) returnDto.getValue();
            String usable_num = value.get("usable_num").toString();//可用优惠券数量
            String unusable_num = value.get("unusable_num").toString();//不可用优惠券数量
            List<Map> couponList = (List<Map>) value.get("data");//优惠券信息列表
            for (Map data : couponList) {
                Map singleResult = new HashMap();
//                Integer aim_at = (Integer) data.get("aim_at");//针对商品或者针对订单

//                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//                DateTime dateTime = formatter.parseDateTime(timeStamp2Date(StringUtils.removeEnd(data.get("create_time").toString(), "000"), "yyyy-MM-dd HH:mm:ss"));
                String marked_words = data.get("marked_words").toString();
                Integer id = (Integer) data.get("id");
                singleResult.put("userCouponId", id);//优惠券id  b_coupon_detail
                singleResult.put("couponLimitName", marked_words);//前台提示语

                if (data.get("usable").toString().equals("1")) {//优惠券是否可用
                    singleResult.put("isUse", 0);
                } else {
                    singleResult.put("isUse", 1);
                }

                String effectiveTime = data.get("effectiveTime").toString();//剩余有效时间
                singleResult.put("effectiveTime", effectiveTime);

                CouponView couponView = (CouponView) data.get("couponView");
                if (couponView.getIsAllType() == 0) {
                    singleResult.put("couponLimitType", 0);//全品
                } else {
                    singleResult.put("couponLimitType", 1);//限品
                }

                Integer coupon_type = (Integer) data.get("coupon_type");//优惠券规则类型 100现金券 200打折券 300 现价券 400包邮券
                singleResult.put("couponType", coupon_type);
                if (coupon_type == 100) {//现金
                    singleResult.put("userCouponAmount", couponView.getMaxMoney());
                } else if (coupon_type == 200) {//打折
                    singleResult.put("userCouponAmount", couponView.getMaxDiscount());
                } else if (coupon_type == 300) {//限价
                    singleResult.put("userCouponAmount", couponView.getMaxMoney());
                } else if (coupon_type == 400) {//包邮

                }

                dataList.add(singleResult);
            }
            Map results = new HashMap();
            results.put("list", dataList);
            return ResultMap.successResult(results);
        } catch (Exception e) {
            logger.error("", e);
            return ResultMap.errorResult("获取优惠券失败");
        }
    }


    /**
     * 获取店员优惠券
     *
     * @param siteId
     * @param managerId
     * @return
     */
    public List<Map<String, Object>> getCustomerCoupon(Integer siteId, Integer storeUserId, String managerId) {


        YbMerchant ybMerchant = ybMerchantMapper.getMerchant(siteId + "");
        StoreAdminExt storeAdminext = this.storeAdminextMapper.selectByPrimaryKey(storeUserId, siteId);
        List<Map<String, Object>> couponList = couponActivityService.getClerkUsableCoupons(siteId, managerId);
        if (null == couponList || couponList.isEmpty()) {
            return null;
        }
        couponList.stream().forEach(a -> {
            StringBuffer sbf = new StringBuffer();
            switch (siteId) {//根据site_id和优惠券id生成不同的url
                case 100001:
                    sbf.append("http://newwx.test.51jk.com/");
                case 1001:
                    sbf.append("http://1001.pre.51jk.com/");
                default:
                    /*if (ybMerchant.getShopwx_url() == null || "".equals(ybMerchant.getShopwx_url())) {
                        sbf.append("http://" + siteId + ".weixin-run.51jk.com/");
                    } else {*/
                        String url = ybMerchant.getShopwx_url();
                        if (url.contains(",")) {
                            url = url.split(",")[0];
                        }
                        if (url.indexOf("http://") != -1) {
                            sbf.append(url + "/");
                        } else {
                            sbf.append("http://" + url + "/");
                        }

                    /*}*/
                    sbf.append("new/sendCoupons?activityId="
                        + a.get("active_id") + "&siteId=" + +siteId + "&managerId=" + managerId
                        + "&storeId=" + storeAdminext.getStore_id() + "&ruleId=" + a.get("rule_id") + "&from=singlemessage");
            }
            a.put("couponUrl", sbf);
        });
        return couponList;
    }

    /**
     * 派券提醒
     *
     * @param siteId
     * @param storeAdminId
     * @param storeId
     * @return
     * @throws Exception
     */
    public String notifySendCoupons(Integer siteId, Integer storeAdminId, Integer storeId) throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", PushType.NOTIFY_SEND_COUPON.getValue());
        messageMap.put("siteId", String.valueOf(siteId));
        messageMap.put("storeAdminId", String.valueOf(storeAdminId));
        messageMap.put("storeId", String.valueOf(storeId));
        messageMap.put("password", "");
        delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), PushType.NOTIFY_SEND_COUPON.getValue(), null, null);
        return "";
    }

    /**
     * 推送任务消息的接口
     *
     * @param siteId
     * @param storeAdminId
     * @param storeId
     * @return
     * @throws Exception
     */
    public String notifyTaskMessage(Integer siteId, Integer storeAdminId, Integer storeId, PushType pushType, String taskName) throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", pushType.getValue());
        messageMap.put("siteId", String.valueOf(siteId));
        messageMap.put("storeAdminId", storeAdminId);
        if (StringUtil.isNotEmpty(taskName) || !"null".equalsIgnoreCase(taskName)) {
            messageMap.put("taskName", taskName);
        } else {
            messageMap.put("taskName", "");
        }
        if (storeId == null) {
            //根据店员id查询门店id
            Map<String, Object> map = storeAdminextMapper.queryStoreId(siteId, storeAdminId);
//            String storeId1 = String.valueOf(map.get("storeId"));
            messageMap.put("storeId", String.valueOf(map.get("storeId")));
        } else {
            messageMap.put("storeId", storeId);
        }

        //如果是完成任务先更新数据库延迟时间
        if ("task_finishTask".equals(pushType.getValue())) {
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            if (hour > 8 && hour < 18) {    //符合条件,startTime为当前时间
//                Calendar instance = Calendar.getInstance();
//                instance.add(Calendar.MINUTE,2);
                delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), pushType.getValue(), null, null);
                return "";
            } else {
                LocalDateTime nextNotifyTime = now.withHour(9);
//            long seconds = 0;
                if (nextNotifyTime.compareTo(now) < 0) {   //大于今天6点第二天九点发
                    nextNotifyTime = nextNotifyTime.plusDays(1L);
//                final Duration between = Duration.between(nextNotifyTime, now);
//                seconds = between.getSeconds();
                }
                ZoneId zone = ZoneId.systemDefault();//系统默认时区
                Instant instant = nextNotifyTime.atZone(zone).toInstant();
                Date date = Date.from(instant); //开始时间

                //{"delaySeconds":60,"num":5}
//            StringBuffer sb = new StringBuffer();
//            sb.append("{'delaySeconds':");
//            sb.append(seconds); //这个任意
//            sb.append(",'num':1");
//            sb.append("}");
                //更新数据库延迟时间
//            bMessageSettingMapper.updateDelayTime(sb.toString(),PushType.settingId,pushType.getValue());
                delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), pushType.getValue(), date, null);
            }

        } else {
            delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), pushType.getValue(), null, null);
        }
        return "";
    }


    public void notifyVisitMessage(List<BVisitMessage> list, Integer siteId, Integer storeAdminId, Integer storeId, PushType pushType, String taskName) throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", pushType.getValue());
        messageMap.put("siteId", String.valueOf(siteId));
        messageMap.put("storeAdminId", storeAdminId);
        messageMap.put("storeId", storeId);
        messageMap.put("taskName", taskName);
        messageMap.put("BVisitMessage", list);
        messageMap.put("taskNums", list.size());
//        messageMap.put("buyerId", bClerkVisit.getBuyerId());
//        messageMap.put("goodsId", bClerkVisit.getGoodsIds());
//        messageMap.put("visitId", bClerkVisit.getId());
//        messageMap.put("activityId ", bClerkVisit.getActivityIds());

        delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), pushType.getValue(), null, null);

    }

    public Result memberFirst(Integer siteId, String mobile) {
        Result result = null;
        if (!StringUtil.isMobileNO(mobile)) return Result.fail("手机号格式不正确");
        MemberInfo memberInfo = null;
        try {
            memberInfo = memberMapper.getMemberByMobile(siteId, mobile);
            result = Result.success(memberInfo);
        } catch (Exception e) {
            result = Result.fail("查询异常");
        }
        return result;
    }

    public String getMobileByTradesId(Integer siteId, String tradesId) {
        return memberMapper.getMobileByTradesId(siteId, tradesId);
    }
}
