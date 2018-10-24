package com.jk51.modules.offline.service;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.configuration.PathUrlConfig;
import com.jk51.model.erp.TRGoods;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.mapper.BMemberInfoMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.integral.mapper.IntegralRuleMapper;
import com.jk51.modules.integral.mapper.OffIntegralLogMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-06
 * 修改记录:
 */
@Service
public class TianROfflineService {
    private static final Logger log = LoggerFactory.getLogger(TianROfflineService.class);
    @Autowired
    private BMemberMapper memberMapper;
    @Autowired
    private BMemberInfoMapper memberInfoMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private MerchantERPMapper merchantERPMapper;


/*    *//**
     * 获取会员信息
     *
     * @return
     *//*
    public Map<String, Object> getUser(Integer siteId, String mobile) {
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            return null;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        Map<String, Object> memberParams = new HashMap<>();
        Integer code = 0;
        String msg = "";
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);
        log.info("##获取会员信息##手机号:[{}]", mobile);
        String url = baseUrl + "/getinfo/" + mobile + "/0";
        Map<String, Object> result = httpResponse(url);
        if ((Integer) result.get("code") == -1) {//判断是否正常获取数据
            return result;
        }
        memberParams.put("code", (Integer) result.get("code"));
        memberParams.put("msg", result.get("msg"));
        Map<String, Object> memberInfo = new HashMap<>();
        Map map = (Map) ((List) result.get("info")).get(0);
        //更新b_member 表信息
        Map memberMap = new HashMap();
        memberInfo.put("name", map.get("huiyname"));
        memberMap.put("name", map.get("huiyname"));
        if (!map.get("sex").equals("") && map.get("sex") != null) {
            Integer sex = map.get("sex").equals("男") ? 1 : 0;
            memberMap.put("sex", sex);
        } else {
            memberMap.put("sex", 3);
        }
        memberInfo.put("sex", map.get("sex"));
        memberMap.put("email", map.get("email") == null ? "" : map.get("email"));
        memberMap.put("site_id", siteId);
        memberMap.put("mobile", mobile);
        memberInfo.put("mobile", map.get("lxdh"));
        int i = memberMapper.updateMember(memberMap);
        if (i != 0) {//更新b_member_info表信息
            Map memberInfoMap = new HashMap();
            Map<String, Object> areaIds = erpToolsService.getareaIds(map.get("address").toString());
            memberInfoMap.put("province", areaIds.get("province"));
            memberInfoMap.put("city", areaIds.get("city"));
            memberInfoMap.put("area", areaIds.get("area"));
            memberInfoMap.put("address", areaIds.get("address"));
            memberInfo.put("address", map.get("address"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sdf.parse(map.get("csrq").toString());
                memberInfoMap.put("birthday", date);
                memberInfo.put("birthday", map.get("csrq").toString());
            } catch (ParseException e) {
                memberInfoMap.put("birthday", "1970-01-01");
                ((Map) ((List) result.get("info")).get(0)).put("csrq", "1970-01-01");
                memberInfo.put("birthday", "1970-01-01");
            }
            memberInfoMap.put("card_no", map.get("huiybh"));
            memberInfo.put("card_no", map.get("huiybh"));
            memberInfoMap.put("site_id", siteId);
            memberInfoMap.put("member_id", member.get("buyer_id"));
            memberParams.put("info", memberInfo);
            int j = memberInfoMapper.updateMemberInfo(memberInfoMap);
            if (j != 0) {
                memberMapper.updateFirstErp(siteId, mobile);
                return memberParams;
            } else {
                code = 2;
                msg = "会员信息更新失败";
            }
        } else {//51后台没有会员信息
            code = -1;
            msg = "会员信息更新失败";
        }
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }*/

/*    *//**
     * 更新会员信息
     *
     * @return
     *//*
    public Map<String, Object> updateinfo(Integer siteId, String mobile, String name, String sex, String birthday, String address) {
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            return null;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        try {
            address = address.replace("|", "%7C");
            log.info("处理后地址地址为" + address);
        } catch (Exception e) {
            log.info("处理地址中的特殊字符失误" + e);
        }
        String url = baseUrl + "/updateinfo/" + mobile + "/" + name + "/" + sex + "/" + birthday + "/" + address + "";
        Map<String, Object> result = httpResponse(url);
        if ((Integer) result.get("code") == 0) {//修改先下信息操作成功，将信息保存到线上
            return getUser(siteId, mobile);//调用获取会员方法
        }
        return result;
    }*/

//    /**
//     * 获取积分
//     * 积分字段为整数，但是天润积分为小数，不一致，不好保存
//     *
//     * @return
//     */
//    public Map<String, Object> getIntegralByMobile(String mobile) {
//        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
//        if (StringUtil.isEmpty(merchantErpInfo)) {
//            return null;
//        }
//        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
//        String url = baseUrl + "/getintegral/" + mobile + "";
//        Map<String, Object> res = new HashMap<>();
//        res.put("mobile", mobile);
//        res.put("desc", "同步积分");
//        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(100190), mobile);//获取会员信息
//        res.put("on_integral", member.get("integrate"));
//        try {
//            String param = URLEncoder.encode(JacksonUtils.mapToJson(res), "UTF-8");//推送线上总积分到商户
//            String erpUrl = baseUrl + "/changeIntegral/" + param;
//            log.info("推送线上积分变化" + erpUrl);
//            httpResponse(erpUrl);
//        } catch (Exception e) {
//            log.info("线上积分推送异常" + e);
//        }
//        Map<String, Object> result = httpResponse(url);
//        return result;
//    }

