package com.jk51.modules.offline.service;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.erpDataConfig.baodao.DataSourceConfig_BaoDao;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.integral.mapper.IntegralRuleMapper;
import com.jk51.modules.integral.mapper.OffIntegralLogMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:线下积分模块
 * 作者: dumingliang
 * 创建日期: 2017-12-02
 * 修改记录:
 */
@Service
public class OfflineIntegrateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineIntegrateService.class);
    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private BMemberMapper memberMapper;
    @Autowired
    private GuangJiOfflineService guangJiOfflineService;
    @Autowired
    private BaoDaoOfflineService baoDaoOfflineService;
    @Autowired
    private DeKangOfflineService deKangOfflineService;
    @Autowired
    private OffIntegralLogMapper offIntegralLogMapper;
    @Autowired
    private IntegralRuleMapper integralRuleMapper;
    @Resource
    private DataSourceConfig_BaoDao config_baoDao;

    //获取线下总积分
    public Map<String, Object> getOffTotalScore(Integer siteId, String mobile) {
        LOGGER.info("erp获取会员线下总积分，商家id{},会员手机号码:{}.", siteId, mobile);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(siteId)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            result.put("code", 400);
            result.put("msg", "刚商户未对接erp");
            return result;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        Integer status = Integer.parseInt(merchantErpInfo.get("status").toString());//erp开关状态 0 关闭  1 正常
        Integer integrateFlag = Integer.parseInt(merchantErpInfo.get("integrate").toString());//模块开关 0 关闭  1 正常
        if (status == 1 && integrateFlag == 1) {
            Map<String, Object> response = new HashMap<>();
            if (siteId == 100166) {//九洲,
                response = jiuzhou_getoffTotalScore(siteId, baseUrl, mobile);
            } else if (siteId == 100190) {//天润
                response = tianrun_getoffTotalScore(siteId, baseUrl, mobile);
            } else if (siteId == 100204) {//广济，比较特殊，提供的是数据库权限
                response = guangJiOfflineService.queryScore(siteId, mobile);
            } else if (siteId == 100173) {//中联
                response = zhonglian_getoffTotalScore(siteId, baseUrl, mobile);
            } else if (siteId == 100030) {//宝岛
                response = queryScore_Baodao(siteId, mobile);
            } else if (siteId == 100213 || siteId == 100239 || siteId == 100271) {//济生,天伦,内蒙国大
                response = queryScore_jishengtang(siteId, baseUrl, mobile);
            } else if (siteId == 100272) {//成都聚仁堂
                response = queryScore_jurentang(siteId, baseUrl, mobile);
            } else if (siteId == 100268) {//德仁堂
                response = queryScore_derentang(siteId, baseUrl, mobile);
            }
            /*else if (siteId == 100215) {//德康大药房
                response = deKangOfflineService.queryScore(siteId, mobile);
            }*/
            else {
                LOGGER.info("积分通道未打通");
                result.put("code", 404);
                result.put("msg", "会员通道未打通");
                return result;
            }
            return response;
        } else {//erp对接通道关闭
            LOGGER.info("erp对接通道关闭");
            result.put("code", 404);
            result.put("msg", "erp对接通道关闭");
            return result;
        }
    }

    //九洲，获取用户线下总积分（金豆）
    public Map<String, Object> jiuzhou_getoffTotalScore(Integer siteId, String baseUrl, String mobile) {
        Map<String, Object> responseParams = new HashedMap();
        String url = baseUrl + "/user/queryScore?mobile_no=" + mobile;
        LOGGER.info("九洲商户获取总积分请求地址是:[{}],手机号:[{}]", url, mobile);
        Map<String, Object> result = erpToolsService.requestErp(url);//接收erp接口信息
        if ((Integer) result.get("code") != 1) {//未获取到正确的积分数据
            responseParams.put("code", -1);
            responseParams.put("msg", "没有获取到积分数据");
            return responseParams;
        }
        BigInteger olIntegral = new BigInteger((String) ((Map) ((List) result.get("info")).get(0)).get("gold_score"));
        LOGGER.info("offlineScore:{}", olIntegral.toString());
        //更新线下积分
        Map map = new HashMap();
        map.put("olIntegral", olIntegral);
        map.put("siteId", siteId);
        map.put("mobile", mobile);
        memberMapper.updateOfflineIntegral(map);//更改51后台的用户线下积分
        result.put("code", 0);
        return result;
    }

    //天润:获取用户线下总积分（天润线下积分总数为小数点后两位，本地数据库为整数，所以没有保存）
    public Map<String, Object> tianrun_getoffTotalScore(Integer siteId, String baseUrl, String mobile) {
        getOnFromOff(mobile);
        String url = baseUrl + "/getintegral/" + mobile + "";
        tianrun_PushOnToOff(siteId, baseUrl, mobile);
        Map<String, Object> result = erpToolsService.requestErp(url);
        return result;
    }

    //天润:将用户线上总积分推送到线下商户
    public void tianrun_PushOnToOff(Integer siteId, String baseUrl, String mobile) {
        Map<String, Object> res = new HashMap<>();
        res.put("mobile", mobile);
        res.put("desc", "同步积分");
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);//获取会员信息
        res.put("on_integral", member.get("integrate"));//获取会员线上总积分
        try {
            String param = URLEncoder.encode(JacksonUtils.mapToJson(res), "UTF-8");//推送线上总积分到商户
            String erpUrl = baseUrl + "/changeIntegral/" + param;
            LOGGER.info("天润[tianrun_PushOnToOff]：推送线上积分变化" + erpUrl);
            erpToolsService.requestErp(erpUrl);
        } catch (Exception e) {
            LOGGER.info("天润[tianrun_PushOnToOff]：线上积分推送异常" + e.getMessage());
        }
    }

    //中联:获取用户线下总积分并将积分同步到51后台
    public Map<String, Object> zhonglian_getoffTotalScore(Integer siteId, String baseUrl, String mobile) {
        String url = baseUrl + "/queryScore?mobile=" + mobile;
        LOGGER.info("##中联获取会员积分##请求地址是:[{}],手机号:[{}],", url, mobile);
        Map<String, Object> erp_params = new HashMap<>();
        Map<String, Object> responseParams = new HashMap<>();
        BigInteger olIntegral = new BigInteger("0");
        try {
            String reresult = OkHttpUtil.get(url);
            erp_params = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
            if (erp_params.get("code").equals("0")) {
                olIntegral = new BigInteger((String) ((Map) ((List) erp_params.get("info")).get(0)).get("gold_score"));
            }
        } catch (Exception e) {
            LOGGER.info("解析异常" + e.getMessage());//类型转换异常则没有查询到数据,接口调用失败
            responseParams.put("code", 0);
            responseParams.put("msg", "积分同步失败");
            return responseParams;
        }
        LOGGER.info("offlineScore:{}", olIntegral);
        //更新线下积分
        Map map = new HashMap();
        map.put("olIntegral", olIntegral);
        map.put("siteId", siteId);
        map.put("mobile", mobile);
        int i = memberMapper.updateOfflineIntegral(map);
        return erp_params;
    }

    //获取线下积分总额，并更新至线下
    public Map<String, Object> queryScore_Baodao(Integer siteId, String mobile_no) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> responseParams = new HashMap<>();
        String code = "0";
        String msg = "";
        Map<String, Object> scoreMap = getLogListFromBaoDao(siteId, mobile_no);
        Object score = scoreMap.get("gold_score");
        LOGGER.info("宝岛siteId:{},用户:{},offlineScore:{}", siteId, mobile_no, score);
        result.add(scoreMap);
        responseParams.put("info", result);
        Double d = Double.parseDouble(score.toString());
        Integer olIntegral = Integer.valueOf(d.intValue());
        //更新线下积分
        Map map = new HashMap();
        map.put("olIntegral", olIntegral);
        map.put("siteId", siteId);
        map.put("mobile", mobile_no);
        int i = memberMapper.updateOfflineIntegral(map);
        responseParams.put("code", 0);
        responseParams.put("msg", msg);
        return responseParams;
    }

    //获取线下积分总额，并更新至线下
    public Map<String, Object> queryScore_jishengtang(Integer siteId, String baseUrl, String mobile) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = baseUrl + "/user/queryScore";
            LOGGER.info("商户:[{}],获取总积分请求地址是:[{}],手机号:[{}]", siteId, url, mobile);
            Map<String, Object> requestParams = new HashedMap();
            requestParams.put("mobile_no", mobile);
            result = erpToolsService.requestHeaderPar(url, requestParams);//接收erp接口信息
            if ((Integer) result.get("code") != 0) {
                return result;//判断是否成功请求到数据(String)((Map)((List)result.get("info"))).get("gold_score")
            }
            Double dd = Double.valueOf(((Map) ((List) result.get("info")).get(0)).get("gold_score").toString());
            LOGGER.info("商户siteId:{},mobile:{},offlineScore:{}", siteId, mobile, dd.toString());
            //更新线下积分
            Map map = new HashMap();
            map.put("olIntegral", dd);
            map.put("siteId", siteId);
            map.put("mobile", mobile);
            memberMapper.updateOfflineIntegral(map);
        } catch (Exception e) {
            LOGGER.info("商户:{},更新线下积分失败，原因:{}", siteId, e.getMessage());
            result.put("code", 400);
            result.put("msg", "无数据");
        }
        return result;
    }

    //成都聚仁堂获取线下积分总额，并更新至线下
    public Map<String, Object> queryScore_jurentang(Integer siteId, String baseUrl, String mobile) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = baseUrl + "/QueryScore";
            LOGGER.info("商户:[{}],获取总积分请求地址是:[{}],手机号:[{}]", siteId, url, mobile);
            Map<String, Object> requestParams = new HashedMap();
            requestParams.put("mobile", mobile);
            result = erpToolsService.requestHeaderPar(url, requestParams);//接收erp接口信息
            if ((Integer) result.get("Code") != 0) {
                return result;//判断是否成功请求到数据(String)((Map)((List)result.get("info"))).get("gold_score")
            }
            Double dd = Double.valueOf(((Map) ((List) result.get("info")).get(0)).get("gold_score").toString());
            LOGGER.info("商户siteId:{},mobile:{},offlineScore:{}", siteId, mobile, dd.toString());
            //更新线下积分
            Map map = new HashMap();
            map.put("olIntegral", dd);
            map.put("siteId", siteId);
            map.put("mobile", mobile);
            memberMapper.updateOfflineIntegral(map);
        } catch (Exception e) {
            LOGGER.info("商户:{},更新线下积分失败，原因:{}", siteId, e.getMessage());
            result.put("code", 400);
            result.put("msg", "无数据");
        }
        return result;
    }

    //成都德仁堂获取线下积分总额，并更新至线下
    public Map<String, Object> queryScore_derentang(Integer siteId, String baseUrl, String mobile) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = baseUrl + "/user/queryScore";
            LOGGER.info("商户:[{}],获取总积分请求地址是:[{}],手机号:[{}]", siteId, url, mobile);
            Map<String, Object> requestParams = new HashedMap();
            requestParams.put("mobile_no", mobile);
            result = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));//接收erp接口信息
            if (!result.containsKey("code") || (Integer) result.get("code") != 0) {
                return result;//判断是否成功请求到数据(String)((Map)((List)result.get("info"))).get("gold_score")
            }
            Double dd = Double.valueOf(((Map) ((List) result.get("info")).get(0)).get("gold_score").toString());
            LOGGER.info("商户siteId:{},mobile:{},offlineScore:{}", siteId, mobile, dd.toString());
            //更新线下积分
            Map map = new HashMap();
            map.put("olIntegral", dd);
            map.put("siteId", siteId);
            map.put("mobile", mobile);
            memberMapper.updateOfflineIntegral(map);
        } catch (Exception e) {
            LOGGER.info("商户:{},更新线下积分失败，原因:{}", siteId, e.getMessage());
            result.put("code", 400);
            result.put("msg", "无数据");
        }
        return result;
    }

    //获取线下积分总额，并更新至线下
    public Map<String, Object> queryScoreList_jishengtang(Integer siteId, String mobile, Integer pageNum, Integer pageSize) throws Exception {
        Map<String, Object> result1 = new HashedMap();
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(siteId)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            result1.put("code", 400);
            result1.put("msg", "刚商户未对接erp");
            return result1;
        } else if (Integer.parseInt(merchantErpInfo.get("status").toString()) != 1) {
            result1.put("code", 400);
            result1.put("msg", "刚商户未对接erp");
            return result1;
        } else if (Integer.parseInt(merchantErpInfo.get("integrate").toString()) != 1) {
            result1.put("code", 400);
            result1.put("msg", "刚商户未对接积分erp");
            return result1;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        String url = baseUrl + "/user/getScoreList";
        LOGGER.info("商户:{},获取用户积分列表请求地址是:[{}],手机号:[{}]", siteId, url, mobile);
        Map<String, Object> requestParams = new HashedMap();
        requestParams.put("mobile_no", mobile);
        Map<String, Object> result = erpToolsService.requestHeaderPar(url, requestParams);//接收erp接口信息
        return result;
/*        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        List<Map> scoreList = new ArrayList<>();
        for (int i = 0; i <= pageSize - 1; i++) {
            Map<String, Object> score = new HashMap<>();
            score.put("num", "300011535164689");
            score.put("mname", "双黄连口服液,双黄连口服液");
            score.put("spec", "10ml*10支,10ml*20支");
            score.put("create_time", "2015-10-10 11:28:49");
            score.put("sumMoney", "7.42");
            score.put("integral", pageNum * pageSize + i);
            score.put("notes", "销售增加积分");
            scoreList.add(score);
        }
        response.put("info", scoreList);
        response.put("total", 100);
        return response;*/
    }

    //获取线下积分总额，并更新至线下
    public Map<String, Object> queryScoreList_jurentang(Integer siteId, String mobile, Integer pageNum, Integer pageSize) throws Exception {
        Map<String, Object> result1 = new HashedMap();
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(siteId)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            result1.put("code", 400);
            result1.put("msg", "刚商户未对接erp");
            return result1;
        } else if (Integer.parseInt(merchantErpInfo.get("status").toString()) != 1) {
            result1.put("code", 400);
            result1.put("msg", "刚商户未对接erp");
            return result1;
        } else if (Integer.parseInt(merchantErpInfo.get("integrate").toString()) != 1) {
            result1.put("code", 400);
            result1.put("msg", "刚商户未对接积分erp");
            return result1;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        String url = baseUrl + "/QueryScoreList";
        LOGGER.info("商户:{},获取用户积分列表请求地址是:[{}],手机号:[{}]", siteId, url, mobile);
        Map<String, Object> requestParams = new HashedMap();
        requestParams.put("mobile", mobile);
        requestParams.put("pageNum", 1);
        requestParams.put("pageSize", 15);
        Map<String, Object> result = erpToolsService.requestHeaderPar(url, requestParams);//接收erp接口信息
        return result;
    }


    /**
     * 记录线上会员的积分使用情况
     **/
    public Map<String, Object> recordIntegerChange(Integer siteId, Map<String, Object> requestParams) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(siteId)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            result.put("code", 400);
            result.put("msg", "刚商户未对接erp");
            return result;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        Map<String, Object> requestParam = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        LOGGER.info("推送积分使用情况:站点：{},详情：{}", siteId, requestParams.toString());
        try {
            String mobile = requestParams.get("mobile").toString();//会员手机号码
            Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);//获取会员信息
            if (StringUtil.isEmpty(member)) {
                LOGGER.info("siteId:{},mobile:{},会员不存在。", siteId, mobile);
                response.put("code", 1);
                response.put("msg", "该会员不存在");
                return response;
            }
            requestParam.put("mobile", mobile);
            String tradesId = "";//订单号
            String goodsNo = "";//积分购买商品的商品编码
            if (!StringUtil.isEmpty(requestParams.get("tradesId"))) {
                tradesId = requestParams.get("tradesId").toString();
                if (!StringUtil.isEmpty(requestParams.get("goodsNo"))) {
                    goodsNo = requestParams.get("goodsNo").toString();
                } else {
                    goodsNo = erpToolsService.getGoodsCodebyTradesId(siteId, Long.valueOf(tradesId));
                }
            }
            requestParam.put("tradesId", tradesId);
            requestParam.put("goods_num", goodsNo);
            Map<String, Object> integralScore = getOffTotalScore(siteId, mobile);//获取用户线下积分总数
            Double offIntegral = 0d;
            if ("0".equals(integralScore.get("code").toString())) {
                offIntegral = Double.valueOf(((Map) ((List) integralScore.get("info")).get(0)).get("gold_score").toString());//获取线下积分
            }
            Integer type = Integer.parseInt(requestParams.get("type").toString());//状态码,1:线上消费，0:线下消费
            String totalScore = requestParams.get("sumScore").toString();//此次变化总积分（线上+线下）
            requestParam.put("sumScore", totalScore);
            String on_costScore = "0";
            if (!StringUtil.isEmpty(requestParams.get("on_costScore"))) {
                on_costScore = requestParams.get("on_costScore").toString();//线上变化积分，如果是兑换积分，积分为负，如果是赠送积分，积分为正
            }
            requestParam.put("on_costScore", on_costScore);
            String off_costScore = "0";
            if (!StringUtil.isEmpty(requestParams.get("off_costScore"))) {
                off_costScore = requestParams.get("off_costScore").toString();//线下变化积分，如果是兑换积分，积分为负，如果是赠送积分，积分为正
            }
            requestParam.put("off_costScore", off_costScore);
            String on_integral = "0";
            if (!StringUtil.isEmpty(requestParams.get("on_integral"))) {
                on_integral = requestParams.get("on_integral").toString();//剩余线上积分
            } else {
                on_integral = member.get("integrate").toString();
            }
            requestParam.put("on_integral", on_integral);
            String off_integral = "0";
            if (!StringUtil.isEmpty(requestParams.get("off_integral"))) {
                off_integral = requestParams.get("off_integral").toString();//剩余线下积分
            } else {
                off_integral = String.valueOf(offIntegral);
            }
            requestParam.put("off_integral", off_integral);
            String create_time = requestParams.get("create_time").toString();//创建时间
            requestParam.put("create_time", create_time);
            String desc = requestParams.get("desc").toString();//描述，格式固定
            requestParam.put("desc", desc);
            if (type == 1) {//线上消费，记录此次信息，将信息传递到线下,不管线上消费有没有用到线下积分，都要把积分传过去(包括赠送和消费)
                if (desc.contains("兑换积分")) {//会消耗线上积分，也可能会消耗线下积分
                    if (!off_costScore.equals("0")) {//线上消费使用到线下积分
                        off_integral = String.valueOf(offIntegral + Double.parseDouble(off_costScore));//计算线下剩余积分
                        requestParam.put("off_integral", off_integral);
                        LOGGER.info("线上消费，积分兑换，使用线下积分,使用线上积分{}.线上积分剩余:{}.使用线下积分{},线下积分剩余{},创建时间:{},描述:{}。",
                            on_costScore, on_integral, off_costScore, off_integral, create_time, desc);
                        offIntegralLogMapper.insertSelect(siteId, mobile, tradesId, totalScore, off_costScore, on_costScore, off_integral, on_integral, create_time, desc);//记录此次消费记录
                    } else {//线上消费没有使用线下积分
                        LOGGER.info("线上消费，积分兑换，没有使用线下积分,使用线上积分{}.线上积分剩余:{}.使用线下积分{},线下积分剩余{},创建时间:{},描述:{}。",
                            on_costScore, on_integral, off_costScore, off_integral, create_time, desc);
                    }
                } else {//线上积分增加,退款，线下积分也会增加
                    LOGGER.info("线上消费，增加线上积分:{},增加线下积分:{},线上积分总额:{} ", on_costScore, off_costScore, on_integral);
                }
                if (siteId == 100030) {//宝岛
                    return logToBaoDao(mobile, desc, create_time, on_costScore, off_costScore);
                }
                if (siteId == 100190) {//天润
                    LOGGER.info("变化积分{}", requestParam);
                    String param = URLEncoder.encode(JacksonUtils.mapToJson(requestParam), "UTF-8");//推送积分变化到商户后台
                    String url = baseUrl + "/changeIntegral/" + param;
                    LOGGER.info("推送线上积分变化" + url);
                    return erpToolsService.requestErp(url);//code：0，msg：“线上积分插入成功”
                }
                if (siteId == 100272) {//聚仁堂
                    LOGGER.info("变化积分{}", requestParam);
                    String url = baseUrl + "/ScoreOnline";
                    LOGGER.info("推送线上积分变化" + url);
                    return erpToolsService.requestErp(url, requestParam);//code：0，msg：“线上积分插入成功”
                }
                response.put("code", -1);
                response.put("msg", "未找到对应商户");
                return response;
            } else {//线下消费，保存到本地,线下积分增加或者减少，或者兑换线上积分
                LOGGER.info("线下消费，同步积分,站点：{}，参数：{}", siteId, requestParam.toString());
                try {
                    //记录此次消费记录
                    offIntegralLogMapper.insertSelect(siteId, mobile, tradesId, totalScore, off_costScore, on_costScore, off_integral, on_integral, create_time, desc);
                } catch (Exception e) {
                    LOGGER.info("线下订单插入失败==off==recordIntegerChange" + e);
                    response.put("code", -1);
                    response.put("msg", "订单号已存在");
                    return response;
                }
                //处理此次线下消费的线上积分和线下积分
                if (desc.contains("兑换积分")) {
                    if (!on_costScore.equals("0")) {//消耗线上积分
                        LOGGER.info("线下消费，兑换线上积分，兑换线上积分{},线上剩余积分{},消耗线下积分{},线下剩余积分{},描述{}，创建时间{}。",
                            on_costScore, on_integral, off_costScore, off_integral, desc, create_time);
                        Map<String, Object> intelog = new HashMap<>();//保存到线上积分记录列表使用
                        intelog.put("siteId", siteId);
                        intelog.put("buyerId", member.get("buyer_id"));
                        intelog.put("buyerNick", member.get("buyer_nick"));
                        intelog.put("integralDesc", desc);//说明
                        intelog.put("integralAdd", 0);//获得积分数
                        intelog.put("integralDiff", on_costScore);//使用积分数（线上积分兑换线下积分）
                        intelog.put("mark", "线下消费" + desc);//标识符串，需要拓展
                        intelog.put("type", CommonConstant.TYPE_ORDER_CHANGE);//规则类型
                        Double integrate = Double.valueOf(member.get("integrate").toString());//线上积分
                        Map<String, Object> memberInte = new HashMap<>();//保存到会员信息列表使用
                        memberInte.put("siteId", siteId);
                        memberInte.put("buyerId", member.get("buyer_id"));
                        memberInte.put("buyerNick", member.get("buyer_nick"));
                        if (integrate == Double.valueOf(on_integral) - Double.valueOf(on_costScore)) {//51后台统计的线上积分和商户后台统计的线上积分保持一致，
                            LOGGER.info("线下消费线上积分数量：{},积分保持一致,剩余线上积分{},变动前51后台线上积分{}", on_costScore, on_integral, integrate);
                            memberInte.put("integrate", on_integral);
                            intelog.put("integralOverplus", on_integral);//线上剩余积分，需要进行比对
                        } else {//积分不一致，做出记录
                            LOGGER.info("线下消费线上积分数量：{},积分不一致,剩余线上积分{},变动前51后台线上积分{}", on_costScore, on_integral, integrate);
                            Double curOnScore = integrate + Double.valueOf(on_costScore);
                            memberInte.put("integrate", curOnScore);
                            intelog.put("integralOverplus", curOnScore);//线上剩余积分，需要进行比对
                        }
                        integralRuleMapper.insertIntegralLog(intelog); //将线下消费的线上积分放到Integrallog表中
                        integralRuleMapper.updateMemberIntegral(memberInte);//修改用户的线上积分总数
                    } else {//不消耗线上积分
                        LOGGER.info("线下消费，不兑换兑换线上积分，兑换线上积分{},线上剩余积分{},消耗线下积分{},线下剩余积分{},描述{}，创建时间{}。",
                            on_costScore, on_integral, off_costScore, off_integral, desc, create_time);
                    }
                }
                response.put("code", 0);
                response.put("msg", "请求成功");
                return response;
            }
        } catch (Exception e) {
            LOGGER.info("积分消费异常" + e.getMessage());
            response.put("code", -1);
            response.put("msg", "请求异常");
            return response;
        }
    }

    /**
     * 获取线下使用线上的积分情况,并将值转移至线上
     *
     * @return
     */
    @Transactional
    public Map<String, Object> getOnFromOff(String mobile) {
        Map member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(100190), mobile);//获取会员信息
        if (StringUtil.isEmpty(member)) {
            return null;
        }
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(100190)));
        if (StringUtil.isEmpty(merchantErpInfo)) {
            response.put("code", 400);
            response.put("msg", "刚商户未对接erp");
            return response;
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        //获取线下使用线上的积分列表
        String url = baseUrl + "/getuseonintegral/" + mobile;
        LOGGER.info("天润商家线下使用线上积分:" + url);
        Map<String, Object> result = erpToolsService.requestErp(url);
        LOGGER.info("天润商家线下使用线上积分使用情况" + result.toString());
        if ((Integer) result.get("code") == 0) {
            List integralUsed = (List) (result.get("info"));//积分情况转换为list数组
            Integer score = 0;
            for (Object m : integralUsed) {
                Map<String, Object> integral = (Map<String, Object>) m;
                try {
                    integral.put("siteId", 100190);
                    offIntegralLogMapper.insertOffIntegral(integral);//保存到线下积分记录中
                    integral.put("on_costScore", new BigInteger(String.valueOf(-Float.valueOf(integral.get("on_costScore").toString()).intValue())));
                    integral.put("type", 100);
                    integral.put("buyerId", member.get("buyer_id"));
                    integral.put("mobile", member.get("mobile"));
                    integral.put("mark", "积分兑换，订单号:" + integral.get("tradesId"));
                    integral.put("desc", integral.get("desc"));
                    integral.put("on_integral", integral.get("on_integral"));//线上剩余积分
                    integralRuleMapper.insertlogList(integral);//线下消费线上积分记录保存到积分消费记录中
                    score += Integer.valueOf(integral.get("on_costScore").toString());
                } catch (NumberFormatException e) {
                    LOGGER.info("数字类型转换失败" + e.getMessage());
                } catch (Exception e) {
                    LOGGER.info("订单插入异常" + e.getMessage());
                }
            }
            Map<String, Object> memberIntegral = new HashMap<>();
            memberIntegral.put("integrate", Integer.parseInt(member.get("integrate").toString()) - score);
            //使用线上积分替换线下积分数量
            memberIntegral.put("total_consume_integrate", Integer.parseInt(member.get("total_consume_integrate").toString()) + score);
            memberIntegral.put("siteId", 100190);
            memberIntegral.put("buyerId", member.get("buyer_id"));
            integralRuleMapper.updateMemberIntegralbyOffChange(memberIntegral);//修改用户线上积分
        } else {
            return null;
        }
        return null;
    }

    /**
     * 从erp获取会员积分消费信息
     *
     * @param siteId
     * @param integralList
     * @return
     */
    public Map<String, Object> getIntegralfromERP(Integer siteId, List<Map<String, Object>> integralList) {
        LOGGER.info("天润会员积分消费信息siteId{},积分信息{}", siteId, integralList.toString());
        Map<String, Object> requestParam = new HashMap<>();
        try {
            String tradesId = "";
            for (Object m : integralList) {
                Map<String, Object> integral = (Map<String, Object>) m;
                Integer result = this.updateMemberIntegrate(siteId, integral);
                if (result == 0) {
                    LOGGER.info("本次订单记录失败,getIntegralfromERP,订单信息{}", integral.get("tradesId"));
                    continue;
                } else {
                    tradesId += integral.get("tradesId") + ",";
                    LOGGER.info("本次订单记录成功,getIntegralfromERP,订单信息{}", integral.get("tradesId"));
                    continue;
                }
            }
            requestParam.put("code", 0);
            requestParam.put("msg", "更新成功");
            requestParam.put("info", tradesId);
            return requestParam;
        } catch (Exception e) {
            LOGGER.info("天润修改用户线上积分信息getIntegralfromERP:" + e.getMessage());
            requestParam.put("code", -1);
            requestParam.put("msg", "用户线上积分信息修改失败");
            return requestParam;
        }
    }

    @Transactional
    public Integer updateMemberIntegrate(Integer siteId, Object integrateObj) {
        Map member = new HashMap();
        Integer score = 0;
        try {
            Map<String, Object> integral = (Map<String, Object>) integrateObj;
            score = Integer.valueOf(integral.get("on_costScore").toString());
            member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), integral.get("mobile").toString());//获取会员信息
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
            if (integral.containsKey("create_time") && !StringUtil.isEmpty(integral.get("create_time"))) {
                integral.put("create_time", integral.get("create_time"));
            } else {
                integral.put("create_time", DateUtils.getCurrentTime());
            }
            offIntegralLogMapper.insertOffIntegral(integral);//保存到线下积分记录中
            integral.put("on_costScore", new BigInteger(String.valueOf(-Float.valueOf(integral.get("on_costScore").toString()).intValue())));
            integral.put("type", 100);//线下消费线上积分
            integral.put("buyerId", member.get("buyer_id"));
            integral.put("mobile", member.get("mobile"));
            if (integral.containsKey("tradesId") && !StringUtil.isEmpty(integral.get("tradesId"))) {
                integral.put("mark", "积分兑换，订单号:" + integral.get("tradesId"));
                integralRuleMapper.insertlogList(integral);//线下消费线上积分记录保存到积分消费记录中
            }
            return 1;
        } catch (NumberFormatException e) {
            LOGGER.info("数字类型转换失败===getIntegralfromERP" + e.getMessage());
            return 0;
        } catch (Exception e) {
            LOGGER.info("订单插入异常===getIntegralfromERP" + e.getMessage());
            return 0;
        }
    }

    /**
     * @param mobile        手机号码
     * @param desc          来源
     * @param create_time   产生时间
     * @param on_costScore  线上积分变动
     * @param off_costScore 线下积分变动
     * @return
     */
    @Transactional
    public Map<String, Object> logToBaoDao(String mobile, String desc, String create_time, String on_costScore, String off_costScore) {
        Map<String, Object> result = new HashMap<>();
        try {
            Double d_on = Double.parseDouble(on_costScore);
            Double d_off = Double.parseDouble(off_costScore);
            String insertLog_SQL = "insert into membership_points (mobile,status,source,insertion,xsintegral,status51) " +
                "values (" + mobile + ",'0','" + desc + "',to_date('" + create_time + "','yyyy/mm/dd HH24:MI:SS'),'" +
                (Integer.valueOf(d_on.intValue()) + Integer.valueOf(d_off.intValue())) + "','1')";
            LOGGER.info("宝岛积分插入记录表SQL语句" + insertLog_SQL);
            //操作积分记录表
            config_baoDao.getBaoDaoJDBCTemplate().update(insertLog_SQL);
           /* String updateIntegrate_SQL = "update querymember SET integral_xs=NVL(integral_xs,'0')+" + on_costScore + ",integral_xx=NVL(integral_xx,'0')+" + off_costScore + "," +
                "offline_integral=NVL(offline_integral,'0')+" + on_costScore + "+" + off_costScore + " where mobile=" + mobile + "";
            config_baoDao.getBaoDaoJDBCTemplate().update(updateIntegrate_SQL);*/
            result.put("code", 0);
            result.put("msg", "积分插入成功");
        } catch (Exception e) {
            LOGGER.info("宝岛插入积分记录有误:{}", e.getMessage());
            result.put("code", -1);
        }
        return result;
    }

    /**
     * 获取宝岛线下列表中未同步的积分记录,对51后台进行积分操作
     *
     * @param mobile
     * @return
     */
    //从宝岛后台获取积分消费记录表
    @Transactional
    public Map<String, Object> getLogListFromBaoDao(Integer siteId, String mobile) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> member = memberMapper.selectMemberMapByPhoneNum(String.valueOf(siteId), mobile);//获取用户的积分信息
        Integer onScore = Integer.parseInt(member.get("integrate").toString());//51后台线上总积分
        Integer offScore = Integer.parseInt(member.get("offline_integral").toString());//51后台线下总积分
        try {
            String sql = String.format("select NVL(offline_integral,'0') as total_score from querymember where mobile =?");//获取积分为线上线下积分总和
            List<Map<String, Object>> result = config_baoDao.getBaoDaoJDBCTemplate().queryForList(sql, new Object[]{mobile});

            String list_sql = String.format("select mobile as mobile,source as source,insertion as create_time,NVL(xsintegral,'0') as allScore," +
                "remarks1 as tradesId from membership_points where mobile=%s and status51=0 order by create_time asc", mobile);//查询消耗的总积分列表
            LOGGER.info("宝岛查询线下erp积分明细语句" + list_sql);
            List<Map<String, Object>> logList = config_baoDao.getBaoDaoJDBCTemplate().queryForList(list_sql);
            for (Map<String, Object> log : logList) {
                String dd = String.valueOf(log.get("create_time"));
                if (dd.indexOf(".") < 0) {
                    log.put("create_time", dd);
                } else {
                    log.put("create_time", dd.substring(0, 19));
                }
                try {
                    Integer total_consum_integral = Integer.parseInt(log.get("allScore").toString());//本次变化总积分
                    if (total_consum_integral > 0) {//增加线下积分
                        offScore = offScore + Integer.parseInt(log.get("allScore").toString());
                    } else {//消耗积分，取值为负数，优先消耗线下积分，然后消耗线上积分
                        Integer offline_consum_integral = 0;
                        Integer online_consum_integral = 0;
                        if (offScore + total_consum_integral >= 0) {//51后台线下积分大于本次消耗的总积分
                            offScore = offScore + total_consum_integral;//本次消费后剩余线下总积分
                            offline_consum_integral = -total_consum_integral;//本次消费了线下积分
                        } else {//此次消费消耗到了线上积分
                            offline_consum_integral = -offScore;
                            online_consum_integral = total_consum_integral + offScore;
                            offScore = 0;
                            onScore = onScore + offScore + total_consum_integral;//本次消费后剩余线上总积分
                        }
                        Map<String, Object> integralObj = new HashMap<>();
                        integralObj.put("siteId", siteId);
                        integralObj.put("mobile", mobile);
                        integralObj.put("sumScore", -total_consum_integral);//为正数
                        integralObj.put("off_costScore", offline_consum_integral);//本次消耗的线下积分
                        integralObj.put("on_costScore", online_consum_integral);//本次消耗的线上积分
                        integralObj.put("off_integral", offScore);
                        integralObj.put("on_integral", onScore);
                        integralObj.put("create_time", log.get("create_time"));
                        integralObj.put("desc", "兑换积分");
                        integralObj.put("tradesId", log.get("tradesId"));
                        updateMemberIntegrate(siteId, integralObj);//修改记录积分
                    }
                    continue;
                } catch (Exception e) {
                    LOGGER.info("此次消费记录读取有误{},订单号:{},时间:{}", e.getMessage(), log.get("tradesId"), log.get("create_time"));
                    continue;
                }
            }
            if (logList.size() > 0) {
                String update51 = "update membership_points set status51=1 where mobile='" + mobile + "' and insertion" +
                    " between to_date('" + logList.get(0).get("create_time") + "','yyyy/mm/dd HH24:MI:SS') " +
                    "and to_date('" + logList.get(logList.size() - 1).get("create_time") + "','yyyy/mm/dd HH24:MI:SS')";
                LOGGER.info("修改中间表已经统计过的数据状态SQL语句:{}", update51);
                config_baoDao.getBaoDaoJDBCTemplate().update(update51);
            } else {
                if (onScore > 0) {
                    String updateIntegrate_SQL = "update querymember SET integral_xs=NVL(integral_xs,'0')+" + onScore + ",integral_xx=NVL(integral_xx,'0')+" + offScore + "," +
                        "offline_integral=NVL(offline_integral,'0')+" + onScore + "+" + offScore + " where mobile=" + mobile + "";
                    LOGGER.info("宝岛积分初始化SQL语句" + updateIntegrate_SQL);
                    config_baoDao.getBaoDaoJDBCTemplate().update(updateIntegrate_SQL);
                    String insertLog_SQL = "insert into membership_points (mobile,status,source,insertion,xsintegral,status51) " +
                        "values (" + mobile + ",'1','" + "线上积分初始化" + "',to_date('" + DateUtils.getCurrentTime() + "','yyyy/mm/dd HH24:MI:SS'),'" +
                        (Integer.valueOf(onScore) + Integer.valueOf(offScore)) + "','1')";
                    LOGGER.info("宝岛积分插入记录表SQL语句" + insertLog_SQL);
                    config_baoDao.getBaoDaoJDBCTemplate().update(insertLog_SQL);
                }
            }
            if (result.size() == 0) {
                response.put("gold_score", 0);
                return response;
            } else {
                Integer gold_score = Integer.parseInt(result.get(0).get("total_score").toString()) - onScore;
                if (gold_score == offScore) {
                    response.put("gold_score", gold_score);
                } else {
                    LOGGER.info("getLogListFromBaoDao51后台和中间表线下总积分不一致");
                    if (gold_score >= 0) {
                        response.put("gold_score", gold_score);
                    } else {
                        response.put("gold_score", 0);
                    }
                }
                return response;
            }
        } catch (Exception e) {
            LOGGER.error("宝岛积分同步存在问题 {}", e.getMessage());
            response.put("gold_score", 0);
            return response;
        }
    }

}
