package com.jk51.modules.merchant.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.modules.appInterface.controller.IMIndexCountService;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.merchant.mapper.StaticsRecordMapper;
import com.jk51.modules.merchant.mapper.WebPageMapper;
import com.jk51.modules.persistence.mapper.FlowAnalysisMapper;
import com.jk51.modules.persistence.mapper.SBMemberInfoMapper;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.persistence.mapper.TransAnalysisMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.el.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计数据定时添加数据
 */
@Component
public class TimerInsertService {

    @Autowired
    private DataProfileService dataProfileService;
    @Autowired
    private WebPageMapper webPageMapper;
    @Autowired
    FlowAnalysisMapper flowAnalysisMapper;
    @Autowired
    private IMIndexCountService imIndexCountService;
    @Autowired
    private StaticsRecordMapper staticsRecordMapper;
    @Autowired
    private SelectDataService selectDataService;
    @Autowired
    private TransAnalysisMapper transAnalysisMapper;
    @Autowired
    private SBMemberMapper sbMemberMapper;
    @Autowired
    private SBMemberInfoMapper sbMemberInfoMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private GoodsEsMapper goodsEsMapper;


    private static final Logger log = LoggerFactory.getLogger(TimerInsertService.class);

    /**
     * 数据概况添加
     * @param endTime
     * @throws Exception
     */
    @Transactional
    public void dataInsert(String endTime) throws Exception {
        Map<String,Object> param = new HashMap<>();
        param.put("advanceDay",30);
        param.put("advanceDays",30);
        param.put("endTime",endTime);
        param.put("sTime",endTime);

        //遍历所有的商户id，按照商户id添加当天数据
        List<Integer> siteId1 = getSiteId();
        for (Integer siteId : siteId1) {
            param.put("siteId", siteId);

            //排行榜用的map
            Map<String, Object> map = new HashedMap();
            map.put("nowDay", endTime);
            map.put("siteId", siteId);

            //支付金额
            List<Map<String, Object>> tradesMoneyBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, null);
            Map<String, Object> tradesMoneyMap = dataProfileService.getProportion(tradesMoneyBydatesMap);
            insertDate(tradesMoneyBydatesMap, tradesMoneyMap, "trades_money_bydates", "支付金额", siteId, endTime);
            //支付金额--送货上门
            List<Map<String, Object>> tradesMoneyToDoorBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, 150);
            Map<String, Object> toDoortMap = dataProfileService.getProportion(tradesMoneyToDoorBydatesMap);
            insertDate(tradesMoneyToDoorBydatesMap, toDoortMap, "trades_money_todoor_bydates", "支付金额—-送货上门", siteId, endTime);
            //支付金额--门店自提
            List<Map<String, Object>> tradesMoneyToStoreBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, 160);
            Map<String, Object> toStoreMap = dataProfileService.getProportion(tradesMoneyToStoreBydatesMap);
            insertDate(tradesMoneyToStoreBydatesMap, toStoreMap, "trades_money_tostore_bydates", "支付金额--门店自提", siteId, endTime);
            //支付金额--门店直购
            List<Map<String, Object>> tradesMoneyDirectBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, 170);
            Map<String, Object> DirectMap = dataProfileService.getProportion(tradesMoneyDirectBydatesMap);
            insertDate(tradesMoneyDirectBydatesMap, DirectMap, "trades_money_direct_bydates", "支付金额--门店直购", siteId, endTime);
            //订单数量统计
            List<Map<String, Object>> tradesCountBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, null);
            Map<String, Object> tradesCountMap = dataProfileService.getProportion(tradesCountBydatesMap);
            insertDate(tradesCountBydatesMap, tradesCountMap, "trades_Count_bydates", "订单数量", siteId, endTime);
            //订单数量统计--送货上门
            List<Map<String, Object>> tradesCountToDoorBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, 150);
            Map<String, Object> countToDoortMap = dataProfileService.getProportion(tradesCountToDoorBydatesMap);
            insertDate(tradesCountToDoorBydatesMap, countToDoortMap, "trades_Count_todoor_bydates", "订单数量—-送货上门", siteId, endTime);
            //订单数量统计--门店自提
            List<Map<String, Object>> tradesCountToStoreBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, 160);
            Map<String, Object> tradesCounttoStoreMap = dataProfileService.getProportion(tradesCountToStoreBydatesMap);
            insertDate(tradesCountToStoreBydatesMap, tradesCounttoStoreMap, "trades_Count_tostore_bydates", "订单数量--门店自提", siteId, endTime);
            //订单数量统计--门店直购
            List<Map<String, Object>> tradesCountDirectBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, 170);
            Map<String, Object> tradesCountDirectMap = dataProfileService.getProportion(tradesCountDirectBydatesMap);
            insertDate(tradesCountDirectBydatesMap, tradesCountDirectMap, "trades_Count_direct_bydates", "订单数量--门店直购", siteId, endTime);
            //订单客单价统计
            List<Map<String, Object>> tradesUnitPricesBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, null);
            Map<String, Object> tradesUnitPricesMap = dataProfileService.getProportion(tradesUnitPricesBydatesMap);
            insertDate(tradesUnitPricesBydatesMap, tradesUnitPricesMap, "trades_unitPrice_bydates", "支付金额", siteId, endTime);
            //订单客单价统计--送货上门
            List<Map<String, Object>> unitPriceToDoorBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, 150);
            Map<String, Object> unitPricetoDoortMap = dataProfileService.getProportion(unitPriceToDoorBydatesMap);
            insertDate(unitPriceToDoorBydatesMap, unitPricetoDoortMap, "trades_unitPrice_todoor_bydates", "支付金额—-送货上门", siteId, endTime);
            //订单客单价统计--门店自提
            List<Map<String, Object>> unitPriceToStoreBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, 160);
            Map<String, Object> unitPricetoStoreMap = dataProfileService.getProportion(unitPriceToStoreBydatesMap);
            insertDate(unitPriceToStoreBydatesMap, unitPricetoStoreMap, "trades_unitPrice_tostore_bydates", "支付金额--门店自提", siteId, endTime);
            //订单客单价统计--门店直购
            List<Map<String, Object>> unitPriceDirectBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, 170);
            Map<String, Object> unitPriceDirectMap = dataProfileService.getProportion(unitPriceDirectBydatesMap);
            insertDate(unitPriceDirectBydatesMap, unitPriceDirectMap, "trades_unitPrice_direct_bydates", "支付金额--门店直购", siteId, endTime);

            //注册会员
            List<Map<String, Object>> memberCountBydaysMap = dataProfileService.selectMemberCountBydays(siteId, endTime, 29);
            Map<String, Object> lmMap = dataProfileService.getProportion(memberCountBydaysMap);
            insertDate(memberCountBydaysMap, lmMap, "member_count", "注册会员", siteId, endTime);

            //积极店员
            List<Map<String, Object>> energyClerksMap = dataProfileService.selectEnergyClerks(siteId, endTime, 29);
            Map<String, Object> eMap = dataProfileService.getProportion(energyClerksMap);
            insertDate(energyClerksMap, eMap, "energy_clerks", "积极店员", siteId, endTime);

            //商品浏览量
            Map<String, Object> GoodsWebVisitorsMap = dataProfileService.queryGoodsWebVisitors(siteId, param);
            Map<String, Object> goodsWebVisitorsM = (Map<String, Object>) GoodsWebVisitorsMap.get("result");
            List<Map<String, Object>> goodsWebVisitorsMList = (List<Map<String, Object>>) goodsWebVisitorsM.get("result");
            Map<String, Object> goodsWebVisitorsMMap = (Map<String, Object>) goodsWebVisitorsM.get("proportion");
            insertDate(goodsWebVisitorsMList, goodsWebVisitorsMMap, "goods_web_visitors", "商品浏览量", siteId, endTime);

            //浏览总量
            Map<String, Object> WebVisitorsMap = dataProfileService.queryWebVisitors(siteId, param);
            Map<String, Object> webVisitorsM = (Map<String, Object>) WebVisitorsMap.get("result");
            List<Map<String, Object>> webVisitorsMList = (List<Map<String, Object>>) webVisitorsM.get("result");
            Map<String, Object> webVisitorsMMap = (Map<String, Object>) webVisitorsM.get("proportion");
            insertDate(webVisitorsMList, webVisitorsMMap, "web_visitors", "浏览总量", siteId, endTime);

            /*//咨询次数
            TreeSet<Map<String, Object>> advisoryHistory = imIndexCountService.getAdvisoryHistory(siteId, endTimeQi, 30);
            Map<String, Object> yestodayMap = imIndexCountService.getAdvisoryMap(siteId,endTimeQi,-1);
            Map<String, Object> weekMap = imIndexCountService.getAdvisoryMap(siteId,endTimeQi,-7);*/

            //咨询满意度

            //店员拉新数排行榜
            Map<String, Object> pullNewCountMap = dataProfileService.getPullNewCount(map);
            insertDate(null, pullNewCountMap, "pull_new_count", "店员拉新数排行榜", siteId, endTime);

            //商品支付金额排行榜
            Map<String, Object> paymentMoneyMap = dataProfileService.getPaymentMoney(map);
            insertDate(null, paymentMoneyMap, "payment_money", "商品支付金额排行榜", siteId, endTime);
        }

    }

    //执行添加
    public void insertDate(List<Map<String, Object>> list, Map<String, Object> map, String staticsName, String staticsDesc, Integer siteId, String time) {
        Map<String, Object> params = new HashedMap();
        Map<String, Object> dateMap = new HashedMap();
        dateMap.put("data", list);
        dateMap.put("ratio", map);
        String str = JacksonUtils.mapToJson(dateMap);
        params.put("siteId", siteId);
        params.put("staticsName", staticsName);
        params.put("staticsValue", str);
        params.put("staticsDesc", staticsDesc);
        params.put("createTime", time);
        Integer i = staticsRecordMapper.insertRecord(params);
        log.info("========================数据每天定时添加执行啦==============================:{},{}", siteId,i);
    }


    /**
     * 流量分析添加
     * @throws Exception
     */
    @Transactional
    public void dataInsertFlow(String sTime) throws Exception {

        Map<String, Object> params = new HashMap<>();
        //遍历所有的商户id，按照商户id添加当天数据
        List<Integer> siteId1 = getSiteId();
        for (Integer siteId : siteId1) {
        //Integer siteId = 100190;

            //总访客数
            List<Map<String, Object>> allVisitorList = dataProfileService.getAllVisitor(siteId, sTime, 30);
            //Map<String, Object> allVisitorMap = dataProfileService.firstFunnelStatistics(siteId, sTime, 7);
            Map<String, Object> allVisitorMap = dataProfileService.getFlowProportion(allVisitorList);
            insertDateFlow(allVisitorList, allVisitorMap, "all_visitor", "总访客数", siteId, sTime);

            //老访客数
            List<Map<String, Object>> oldVisitorList = dataProfileService.selectOldVisitors(siteId,sTime,30);
            Map<String, Object> oldVisitorMap = dataProfileService.getFlowProportion(oldVisitorList);
            insertDateFlow(oldVisitorList, oldVisitorMap, "old_visitor", "老访客数", siteId, sTime);

            //新访客数
            /*List<Map<String, Object>> newVisitorList = dataProfileService.getNewVisitor(siteId,sTime,30);
            Map<String, Object> newVisitorMap = dataProfileService.getFlowProportion(newVisitorList);
            insertDateFlow(newVisitorList, newVisitorMap, "new_visitor", "新访客数", siteId, sTime);*/
            List newVisitor=new ArrayList();
            List<Map<String, Object>> pList = allVisitorList;
            List<Map<String, Object>> oList = oldVisitorList;
            for(int i=0;i<pList.size();i++){
                Map<String, Object> pmap = pList.get(i);
                Map<String, Object> omap = oList.get(i);
                Integer totalCount=Integer.parseInt(pmap.get("value").toString());
                Integer oldCount=Integer.parseInt(omap.get("value").toString());
                Map temp=new HashMap<String,Object>();
                temp.put("query_time",pmap.get("query_time"));
                temp.put("value",totalCount-oldCount);
                newVisitor.add(temp);
            }
            Map proportion = dataProfileService.getFlowProportion(newVisitor);
            insertDateFlow(newVisitor, proportion, "new_visitor", "新访客数", siteId, sTime);

            //人均浏览数
            List<Map<String, Object>> avgReadList = (List<Map<String, Object>>) dataProfileService.getAverageVisitor(siteId,sTime,30);
            Map<String, Object> avgReadMap = dataProfileService.getFlowProportion(avgReadList);
            insertDateFlow(avgReadList, avgReadMap, "avg_read", "人均浏览数", siteId, sTime);

            //商品停留时间
            List<Map<String, Object>> avgReadTimeList = dataProfileService.getAvgReadTime(siteId,sTime,30);
            Map<String, Object> avgReadTimeMap = dataProfileService.getFlowProportion(avgReadTimeList);
            insertDateFlow(avgReadTimeList, avgReadTimeMap, "avg_read_time", "商品停留时间", siteId, sTime);

            //排行榜
            List<Map<String, Object>> mapTime = (List<Map<String, Object>>) dataProfileService.selectWebMemberCountByHour(siteId,sTime);//时段
            Map<String, Object> mapMax = dataProfileService.selectWebMemberCountByHourMax(siteId,sTime); //时段最高峰
            List<Map<String, Object>> mapArea = (List<Map<String, Object>>) dataProfileService.areaDistribution(siteId, sTime);//区域
            Map<String, Object> mapAge = (Map<String, Object>) dataProfileService.ageDistribution(siteId,sTime);//年龄
            Map<String, Object> mapSex = (Map<String, Object>) dataProfileService.sexDistribution(siteId,sTime);//性别
            params.put("mapTime",mapTime);
            params.put("mapArea",mapArea);
            params.put("mapAge",mapAge);
            params.put("mapSex",mapSex);
            params.put("mapMax",mapMax);
            insertDateFlow(null, params, "flow_charts", "排行榜", siteId, sTime);
        }
    }
    //执行流量分析数据记录添加
    public void insertDateFlow(List<Map<String, Object>> list, Map<String, Object> map, String staticsName, String staticsDesc, Integer siteId, String time) {
        Map<String, Object> params = new HashedMap();
        Map<String, Object> dateMap = new HashedMap();
        dateMap.put("data", list);
        dateMap.put("ratio", map);
        String str = JacksonUtils.mapToJson(dateMap);
        params.put("siteId", siteId);
        params.put("staticsName", staticsName);
        params.put("staticsValue", str);
        params.put("staticsDesc", staticsDesc);
        params.put("createTime",time);
        int i = flowAnalysisMapper.insertRemain(params);
        log.info("========================流量分析数据每天定时添加执行啦==============================:{},{}", siteId,i);
    }

    /**
     * 流量分析添加
     * @throws Exception
     */
    @Transactional
    public void dataInsertTransaction(String endTime) throws Exception {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> graph = new HashMap<>();
        //遍历所有的商户id，按照商户id添加当天数据
        List<Integer> siteId1 = getSiteId();
        for (Integer siteId : siteId1) {
            //支付金额
            List<Map<String, Object>> tradesMoneyBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, null);
            Map<String, Object> tradesMoneyMap = dataProfileService.getProportion(tradesMoneyBydatesMap);
            graph.put("tradesMoneyBydatesMap",tradesMoneyBydatesMap);
            params.put("tradesPay",tradesMoneyMap);

            //支付金额--送货上门
            List<Map<String, Object>> tradesMoneyToDoorBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, 150);
            Map<String, Object> toDoortMap = dataProfileService.getProportion(tradesMoneyToDoorBydatesMap);
            graph.put("tradesMoneyToDoorBydatesMap",tradesMoneyToDoorBydatesMap);
            params.put("trades_ToDoorPay",toDoortMap);


            //支付金额--门店自提
            List<Map<String, Object>> tradesMoneyToStoreBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, 160);
            Map<String, Object> toStoreMap = dataProfileService.getProportion(tradesMoneyToStoreBydatesMap);
            graph.put("tradesMoneyToStoreBydatesMap",tradesMoneyToStoreBydatesMap);
            params.put("trades_StoreToPay",toStoreMap);

            //支付金额--门店直购
            List<Map<String, Object>> tradesMoneyDirectBydatesMap = dataProfileService.selectTradesMoneyBydates(siteId, endTime, 29, 170);
            Map<String, Object> DirectMap = dataProfileService.getProportion(tradesMoneyDirectBydatesMap);
            graph.put("tradesMoneyDirectBydatesMap",tradesMoneyDirectBydatesMap);
            params.put("trades_directPay",DirectMap);

            //订单数量统计
            List<Map<String, Object>> tradesCountBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, null);
            Map<String, Object> tradesCountMap = dataProfileService.getProportion(tradesCountBydatesMap);
            graph.put("tradesCountBydatesMap",tradesCountBydatesMap);
            params.put("tradesCount",tradesCountMap);

            //订单数量统计--送货上门
            List<Map<String, Object>> tradesCountToDoorBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, 150);
            Map<String, Object> countToDoortMap = dataProfileService.getProportion(tradesCountToDoorBydatesMap);
            graph.put("tradesCountToDoorBydatesMap",tradesCountToDoorBydatesMap);
            params.put("tradesCount_ToDoorPay",countToDoortMap);

            //订单数量统计--门店自提
            List<Map<String, Object>> tradesCountToStoreBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, 160);
            Map<String, Object> tradesCounttoStoreMap = dataProfileService.getProportion(tradesCountToStoreBydatesMap);
            graph.put("tradesCountToStoreBydatesMap",tradesCountToStoreBydatesMap);
            params.put("tradesCount_StoreToPay",tradesCounttoStoreMap);

            //订单数量统计--门店直购
            List<Map<String, Object>> tradesCountDirectBydatesMap = dataProfileService.selectTradesCountBydates(siteId, endTime, 29, 170);
            Map<String, Object> tradesCountDirectMap = dataProfileService.getProportion(tradesCountDirectBydatesMap);
            graph.put("tradesCountDirectBydatesMap",tradesCountDirectBydatesMap);
            params.put("tradesCount_directPay",tradesCountDirectMap);

            //订单客单价统计
            List<Map<String, Object>> tradesUnitPricesBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, null);
            Map<String, Object> tradesUnitPricesMap = dataProfileService.getProportion(tradesUnitPricesBydatesMap);
            graph.put("tradesUnitPricesBydatesMap",tradesUnitPricesBydatesMap);
            params.put("unitPay",tradesUnitPricesMap);

            //订单客单价统计--送货上门
            List<Map<String, Object>> unitPriceToDoorBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, 150);
            Map<String, Object> unitPricetoDoortMap = dataProfileService.getProportion(unitPriceToDoorBydatesMap);
            graph.put("unitPriceToDoorBydatesMap",unitPriceToDoorBydatesMap);
            params.put("unitPay_ToDoorPay",unitPricetoDoortMap);

            //订单客单价统计--门店自提
            List<Map<String, Object>> unitPriceToStoreBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, 160);
            Map<String, Object> unitPricetoStoreMap = dataProfileService.getProportion(unitPriceToStoreBydatesMap);
            graph.put("unitPriceToStoreBydatesMap",unitPriceToStoreBydatesMap);
            params.put("unitPay_StoreToPay",unitPricetoStoreMap);

            //订单客单价统计--门店直购
            List<Map<String, Object>> unitPriceDirectBydatesMap = dataProfileService.selectunitPriceByDates(siteId, endTime, 29, 170);
            Map<String, Object> unitPriceDirectMap = dataProfileService.getProportion(unitPriceDirectBydatesMap);
            graph.put("unitPriceDirectBydatesMap",unitPriceDirectBydatesMap);
            params.put("unitPay_directPay",unitPriceDirectMap);
            insertDataTrans(null, params, "trade_analysis", "交易分析12图", siteId, endTime);
            insertDataTrans(null, graph, "trade_graph", "交易分析曲线图", siteId, endTime);

            //门店直购和漏斗数据
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> direct = dataProfileService.selectDirectStores(siteId, endTime, 170);//查询直购订单数据
            Map<String, Object> funnel = dataProfileService.selectWebPayFunnel(siteId, endTime, 1);//查询支付漏斗数据
            response.put("direct",direct);
            response.put("funnel",funnel);
            insertDataTrans(null, response, "direct_funnel_data", "门店直购和漏斗数据", siteId, endTime);

            //排行榜
            Map<String, Object> charts = new HashMap<>();
            Map<String, Object> eMap = new HashMap<>();
            eMap.put("siteId",siteId);
            eMap.put("nowDay",endTime);
            Map<String, Object> mapStorePay = dataProfileService.getStorePay(eMap);//门店支付排行榜
            Map<String, Object> mapAeraPay = dataProfileService.getAeraPay(eMap);//区域支付排行榜
            charts.put("mapStorePay",mapStorePay);
            charts.put("mapAeraPay",mapAeraPay);
            insertDataTrans(null, charts, "pay_ranking_lis", "排行榜", siteId, endTime);

        }
    }
    //执行交易分析数据记录添加
    public void insertDataTrans(List<Map<String, Object>> list, Map<String, Object> map, String staticsName, String staticsDesc, Integer siteId, String time){
        Map<String, Object> params = new HashedMap();
        Map<String, Object> dateMap = new HashedMap();
        dateMap.put("data", list);
        dateMap.put("ratio", map);
        String str = JacksonUtils.mapToJson(dateMap);
        params.put("siteId", siteId);
        params.put("staticsName", staticsName);
        params.put("staticsValue", str);
        params.put("staticsDesc", staticsDesc);
        params.put("createTime",time);
        int i = transAnalysisMapper.insertTrans(params);
        log.info("========================交易分析数据每天定时添加执行啦==============================:{},{}", siteId,i);
    }









    //获取所有商户的siteId
    public List<Integer> getSiteId() {
        List<Integer> list = staticsRecordMapper.getSiteId();
        return list;
    }
    //时间格式转换(日期)
    public List<String> formatDayTime(String sTime) throws  Exception{
        List<String> list = new ArrayList<>();
        Date parse = new Date();
        parse = DateUtils.parse(sTime, "yyyy-MM-dd hh:mm:ss");
        for (int i = 1;i<=30;i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(Calendar.DATE, -i);
            Date d = calendar.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(d);
            list.add(date);
        }
        return list;
    }
    //平均停留时间
    public void AvgReadTimeInserts(String endTime)throws Exception{
        List<Integer> siteIds = getSiteId();
        for (Integer site_id:siteIds){
            List<String> list = formatDayTime(endTime);
            List<Map<String,Object>> lMap = new ArrayList<>();

            for (String str : list){
                Map<String,Object> map = new HashMap<>();
                Double i = webPageMapper.getAvgReadTimeTodayInit(site_id,str);
                map.put("value",i==null?0.0d:i);
                map.put("query_time",str);
                lMap.add(map);
            }
            Map<String, Object> params = new HashedMap();
            params.put("siteId", site_id);
            params.put("staticsName", "remain_time");
            params.put("staticsValue", JacksonUtils.obj2json(lMap));
            params.put("staticsDesc", "商品平均停留时间");
            params.put("createTime",endTime);
            int i = flowAnalysisMapper.insertRemain(params);

            System.out.println("插入成功  = [" + params + "]");
        }

    }

    /**
     * 修改数据状态
     * @param endTime
     */
    public void updateStruts(String endTime) {
        dataProfileService.updateStruts(endTime);
    }


    /**
     * 补填用户邀请码
     */
    @Transactional
    public void updUserInvitecode(){
        log.info("============开始执行处理用户邀请码方法=============");
        int i = 0;
        List<Integer> siteIds = getSiteId();//获取全部商家siteId
        if(siteIds.size()>0){
            for (Integer siteId : siteIds) {
                List<Map<String, Object>> lstLogs = new ArrayList<Map<String, Object>>();//每一个商家生成一条总日志
                //获取用户等信息
                List<Map<String,Object>> mapList = sbMemberMapper.getUserAndInfoAndCocernBySiteId(siteId);
                Integer buyerId = 0;
                String inviteCode = "";
                String sceneStr = "";
                String mobile = "";
                if(mapList.size()>0){
                    for (Map<String,Object> map : mapList) {
                        Map<String, Object> mapLog = new HashMap<>();//记录每修改一条的日志

                        inviteCode = map.get("invite_code").toString();
                        sceneStr = map.get("scene_str").toString();
                        if(map.get("register_clerks").equals(0) && map.get("register_stores").equals(0) && StringUtil.isEmpty(inviteCode) && !StringUtil.isEmpty(sceneStr) && sceneStr.contains("admin")){
                            buyerId = Integer.parseInt(map.get("buyer_id").toString());
                            mobile = map.get("mobile").toString();

                            SBMember member = sbMemberMapper.findMemberBySiteIdAndMobile(siteId,mobile);
                            SBMemberInfo memberInfo = sbMemberInfoMapper.getMemberInfo(buyerId,siteId);

                            mapLog.put("siteId",siteId);
                            mapLog.put("buyerId",member.getBuyer_id());
                            mapLog.put("yuanInviteCode",memberInfo.getInvite_code());
                            mapLog.put("yuanRegister_clerks",member.getRegister_clerks());
                            mapLog.put("yuanRegister_stores",member.getRegister_stores());

                            //邀请码处理
                            try {
                                StoreAdminExt storeAdminext = storeAdminExtMapper.selectClerkListLikeByInviteCode(siteId, sceneStr.substring(sceneStr.indexOf("_")+1)).get(0);
                                if (storeAdminext != null) {
                                    String ivcode = storeAdminext.getClerk_invitation_code();
                                    if (ivcode.contains("_")){
                                        memberInfo.setInvite_code(storeAdminext.getClerk_invitation_code());
                                        mapLog.put("updateInviteCode",storeAdminext.getClerk_invitation_code());
                                    }else {
                                        memberInfo.setInvite_code(storeAdminext.getStore_id() + "_" + storeAdminext.getClerk_invitation_code());
                                        mapLog.put("updateInviteCode",storeAdminext.getStore_id() + "_" + storeAdminext.getClerk_invitation_code());
                                    }
                                    member.setRegister_clerks(Long.parseLong(String.valueOf(storeAdminext.getId())));
                                    member.setRegister_stores(storeAdminext.getStore_id());

                                    mapLog.put("updateRegister_clerks",storeAdminext.getId());
                                    mapLog.put("updateRegister_stores",storeAdminext.getStore_id());
                                }else{
                                    continue;
                                }
                            } catch (Exception e) {
                                log.error("b_concern表sceneStr字段格式不正确",e.getMessage());
                            }

                            Integer x = sbMemberMapper.updateMemberByMemberId(member);
                            if(x==1){
                                x = sbMemberInfoMapper.updateMemberInfoByMemberId(memberInfo);
                                if(x==1){
                                    lstLogs.add(mapLog);
                                    i++;
                                }
                            }

                        }else{
                            continue;
                        }
                    }
                }else{
                    continue;
                }

                //保存日志
                try {
                    if(lstLogs.size()>0){
                        goodsEsMapper.insertLog(siteId.toString(),"updUserIviteCOde="+JacksonUtils.obj2json(lstLogs),"修改成功");
                    }else {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        log.info("============执行结束(共计处理"+i+"条)=============");
    }


}