    /**
     * 获取线下积分消费明细
     *
     * @param mobile
     * @return
     */
    public Map<String, Object> getScoreList(String mobile, Integer pageNum, Integer pageSize) {
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            return null;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        String url = baseUrl + "/integralDetails/" + mobile;
        Map<String, Object> result = httpResponse(url);
        return result;
    }

//    /**
//     * 记录线上会员的积分使用情况
//     **/
//    public Map<String, Object> recordIntegerChange(Integer siteId, Map<String, Object> requestParams) {
//        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
//        if (StringUtil.isEmpty(merchantErpInfo)) {
//            return null;
//        }
//        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
//        Map<String, Object> requestParam = new HashMap<>();
//        Map<String, Object> response = new HashMap<>();
//        log.info("推送积分使用情况:站点：{},详情：{}", siteId, requestParams.toString());
//        try {
//            String mobile = requestParams.get("mobile").toString();//会员手机号码
//            Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);//获取会员信息
//            if (StringUtil.isEmpty(member)) {
//                response.put("code", 1);
//                response.put("msg", "该会员不存在");
//                return response;
//            }
//            requestParam.put("mobile", mobile);
//            String tradesId = "";//订单号
//            String goodsNo = "";//积分购买商品的商品编码
//            if (!StringUtil.isEmpty(requestParams.get("tradesId"))) {
//                tradesId = requestParams.get("tradesId").toString();
//                if (!StringUtil.isEmpty(requestParams.get("goodsNo"))) {
//                    goodsNo = requestParams.get("goodsNo").toString();
//                } else {
//                    goodsNo = erpToolsService.getGoodsCodebyTradesId(siteId, Long.valueOf(tradesId));
//                }
//            }
//            requestParam.put("tradesId", tradesId);
//            requestParam.put("goods_num", goodsNo);
//            Map<String, Object> integralScore = getIntegralByMobile(mobile);//获取用户线下积分总数
//            Double offIntegral = 0d;
//            if ("0".equals(integralScore.get("code").toString())) {
//                offIntegral = Double.valueOf(((Map) ((List) integralScore.get("info")).get(0)).get("gold_score").toString());//获取线下积分
//            }
//            Integer type = Integer.parseInt(requestParams.get("type").toString());//状态码,1:线上消费，0:线下消费
//            String totalScore = requestParams.get("sumScore").toString();//此次变化总积分（线上+线下）
//            requestParam.put("sumScore", totalScore);
//            String on_costScore = "0";
//            if (!StringUtil.isEmpty(requestParams.get("on_costScore"))) {
//                on_costScore = requestParams.get("on_costScore").toString();//线上变化积分，如果是兑换积分，积分为负，如果是赠送积分，积分为正
//            }
//            requestParam.put("on_costScore", on_costScore);
//            String off_costScore = "0";
//            if (!StringUtil.isEmpty(requestParams.get("off_costScore"))) {
//                off_costScore = requestParams.get("off_costScore").toString();//线下变化积分，如果是兑换积分，积分为负，如果是赠送积分，积分为正
//            }
//            requestParam.put("off_costScore", off_costScore);
//            String on_integral = "0";
//            if (!StringUtil.isEmpty(requestParams.get("on_integral"))) {
//                on_integral = requestParams.get("on_integral").toString();//剩余线上积分
//            } else {
//                on_integral = member.get("integrate").toString();
//            }
//            requestParam.put("on_integral", on_integral);
//            String off_integral = "0";
//            if (!StringUtil.isEmpty(requestParams.get("off_integral"))) {
//                off_integral = requestParams.get("off_integral").toString();//剩余线下积分
//            } else {
//                off_integral = String.valueOf(offIntegral);
//            }
//            requestParam.put("off_integral", off_integral);
//            String create_time = requestParams.get("create_time").toString();//创建时间
//            requestParam.put("create_time", create_time);
//            String desc = requestParams.get("desc").toString();//描述，格式固定
//            requestParam.put("desc", desc);
//            if (type == 1) {//线上消费，记录此次信息，将信息传递到线下,不管线上消费有没有用到线下积分，都要把积分传过去(包括赠送和消费)
//                if (desc.contains("兑换积分")) {//会消耗线上积分，也可能会消耗线下积分
//                    if (!off_costScore.equals("0")) {//线上消费使用到线下积分
//                        off_integral = String.valueOf(offIntegral + Double.parseDouble(off_costScore));//计算线下剩余积分
//                        requestParam.put("off_integral", off_integral);
//                        log.info("线上消费，积分兑换，使用线下积分,使用线上积分{}.线上积分剩余:{}.使用线下积分{},线下积分剩余{},创建时间:{},描述:{}。",
//                            on_costScore, on_integral, off_costScore, off_integral, create_time, desc);
//                        offIntegralLogMapper.insertSelect(siteId, mobile, tradesId, totalScore, off_costScore, on_costScore, off_integral, on_integral, create_time, desc);//记录此次消费记录
//                    } else {//线上消费没有使用线下积分
//                        log.info("线上消费，积分兑换，没有使用线下积分,使用线上积分{}.线上积分剩余:{}.使用线下积分{},线下积分剩余{},创建时间:{},描述:{}。",
//                            on_costScore, on_integral, off_costScore, off_integral, create_time, desc);
//                    }
//                } else {//线上积分增加,退款，线下积分也会增加
//                    log.info("线上消费，增加线上积分:{},增加线下积分:{},线上积分总额:{} ", on_costScore, off_costScore, on_integral);
//                }
//                log.info("变化积分{}", requestParam);
//                String param = URLEncoder.encode(JacksonUtils.mapToJson(requestParam), "UTF-8");//推送积分变化到商户后台
//                String url = baseUrl + "/changeIntegral/" + param;
//                log.info("推送线上积分变化" + url);
//                return httpResponse(url);
//            } else {//线下消费，保存到本地,线下积分增加或者减少，或者兑换线上积分
//                log.info("线下消费，同步积分,站点：{}，参数：{}", siteId, requestParam.toString());
//                try {
//                    //记录此次消费记录
//                    offIntegralLogMapper.insertSelect(siteId, mobile, tradesId, totalScore, off_costScore, on_costScore, off_integral, on_integral, create_time, desc);
//                } catch (Exception e) {
//                    log.info("线下订单插入失败==off==recordIntegerChange" + e);
//                    response.put("code", -1);
//                    response.put("msg", "订单号已存在");
//                    return response;
//                }
//                //处理此次线下消费的线上积分和线下积分
//                if (desc.contains("兑换积分")) {
//                    if (!on_costScore.equals("0")) {//消耗线上积分
//                        log.info("线下消费，兑换线上积分，兑换线上积分{},线上剩余积分{},消耗线下积分{},线下剩余积分{},描述{}，创建时间{}。",
//                            on_costScore, on_integral, off_costScore, off_integral, desc, create_time);
//                        Map<String, Object> intelog = new HashMap<>();//保存到线上积分记录列表使用
//                        intelog.put("siteId", siteId);
//                        intelog.put("buyerId", member.get("buyer_id"));
//                        intelog.put("buyerNick", member.get("buyer_nick"));
//                        intelog.put("integralDesc", desc);//说明
//                        intelog.put("integralAdd", 0);//获得积分数
//                        intelog.put("integralDiff", on_costScore);//使用积分数（线上积分兑换线下积分）
//                        intelog.put("mark", "线下消费" + desc);//标识符串，需要拓展
//                        intelog.put("type", CommonConstant.TYPE_ORDER_CHANGE);//规则类型
//                        Double integrate = Double.valueOf(member.get("integrate").toString());//线上积分
//                        Map<String, Object> memberInte = new HashMap<>();//保存到会员信息列表使用
//                        memberInte.put("siteId", siteId);
//                        memberInte.put("buyerId", member.get("buyer_id"));
//                        memberInte.put("buyerNick", member.get("buyer_nick"));
//                        if (integrate == Double.valueOf(on_integral) - Double.valueOf(on_costScore)) {//51后台统计的线上积分和商户后台统计的线上积分保持一致，
//                            log.info("线下消费线上积分数量：{},积分保持一致,剩余线上积分{},变动前51后台线上积分{}", on_costScore, on_integral, integrate);
//                            memberInte.put("integrate", on_integral);
//                            intelog.put("integralOverplus", on_integral);//线上剩余积分，需要进行比对
//                        } else {//积分不一致，做出记录
//                            log.info("线下消费线上积分数量：{},积分不一致,剩余线上积分{},变动前51后台线上积分{}", on_costScore, on_integral, integrate);
//                            Double curOnScore = integrate + Double.valueOf(on_costScore);
//                            memberInte.put("integrate", curOnScore);
//                            intelog.put("integralOverplus", curOnScore);//线上剩余积分，需要进行比对
//                        }
//                        integralRuleMapper.insertIntegralLog(intelog); //将线下消费的线上积分放到Integrallog表中
//                        integralRuleMapper.updateMemberIntegral(memberInte);//修改用户的线上积分总数
//                    } else {//不消耗线上积分
//                        log.info("线下消费，不兑换兑换线上积分，兑换线上积分{},线上剩余积分{},消耗线下积分{},线下剩余积分{},描述{}，创建时间{}。",
//                            on_costScore, on_integral, off_costScore, off_integral, desc, create_time);
//                    }
//                }
//                //统计线下消费的线下积分部分
//             /*   Map<String, Object> memberOffInte = new HashMap<>();//更改会员的线下积分使用
//                memberOffInte.put("siteId", siteId);
//                memberOffInte.put("mobile", mobile);
//                memberOffInte.put("olIntegral", offIntegral);//修改51后台用户线下积分(线下积分为小数，数据库字段为整型，可能会报错)
//                memberMapper.updateOfflineIntegral(memberOffInte);
//                if (!StringUtil.isEmpty(offDesc)) {
//                    offIntegralLogMapper.insertSelect(siteId, mobile, tradesId, totalScore, off_costScore, on_costScore,
//                            off_integral, on_integral, create_time, offDesc);//积分异常记录
//                }*/
//                response.put("code", 0);
//                response.put("msg", "请求成功");
//                return response;
//            }
//        } catch (Exception e) {
//            log.info("积分消费异常" + e);
//            response.put("code", -1);
//            response.put("msg", "请求异常");
//            return response;
//        }
//    }

