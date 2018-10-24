package com.jk51.modules.offline.service;

import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.configuration.PathUrlConfig;
import com.jk51.model.coupon.CouponRule;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-09-21
 * 修改记录:
 */
@Component
@RunMsgWorker(queueName = "crashCouponInfo99")
public class CouponErpService implements MessageWorker {

    private static final Logger logger = LoggerFactory.getLogger(CouponErpService.class);

    public static final String topicName = "crashCouponInfo99";


    @Autowired
    private CouponRuleMapper ruleMapper;

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private StoresMapper storesMapper;

    @Value("${erp.is_open}")
    private String is_Open;
    @Autowired
    private PathUrlConfig pathUrlConfig;

    @Autowired
    private MerchantERPMapper merchantERPMapper;


    @Override
    public void consume(Message message) throws Exception {
        String messageBodyAsString = message.getMessageBodyAsString();
        logger.info("开始处理优惠券推送信息!, {}", message);
        Map<String, Object> couponObj = JacksonUtils.json2map(messageBodyAsString);
        logger.info("优惠券信息:{}", couponObj.toString());
        if (Integer.parseInt(couponObj.get("siteId").toString()) == 100166) {
            sendCouponToErp_jiuzhou(couponObj);
        }
    }

    /**
     * 九洲优惠券信息推送
     *
     * @param couponObj
     * @return
     */
    public void sendCouponToErp_jiuzhou(Map<String, Object> couponObj) throws Exception {
        if (is_Open.equals("false")) {
            return;
        }
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(100166);
        if (!erpMap.containsKey("erpUrl")) {
            return;
        }
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        logger.info("推送的优惠券信息" + couponObj.toString());
        Map<String, Object> requestParams = new HashMap<>();//返回值参数
        Integer ruleId = Integer.parseInt(couponObj.get("ruleId").toString());//优惠券规则id
        CouponRule couponRule = ruleMapper.findCouponRuleById(ruleId, 100166);
        String mobile = couponObj.get("mobile").toString();//会员手机号码
        String couponNo = couponObj.get("couponNo").toString();//优惠券编码
        Map<String, Object> timeRule = JacksonUtils.json2map(couponRule.getTimeRule());//查询满减规则和商品规则
        Integer validity_type = Integer.parseInt(timeRule.get("validity_type").toString());
        String startTime = "";
        String endTime = "";
        if (validity_type == 1) {//绝对时间
            startTime = couponObj.get("startTime").toString() + " 00:00:00";//优惠券使用开始时间
            endTime = couponObj.get("endTime").toString() + " 23:59:59";//优惠券使用结束时间
        } else {
            startTime = couponObj.get("startTime").toString();//优惠券使用开始时间
            endTime = couponObj.get("endTime").toString();//优惠券使用结束时间
        }
        if (StringUtil.isEmpty(couponRule)) {
            logger.info("该优惠券不存在，无法进行推送。信息:{}。", couponObj.toString());
        } else {
            String status = couponObj.get("status").toString();
            String s = status.split(",")[1];
            if (s.equals("0")) {//该优惠券被领取了
                try {
                    Map<String, Object> goodsRule = JacksonUtils.json2map(couponRule.getGoodsRule());//查询满减规则和商品规则
                    Integer type = Integer.parseInt(goodsRule.get("type").toString());// 0全部 1指定类目 2指定商品 3指定商品不参加
                    String goodsIds = goodsRule.get("promotion_goods").toString();
                    String gCodes = "";//对应的商品编码，多个商品编码用逗号隔开
                    if (!goodsIds.equals("all")) {
                        gCodes = goodsMapper.selectGcodefromGids(100166, goodsIds);
                    }
                    //价格优惠
                    Integer rule_type = Integer.parseInt(goodsRule.get("rule_type").toString());// 0每满多少减 1满多少元减/折多少 2满多少件减/折多少 3每个商品限价多少元  4立减多少钱/折 5第二件半价（打折券）
                    Object rule = goodsRule.get("rule");
                    //门店
                    Map<String, Object> limitRule = JacksonUtils.json2map(couponRule.getLimitRule());
                    Integer apply_store = Integer.parseInt(limitRule.get("apply_store").toString());//-1全部门店 1具体门店
                    String storenumbers = "";//门店编码，多个用逗号隔开
                    if (apply_store == 1) {//指定服务门店
                        storenumbers = storesMapper.selectStoreNumbers(100166, limitRule.get("use_stores").toString());
                    }
                    String orderType = limitRule.get("order_type").toString();//订单类型 100自提订单 200送货上门 300门店直购 400预购订单 多选用逗号隔开
                    if (orderType.contains("100")) {//自提订单推送
                        requestParams.put("status", couponObj.get("status"));
                        requestParams.put("mobile", mobile);//会员手机号码
                        requestParams.put("couponNo", couponNo);//优惠券编码
                        requestParams.put("startTime", startTime);//优惠券使用开始时间
                        requestParams.put("endTime", endTime);//优惠券使用结束时间
                        requestParams.put("applyStores", storenumbers);//适用的门店编码
                        requestParams.put("gCodesType", type);//商品指定类目0全部 1指定类目 2指定商品 3指定商品不参加
                        requestParams.put("applyGcodes", gCodes);
                        Map<String, Object> full = JacksonUtils.json2map(JacksonUtils.obj2json(rule));
                        if (rule_type == 0) {//每满减
                            requestParams.put("rule_type", "0");
                            requestParams.put("each_full_money", full.get("each_full_money"));
                            requestParams.put("reduce_price", full.get("reduce_price"));
                            requestParams.put("max_reduce", full.get("max_reduce"));
                        } else if (rule_type == 4) {//立减
                            requestParams.put("rule_type", "4");
                            requestParams.put("each_full_money", 0);
                            requestParams.put("reduce_price", full.get("direct_money"));
                        }
                        logger.info("九洲推送优惠券信息:{}", requestParams.toString());
                        String url = baseUrl + "/coupon/pushCoupon";
                        String JSONObject = JacksonUtils.mapToJson(requestParams);
                        logger.info("九洲推送优惠券信息:{},请求路径:{}.", JSONObject, url);
                        for (int i = 1; i <= 3; i++) {
                            Map<String, Object> response = JacksonUtils.json2map(OkHttpUtil.postJson(url, JSONObject));
                            logger.info("九洲推送优惠券领取使用信息返回值:{}.", response.toString());
                            if (Integer.parseInt(response.get("code").toString()) == 1) {
                                logger.info("九洲推送优惠券，券号{},第{}次，成功", couponNo, i);
                                break;
                            } else {
                                logger.info("九洲推送优惠券,券号{},第{}次，失败", couponNo, i);
                                continue;
                            }
                        }
                        logger.info("会员领取优惠券推送完毕");
                    }
                } catch (Exception e) {
                    logger.info("类型转换异常" + e);
                }
            } else if (s.equals("1")) {//优惠券被退还，可重新使用
                requestParams.put("couponNo", couponNo);
                requestParams.put("status", 0);
                logger.info("九洲推送优惠券使用状态:{}", requestParams.toString());
                String JSONObject = JacksonUtils.mapToJson(requestParams);
                String url = baseUrl + "/coupon/pushState?couponNo";
                logger.info("九洲推送优惠券信息:{},请求路径:{}.", JSONObject, url);
                try {
                    for (int i = 1; i <= 3; i++) {
                        Map<String, Object> response = JacksonUtils.json2map(OkHttpUtil.postJson(url, JSONObject));
                        logger.info("九洲推送优惠券使用状态:{}.", response.toString());
                        if (Integer.parseInt(response.get("code").toString()) == 1) {
                            logger.info("九洲推送优惠券退还，券号{},第{}次，成功", couponNo, i);
                            break;
                        } else {
                            logger.info("九洲推送优惠券退还,券号{},第{}次，失败", couponNo, i);
                            continue;
                        }
                    }
                } catch (Exception e) {
                    logger.info("九洲推送优惠券异常:{}.", e);
                }

            } else if (s.equals("-1")) {//该优惠券被使用了
                requestParams.put("couponNo", couponNo);
                requestParams.put("status", 1);
                logger.info("九洲推送优惠券使用状态:{}", requestParams.toString());
                String JSONObject = JacksonUtils.mapToJson(requestParams);
                String url = baseUrl + "/coupon/pushState";
                logger.info("九洲推送优惠券信息:{},请求路径:{}.", JSONObject, url);
                try {
                    for (int i = 1; i <= 3; i++) {
                        Map<String, Object> response = JacksonUtils.json2map(OkHttpUtil.postJson(url, JSONObject));
                        logger.info("九洲推送优惠券使用状态:{}.", response.toString());
                        if (Integer.parseInt(response.get("code").toString()) == 1) {
                            logger.info("九洲推送优惠券，券号{},第{}次，成功", couponNo, i);
                            break;
                        } else {
                            logger.info("九洲推送优惠券,券号{},第{}次，失败", couponNo, i);
                            continue;
                        }
                    }

                } catch (Exception e) {
                    logger.info("九洲推送优惠券异常:{}.", e);
                }
            }

        }
    }

}
