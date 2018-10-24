package com.jk51.modules.merchant.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.controller.IMIndexCountService;
import com.jk51.modules.merchant.service.DataProfileService;
import com.jk51.modules.merchant.service.SelectDataService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:51后台数据概况
 * 作者:
 * 创建日期: 2017-06-06
 * 修改记录:
 */
@Controller
@RequestMapping("/dataProfile")
public class DataProfileController {
    private static final Logger logger = LoggerFactory.getLogger(DataProfileController.class);
    @Autowired
    private DataProfileService dataProfileService;
    @Autowired
    private IMIndexCountService imIndexCountService;
    @Autowired
    private SelectDataService selectDataService;


    /**
     * 插入微信浏览数据
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/insertWebInfo")
    @ResponseBody
    public Map<String, Object> insertWebInfo(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> memberinfo = new HashMap<>();
        try {
            dataProfileService.insertSelect(param);
            memberinfo.put("msg", "OK");
        } catch (Exception e) {
            logger.info("插入失败，会员浏览插入失败insertWebInfo=====" + e);
        }
        return memberinfo;
    }

    /**
     * 查询注册会员数
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/selectMemberCountBydays")
    @ResponseBody
    public Map<String, Object> selectMemberCountBydays(Integer siteId, String sTime, Integer dayNum) {
        Map<String, Object> memberinfo = new HashMap<>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", siteId.toString());
        params.put("sTime", sTime);
        params.put("advanceDays", dayNum.toString());
        params.put("type", SelectDataService.MEMBER_COUNT);
        try {
            return (Map<String, Object>) selectDataService.queryDataProfile(params);
        } catch (Exception e) {
            logger.info("查询注册会员数失败{}" + e);
        }
        return memberinfo;
    }

    /**
     * 统计支付金额
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/selectTradesMoneyBydates")
    @ResponseBody
    public Map<String, Object> selectTradesMoneyBydates(Integer siteId, String sTime, Integer dayNum, Integer postStyle) {
        Map<String, Object> tradesinfo = new HashMap<>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", siteId.toString());
        params.put("sTime", sTime);
        params.put("advanceDays", dayNum.toString());
        params.put("type", SelectDataService.TRADES_MONEY_BYDATES);
        try {
            return (Map<String, Object>) selectDataService.queryDataProfile(params);
        } catch (Exception e) {
            logger.info("统计支付金额失败{}" + e);
        }
        return tradesinfo;
    }

    /**
     * 统计支付订单数
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/selectTradesCountBydates")
    @ResponseBody
    public Map<String, Object> selectTradesCountBydates(Integer siteId, String sTime, Integer dayNum, Integer postStyle) {
        Map<String, Object> tradesCountinfo = new HashMap<>();
        List<Map<String, Object>> map = dataProfileService.selectTradesCountBydates(siteId, sTime, dayNum, postStyle);
        tradesCountinfo.put("result", map.subList(1, map.size()));
        tradesCountinfo.put("proportion", dataProfileService.getProportion(map));
        return tradesCountinfo;
    }

    /**
     * 统计客单价
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/selectUnitPriceBydates")
    @ResponseBody
    public Map<String, Object> selectUnitPriceBydates(Integer siteId, String sTime, Integer dayNum, Integer postStyle) {
        Map<String, Object> tradesUnitPriceinfo = new HashMap<>();
        List<Map<String, Object>> map = dataProfileService.selectunitPriceByDates(siteId, sTime, dayNum, postStyle);
        tradesUnitPriceinfo.put("result", map.subList(1, map.size()));
        tradesUnitPriceinfo.put("proportion", dataProfileService.getProportion(map));
        return tradesUnitPriceinfo;
    }

    /**
     * 右侧店员拉新数
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getPullNewCount")
    @ResponseBody
    public Map<String, Object> getPullNewCount(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        String nowDay = params.get("nowDay").toString();
        params.put("sTime", nowDay);
        params.put("type", SelectDataService.PULL_NEW_COUNT);
        try {
            Object obj = selectDataService.queryDataProfile(params);
            if (obj instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
                if (null != list && 0 != list.size()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", 0);
                    map.put("message", "查询店员拉新数排行榜成功");
                    map.put("result", list);
                    return map;
                } else {
                    Map<String, Object> hm = new HashedMap();
                    hm.put("code", -1);
                    hm.put("message", "查询员拉新数排行榜失败");
                    return hm;
                }

            } else if (obj instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) obj;
                Map<String, Object> ratioMap = (Map<String, Object>) map.get("ratio");

                if (0 == (Integer) ratioMap.get("code")) {
                    List<Map<String, Object>> resultList = (List<Map<String, Object>>) ratioMap.get("result");
                    if (null != resultList && 0 != resultList.size()) {
                        map.put("code", 0);
                        map.put("message", "查询店员拉新数排行榜成功");
                        map.put("result", resultList);
                        return map;
                    } else {
                        Map<String, Object> hm = new HashedMap();
                        hm.put("code", -1);
                        hm.put("message", "查询员拉新数排行榜失败,或当天没有店员拉新");
                        return hm;
                    }
                } else {
                    Map<String, Object> hm = new HashedMap();
                    hm.put("code", -1);
                    hm.put("message", "查询员拉新数排行榜失败,或当天没有店员拉新");
                    return hm;
                }
            } else {
                Map<String, Object> hm = new HashedMap();
                hm.put("code", -1);
                hm.put("message", "查询员拉新数排行榜失败,或当天没有店员拉新");
                return hm;
            }
        } catch (Exception e) {
            Map<String, Object> map = new HashedMap();
            logger.info("查询员拉新数排行榜失败:{}", e);
            map.put("code", -1);
            map.put("message", "查询员拉新数排行榜失败");
            return map;
        }

    }

    /**
     * 右侧商品支付金额
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getPaymentMoney")
    @ResponseBody
    public Map<String, Object> getPaymentMoney(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        String nowDay = params.get("nowDay").toString();
        params.put("sTime", nowDay);
        params.put("type", SelectDataService.PAYMENT_MONEY);
        try {
            Object obj = selectDataService.queryDataProfile(params);
            if (obj instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
                if (null != list && 0 != list.size()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", 0);
                    map.put("message", "查询员支付金额排行榜成功");
                    map.put("result", list);
                    return map;
                } else {
                    Map<String, Object> hm = new HashedMap();
                    hm.put("code", -1);
                    hm.put("message", "查询员支付金额排行榜失败,或当天没有商品售出");
                    return hm;
                }
            } else if (obj instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) obj;
                Map<String, Object> ratioMap = (Map<String, Object>) map.get("ratio");
                if (0 == (Integer) ratioMap.get("code")) {
                    List<Map<String, Object>> resultList = (List<Map<String, Object>>) ratioMap.get("result");
                    if (null != resultList && 0 != resultList.size()) {
                        map.put("code", 0);
                        map.put("message", "查询员支付金额排行榜成功");
                        map.put("result", resultList);
                        return map;
                    } else {
                        Map<String, Object> hm = new HashedMap();
                        hm.put("code", -1);
                        hm.put("message", "查询员支付金额排行榜失败,或当天没有商品售出");
                        return hm;
                    }

                } else {
                    Map<String, Object> hm = new HashedMap();
                    hm.put("code", -1);
                    hm.put("message", "查询员支付金额排行榜失败,或当天没有商品售出");
                    return hm;
                }
            } else {
                Map<String, Object> hm = new HashedMap();
                hm.put("code", -1);
                hm.put("message", "查询员支付金额排行榜失败,或当天没有商品售出");
                return hm;
            }
        } catch (Exception e) {
            Map<String, Object> map = new HashedMap();
            logger.info("查询员支付金额排行榜失败:{}", e);
            map.put("code", -1);
            map.put("message", "查询员支付金额排行榜失败");
            return map;
        }
    }

    /**
     * 统计用户满意度
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getServiceSatisfactionMap")
    @ResponseBody
    public Map<String, Object> getServiceSatisfactionMap(Integer siteId, String sTime, Integer dayNum) {
        Map<String, Object> tradesinfo = new HashMap<>();
        List<Map<String, Object>> map = dataProfileService.getServiceSatisfactionMap(siteId, sTime, dayNum);
        tradesinfo.put("result", map.subList(1, map.size()));
        tradesinfo.put("proportion", dataProfileService.getProportion(map));
        return tradesinfo;
    }

    /**
     * 返回当前积极店员数据
     *
     * @param siteId
     * @param sTime
     * @param dayNum
     * @return
     */
    @RequestMapping("getEnergyClerksNum")
    @ResponseBody
    public Map<String, Object> getEnergyClerksNum(Integer siteId, String sTime, Integer dayNum) {
        Map<String, Object> energyClerkinfo = new HashMap<>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", siteId.toString());
        params.put("sTime", sTime);
        params.put("advanceDays", dayNum.toString());
        params.put("type", SelectDataService.ENERGY_CLERKS);
        try {
            return (Map<String, Object>) selectDataService.queryDataProfile(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return energyClerkinfo;
    }

    /**
     * 查询近n天的浏览总量
     */
    @RequestMapping(value = "/webVisitors")
    @ResponseBody
    public Object WebVisitors(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        params.put("type", SelectDataService.WEB_VISITORS);
        try {
            Map<String, Object> data = (Map<String, Object>) selectDataService.queryDataProfile(params);
            result.put("code", 0);
            result.put("msg", "查询浏览总量成功");
            result.put("result", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", -1);
            result.put("msg", "查询浏览总量失败");
            result.put("result", e.getMessage());
        }
        return result;
    }

    /**
     * 查询近n天的商品浏览总量
     */
    @RequestMapping(value = "/goodsWebVisitors")
    @ResponseBody
    public Object GoodsWebVisitors(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        params.put("type", SelectDataService.GOODS_WEB_VISITORS);
        try {
            Map<String, Object> data = (Map<String, Object>) selectDataService.queryDataProfile(params);
            result.put("code", 0);
            result.put("msg", "查询被访商品成功");
            result.put("result", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", -1);
            result.put("msg", "查询被访商品失败");
            result.put("result", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/advisoryCount")
    @ResponseBody
    public Object ConsultCount(HttpServletRequest request) {
        Map<String, Object> resultParams = new HashMap<String, Object>();
        try {
            Map<String, Object> params = ParameterUtil.getParameterMap(request);
            Integer site_id = Integer.parseInt(String.valueOf(params.get("site_id")));
            String date = (String) params.get("sTime");
            Integer days = Integer.parseInt(params.get("advanceDays").toString());
            String sdate = date + " 00:00:01";
            Map<String, Object> rs = imIndexCountService.getAdvisoryHistory(site_id, date, days);

            resultParams.put("code", 0);
            resultParams.put("msg", "查询咨询次数成功");
            resultParams.put("result", rs);
            return resultParams;
        } catch (Exception e) {
            logger.info("查询咨询次数失败{}",e);
            resultParams.put("code", -1);
            resultParams.put("msg", "查询咨询次数失败");
            resultParams.put("result", null);
        }
        return resultParams;
    }

    /**
     * 交易分析
     *
     * @param siteId
     * @param eTime
     * @return
     */
    @RequestMapping("getTradesAnalysisData")
    @ResponseBody
    public Map<String, Object> tradesAnalysis(Integer siteId, String eTime, Integer advancesDays) {
        logger.info("交易分析查询参数，站点id：{}，统计时间：{}", siteId, eTime);
        Map<String, Object> requestParams = new HashMap<>();
        Map<String, Object> map = dataProfileService.getTransMap(siteId, eTime, "transMap");
        if (!StringUtil.isEmpty(map)) {
            //支付金额
            requestParams.put("tradesPay", map.get("tradesPay"));
            requestParams.put("trades_directPay", map.get("trades_directPay"));
            requestParams.put("trades_StoreToPay", map.get("trades_StoreToPay"));
            requestParams.put("trades_ToDoorPay", map.get("trades_ToDoorPay"));
            //订单数
            requestParams.put("tradesCount", map.get("tradesCount"));
            requestParams.put("tradesCount_directPay", map.get("tradesCount_directPay"));
            requestParams.put("tradesCount_StoreToPay", map.get("tradesCount_StoreToPay"));
            requestParams.put("tradesCount_ToDoorPay", map.get("tradesCount_ToDoorPay"));
            //客单价
            requestParams.put("unitPay", map.get("unitPay"));
            requestParams.put("unitPay_directPay", map.get("unitPay_directPay"));
            requestParams.put("unitPay_StoreToPay", map.get("unitPay_StoreToPay"));
            requestParams.put("unitPay_ToDoorPay", map.get("unitPay_ToDoorPay"));
            return requestParams;
        }else {
            List<Map<String, Object>> tradesList = dataProfileService.selectTradesMoneyBydates(siteId, eTime, advancesDays, null);//支付总金额
            requestParams.put("tradesPay", dataProfileService.getProportion(tradesList));

            List<Map<String, Object>> trades_DirectList = dataProfileService.selectTradesMoneyBydates(siteId, eTime, advancesDays, 170);//支付总金额_门店直购
            requestParams.put("trades_directPay", dataProfileService.getProportion(trades_DirectList));

            List<Map<String, Object>> trades_StoreToList = dataProfileService.selectTradesMoneyBydates(siteId, eTime, advancesDays, 160);//支付总金额_门店自提
            requestParams.put("trades_StoreToPay", dataProfileService.getProportion(trades_StoreToList));

            List<Map<String, Object>> trades_ToDoorList = dataProfileService.selectTradesMoneyBydates(siteId, eTime, advancesDays, 150);//支付总金额_送货上门
            requestParams.put("trades_ToDoorPay", dataProfileService.getProportion(trades_ToDoorList));

            List<Map<String, Object>> tradesCount = dataProfileService.selectTradesCountBydates(siteId, eTime, advancesDays, null);//订单数
            requestParams.put("tradesCount", dataProfileService.getProportion(tradesCount));

            List<Map<String, Object>> tradesCount_DirectList = dataProfileService.selectTradesCountBydates(siteId, eTime, advancesDays, 170);//订单数_门店直购
            requestParams.put("tradesCount_directPay", dataProfileService.getProportion(tradesCount_DirectList));

            List<Map<String, Object>> tradesCount_StoreToList = dataProfileService.selectTradesCountBydates(siteId, eTime, advancesDays, 160);//订单数_门店自提
            requestParams.put("tradesCount_StoreToPay", dataProfileService.getProportion(tradesCount_StoreToList));

            List<Map<String, Object>> tradesCount_ToDoorList = dataProfileService.selectTradesCountBydates(siteId, eTime, advancesDays, 150);//订单数_送货上门
            requestParams.put("tradesCount_ToDoorPay", dataProfileService.getProportion(tradesCount_ToDoorList));

            List<Map<String, Object>> unitPayList = dataProfileService.selectunitPriceByDates(siteId, eTime, advancesDays, null);//客单价
            requestParams.put("unitPay", dataProfileService.getProportion(unitPayList));

            List<Map<String, Object>> unitPay_DirectList = dataProfileService.selectunitPriceByDates(siteId, eTime, advancesDays, 170);//客单价_门店直购
            requestParams.put("unitPay_directPay", dataProfileService.getProportion(unitPay_DirectList));

            List<Map<String, Object>> unitPay_StoreToList = dataProfileService.selectunitPriceByDates(siteId, eTime, advancesDays, 160);//客单价_门店自提
            requestParams.put("unitPay_StoreToPay", dataProfileService.getProportion(unitPay_StoreToList));

            List<Map<String, Object>> unitPay_ToDoorList = dataProfileService.selectunitPriceByDates(siteId, eTime, advancesDays, 150);//客单价_送货上门
            requestParams.put("unitPay_ToDoorPay", dataProfileService.getProportion(unitPay_ToDoorList));
            return requestParams;
        }
    }

    /**
     * 曲线图
     *
     * @param siteId
     * @param eTime
     * @param advanceDays
     * @param type
     * @return
     */
    @ResponseBody
    @PostMapping("graphPicByTypes")
    public Map<String, Object> graphPicByTypes(Integer siteId, String eTime, Integer advanceDays, String type, Integer postStyle) {
        List<Map<String, Object>> responseParamsList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> transChartsMap = dataProfileService.getTransMap(siteId, eTime, "transPicMap");
        if (!StringUtil.isEmpty(transChartsMap)){
            if ("trades".equals(type) && StringUtil.isEmpty(postStyle)){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesMoneyBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("trades".equals(type) && 150 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesMoneyToDoorBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("trades".equals(type) && 160 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesMoneyToStoreBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("trades".equals(type) && 170 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesMoneyDirectBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("unit".equals(type) && null == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesUnitPricesBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("unit".equals(type) && 150 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("unitPriceToDoorBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("unit".equals(type) && 160 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("unitPriceToStoreBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("unit".equals(type) && 170 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("unitPriceDirectBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("count".equals(type) && null == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesCountBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("count".equals(type) && 150 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesCountToDoorBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("count".equals(type) && 160 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesCountToStoreBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }else if ("count".equals(type) && 170 == postStyle){
                responseParamsList = (List<Map<String, Object>>)transChartsMap.get("tradesCountDirectBydatesMap");
                responseParamsList = subList2Trades(responseParamsList,advanceDays);
            }
            result.put("value", responseParamsList);
            return result;
        }else {
            switch (type) {
                case "trades":
                    responseParamsList = dataProfileService.selectTradesMoneyBydates(siteId, eTime, advanceDays, postStyle);
                    responseParamsList = responseParamsList.subList(1,responseParamsList.size());
                    break;
                case "count":
                    responseParamsList = dataProfileService.selectTradesCountBydates(siteId, eTime, advanceDays, postStyle);
                    responseParamsList = responseParamsList.subList(1,responseParamsList.size());
                    break;
                case "unit":
                    responseParamsList = dataProfileService.selectunitPriceByDates(siteId, eTime, advanceDays, postStyle);
                    responseParamsList = responseParamsList.subList(1,responseParamsList.size());
                    break;
            }
            result.put("value", responseParamsList);
            return result;
        }
    }

    //根据天数截取List
    public List<Map<String, Object>> subList2Trades(List<Map<String, Object>> list,Integer day){
        List<Map<String, Object>> oList = new ArrayList<>();
        try{
            if (7 == day){
                oList = list.subList(list.size()-7,list.size());
            }else {
                oList = list;
            }

        }catch (Exception e){
            logger.info("交易分析曲线图截取异常:{}",e);
        }
        return oList;
    }

    /**
     * 查询支付漏斗和门店直购数据
     *
     * @param siteId
     * @param eTime
     * @return
     */
    @ResponseBody
    @PostMapping("gettradesDirectAndFunnels")
    public Map<String, Object> gettradesDirectAndFunnels(Integer siteId, String eTime) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> transChartsMap = dataProfileService.getTransMap(siteId, eTime, "transGraphMap");
        if (!StringUtil.isEmpty(transChartsMap)){

            return transChartsMap;
        }else {
            response.put("direct", dataProfileService.selectDirectStores(siteId, eTime, 170));//查询直购订单数据
            response.put("funnel", dataProfileService.selectWebPayFunnel(siteId, eTime, 1));//查询支付漏斗数据
            return response;
        }
    }

    /**
     * 交易分析排行榜
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getCharts")
    public Map<String, Object> getCharts(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        String time = String.valueOf(params.get("nowDay"));
        Map<String, Object> mapStorePay = new HashMap<>();
        Map<String, Object> mapAeraPay = new HashMap<>();
        Map<String, Object> transChartsMap = dataProfileService.getTransMap(siteId, time, "transChartsMap");
        if (!StringUtil.isEmpty(transChartsMap)){
            mapStorePay = (Map<String, Object>)transChartsMap.get("mapStorePay");
            mapAeraPay = (Map<String, Object>)transChartsMap.get("mapAeraPay");

        }else {

            mapStorePay = dataProfileService.getStorePay(params);
            mapAeraPay = dataProfileService.getAeraPay(params);

        }
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(mapStorePay) && StringUtil.isEmpty(mapAeraPay)){
            map.put("code", -1);
            map.put("message", "查询交易分析支付排行榜异常，或当天没有交易记录");
            return map;
        }else {
            map.put("code",0);
            map.put("storePay",mapStorePay);
            map.put("aeraPay",mapAeraPay);
            map.put("message", "查询交易分析支付排行榜成功");
            return map;
        }
    }

    /**
     * 交易分析排行榜--门店
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getStoreCharts")
    public Map<String, Object> getStoreCharts(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        String time = String.valueOf(params.get("nowDay"));
        Map<String, Object> mapStorePay = new HashMap<>();
        Map<String, Object> transChartsMap = dataProfileService.getTransMap(siteId, time, "transChartsMap");
        if (!StringUtil.isEmpty(transChartsMap)){
            mapStorePay = (Map<String, Object>)transChartsMap.get("mapStorePay");
        }else {
            mapStorePay = dataProfileService.getStorePay(params);
        }
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(mapStorePay)){
            map.put("code", -1);
            map.put("message", "查询门店支付排行榜异常，或当天没有交易记录");
            return map;
        }else {
            map.put("code",0);
            map.put("storePay",mapStorePay);
            map.put("message", "查询门店支付排行榜成功");
            return map;
        }
    }

    /**
     * 交易分析排行榜--区域
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getAreaCharts")
    public Map<String, Object> getAreaCharts(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        String time = String.valueOf(params.get("nowDay"));
        Map<String, Object> mapAeraPay = new HashMap<>();
        Map<String, Object> transChartsMap = dataProfileService.getTransMap(siteId, time, "transChartsMap");
        if (!StringUtil.isEmpty(transChartsMap)){
            mapAeraPay = (Map<String, Object>)transChartsMap.get("mapAeraPay");
        }else {
            mapAeraPay = dataProfileService.getAeraPay(params);
        }
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isEmpty(mapAeraPay)){
            map.put("code", -1);
            map.put("message", "查询区域支付排行榜异常，或当天没有交易记录");
            return map;
        }else {
            map.put("code",0);
            map.put("aeraPay",mapAeraPay);
            map.put("message", "查询区域支付排行榜成功");
            return map;
        }
    }

    /**
     * 新访客
     */
    @RequestMapping(value = "/newVisitor")
    @ResponseBody
    public Object queryNewVisitor(Integer siteId,String sTime, Integer advanceDays){
        Map<String, Object> newinfo = new HashMap<>();
        List<Map<String, Object>> map = dataProfileService.getNewVisitor(siteId,sTime,advanceDays);
        newinfo.put("result",map.subList(1, map.size()));
        newinfo.put("proportion",dataProfileService.getProportion(map));
        return newinfo;
    }
    /**
     * 旧老访客
     */
/*    @RequestMapping(value = "/oldVisitor")
    @ResponseBody
    public Object queryOldVisitor(Integer siteId,String sTime, Integer advanceDays){
        Map<String, Object> newinfo = new HashMap<>();
        List<Map<String, Object>> map = dataProfileService.getOldVisitor(siteId,sTime,advanceDays);
        newinfo.put("result",map.subList(1, map.size()));
        newinfo.put("proportion",dataProfileService.getProportion(map));
        return newinfo;
    }*/

    /**
     * 人均访问数
     */
    @RequestMapping(value = "/averageVisitor")
    @ResponseBody
    public Object queryAverageVisitor(Integer siteId,String sTime, Integer advanceDays){
        Map<String, Object> avginfo = new HashMap<>();
        List<Map<String, Object>> map = (List<Map<String, Object>>) dataProfileService.getAverageVisitor(siteId,sTime,advanceDays);
        avginfo.put("result",map.subList(1, map.size()));
        avginfo.put("proportion",dataProfileService.getProportion(map));
        return avginfo;
    }


    /**
     * 跳失率
     */
    @RequestMapping(value = "/bounceRate")
    @ResponseBody
    public Object bounceRate(Integer siteId,String sTime, Integer advanceDays){
        Map<String, Object> tsinfo = new HashMap<>();
        List<Map<String, Object>> map = (List<Map<String, Object>>) dataProfileService.bounceRate(siteId, sTime, advanceDays);
        tsinfo.put("result",map.subList(1, map.size()));
        tsinfo.put("proportion",dataProfileService.getProportion(map));
        return tsinfo;
    }

    //---------------------------------------------------流量分析流量来源分布接口拆开----------------------------------------------------

    /**
     * 流量分析排行榜
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getFlowCharts")
    public Map<String, Object> getFlowCharts(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //时段
                List<Map<String, Object>> mapTime = (List<Map<String, Object>>) dataProfileService.selectWebMemberCountByHour(siteId,sTime);

                //时段最高峰
                Map<String, Object> mapMax = dataProfileService.selectWebMemberCountByHourMax(siteId,sTime);

                //区域
                List<Map<String, Object>> mapArea = (List<Map<String, Object>>) dataProfileService.areaDistribution(siteId, sTime);

                //年龄
                Map<String, Object> mapAge = (Map<String, Object>) dataProfileService.ageDistribution(siteId,sTime);

                //性别
                Map<String, Object> mapSex = (Map<String, Object>) dataProfileService.sexDistribution(siteId,sTime);
                params.put("mapTime",mapTime);
                params.put("mapArea",mapArea);
                params.put("mapAge",mapAge);
                params.put("mapSex",mapSex);
                params.put("mapMax",mapMax);
                return params;
            }
        }catch (Exception e){
            logger.info("流量分析排行榜查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","流量分析查询异常");
            return params;
        }
    }
    /**
     * 时段分布
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("selectWebMemberCountByHour")
    public Map<String, Object> selectWebMemberCountByHour(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //时段
                List<Map<String, Object>> mapTime = (List<Map<String, Object>>) dataProfileService.selectWebMemberCountByHour(siteId,sTime);
                params.put("mapTime",mapTime);
                return params;
            }
        }catch (Exception e){
            logger.info("时段分布查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","时段分布查询异常");
            return params;
        }
    }
    /**
     * 时段分布最高峰
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("selectWebMemberCountByHourMax")
    public Map<String, Object> selectWebMemberCountByHourMax(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //时段最高峰
                Map<String, Object> mapMax = dataProfileService.selectWebMemberCountByHourMax(siteId,sTime);
                params.put("mapMax",mapMax);
                return params;
            }
        }catch (Exception e){
            logger.info("时段分布最高峰查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","时段分布最高峰查询异常");
            return params;
        }
    }
    /**
     * 区域排行榜
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("areaDistribution")
    public Map<String, Object> areaDistribution(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //区域
                List<Map<String, Object>> mapArea = (List<Map<String, Object>>) dataProfileService.areaDistribution(siteId, sTime);
                params.put("mapArea",mapArea);
                return params;
            }
        }catch (Exception e){
            logger.info("区域排行榜查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","区域查询异常");
            return params;
        }
    }

    /**
     * 年龄排行榜
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("ageDistribution")
    public Map<String, Object> ageDistribution(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //年龄
                Map<String, Object> mapAge = (Map<String, Object>) dataProfileService.ageDistribution(siteId,sTime);
                params.put("mapAge",mapAge);
                return params;
            }
        }catch (Exception e){
            logger.info("年龄排行榜查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","年龄查询异常");
            return params;
        }
    }
    /**
     * 性别排行榜
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("sexDistribution")
    public Map<String, Object> sexDistribution(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //性别
                Map<String, Object> mapSex = (Map<String, Object>) dataProfileService.sexDistribution(siteId,sTime);
                params.put("mapSex",mapSex);
                return params;
            }
        }catch (Exception e){
            logger.info("性别查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","性别查询异常");
            return params;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 流量分析曲线图
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getFlowPic")
    public Map<String, Object> getFlowPic(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
        Integer advanceDays = Integer.parseInt(String.valueOf(params.get("advanceDays")));
        String sTime = String.valueOf(params.get("sTime"));
        String type = String.valueOf(params.get("type"));

        //浏览总量用map
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        map.put("site_id",siteId);
        map.put("sTime",sTime);
        map.put("advanceDays",advanceDays);


        List<Map<String,Object>> responseParamsList = null;
        Map<String, Object> result = new HashMap<>();
        Map<String,Object> param= new HashMap<String,Object>();
        try{
            switch (type) {
                case "newVisitor":  //新访客数
                    map.put("type",SelectDataService.NEW_VISITORS);
                    Map<String,Object> newVisitor  =(Map<String,Object>) selectDataService.queryDataProfile(map);
                    responseParamsList = (List<Map<String, Object>>) newVisitor.get("result");
                    break;
                case "oldVisitor":   //老访客数
                    map.put("type",SelectDataService.OLD_VISITORS);
                    Map<String,Object> oldVisitor =(Map<String,Object>) selectDataService.queryDataProfile(map);
                    responseParamsList = (List<Map<String, Object>>) oldVisitor.get("result");

                    break;
                case "webVisitors":    //浏览量
                    map.put("type",SelectDataService.WEB_VISITORS);
                    Map<String,Object> web = (Map<String, Object>) selectDataService.queryDataProfile(map);
                    responseParamsList = (List<Map<String, Object>>) web.get("result");
                    break;
                case "allVisitor":    //总访客数
                    map.put("type",SelectDataService.TOTAL_VISITORS);
                    Map<String,Object> allVisitor =(Map<String,Object>)  selectDataService.queryDataProfile(map);
                    responseParamsList = (List<Map<String, Object>>) allVisitor.get("result");
                    break;
                case "averageVisitor":    //人均浏览数
                    map.put("type",SelectDataService.AVERAGE_VISITOR);
                    Map<String,Object> averageVisitor =(Map<String,Object>) selectDataService.queryDataProfile(map);
                    responseParamsList = (List<Map<String, Object>>) averageVisitor.get("result");
                    break;
                case "avgReadTime":    //商品停留时间
                    map.put("type",SelectDataService.GET_AVG_READ_TIME);
                    Map<String, Object> avgReadTime = (Map<String, Object>) selectDataService.queryDataProfile(map);
                    responseParamsList = (List<Map<String, Object>>) avgReadTime.get("result");
                    break;
            }
            if(advanceDays==7)
                result.put("value",responseParamsList.size()>7?responseParamsList.subList(1,responseParamsList.size()):responseParamsList);
            if(advanceDays==30)
                result.put("value",responseParamsList.size()>30?responseParamsList.subList(1,responseParamsList.size()):responseParamsList);
            return result;
        }catch (Exception e){
            logger.info("流量分析查询异常:{}",e);
            result.put("code",-1);
            result.put("msg","流量分析查询异常");
            return result;
        }

    }

    /**
     * 流量分析比例
     * @param request
     * @return
     */
    /*@ResponseBody
    @PostMapping("getFlowProportion")
    public Map<String, Object> getFlowProportion(HttpServletRequest request) {
        try{
            Map<String, Object> par = ParameterUtil.getParameterMap(request);
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));

            //浏览总量用map
            Map<String, Object> map = ParameterUtil.getParameterMap(request);
            map.put("site_id",siteId);
            map.put("sTime",sTime);
            map.put("advanceDays",7);

            Map<String, Object> params = new HashMap<>();

            //总访客数
            map.put("type",SelectDataService.TOTAL_VISITORS);
            Map<String, Object> allVisitorList = (Map<String, Object>)selectDataService.queryDataProfile(map);
            Map<String, Object> qMap = (Map<String, Object>)allVisitorList.get("proportion");
            if (!StringUtil.isEmpty(qMap.get("todayValue"))){
                qMap.put("firstVisitValue",qMap.get("todayValue"));
            }
            if (!StringUtil.isEmpty(qMap.get("yesterdayProportion"))){
                qMap.put("firstYesterdayVisitProportion",qMap.get("yesterdayProportion"));
            }
            params.put("allVisitor",allVisitorList.get("proportion"));

            //老访客数
            map.put("type",SelectDataService.OLD_VISITORS);
            //List<Map<String, Object>> oldVisitorList = (List<Map<String, Object>>)selectDataService.queryDataProfile(map);
            Map<String, Object> oldVisitorList = (Map<String, Object>)selectDataService.queryDataProfile(map);
            params.put("oldVisitor",oldVisitorList.get("proportion"));

            //新访客数
            List newVisitor=new ArrayList<Object>();
            List<Map<String, Object>> pList = (List<Map<String, Object>>)allVisitorList.get("result");
            Float firstVisitValue = Float.parseFloat(pList.get(pList.size()-1).get("value").toString()) ;
            Float yesterdayValue = Float.parseFloat(pList.get(pList.size()-2).get("value").toString()) ;
            List<Map<String, Object>> oList = (List<Map<String, Object>>)oldVisitorList.get("result");
            Integer o1 = Integer.parseInt(oList.get(oList.size()-1).get("value").toString());
            Integer o2 = Integer.parseInt(oList.get(oList.size()-2).get("value").toString());
            Map map1=new HashMap();
            map1.put("value",0);
            Map map2=new HashMap();
            map2.put("value",yesterdayValue-o2);
            Map map3=new HashMap();
            map3.put("value",firstVisitValue-o1);
           newVisitor.add(map1);
           newVisitor.add(map2);
           newVisitor.add(map3);
            params.put("newVisitor",dataProfileService.getFlowProportion(newVisitor));

            //浏览量
            map.put("type",SelectDataService.WEB_VISITORS);
            Map<String,Object> webMap = (Map<String, Object>) selectDataService.queryDataProfile(map);
            params.put("webVisitors",webMap.get("proportion"));

            //人均浏览数
            map.put("type",SelectDataService.AVERAGE_VISITOR);
            map.put("advanceDays",2);
            //List<Map<String, Object>> avgList = (List<Map<String, Object>>) selectDataService.queryDataProfile(map);
            Map<String, Object> avgList = (Map<String, Object>)selectDataService.queryDataProfile(map);
            params.put("averageVisitor",avgList.get("proportion"));

            return params;
        }catch (Exception e){
            Map<String, Object> params = new HashMap<>();
            logger.info("流量分析比例查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","流量分析查询异常");
            return params;
        }
    }*/

    /**
     * 访客数
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getFlowProportion")
    public Map<String, Object> getFlowProportion(HttpServletRequest request) {
        try{
            Map<String, Object> par = ParameterUtil.getParameterMap(request);
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));

            //浏览总量用map
            Map<String, Object> map = ParameterUtil.getParameterMap(request);
            map.put("site_id",siteId);
            map.put("sTime",sTime);
            map.put("advanceDays",7);

            Map<String, Object> params = new HashMap<>();

            //总访客数
            Long d1 = new Date().getTime();
            map.put("type",SelectDataService.TOTAL_VISITORS);
            Map<String, Object> allVisitorList = (Map<String, Object>)selectDataService.queryDataProfile(map);
            Map<String, Object> qMap = (Map<String, Object>)allVisitorList.get("proportion");
            if (!StringUtil.isEmpty(qMap.get("todayValue"))){
                qMap.put("firstVisitValue",qMap.get("todayValue"));
            }
            if (!StringUtil.isEmpty(qMap.get("yesterdayProportion"))){
                qMap.put("firstYesterdayVisitProportion",qMap.get("yesterdayProportion"));
            }
            params.put("allVisitor",allVisitorList.get("proportion"));
            logger.info("总访客用时++++++++++++++++++++++++++++++++++++++++++:{}",new Date().getTime()-d1);

            //老访客数
            Long d2 = new Date().getTime();
            map.put("type",SelectDataService.OLD_VISITORS);
            //List<Map<String, Object>> oldVisitorList = (List<Map<String, Object>>)selectDataService.queryDataProfile(map);
            Map<String, Object> oldVisitorList = (Map<String, Object>)selectDataService.queryDataProfile(map);
            params.put("oldVisitor",oldVisitorList.get("proportion"));
            logger.info("老访客数用时++++++++++++++++++++++++++++++++++++++++++:{}",new Date().getTime()-d2);

            //新访客数
            Long d3 = new Date().getTime();
            List newVisitor=new ArrayList<Object>();
            List<Map<String, Object>> pList = (List<Map<String, Object>>)allVisitorList.get("result");
            Float firstVisitValue = Float.parseFloat(pList.get(pList.size()-1).get("value").toString()) ;
            Float yesterdayValue = Float.parseFloat(pList.get(pList.size()-2).get("value").toString()) ;
            List<Map<String, Object>> oList = (List<Map<String, Object>>)oldVisitorList.get("result");
            Integer o1 = Integer.parseInt(oList.get(oList.size()-1).get("value").toString());
            Integer o2 = Integer.parseInt(oList.get(oList.size()-2).get("value").toString());
            Map map1=new HashMap();
            map1.put("value",0);
            Map map2=new HashMap();
            map2.put("value",yesterdayValue-o2);
            Map map3=new HashMap();
            map3.put("value",firstVisitValue-o1);
            newVisitor.add(map1);
            newVisitor.add(map2);
            newVisitor.add(map3);
            params.put("newVisitor",dataProfileService.getFlowProportion(newVisitor));
            logger.info("新访客数用时++++++++++++++++++++++++++++++++++++++++++:{}",new Date().getTime()-d3);
            return params;
        }catch (Exception e){
            Map<String, Object> params = new HashMap<>();
            logger.info("流量分析比例查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","流量分析查询异常");
            return params;
        }
    }



    /**
     * 停留时间
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("getAvgReadTime")
    public Map<String, Object> getAvgReadTime(HttpServletRequest request) {
        try{
            Map<String, Object> par = ParameterUtil.getParameterMap(request);
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> params = new HashMap<>();
            //商品停留时间
            List<Map<String, Object>> avgReadTimeList = dataProfileService.getAvgReadTime(siteId,sTime,2);
            params.put("avgReadTime",dataProfileService.getFlowProportion(avgReadTimeList));
            return params;
        }catch (Exception e){
            Map<String, Object> params = new HashMap<>();
            logger.info("停留时间查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","停留时间查询异常");
            return params;
        }
    }

    /**
     * 浏览次数
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("webVisitors2")
    public Map<String, Object> webVisitors2(HttpServletRequest request) {
        try{
            Map<String, Object> par = ParameterUtil.getParameterMap(request);
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> params = new HashMap<>();

            //浏览总量用map
            Map<String, Object> map = ParameterUtil.getParameterMap(request);
            map.put("site_id",siteId);
            map.put("sTime",sTime);
            map.put("advanceDays",7);
            //浏览量
            map.put("type",SelectDataService.WEB_VISITORS);
            Map<String,Object> webMap = (Map<String, Object>) selectDataService.queryDataProfile(map);
            params.put("webVisitors",webMap.get("proportion"));

            //人均浏览数
            map.put("type",SelectDataService.AVERAGE_VISITOR);
            map.put("advanceDays",2);
            Map<String, Object> avgList = (Map<String, Object>)selectDataService.queryDataProfile(map);
            params.put("averageVisitor",avgList.get("proportion"));

            return params;
        }catch (Exception e){
            Map<String, Object> params = new HashMap<>();
            logger.info("浏览次数查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","浏览次数查询异常");
            return params;
        }
    }

    //-------------------------------合接口------------------------------------------
    /**
     * 时段分布 & 最高峰 & 区域
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("selectWebMemberCountByHourAndMaxAndArea")
    public Map<String, Object> selectWebMemberCountByHourAndMaxAndArea(HttpServletRequest request) {
        Map<String, Object> par = ParameterUtil.getParameterMap(request);
        Map<String, Object> params = new HashMap<>();
        try{
            Integer siteId = Integer.parseInt(String.valueOf(par.get("siteId")));
            String sTime = String.valueOf(par.get("sTime"));
            Map<String, Object> flowChartsMap = dataProfileService.getFlowChartsMap(siteId, sTime);
            if (!StringUtil.isEmpty(flowChartsMap)){
                params = (Map<String, Object>)flowChartsMap.get("ratio");
                return params;
            }else {
                //时段最高峰
                Map<String, Object> mapMax = dataProfileService.selectWebMemberCountByHourMax(siteId,sTime);
                params.put("mapMax",mapMax);

                //时段
                List<Map<String, Object>> mapTime = (List<Map<String, Object>>) dataProfileService.selectWebMemberCountByHour(siteId,sTime);
                params.put("mapTime",mapTime);

                //区域
                List<Map<String, Object>> mapArea = (List<Map<String, Object>>) dataProfileService.areaDistribution(siteId, sTime);
                params.put("mapArea",mapArea);

                //商品停留时间
                List<Map<String, Object>> avgReadTimeList = dataProfileService.getAvgReadTime(siteId,sTime,2);
                params.put("avgReadTime",dataProfileService.getFlowProportion(avgReadTimeList));
                return params;
            }
        }catch (Exception e){
            logger.info("时段分布最高峰查询异常:{}",e);
            params.put("code",-1);
            params.put("msg","时段分布最高峰查询异常");
            return params;
        }
    }




    @RequestMapping(value="/abccba")
    @ResponseBody
    public Object test(Integer siteId,String sTime, Integer advanceDays) throws Exception {
        Map<String,Object> param= new HashMap<String,Object>();
        param.put("siteId",siteId);
        param.put("sTime",sTime);
        param.put("advanceDays",advanceDays);
        param.put("type",SelectDataService.OLD_VISITORS);
        Object o = dataProfileService.getNewVisitor2(siteId, sTime, advanceDays);
        return o;
    }

    @PostMapping(value = "/nsertWebInfo")
    @ResponseBody
    public Map<String, Object> nsertWebInfo(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> memberinfo = new HashMap<>();
        try {
            dataProfileService.insertSelect(param);
            memberinfo.put("msg", "OK");
        } catch (Exception e) {
            logger.info("插入失败，会员浏览插入失败insertWebInfo=====" + e);
        }
        return memberinfo;
    }
}