    /**
     * http请求
     *
     * @param url
     * @return
     */
    public Map<String, Object> httpResponse(String url) {
        Map<String, Object> response = new HashMap<>();
        try {
            String responseText = OkHttpUtil.get(url);
            response = JacksonUtils.json2map(responseText.toString());
            return response;
        } catch (Exception e) {
            log.info("http请求异常=====" + e);
            response.put("code", -1);
            response.put("message", "通讯异常，请稍后重试。");
            return response;
        }
    }

//    /**
//     * 获取线下使用线上的积分情况,并将值转移至线上
//     *
//     * @return
//     */
//    @Transactional
//    public Map<String, Object> getOnFromOff(String mobile) {
//        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
//        if (StringUtil.isEmpty(merchantErpInfo)) {
//            return null;
//        }
//        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
//        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(100190), mobile);//获取会员信息
//        if (StringUtil.isEmpty(member)) {
//            return null;
//        }
//        //获取线下使用线上的积分列表
//        String url = baseUrl + "/getuseonintegral/" + mobile;
//        log.info("天润商家线下使用线上积分:" + url);
//        Map<String, Object> result = httpResponse(url);
//        log.info("天润商家线下使用线上积分使用情况" + result.toString());
//        if ((Integer) result.get("code") == 0) {
//            List integralUsed = (List) (result.get("info"));//积分情况转换为list数组
//            Integer score = 0;
//            for (Object m : integralUsed) {
//                Map<String, Object> integral = (Map<String, Object>) m;
//                try {
//                    integral.put("siteId", 100190);
//                    offIntegralLogMapper.insertOffIntegral(integral);//保存到线下积分记录中
//                    integral.put("on_costScore", new BigInteger(String.valueOf(-Float.valueOf(integral.get("on_costScore").toString()).intValue())));
//                    integral.put("type", 100);
//                    integral.put("buyerId", member.get("buyer_id"));
//                    integral.put("mobile", member.get("mobile"));
//                    integral.put("mark", "积分兑换，订单号:" + integral.get("tradesId"));
//                    integral.put("desc", integral.get("desc"));
//                    integral.put("on_integral", integral.get("on_integral"));//线上剩余积分
//                    integralRuleMapper.insertlogList(integral);//线下消费线上积分记录保存到积分消费记录中
//                    score += Integer.valueOf(integral.get("on_costScore").toString());
//                } catch (NumberFormatException e) {
//                    log.info("数字类型转换失败" + e);
//                } catch (Exception e) {
//                    log.info("订单插入异常" + e);
//                }
//            }
//            Map<String, Object> memberIntegral = new HashMap<>();
//            memberIntegral.put("integrate", Integer.parseInt(member.get("integrate").toString()) - score);
//            //使用线上积分替换线下积分数量
//            memberIntegral.put("total_consume_integrate", Integer.parseInt(member.get("total_consume_integrate").toString()) + score);
//            memberIntegral.put("siteId", 100190);
//            memberIntegral.put("buyerId", member.get("buyer_id"));
//            integralRuleMapper.updateMemberIntegralbyOffChange(memberIntegral);//修改用户线上积分
//        } else {
//            return null;
//        }
//        return null;
//    }

