package com.jk51.modules.merchant.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.date.StatisticsDate;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Member;
import com.jk51.modules.clerkvisit.mapper.BVisitDescMapper;
import com.jk51.modules.clerkvisit.mapper.BVisitStatisticsMapper;
import com.jk51.modules.im.mapper.ImRecodeMapper;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.merchant.mapper.WebPageMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:51后台数据概况
 * 作者:
 * 创建日期: 2017-06-06
 * 修改记录:
 */
@Service
public class DataProfileService {

    @Autowired
    private WebPageMapper webPageMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private ImRecodeMapper imRecodeMapper;
    @Autowired
    private BVisitDescMapper bVisitDescMapper;
    @Autowired
    private BVisitStatisticsMapper bVisitStatisticsMapper;
    private static final Logger log = LoggerFactory.getLogger(DataProfileService.class);

    @Async
    public void insertSelect(Map<String, Object> webPage) {
        try{
            //获取停留时间
            webPage = getRemainTime(webPage,null,null);
            //获取IP名称
            String ip = webPage.get("ip").toString();
            String wIp = changeIPName(ip);
            webPage.put("ipName", wIp);
            if(StringUtil.isEmpty(webPage.get("siteId"))){
                log.info("无siteId，不保存数据");
                return;
            }

            //OpenId
            Member member=null;
            if(!StringUtil.isEmpty(webPage.get("memberId"))&&!StringUtil.isEmpty(webPage.get("siteId"))){
                int siteId=Integer.parseInt(webPage.get("siteId")+"");
                int memberId=Integer.parseInt(webPage.get("memberId")+"");
                member=memberMapper.getMemberByMemberId(siteId,memberId);
            }
            if(!StringUtil.isEmpty(webPage.get("openId"))&&!"0".equals(webPage.get("openId"))&&!StringUtil.isEmpty(member)){
                if("2".equals(webPage.get("uvType"))){
                    if(StringUtil.isEmpty(member.getAliUserId())){
                        member.setAliUserId(webPage.get("openId")+"");
                        member.setOpenId(null);
                        memberMapper.updateOpenId(member);
                    }
                }else {
                    //StringUtil.isEmpty(member.getOpenId())||"0".equals(member.getOpenId()))
                    if(StringUtil.isEmpty(member.getOpenId())||"0".equals(member.getOpenId())){
                        member.setOpenId(webPage.get("openId")+"");
                        member.setAliUserId(null);
                        memberMapper.updateOpenId(member);
                    }

                }

            }
            String url=webPage.get("url").toString();
            Map<String, String> mapRequest = URLRequest(url);
            if(!StringUtil.isEmpty(mapRequest.get("visitId"))&&!StringUtil.isEmpty(mapRequest.get("storeAdminId"))){
                int visitId=Integer.parseInt(mapRequest.get("visitId")+"");
                int siteId=Integer.parseInt(webPage.get("siteId")+"");
        //            int storeAdminId=Integer.parseInt(webPage.get("storeAdminId")+"");
                bVisitDescMapper.updatePageStatus(siteId,visitId);
            }
             webPageMapper.insertSelect(webPage);
        }catch (Exception e){
            log.info("进入微信报错：{}",e);
        }
    }