  /*  *//**
     * 从erp获取会员积分消费信息
     *
     * @param siteId
     * @param integralList
     * @return
     *//*
    public Map<String, Object> getIntegralfromERP(Integer siteId, List<Map<String, Object>> integralList) {
        log.info("天润会员积分消费信息siteId{},积分信息{}", siteId, integralList.toString());
        Map<String, Object> requestParam = new HashMap<>();
        try {
            String tradesId = "";
            for (Object m : integralList) {
                Map<String, Object> integral = (Map<String, Object>) m;
                Integer result = this.updateMemberIntegrate(siteId, integral);
                if (result == 0) {
                    log.info("本次订单记录失败,getIntegralfromERP,订单信息{}", integral.get("tradesId"));
                    continue;
                } else {
                    tradesId += integral.get("tradesId") + ",";
                    log.info("本次订单记录成功,getIntegralfromERP,订单信息{}", integral.get("tradesId"));
                    continue;
                }
            }
            requestParam.put("code", 0);
            requestParam.put("msg", "更新成功");
            requestParam.put("info", tradesId);
            return requestParam;
        } catch (Exception e) {
            log.info("天润修改用户线上积分信息getIntegralfromERP:" + e);
            requestParam.put("code", -1);
            requestParam.put("msg", "用户线上积分信息修改失败");
            return requestParam;
        }
    }*/