    //查询注册会员数
    @Transactional
    public List<Map<String, Object>> selectMemberCountBydays(Integer siteId, String endTime, Integer advanceDay) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDay);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDay);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = memberMapper.selectMemberCountBydays(siteId, sTime, eTime);
        return rearragement(currentResult, startDate, endTime);
    }

    /**
     * 统计一段时间内支付金额
     * post_style 150(送货上门),160(门店自提)，170(门店直销)，
     *
     * @param siteId
     * @param endTime
     * @param advanceDay
     * @return
     */
    @Transactional
    public List<Map<String, Object>> selectTradesMoneyBydates(Integer siteId, String endTime, Integer advanceDay, Integer post_style) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDay);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDay);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = tradesMapper.selectTradesMoneyBydates(siteId, sTime, eTime, 1, post_style);
        return rearragement(currentResult, startDate, endTime);
    }

    /**
     * post_style 150(送货上门),160(门店自提)，170(门店直销)，
     * 统计一段时间内订单数量
     *
     * @param siteId
     * @param endTime
     * @param advanceDay
     * @return
     */
    @Transactional
    public List<Map<String, Object>> selectTradesCountBydates(Integer siteId, String endTime, Integer advanceDay, Integer post_style) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDay);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDay);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = tradesMapper.selectTradesCountBydates(siteId, sTime, eTime, 1, post_style);
        return rearragement(currentResult, startDate, endTime);
    }

    /**
     * post_style 150(送货上门),160(门店自提)，170(门店直销)，
     * 统计一段时间内客单价
     *
     * @param siteId
     * @param endTime
     * @param advanceDay
     * @return
     */
    @Transactional
    public List<Map<String, Object>> selectunitPriceByDates(Integer siteId, String endTime, Integer advanceDay, Integer post_style) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDay);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDay);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = tradesMapper.selectunitPriceByDates(siteId, sTime, eTime, post_style);
        return rearragement(currentResult, startDate, endTime);
    }

    /**
     * 查询用户满意度
     *
     * @return
     */
    @Transactional
    public List<Map<String, Object>> getServiceSatisfactionMap(Integer siteId, String endTime, Integer advanceDay) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDay);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDay);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = imRecodeMapper.getServiceSatisfactionMap(siteId, sTime, eTime);
        return rearragement(currentResult, startDate, endTime);
    }


    //右侧店员拉新数
    @Transactional
    public Map<String, Object> getPullNewCount(Map<String, Object> params) {
        Map<String, Object> resultParmas = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            String nowDay = params.get("nowDay").toString();
            Long l10 = new Date().getTime();
            List<Map<String, Object>> result = memberMapper.getPullNewCount(siteId, nowDay);
            log.info("店员拉新排行榜++++++++:{}",new Date().getTime()-l10);
            if (!StringUtil.isEmpty(result)) {
                resultParmas.put("code", 0);
                resultParmas.put("message", "查询右侧店员拉新数成功");
                resultParmas.put("result", result);
                return resultParmas;
            }
        } catch (Exception e) {
            log.info("查询右侧店员拉新数失效:{}", e);
            resultParmas.put("code", -1);
            resultParmas.put("message", "查询右侧店员拉新数失败");
        }
        resultParmas.put("code", -1);
        resultParmas.put("message", "查询右侧店员拉新数失败,或当天没有店员拉新");
        return resultParmas;
    }

    //右侧商品支付金额
    @Transactional
    public Map<String, Object> getPaymentMoney(Map<String, Object> params) {
        Map<String, Object> resultParmas = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            String nowDay = params.get("nowDay").toString();
            List<Map<String, Object>> result = tradesMapper.getPaymentMoney(siteId, nowDay);
            if (!StringUtil.isEmpty(result)) {
                resultParmas.put("code", 0);
                resultParmas.put("message", "查询右右侧商品支付金额成功");
                resultParmas.put("result", result);
                return resultParmas;
            }
        } catch (Exception e) {
            log.info("查询右侧商品支付金额失败:{}", e);
            resultParmas.put("code", -1);
            resultParmas.put("message", "查询右侧商品支付金额失败");
        }
        resultParmas.put("code", -1);
        resultParmas.put("message", "查询右侧商品支付金额失败,或当天没有商品售出");
        return resultParmas;
    }

    /**
     * 查询积极店员信息
     *
     * @param siteId
     * @param endTime
     * @param advanceDay
     * @return
     */
    @Transactional
    public List<Map<String, Object>> selectEnergyClerks(Integer siteId, String endTime, Integer advanceDay) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDay);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDay);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = storeAdminExtMapper.getEnergyClerksNum(siteId, sTime, eTime);
        return rearragement(currentResult, startDate, endTime);
    }

    /**
     * 统计数据比较
     *
     * @param energyList
     * @return
     */
    public Map<String, Object> getProportion(List<Map<String, Object>> energyList) {
        Map<String, Object> proportionMap = new HashMap<>();
        Float currentNum = Float.parseFloat(energyList.get(energyList.size() - 1).get("value").toString());
        Float yesterdayNum = Float.parseFloat(energyList.get(energyList.size() - 2).get("value").toString());
        Float lastWeekNum = Float.parseFloat(energyList.get(energyList.size() - 8).get("value").toString());
        proportionMap.put("todayValue", (float) (Math.round(currentNum * 100)) / 100);
        proportionMap.put("yesterdayProportion", StatisticsDate.getProportion(currentNum.floatValue(), yesterdayNum.floatValue()));
        proportionMap.put("lastweekProportion", StatisticsDate.getProportion(currentNum.floatValue(), lastWeekNum.floatValue()));
        return proportionMap;
    }

    /**
     * 流量分析数据比较
     *
     * @param energyList
     * @return
     */
    public Map<String, Object> getFlowProportion(List<Map<String, Object>> energyList) {
        Map<String, Object> proportionMap = new HashMap<>();
        Float currentNum = Float.parseFloat(energyList.get(energyList.size() - 1).get("value").toString());
        Float yesterdayNum = Float.parseFloat(energyList.get(energyList.size() - 2).get("value").toString());
        proportionMap.put("todayValue", currentNum);
        proportionMap.put("yesterdayProportion", StatisticsDate.getProportion(currentNum.floatValue(), yesterdayNum.floatValue()));
        return proportionMap;
    }


    /**
     * 结果补齐重排
     *
     * @param currentResult
     * @param startDate
     * @param endTime
     * @return
     */
    public List<Map<String, Object>> rearragement(List<Map<String, Object>> currentResult, Date startDate, String endTime) {
        List<Map<String, Object>> resultParmas = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        //遍历数组，统计每天的积极店员数量
        if (!StringUtil.isEmpty(currentResult) && currentResult.size() != 0) {
            for (Map<String, Object> date : currentResult) {
                result.put(DateUtils.formatDate((Date) date.get("query_time"), "yyyy-MM-dd"), String.valueOf(date.get("value")));
            }
        }
        List<String> dates = DateUtils.getContinuousDayStr(DateUtils.formatDate(startDate, "yyyy-MM-dd"), endTime);
        for (int i = 0; i < dates.size(); i++) {
            Map<String, Object> clerkNum = new HashMap<>();
            clerkNum.put("query_time", dates.get(i));
            if (result.containsKey(dates.get(i))) {
                if ("null".equals(result.get(dates.get(i)).toString())) {
                    clerkNum.put("value", 0);
                } else {
                    clerkNum.put("value", result.get(dates.get(i)));
                }
            } else {
                clerkNum.put("value", 0);
            }
            resultParmas.add(clerkNum);
        }
        return resultParmas;
    }

    @Transactional
    public Map<String, Object> queryWebVisitors(Integer site_id, Map<String, Object> params) {
        Map<String, Object> respose = new HashMap<>();
        Map<String, Object> resultParams = new HashMap<String, Object>();
        try {
            String sdate = (String) params.get("sTime");
            //惨无人道的前端不给时分秒！
            String startdate = sdate + " 23:59:59";
            Integer days = Integer.parseInt(params.get("advanceDays").toString());
            //days数值不正确
            if (days < 1) {
                resultParams.put("code", -1);
                resultParams.put("msg", "days不能小于1");
                resultParams.put("result", null);
                return resultParams;
            }
            log.info("当前站点:{},截止时间{}，提前天数{},method{}", site_id, sdate, days, "queryWebVisitors");
            Date date = StatisticsDate.processString2Date(startdate);
            List<Map<String, Date>> alldate = StatisticsDate.processDate(date, days);
            Date startDate = DateUtils.getBeforeOrAfterDate(date, -days);
            String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";

            Map<String, Date> dates = new HashMap<String, Date>();
            dates.put("start", StatisticsDate.processString2Date(sTime));
            dates.put("end", StatisticsDate.processString2Date(startdate));
            List<Map<String, Object>> rs = webPageMapper.queryWebVisitors2(site_id, dates);
            List<Map<String, Object>> mm = rearragement(rs, StatisticsDate.processString2Date(sTime), sdate);
            resultParams.put("result", mm.subList(1, mm.size()));
            resultParams.put("proportion", getProportion(mm));
            respose.put("code", 0);
            respose.put("result", resultParams);

        } catch (Exception e) {
            log.info("查询统计有误{}，方法{}", e, "queryWebVisitors");
            respose.put("code", -1);
            respose.put("msg", "查询总记录量失败");
        }
        return respose;
    }

    @Transactional
    public Map<String, Object> queryGoodsWebVisitors(Integer site_id, Map<String, Object> param) {
        Map<String, Object> respose = new HashMap<>();
        Map<String, Object> resultParams = new HashMap<String, Object>();
        try {
            String sdate = (String) param.get("sTime");
            String startdate = sdate + " 23:59:59";
            Integer days = Integer.parseInt(param.get("advanceDays").toString());
            if (days < 1) {
                resultParams.put("code", -1);
                resultParams.put("msg", "days不能小于1");
                resultParams.put("result", null);
                return resultParams;
            }
            Date date = StatisticsDate.processString2Date(startdate);
            List<Map<String, Date>> alldate = StatisticsDate.processDate(date, days);
            Date startDate = DateUtils.getBeforeOrAfterDate(date, -days);
            log.info("当前站点:{},截止时间{}，提前天数{},method{}", site_id, sdate, days, "queryGoodsWebVisitors");
            String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
            Map<String, Date> dates = new HashMap<String, Date>();
            dates.put("start", StatisticsDate.processString2Date(sTime));
            dates.put("end", StatisticsDate.processString2Date(startdate));
            List<Map<String, Object>> rs = webPageMapper.queryGoodsWebVisitors2(site_id, dates);
            List<Map<String, Object>> mm = rearragement(rs, StatisticsDate.processString2Date(sTime), sdate);
            resultParams.put("result", mm.subList(1, mm.size()));
            resultParams.put("proportion", getProportion(mm));
            respose.put("code", 0);
            respose.put("result", resultParams);

        } catch (Exception e) {
            log.info("查询统计有误{}，方法{}", e, "queryWebVisitors");
            respose.put("code", -1);
            respose.put("msg", "查询被访商品失败");
        }
        return respose;

    }

    /**
     * 查询当日门店直购支付人数，促销员人数，支付总金额
     */
    public Map<String, Object> selectDirectStores(Integer siteId, String sTime, Integer postStyle) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> buyerCount = this.selectPayMemberNum(siteId, sTime, 170, 0);
        resultMap.put("buyerCount", buyerCount.get(buyerCount.size() - 1).get("value"));
        List<Map<String, Object>> promoterCount = this.selectStoreUserNum(siteId, sTime, 170, 0);
        resultMap.put("promoterCount", promoterCount.get(promoterCount.size() - 1).get("value"));
        List<Map<String, Object>> tradesPayCount = this.selectTradesMoneyBydates(siteId, sTime, 170, 0);
        resultMap.put("tradesPayCount", tradesPayCount.get(tradesPayCount.size() - 1).get("value"));
        return resultMap;
    }

    /**
     * 查询一定时间内订单支付人数
     *
     * @param siteId
     * @param endTime
     * @param postStyle
     * @param advanceDays
     * @return
     */
    @Transactional
    public List<Map<String, Object>> selectPayMemberNum(Integer siteId, String endTime, Integer postStyle, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = rearragement(tradesMapper.selectPayMemberNum(siteId, sTime, eTime, 1, postStyle), startDate, endTime);
        return currentResult;
    }

    /**
     * 查询一定时间内促销员人数
     *
     * @param siteId
     * @param endTime
     * @param postStyle
     * @param advanceDays
     * @return
     */
    @Transactional
    public List<Map<String, Object>> selectStoreUserNum(Integer siteId, String endTime, Integer postStyle, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        List<Map<String, Object>> currentResult = rearragement(tradesMapper.selectStoreUserNum(siteId, sTime, eTime, postStyle), startDate, endTime);
        return currentResult;
    }

    /**
     * 商城支付漏斗统计只包括送货上门和门店自提
     *
     * @param siteId
     * @param endTime
     * @param advanceDays
     * @return
     */
    public Map<String, Object> selectWebPayFunnel(Integer siteId, String endTime, Integer advanceDays) {
        Map<String, Object> requestParams = new HashMap<>();//结果集
        Map<String, Object> convertParams = new HashMap<>();//转化率
        //访客数
        Map<String, Object> firstPro = firstFunnelStatistics(siteId, endTime, advanceDays);
        requestParams.put("firstFunnel", firstPro);
        //第二层
        Map<String, Object> secondPro = secondFunnelStatistics(siteId, endTime, advanceDays);
        requestParams.put("secondFunnel", secondPro);
        convertParams.put("orderConvertrate", StatisticsDate.getProp(Float.parseFloat(secondPro.get("todayBuyerValue").toString()), Float.parseFloat(firstPro.get("firstVisitValue").toString())));
        //第三层
        Map<String, Object> thirdPro = thirdFunnelStatics(siteId, endTime, advanceDays, 1);
        requestParams.put("thirdFunnel", thirdPro);
        convertParams.put("order_PayConvertrate", StatisticsDate.getProp(Float.parseFloat(thirdPro.get("todayPayBuyerValue").toString()), Float.parseFloat(secondPro.get("todayBuyerValue").toString())));
        convertParams.put("payConvertrate", StatisticsDate.getProp(Float.parseFloat(thirdPro.get("todayPayBuyerValue").toString()), Float.parseFloat(firstPro.get("firstVisitValue").toString())));
        requestParams.put("convertRate", convertParams);
        return requestParams;
    }


    /**
     * 支付漏斗第一层流量统计包括游客和会员，去重
     *
     * @param siteId
     * @param endTime
     * @param advanceDays
     * @return
     */
    @Transactional
    public Map<String, Object> firstFunnelStatistics(Integer siteId, String endTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        /*String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        Long l2 = new Date().getTime();
        List<Map<String, Object>> list = webPageMapper.onlineVisitorsFunnel(siteId, sTime, eTime);
        log.info("shijian++++++:{}",new Date().getTime()-l2);*/
        try{
            String eTime = endTime + " 23:59:59";
            String sTime = endTime + " 00:00:00";
            Date endDate = StatisticsDate.processString2Date(eTime);
            Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
            String aTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") ;
            String bTime = aTime + " 00:00:00";
            String cTime = aTime + " 23:59:59";
            Long l2 = new Date().getTime();
            List<Map<String, Object>> list = webPageMapper.onlineVisitorsFunnel(siteId, sTime, eTime);
            List<Map<String, Object>> list1 = new ArrayList<>();
            String string = webPageMapper.getFirstFunnelStatistics(siteId,bTime);
            if (!StringUtil.isEmpty(string)){
                Map<String,Object> map = JacksonUtils.json2map(string);
                Map<String,Object> mapRatio = (Map<String,Object>)map.get("ratio");
                Map<String,Object> mapFirstFunnel = (Map<String,Object>)mapRatio.get("funnel");
                Map<String,Object> hMap = (Map<String,Object>)mapFirstFunnel.get("firstFunnel");
                Float value = Float.parseFloat(hMap.get("firstVisitValue").toString());
                Map<String,Object> oMap = new HashMap<>();
                oMap.put("value",value);
                oMap.put("query_time",startDate);
                list1.add(oMap);
            }else {
                list1 = webPageMapper.onlineVisitorsFunnel(siteId, bTime, cTime);
            }
            //list1 = webPageMapper.onlineVisitorsFunnel(siteId, bTime, cTime);
            log.info("shijian++++++:{}",new Date().getTime()-l2);
            list.addAll(list1);
            List<Map<String, Object>> currentResult = rearragement(list, startDate, endTime);
            Float currentNum = Float.parseFloat(currentResult.get(currentResult.size() - 1).get("value").toString());
            Float yesterdayNum = Float.parseFloat(currentResult.get(currentResult.size() - 2).get("value").toString());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("firstVisitValue", currentNum);
            requestParams.put("yesterdayValue", yesterdayNum);
            requestParams.put("firstYesterdayVisitProportion", StatisticsDate.getProportion(currentNum.floatValue(), yesterdayNum.floatValue()));
            return requestParams;
        }catch (Exception e){
            log.info("支付漏斗第一层流量统计包括游客和会员，去重 异常:{}",e);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("firstVisitValue", 0);
            requestParams.put("yesterdayValue", 0);
            requestParams.put("firstYesterdayVisitProportion", 0);
            return requestParams;
        }

    }

    /**
     * 总访客数
     *
     * @param siteId
     * @param endTime
     * @param advanceDays
     * @return
     */
    @Transactional
    public List<Map<String, Object>> getAllVisitor(Integer siteId, String endTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        Long l11 = new Date().getTime();
        List<Map<String, Object>> currentResult = rearragement(webPageMapper.onlineVisitorsFunnel(siteId, sTime, eTime), startDate, endTime);
        log.info("查询总访客量SQL执行时间999**++++++++++++++++++++++++++++++++**:{}", new Date().getTime() - l11);
        return currentResult;
    }

    /**
     * 支付漏斗第二层 下单统计包括未付款状态
     *
     * @param siteId
     * @param endTime
     * @param advanceDays
     * @return
     */
    @Transactional
    public Map<String, Object> secondFunnelStatistics(Integer siteId, String endTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        try{
            String eTime = endTime + " 23:59:59";
            String sTime = endTime + " 00:00:00";
            Date endDate = StatisticsDate.processString2Date(eTime);
            Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
            String aTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") ;
            String bTime = aTime + " 00:00:00";
            String cTime = aTime + " 23:59:59";
            List<Integer> postStyles = new ArrayList<>();
            postStyles.add(150);
            postStyles.add(160);
            //下单会员数量
            List<Map<String, Object>> list = tradesMapper.funnelPaymemberCount(siteId, sTime, eTime, null, postStyles);
            String string = webPageMapper.getFirstFunnelStatistics(siteId,bTime);
            List<Map<String, Object>> list1 = new ArrayList<>();
            Double todayPayValue = 0.00;
            if (!StringUtil.isEmpty(string)){
                Map<String,Object> map = JacksonUtils.json2map(string);
                Map<String,Object> mapRatio = (Map<String,Object>)map.get("ratio");
                Map<String,Object> mapFirstFunnel = (Map<String,Object>)mapRatio.get("funnel");
                Map<String,Object> hMap = (Map<String,Object>)mapFirstFunnel.get("secondFunnel");
                Float value = Float.parseFloat(hMap.get("todayBuyerValue").toString());
                todayPayValue = Double.parseDouble(hMap.get("todayPayValue").toString());
                Map<String,Object> oMap = new HashMap<>();
                oMap.put("value",value);
                oMap.put("query_time",startDate);
                list1.add(oMap);
            }else {
                list1 = tradesMapper.funnelPaymemberCount(siteId, bTime, cTime, null, postStyles);
            }
            list.addAll(list1);
            List<Map<String, Object>> buyerCount = rearragement(list, startDate, endTime);
            //下单金额统计
            List<Map<String, Object>> list2 = tradesMapper.funnelPayBefoCount(siteId, sTime, eTime, null, postStyles);
            List<Map<String, Object>> list3 = new ArrayList<>();
            if (!StringUtil.isEmpty(string)){
                Map<String,Object> oMap = new HashMap<>();
                oMap.put("value",todayPayValue);
                oMap.put("query_time",startDate);
                list3.add(oMap);
            }else {
                list3 = tradesMapper.funnelPayBefoCount(siteId, bTime, cTime, null, postStyles);
            }
            list2.addAll(list3);
            List<Map<String, Object>> payCount = rearragement(list2, startDate, endTime);
            Map<String, Object> requestParams = new HashMap<>();
            Float buyercurrentNum = Float.parseFloat(buyerCount.get(buyerCount.size() - 1).get("value").toString());
            Float buyeryesterdayNum = Float.parseFloat(buyerCount.get(buyerCount.size() - 2).get("value").toString());
            requestParams.put("todayBuyerValue", buyercurrentNum);
            requestParams.put("yesterdayBuyerProportion", StatisticsDate.getProportion(buyercurrentNum.floatValue(), buyeryesterdayNum.floatValue()));
            Float paycurrentNum = Float.parseFloat(payCount.get(payCount.size() - 1).get("value").toString());
            Float payyesterdayNum = Float.parseFloat(payCount.get(payCount.size() - 2).get("value").toString());
            requestParams.put("todayPayValue", paycurrentNum);
            requestParams.put("yesterdayPayProportion", StatisticsDate.getProportion(paycurrentNum.floatValue(), payyesterdayNum.floatValue()));
            return requestParams;
        }catch (Exception e){
            log.info("支付漏斗第二层 下单统计包括未付款状态 异常:{}",e);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("todayBuyerValue", 0);
            requestParams.put("yesterdayBuyerProportion", 0);
            requestParams.put("todayPayValue", 0);
            requestParams.put("yesterdayPayProportion", 0);
            return requestParams;
        }

    }

    /**
     * 支付漏斗第三层 下单统计包括已付款状态
     *
     * @param siteId
     * @param endTime
     * @param advanceDays
     * @return
     */
    @Transactional
    public Map<String, Object> thirdFunnelStatics(Integer siteId, String endTime, Integer advanceDays, Integer isPayment) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        try{
            String eTime = endTime + " 23:59:59";
            String sTime = endTime + " 00:00:00";
            Date endDate = StatisticsDate.processString2Date(eTime);
            Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
            String aTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") ;
            String bTime = aTime + " 00:00:00";
            String cTime = aTime + " 23:59:59";
            List<Integer> postStyles = new ArrayList<>();
            postStyles.add(150);
            postStyles.add(160);
            String string = webPageMapper.getFirstFunnelStatistics(siteId,bTime);
            //下单会员数量
            List<Map<String, Object>> list = tradesMapper.funnelPaymemberCount(siteId, sTime, eTime, isPayment, postStyles);
            List<Map<String, Object>> list1 = new ArrayList<>();
            Double todayUnitPayValue = 0.00;
            Double todayPayTradesValue = 0.00;
            if (!StringUtil.isEmpty(string)){
                Map<String,Object> map = JacksonUtils.json2map(string);
                Map<String,Object> mapRatio = (Map<String,Object>)map.get("ratio");
                Map<String,Object> mapFirstFunnel = (Map<String,Object>)mapRatio.get("funnel");
                Map<String,Object> hMap = (Map<String,Object>)mapFirstFunnel.get("thirdFunnel");
                Float value = Float.parseFloat(hMap.get("todayPayBuyerValue").toString());
                todayUnitPayValue = Double.parseDouble(hMap.get("todayUnitPayValue").toString());
                todayPayTradesValue = Double.parseDouble(hMap.get("todayPayTradesValue").toString());
                Map<String,Object> oMap = new HashMap<>();
                oMap.put("value",value);
                oMap.put("query_time",startDate);
                list1.add(oMap);
            }else {
                list1 = tradesMapper.funnelPaymemberCount(siteId, bTime, cTime, isPayment, postStyles);
            }
            list.addAll(list1);
            List<Map<String, Object>> buyerCount = rearragement(list, startDate, endTime);
            Float currentNum = Float.parseFloat(buyerCount.get(buyerCount.size() - 1).get("value").toString());
            Float yesterdayNum = Float.parseFloat(buyerCount.get(buyerCount.size() - 2).get("value").toString());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("todayPayBuyerValue", currentNum);
            requestParams.put("yesterdayPayBuyerProportion", StatisticsDate.getProportion(currentNum.floatValue(), yesterdayNum.floatValue()));

            //下单金额统计
            List<Map<String, Object>> list2 = tradesMapper.funnelPayBefoCount(siteId, sTime, eTime, isPayment, postStyles);
            List<Map<String, Object>> list3 = new ArrayList<>();
            if (!StringUtil.isEmpty(string)){
                Map<String,Object> oMap = new HashMap<>();
                oMap.put("value",todayPayTradesValue);
                oMap.put("query_time",startDate);
                list3.add(oMap);
            }else {
                list3 = tradesMapper.funnelPayBefoCount(siteId, bTime, cTime, isPayment, postStyles);
            }
            list2.addAll(list3);
            List<Map<String, Object>> payCount = rearragement(list2, startDate, endTime);
            Float payCountcurrentNum = Float.parseFloat(payCount.get(payCount.size() - 1).get("value").toString());
            Float payCountyesterdayNum = Float.parseFloat(payCount.get(payCount.size() - 2).get("value").toString());
            requestParams.put("todayPayTradesValue", payCountcurrentNum);
            requestParams.put("yesterdayPayTradesProportion", StatisticsDate.getProportion(payCountcurrentNum.floatValue(), payCountyesterdayNum.floatValue()));

            //客单价
            List<Map<String, Object>> list4 = tradesMapper.funnelUnitPay(siteId, sTime, eTime, isPayment, postStyles);
            List<Map<String, Object>> list5 = new ArrayList<>();
            if (!StringUtil.isEmpty(string)){
                Map<String,Object> oMap = new HashMap<>();
                oMap.put("value",todayUnitPayValue);
                oMap.put("query_time",startDate);
                list5.add(oMap);
            }else {
                list5 = tradesMapper.funnelUnitPay(siteId, bTime, cTime, isPayment, postStyles);
            }
            list4.addAll(list5);
            List<Map<String, Object>> unitpay = rearragement(list4, startDate, endTime);
            Float unitpaycurrentNum = Float.parseFloat(unitpay.get(unitpay.size() - 1).get("value").toString());
            Float unitpayyesterdayNum = Float.parseFloat(unitpay.get(unitpay.size() - 2).get("value").toString());
            requestParams.put("todayUnitPayValue", unitpaycurrentNum);
            requestParams.put("yesterdayUnitPayProportion", StatisticsDate.getProportion(unitpaycurrentNum.floatValue(), unitpayyesterdayNum.floatValue()));
            return requestParams;
        }catch (Exception e){
            log.info("支付漏斗第三层 下单统计包括已付款状态 异常",e);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("todayPayBuyerValue", 0);
            requestParams.put("yesterdayPayBuyerProportion", 0);
            requestParams.put("todayPayTradesValue", 0);
            requestParams.put("yesterdayPayTradesProportion", 0);
            requestParams.put("todayUnitPayValue", 0);
            requestParams.put("yesterdayUnitPayProportion", 0);
            return requestParams;
        }

    }

    /**
     * 区域支付排行榜
     */
    public Map<String, Object> getAeraPay(Map<String, Object> params) {
        Map<String, Object> resultParmas = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            String nowDay = String.valueOf(params.get("nowDay"));
            log.info("当前站点:{},截止时间{}", siteId, nowDay);
            String sTime = nowDay+" 00:00:00";
            String eTime = nowDay+" 23:59:59";

            //获取门店配送的区域支付金额
            List<Map<String, Object>> list = tradesMapper.getAeraPayDistribution(siteId, sTime,eTime);

            //将"湖南省长沙市岳麓区麓谷大道"中的 "岳麓区"拿出来，
            if (0 != list.size()) {
                for (Map<String, Object> mapDistribution : list) {
                    String str = mapDistribution.get("addr").toString();
                    if (str.indexOf("区") > -1) {
                        //市--区：上海市虹口区
                        if (!StringUtil.isEmpty(str.substring(str.indexOf("市") + 1, str.indexOf("区") + 1))) {
                            mapDistribution.put("addr", str.substring(str.indexOf("市") + 1, str.indexOf("区") + 1));
                            //市--县：宿迁市泗洪县
                        } else if (!StringUtil.isEmpty(str.substring(str.indexOf("市") + 1, str.indexOf("县") + 1))) {
                            mapDistribution.put("addr", str.substring(str.indexOf("市") + 1, str.indexOf("县") + 1));
                        }
                    } else {
                        //市--市：苏州市昆山市
                        String s = str.substring(str.indexOf("市") + 1, str.length());
                        mapDistribution.put("addr", s.substring(0, s.indexOf("市") + 1));
                    }
                }
            }
            //获取自提的门店区域
            List<Map<String, Object>> list2 = tradesMapper.getAeraPayPackUp(siteId, sTime,eTime);

            //将配送和自提的区域结合起来，如果有相同的区域名称，支付金额相加
            list2.addAll(list);
            List<Map<String, Object>> resultPackUp = qvChong(list2);
            //返回
            if (resultPackUp.size() > 0) {
                areaListSort(resultPackUp);
                resultParmas.put("result", resultPackUp);
                return resultParmas;
            } else {
                resultParmas.put("code", -1);
                resultParmas.put("message", "查询区域支付排行榜异常,或当天没有支付信息");
                return resultParmas;
            }

        } catch (Exception e) {
            log.info("查询区域支付排行榜异常:{}", e);
            resultParmas.put("code", -1);
            resultParmas.put("message", "查询区域支付排行榜异常");
            return resultParmas;
        }
    }

    /**
     * 门店支付排行榜
     */
    public Map<String, Object> getStorePay(Map<String, Object> params) {
        Map<String, Object> resultParmas = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(String.valueOf(params.get("siteId")));
            String nowDay = String.valueOf(params.get("nowDay"));
            String sTime = nowDay+" 00:00:00";
            String eTime = nowDay+" 23:59:59";
            log.info("当前站点:{},截止时间{}", siteId, nowDay);

            //获取门店配送的区域支付金额
            List<Map<String, Object>> list = tradesMapper.getStorePay(siteId, sTime,eTime);
            //返回
            if (list.size() > 0) {
                resultParmas.put("result", list);
                return resultParmas;
            } else {
                resultParmas.put("code", -1);
                resultParmas.put("message", "查询门店支付排行榜异常,或当天没有支付信息");
                return resultParmas;
            }
        } catch (Exception e) {
            log.info("查询门店付排行榜异常:{}", e);
            resultParmas.put("code", -1);
            resultParmas.put("message", "查询门店支付排行榜异常");
            return resultParmas;
        }
    }


    //区域去重
    public List<Map<String, Object>> qvChong(List<Map<String, Object>> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> m1 = list.get(i);
                for (int j = i + 1; j < list.size(); j++) {
                    Map<String, Object> m2 = list.get(j);
                    //如果区域名称相同，将金额相加，并删除一个
                    if (m1.get("addr").toString().equals(m2.get("addr").toString())) {
                        Double d1 = Double.parseDouble(String.valueOf(m1.get("pay_count")));
                        Double d2 = Double.parseDouble(String.valueOf(m2.get("pay_count")));
                        m1.put("pay_count", d1 + d2);
                        list.remove(j);
                        continue;
                    }
                }
            }
            return list;
        } catch (Exception e) {
            log.info("区域去重异常:{}", e);
            return null;
        }

    }
    //List<Map>排序
    public void areaListSort(List<Map<String, Object>> list) throws Exception {
        Collections.sort(list, new Comparator<Map<String, Object>>() {

            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double updateDate1 = Double.parseDouble(o1.get("pay_count").toString());
                Double updateDate2 = Double.parseDouble(o2.get("pay_count").toString());
                return updateDate2.compareTo(updateDate1);
            }
        });
    }

    /**
     * ( 临时 )获取新访客数
     */
    public List<Map<String, Object>> getNewVisitor(Integer siteId, String endTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        List<Map<String, Date>> dates = StatisticsDate.processDate(endDate, advanceDays + 1);
        List<Map<String, Object>> list = webPageMapper.queryNewVisitor(siteId, dates);
        Collections.reverse(list);
        return list;
    }

    /**
     * 人均浏览数
     */
    public Object getAverageVisitor(Integer siteId, String endTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
//        List<Map<String, Object>> rs = rearragement(webPageMapper.queryAverageVisitor(siteId, datesMap), startDate, endTime);
        //List<Map<String, Object>> currentResult = rearragement(webPageMapper.selectMemberCount(siteId, datesMap), startDate, endTime);
        List<Map<String, Object>> rs = webPageMapper.queryWebVisitors2(siteId, datesMap);
        List<Map<String, Object>> mm = rearragement(rs, StatisticsDate.processString2Date(sTime), endTime);
        List<Map<String, Object>> currentResult = rearragement(webPageMapper.onlineVisitorsFunnel(siteId, sTime, eTime), startDate, endTime);
        if (currentResult == null || currentResult.size() <= 0) {
            return rs;
        }
        boolean flag = true;
        for (int i = 0; i < mm.size(); i++) {
            Map<String, Object> map1 = mm.get(i);
            Map<String, Object> map2 = currentResult.get(i);
            Object v1 = map1.get("value");
            Object v2 = map2.get("value");
            float value = 0;
            flag = "0".equals(v1.toString()) ? false : true;
            flag = "0".equals(v2.toString()) ? false : true;
            if (flag) {
                float i1 = Float.parseFloat(v1.toString());
                float i2 = Float.parseFloat(v2.toString());
                value = i1 / i2;
                BigDecimal b = new BigDecimal(value);
                value = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            }
            map1.put("value", value);
        }
        return mm;
    }

    /**
     * 时段访问
     */
    public Object selectWebMemberCountByHour(Integer siteId, String endTime) {
        log.info("当前站点:{},截止时间{}", siteId, endTime);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        String sTime = endTime + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
        List<Map<String, Object>> rs = webPageMapper.selectWebMemberCountByHour(siteId, datesMap);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Date time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Integer hour;
        if (endTime.equals(sdf.format(time))) {
            String h = DateUtils.formatDate(time, "HH");
            hour = Integer.parseInt(h);
        } else {
            hour = 23;
        }
        int c = 0;
        for (int i = 0; i <= hour; i++) {
            String s = String.format("%02d", ++c);
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("query_time", s + ":00");
            temp.put("value", 0);
            result.add(temp);
        }
        for (Map<String, Object> temp : rs) {
            Object query_time = temp.get("query_time");
            hour = Integer.parseInt(query_time.toString());
            Map<String, Object> map = result.get(hour);
            map.put("value", temp.get("value"));
        }
        //result.remove(result.size() - 1);
        return result;
    }

    /**
     * 时段最高峰
     */
    public Map<String, Object> selectWebMemberCountByHourMax(Integer siteId, String endTime) {
        List<Map<String, Object>> result = (List<Map<String, Object>>) selectWebMemberCountByHour(siteId, endTime);
        Map<String, Object> map = new HashMap<>();
        Collections.sort(result, new MapComparatorDesc());
        Map<String, Object> comMap = result.get(0);
        String i = comMap.get("query_time").toString();
        String str = timeCom(i);
        map.put("maxTime", str);
        return map;
    }

    /**
     * 性别分布
     */
    public Object sexDistribution(Integer siteId, String endTime) {
        log.info("当前站点:{},截止时间{}", siteId, endTime);
        String eTime = endTime + " 23:59:59";
        String sTime = endTime + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
        Long l8 = new Date().getTime();
        List<Map<String, Object>> list = webPageMapper.sexDistribution(siteId, datesMap);
        log.info("性别sql执行时间++++++++++++++++++++++++++++++++:{}", new Date().getTime() - l8);
        Map<String, Integer> sex = new HashMap<String, Integer>();
        sex.put("woman", 0);
        sex.put("man", 0);
        sex.put("unknown", 0);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer sex1 = Integer.parseInt(temp.get("sex").toString());
            switch (sex1) {
                case 0: {
                    Integer value = Integer.parseInt(temp.get("value").toString());
                    sex.put("woman", sex.get("woman") + value);
                    break;
                }
                case 1: {
                    Integer value = Integer.parseInt(temp.get("value").toString());
                    sex.put("man", sex.get("man") + value);
                    break;
                }
                case 3: {
                    Integer value = Integer.parseInt(temp.get("value").toString());
                    sex.put("unknown", sex.get("unknown") + value);
                    break;
                }
                case -1: {
                    Integer value = Integer.parseInt(temp.get("value").toString());
                    sex.put("unknown", sex.get("unknown") + value);
                    break;
                }
            }
        }
        int count = sex.get("woman") + sex.get("man") + sex.get("unknown");
        Float todayValue = Float.parseFloat(getAllVisitor(siteId, endTime, 0).get(0).get("value").toString());
        if (todayValue != null) {
            Integer unknown = todayValue.intValue() - count;
            if (unknown > 0) {
                sex.put("unknown", sex.get("unknown") + unknown);
            }
        }
        log.info("性别方法执行时间++++++++++++++++++++++++++++++++:{}", new Date().getTime() - l8);
        return sex;
    }


    public Object ageDistribution(Integer siteId, String endTime) {
        log.info("当前站点:{},截止时间{}", siteId, endTime);
        String eTime = endTime + " 23:59:59";
        String sTime = endTime + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
        Long l8 = new Date().getTime();
        List<Map<String, Object>> list = webPageMapper.ageDistribution(siteId, datesMap);
        log.info("年龄SQL执行时间++++++++++++++++++++++++++++++++:{}", new Date().getTime() - l8);
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("other", 0);
        result.put("youth", 0);
        result.put("adult", 0);
        result.put("mold", 0);
        result.put("old", 0);
        result.put("minor", 0);
        result.put("middle", 0);
        for (Map<String, Object> temp : list) {
            int age = Integer.parseInt(temp.get("age").toString());
            int value = Integer.parseInt(temp.get("value").toString());
            if (age > 200) {
                result.put("other", result.get("other") + value);
            }
            if (age < 0) {
                result.put("other", result.get("other") + value);
            }
            if (age >= 0 && age < 18) {
                result.put("youth", result.get("youth") + value);
            }
            if (age >= 18 && age < 25) {
                result.put("adult", result.get("adult") + value);
            }
            if (age >= 25 && age < 35) {
                result.put("mold", result.get("mold") + value);
            }
            if (age >= 35 && age < 50) {
                result.put("old", result.get("old") + value);
            }
            if (age >= 50 && age < 60) {
                result.put("minor", result.get("minor") + value);
            }
            if (age >= 60 && age <= 200) {
                result.put("middle", result.get("middle") + value);
            }
        }
        int count = result.get("other") + result.get("youth") + result.get("adult") + result.get("mold") + result.get("old") + result.get("minor") + result.get("middle");
        Float todayValue = Float.parseFloat(getAllVisitor(siteId, endTime, 0).get(0).get("value").toString());
        if (todayValue != null) {
            Integer unknown = todayValue.intValue() - count;
            if (unknown > 0) {
                result.put("other", result.get("other") + unknown);
            }
        }
        log.info("年龄方法执行时间++++++++++++++++++++++++++++++++:{}", new Date().getTime() - l8);
        return result;
    }

    public Object bounceRate(Integer siteId, String endTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        String eTime = endTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
        List<Map<String, Date>> dates = StatisticsDate.processDate(endDate, advanceDays + 1);
        List<Map<String, Object>> list = webPageMapper.bounceRate(siteId, dates);
        List<Map<String, Object>> rearragement = rearragement(list, startDate, endTime);
        List<Map<String, Object>> currentResult = rearragement(webPageMapper.selectMemberCount(siteId, datesMap), startDate, endTime);
        if (currentResult == null || currentResult.size() <= 0) {
            return rearragement;
        }
        boolean flag = true;
        for (int i = 0; i < rearragement.size(); i++) {
            Map<String, Object> map1 = rearragement.get(i);
            Map<String, Object> map2 = currentResult.get(i);
            String s1 = map1.get("value").toString();
            String s2 = map2.get("value").toString();
            Float v1 = Float.parseFloat(s1);
            Float v2 = Float.parseFloat(s2);
            flag = "0".equals(s1) ? true : false;
            flag = "0".equals(s2) ? true : false;
            if (flag) {
                map1.put("value", v1 / v2);
            } else {
                map1.put("value", 0f);
            }
        }
        return rearragement;
    }

    public Object areaDistribution(Integer siteId, String endTime) throws Exception {
        log.info("当前站点:{},截止时间{}", siteId, endTime);
        String eTime = endTime + " 23:59:59";
        String sTime = endTime + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
        //List<Map<String, Object>> list = getAllVisitor(siteId, endTime, 0);
        List<Map<String, Object>> maps = webPageMapper.areaDistribution(siteId, datesMap);
        Integer value = 0;
        for (Map<String, Object> ms : maps) {
            value += Integer.parseInt(ms.get("value").toString());
        }
        for (Map<String, Object> temp : maps) {
            Float v1 = Float.parseFloat(temp.get("value").toString());
            temp.put("ratio", v1 / value);
        }
        List<Map<String, Object>> list1 = ipQvChong(maps);
        listSort(list1);
        if (10 >= list1.size()) {
            return list1;
        } else {
            list1 = list1.subList(0, 10);
            return list1;
        }

    }

    //IP去重
    public List<Map<String, Object>> ipQvChong(List<Map<String, Object>> oldList) {
        try {
            List<Map<String, Object>> newList = new ArrayList<>();

            for (int i = 0; i < oldList.size(); i++) {
                Map<String, Object> oldMap = oldList.get(i);
                if (newList.size() > 0) {
                    boolean isContain = false;
                    for (int j = 0; j < newList.size(); j++) {
                        Map<String, Object> newMap = newList.get(j);
                        if (newMap.get("ip").equals(oldMap.get("ip"))) {
                            Double d1 = Double.parseDouble(String.valueOf(newMap.get("ratio")));
                            Double d2 = Double.parseDouble(String.valueOf(oldMap.get("ratio")));
                            newMap.put("ratio", d1 + d2);
                            Double d3 = Double.parseDouble(String.valueOf(newMap.get("value")));
                            Double d4 = Double.parseDouble(String.valueOf(oldMap.get("value")));
                            newMap.put("value", d3 + d4);
                            isContain = true;
                            //oldList.remove(oldMap);
                            continue;
                        }
                    }
                    if (!isContain) {
                        newList.add(oldMap);
                    }

                } else {
                    newList.add(oldMap);
                }
            }
            return newList;
        } catch (Exception e) {
            log.info("区域去重异常:{}", e);
            return null;
        }

    }

    public Map sortMap(Map oldMap) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> arg0,
                               Map.Entry<String, Integer> arg1) {
                return arg0.getValue() - arg1.getValue();
            }
        });
        Map newMap = new LinkedHashMap();
        for (int i = 0; i < list.size(); i++) {
            newMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return newMap;
    }

    /**
     * 商品停留时间
     *
     * @param siteId
     * @param sTime
     * @param advanceDays
     * @return
     */
    public List<Map<String, Object>> getAvgReadTime(Integer siteId, String sTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, sTime, advanceDays);
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> resule = new HashMap<>();
            Map<String, Object> iMap = new HashMap<>();
            if (booleanDay(sTime)) {
                Long l8 = new Date().getTime();
                //当天数据
                iMap = webPageMapper.getAvgReadTimeToday(siteId, sTime);
                log.info("sql执行时间1111:{}", new Date().getTime() - l8);
                //当天之前的数据日期时间转换
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(sTime);
                String time = getNextDay(date);
                String avgReadTime = webPageMapper.getAvgReadTime(siteId, time);
                log.info("sql执行时间**++++++++++++++++++++++++++++++++**:{}", new Date().getTime() - l8);
                //判断之前的数据是否为空
                list = objToList(avgReadTime, iMap, sTime, siteId, advanceDays);
                Collections.reverse(list);
                log.info("方法执行时间**++++++++++++++++++++++++++++++++**:{}", new Date().getTime() - l8);
            } else {

                String string = webPageMapper.getAvgReadTime(siteId, sTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(sTime);
                String time = getNextDay(date);
                String avgReadTime = webPageMapper.getAvgReadTime(siteId, time);

                if (!StringUtil.isEmpty(string)) {
                    Map<String, Object> sMap = JacksonUtils.json2map(string);
                    list = (List<Map<String, Object>>) sMap.get("data");
                } else {
                    if (!StringUtil.isEmpty(avgReadTime)) {
                        Map<String, Object> avgReadMap = webPageMapper.getAvgReadTime2Init(siteId, sTime);
                        list = objToList(avgReadTime, avgReadMap, sTime, siteId, advanceDays);
                        Collections.reverse(list);
                    } else {
                        list = objToList(string, iMap, sTime, siteId, advanceDays);
                        Collections.reverse(list);
                    }
                }
            }

            if (30 == advanceDays) {
                list = list.subList(list.size() - 31, list.size());
            } else if (7 == advanceDays) {
                list = list.subList(list.size() - 8, list.size());
            } else {
                list = list.subList(list.size() - advanceDays, list.size());
            }
            return list;
        } catch (Exception e) {
            log.info("错误:{}", e);
            return null;
        }

    }

    //获取提前天数的日期
    public static List<String> aheadDay(List<Map<String, Date>> dates) {
        List<String> list = new ArrayList<>();
        for (Map<String, Date> map : dates) {
            Date date = map.get("start");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str = sdf.format(date);
            list.add(str);
        }
        return list;
    }

    //判断传过来的时间是否是当前时间
    public static Boolean booleanDay(String sTime) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (sTime.equals(sdf.format(date))) {
            return true;
        } else {
            return false;
        }
    }


    //获取昨天的时间
    public static String getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;
    }

    //时间段访问量比较器
    static class MapComparatorDesc implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            Integer v1 = Integer.valueOf(m1.get("value").toString());
            Integer v2 = Integer.valueOf(m2.get("value").toString());
            if (v2 != null) {
                return v2.compareTo(v1);
            }
            return 0;
        }
    }

    //判断时间字符串
    public String timeCom(String time) {
        Integer ite = Integer.parseInt(time.split(":")[0]);
        Integer iter = ite - 1;
        return iter + ":00" + " — " + ite + ":00";
    }

    //计算比例
    public Double gradeBoolean(Double d1, Double d2) {
        if (0 == d2) {
            return d1;
        } else {
            double v = ((d1 - d2) / d2) * 100;
            return v;
        }
    }


    //停留时间判断取值表
    public List<Map<String, Object>> objToList(String str, Map<String, Object> eMap, String sTime, Integer siteId, Integer advanceDays) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        if (!StringUtil.isEmpty(str)) {
            Map<String, Object> sMap = JacksonUtils.json2map(str);
            List<Map<String, Object>> lList = (List<Map<String, Object>>) sMap.get("data");
            Collections.reverse(lList);
            Map<String, Object> iMap = new HashMap<>();

            if (StringUtil.isEmpty(eMap)) {
                iMap.put("value", 0);
                iMap.put("query_time", sTime);
                lList.add(0, iMap);
                list.addAll(lList);
                return list;
            }
            lList.add(0, eMap);
            list.addAll(lList);
            return list;
        } else {
            log.info("当前站点:{},截止时间{}，提前天数{}", siteId, sTime, advanceDays);
            String eTime = sTime + " 23:59:59";
            Date endDate = StatisticsDate.processString2Date(eTime);
            List<Map<String, Date>> dates = StatisticsDate.processDate(endDate, advanceDays + 1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<String> nList = new ArrayList<>();
            for (Map<String, Date> map : dates) {
                String time = sdf.format(map.get("start"));
                nList.add(time);
            }
            List<Map<String, Object>> result = webPageMapper.getAvgReadTimeAll(siteId, nList);
            return result;
        }

    }

    /**
     * 重做新访客  查询当日新访客
     */

    public Object getNewVisitor2(Integer siteId, String sTime, Integer advanceDays) {
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, sTime, advanceDays);
        String eTime = sTime + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
        if (advanceDays == null) {
            Map<String, Date> dates = StatisticsDate.processOneDate(endDate);
            List<String> member_ids = webPageMapper.queryMemberIdsByOneDays(siteId, dates);
            List<String> open_ids = webPageMapper.queryOpenIdsByOneDays(siteId, dates, member_ids);
            List<String> ips = webPageMapper.queryIpsByOneDays(siteId, dates);
            result.put("query_time", sTime);
            result.put("value", member_ids.size() + open_ids.size() + ips.size());
            return result;
        } else {
            List<Map<String, Date>> list = StatisticsDate.processDate(endDate, advanceDays + 1);
            for (Map<String, Date> dates : list) {
                List<String> member_ids = webPageMapper.queryMemberIdsByOneDays(siteId, dates);
                List<String> open_ids = webPageMapper.queryOpenIdsByOneDays(siteId, dates, member_ids);
                List<String> ips = webPageMapper.queryIpsByOneDays(siteId, dates);
                Map<String, Object> temp = new HashMap<String, Object>();
                temp.put("query_time", DateUtils.formatDate(dates.get("start"), "yyyy-MM-dd"));
                temp.put("value", member_ids.size() + open_ids.size() + ips.size());
                rs.add(temp);
            }
            return rs;
        }
    }

    /**
     * 查询老访客
     *
     * @param siteId
     * @param endTime
     * @param advanceDays
     * @return
     */
    public List<Map<String, Object>> selectOldVisitors(Integer siteId, String endTime, Integer advanceDays){
        List<Map<String, Object>> oldVisitors = new ArrayList<>();
        log.info("当前站点:{},截止时间{}，提前天数{}", siteId, endTime, advanceDays);
        try{
            Long l11 = new Date().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //获取昨天时间
            Date date = sdf.parse(endTime);
            String time = getNextDay(date);
            //去记录表查询昨天的数据
            String yesToday = webPageMapper.getYestodayData(siteId,time);
            if (!StringUtil.isEmpty(yesToday)){
                String eTime = endTime + " 23:59:59";
                String sTime = endTime + " 00:00:00";
                Map<String, Object> oldVisitor = new HashMap<>();
                Integer currentNum;
                if (true == booleanDay(sTime)){
                    currentNum = webPageMapper.selectopenIdCountTwo(siteId, sTime);//会员编号为空且openid不为空游客中老访客数量
                }else {
                    currentNum = webPageMapper.selectopenIdCountThree(siteId, sTime,eTime);//会员编号为空且openid不为空游客中老访客数量
                }
                oldVisitor.put("query_time", endTime);
                oldVisitor.put("value", currentNum);
                oldVisitors.add(oldVisitor);
                Map<String, Object> yesMap = JacksonUtils.json2map(yesToday);
                List<Map<String, Object>> yesList = (List<Map<String, Object>>) yesMap.get("data");
                yesList.addAll(oldVisitors);
                yesList = yesList.subList(yesList.size()-(advanceDays+1),yesList.size());
                log.info("查询老访客量逻辑执行时间+SQL执行时间888++++++++++++++++++++++++++++++++:{}", new Date().getTime() - l11);
                return yesList;
            }else {
                String eTime = endTime + " 23:59:59";
                Date endDate = StatisticsDate.processString2Date(eTime);
                Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -advanceDays);
                String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
                List<Map<String, Object>> OpenIds = webPageMapper.selectOpenIds(siteId, sTime, eTime);
                List<String> dates = DateUtils.getContinuousDayStr(DateUtils.formatDate(startDate, "yyyy-MM-dd"), endTime);
                for (String currentDate : dates) {
                    Map<String, Object> oldVisitor = new HashMap<>();
                    Integer currentNum = 0;
                    List<String> selectedOpenIds = new ArrayList<>();
                    for (Map<String, Object> map : OpenIds) {
                        if (map.get("query_time").toString().equals(currentDate)) {
                            if (!StringUtil.isEmpty(map.get("openId"))) {
                                selectedOpenIds.add(map.get("openId").toString());
                            }
                        }
                    }
                    if (selectedOpenIds.size() > 0) {
                        currentNum += webPageMapper.selectopenIdCount(siteId, currentDate, selectedOpenIds);//会员编号为空且openid不为空游客中老访客数量
                    }
                    oldVisitor.put("query_time", currentDate);
                    oldVisitor.put("value", currentNum);
                    oldVisitors.add(oldVisitor);
                }
                return oldVisitors;
            }

        }catch (Exception e){
            log.info("查询老访客异常:{}",e);
            return null;
        }

    }

    /**
     * 获取记录表中的中的流量排行榜
     *
     * @param siteId
     * @param endTime
     * @return
     * @throws Exception
     */
    public Map<String, Object> getFlowChartsMap(Integer siteId, String endTime) throws Exception {
        String str = webPageMapper.getFlowChartsMap(siteId, endTime);
        if (!StringUtil.isEmpty(str)) {
            Map<String, Object> map = JacksonUtils.json2map(str);
            return map;
        } else {
            return null;
        }
    }

    /**
     * 获取记录表中的中的交易排行榜
     *
     * @param siteId
     * @param endTime
     * @return
     * @throws Exception
     */
    public Map<String, Object> getTransChartsMap(Integer siteId, String endTime) throws Exception {
        String str = webPageMapper.getTransChartsMap(siteId, endTime);
        if (!StringUtil.isEmpty(str)) {
            Map<String, Object> map = JacksonUtils.json2map(str);
            return map;
        } else {
            return null;
        }
    }

    /**
     * 获取记录表中的中的交易分析数据
     *
     * @param siteId
     * @param endTime
     * @return
     * @throws Exception
     */
    public Map<String, Object> getTransMap(Integer siteId, String endTime, String type) {
        log.info("当前站点:{},截止时间{}，查询类型{}", siteId, endTime, type);
        try {
            String str = null;
            switch (type) {
                case "transMap":  //交易分析12图
                    str = webPageMapper.getTransMap(siteId, endTime);
                    break;
                case "transChartsMap":  //交易排行榜
                    str = webPageMapper.getTransChartsMap(siteId, endTime);
                    break;
                case "transPicMap":  //交易分析曲线图
                    str = webPageMapper.getTransPicMap(siteId, endTime);
                    break;
                case "transGraphMap":  //交易分析门店直购和转化漏斗
                    str = webPageMapper.getTransGraphMap(siteId, endTime);
                    break;
            }
            if (!StringUtil.isEmpty(str)) {
                Map<String, Object> map = JacksonUtils.json2map(str);
                if (!StringUtil.isEmpty(map)) {
                    Map<String, Object> oMap = (Map<String, Object>) map.get("ratio");
                    return oMap;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            log.info("交易分析查询异常:{}", e);
            return null;
        }
    }

    /**
     * 获取记录表中的中的交易分析曲线图
     *
     * @param siteId
     * @param endTime
     * @return
     * @throws Exception
     */
    public Map<String, Object> getTransPicMap(Integer siteId, String endTime) throws Exception {
        String str = webPageMapper.getTransPicMap(siteId, endTime);
        if (!StringUtil.isEmpty(str)) {
            Map<String, Object> map = JacksonUtils.json2map(str);
            return map;
        } else {
            return null;
        }
    }

    /**
     * 获取记录表中的中的交易分析门店直购和转化漏斗
     *
     * @param siteId
     * @param endTime
     * @return
     * @throws Exception
     */
    public Map<String, Object> getTransGraphMap(Integer siteId, String endTime) throws Exception {
        String str = webPageMapper.getTransGraphMap(siteId, endTime);
        if (!StringUtil.isEmpty(str)) {
            Map<String, Object> map = JacksonUtils.json2map(str);
            return map;
        } else {
            return null;
        }
    }

    /**
     * 修改数据表中的IP名称
     * @param start
     * @param end
     */
    public void ip2Name4DB(Integer start,Integer end){
        try{
            for (int i = start;i <= end;i++){
                if (end >= i){
                    //先根据ID查询IP
                    String ip = webPageMapper.selectIPForID(i);
                    //根据IP修改名称
                    String name = changeIPName(ip);
                    //修改表中的数据
                    Integer o = webPageMapper.updateIPForID(i,name);
                    if (1 != o){
                        log.info("区域名称修改异常");
                    }
                }else {
                    break;
                }
            }
        }catch (Exception e){
            log.info("数据库IP转换异常:{}",e);
        }
    }
    /**
     * 修改数据表中的停留时间
     * @param start
     * @param end
     */
    public void updateTime4DB(Integer start,Integer end){
        try{
            for (int id = start;id <= end;id++){
                if (end >= id){
                    //根据ID获取上一次的登录时间，和这一次的时间
                    Map<String,Object> ids = webPageMapper.selectIdsForID(id);//根据ID查询memberId,openId,ip,createTime
                    Date date = (Date)ids.get("createTime");
                    Long d = date.getTime();
                    Map<String, Object> remainTime = getRemainTime(ids,d,id);
                    //将时间相减的值赋予ID所在的对象
                    Long time = Long.parseLong(remainTime.get("stayTime").toString());
                    Integer i = webPageMapper.updateTime4DB(id,time);
                    if (1 != i){
                        log.info("停留时间修改异常");
                    }
                }else {
                    break;
                }
            }
        }catch (Exception e){
            log.info("数据库初始化停留时间异常:{}",e);
        }
    }

    /**
     * 修改数据状态
     * @param time
     */
    public void updateStruts(String time){
        try{
            String start = time + " 00:00:00";
            String end = time + " 23:59:59";
            webPageMapper.updateStruts(start,end);
        }catch (Exception e){
            log.info("修改数据状态异常:{}",e);
        }
    }





    //List<Map>排序
    public void listSort(List<Map<String, Object>> list) throws Exception {
        Collections.sort(list, new Comparator<Map<String, Object>>() {

            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double updateDate1 = Double.parseDouble(o1.get("value").toString());
                Double updateDate2 = Double.parseDouble(o2.get("value").toString());
                return updateDate2.compareTo(updateDate1);
            }
        });
    }

    //初始化获取IP名称
    public String changeIPName(String ip){

        return "未知区域"; //API失效，暂时返回未知区域
//        try{
//            String xIP;
//            String str = HttpClient.doHttpGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=JSON&ip=" + ip);
//            if ("-2".equals(str) || "-3".equals(str) || "-1".equals(str) || "city".equals(str)) {
//                xIP = "未知区域";
//            } else {
//                Map<String, Object> map = JacksonUtils.json2map(str);
//                if (1 == Integer.parseInt(String.valueOf(map.get("ret")))) {
//                    String addr = String.valueOf(map.get("city"));
//                    if (StringUtil.isEmpty(addr)) {
//                        xIP = "未知区域";
//                    } else {
//                        xIP = addr;
//                    }
//                } else {
//                    xIP = "未知区域";
//                }
//            }
//            return xIP;
//        }catch (Exception e){
//            log.info("区域转换异常:{}",e);
//            return "未知区域";
//        }
    }

    //初始化获取停留时间
    public Map<String,Object> getRemainTime(Map<String,Object> webPage,Long d,Integer id){
        try{
            //获取停留时间
            Long time;
            if (null == d){
                time = System.currentTimeMillis();
            }else {
                time = d;
            }
            Date date;
            if (!StringUtil.isEmpty(webPage.get("memberId"))) {  //根据memberId查询
                String memberId = webPage.get("memberId").toString();
                date = webPageMapper.findStayTime(memberId, null, null,id);
            } else if (!StringUtil.isEmpty(webPage.get("openId"))) {  //根据openId查询
                String openId = webPage.get("openId").toString();
                date = webPageMapper.findStayTime(null, openId, null,id);
            } else {//都不存在的时候根据IP查询
                String ip =webPage.get("ip") .toString();
                date = webPageMapper.findStayTime(null, null, ip,id);
            }
            if (!StringUtil.isEmpty(date)) {
                Long stayTimePP = date.getTime();
                Long stayTime = time - stayTimePP;
                log.info("时间值:{}",stayTime);
                if (3600000 < stayTime || 0 > stayTime) {
                    webPage.put("stayTime", 0);
                } else {
                    webPage.put("stayTime", stayTime);
                }
            } else {
                webPage.put("stayTime", 0);
            }
            return webPage;
        }catch (Exception e){
            log.info("获取停留时间异常:{}",e);
            webPage.put("stayTime", 0);
            return webPage;
        }
    }



    private  String TruncateUrlPage(String strURL)
    {
        String strAllParam=null;
        String[] arrSplit=null;
        strURL=strURL.trim();
        arrSplit=strURL.split("[?]");
        if(strURL.length()>1)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[1]!=null)
                {
                    strAllParam=arrSplit[1];
                }
            }
        }
        return strAllParam;
    }
    private  Map<String, String> URLRequest(String URL)
    {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit=null;
        String strUrlParam=TruncateUrlPage(URL);
        if(strUrlParam==null)
        {
            return mapRequest;
        }
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit)
        {
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");
            if(arrSplitEqual.length>1)
            {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
            else
            {
                if(arrSplitEqual[0]!="")
                {
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }


}