  /*  @Transactional
    public Integer updateMemberIntegrate(Integer siteId, Map<String, Object> integral) {
        Map member = new HashMap();
        try {
            member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), integral.get("mobile").toString());//获取会员信息
            Integer score = Integer.valueOf(String.valueOf(-Float.valueOf(integral.get("on_costScore").toString()).intValue()));
            if (StringUtil.isEmpty(member) || (Integer.parseInt(member.get("integrate").toString()) - score < 0)) {
                return 0;
            }
            Map<String, Object> memberIntegral = new HashMap<>();
            memberIntegral.put("integrate", Integer.parseInt(member.get("integrate").toString()) - score);
            //使用线上积分替换线下积分数量
            memberIntegral.put("total_consume_integrate", Integer.parseInt(member.get("total_consume_integrate").toString()) + score);
            memberIntegral.put("siteId", siteId);
            memberIntegral.put("buyerId", member.get("buyer_id"));
            integralRuleMapper.updateMemberIntegralbyOffChange(memberIntegral);//修改用户线上积分
            integral.put("siteId", siteId);
            integral.put("create_time", DateUtils.getCurrentTime());
            offIntegralLogMapper.insertOffIntegral(integral);//保存到线下积分记录中
            integral.put("on_costScore", new BigInteger(String.valueOf(-Float.valueOf(integral.get("on_costScore").toString()).intValue())));
            integral.put("type", 100);//线下消费线上积分
            integral.put("buyerId", member.get("buyer_id"));
            integral.put("mobile", member.get("mobile"));
            integral.put("mark", "积分兑换，订单号:" + integral.get("tradesId"));
            integralRuleMapper.insertlogList(integral);//线下消费线上积分记录保存到积分消费记录中
            return 1;
        } catch (NumberFormatException e) {
            log.info("天润数字类型转换失败===getIntegralfromERP" + e);
            return 0;
        } catch (Exception e) {
            log.info("天润订单插入异常===getIntegralfromERP" + e);
            return 0;
        }
    }*/
}
