package com.jk51.modules.merchant.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.date.StatisticsDate;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.merchant.mapper.StaticsRecordMapper;
import com.jk51.modules.merchant.mapper.WebPageMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:这是数据概况统计的优化方法服务类（二次优化）
 * 作者: dumingliang
 * 创建日期: 2017-07-17
 * 修改记录:
 */
@Service
public class SelectDataService {

    @Autowired
    private StaticsRecordMapper staticsRecordMapper;

    @Autowired
    private WebPageMapper webPageMapper;

    @Autowired
    private DataProfileService dataProfileService;

    @Autowired
    private TradesMapper tradesMapper;

    @Autowired
    private MemberMapper memberMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String ENERGY_CLERKS = "energy_clerks";
    public static final String GOODS_WEB_VISITORS = "goods_web_visitors";
    public static final String MEMBER_COUNT = "member_count";
    public static final String PAYMENT_MONEY = "payment_money";
    public static final String PULL_NEW_COUNT = "pull_new_count";
    public static final String TRADES_MONEY_BYDATES = "trades_money_bydates";
    public static final String WEB_VISITORS = "web_visitors";
    public static final String MEM_Satisfaction = "MEM_Satisfaction";

    public static final String TOTAL_VISITORS="all_visitor";
    public static final String NEW_VISITORS = "new_visitor";
    public static final String OLD_VISITORS = "old_visitor";
    public static final String AVERAGE_VISITOR = "avg_read";
    public static final String BOUNCE_RATE = "bounce_rate";
    public static final String SELECT_WEB_MEMBER_COUNT_BY_HOUR = "select_web_member_count_by_hour";
    public static final String SELECT_WEB_MEMBER_COUNT_BY_HOUR_MAX = "select_web_member_count_by_hour_max";
    public static final String SEX_DISTRIBUTION = "sex_distribution";
    public static final String AGE_DISTRIBUTION = "age_distribution";
    public static final String AREA_DISTRIBUTION = "area_distribution";
    public static final String GET_AVG_READ_TIME = "avg_read_time";
    public static final String types_flow_table=TOTAL_VISITORS+","+NEW_VISITORS+","+OLD_VISITORS+","+AVERAGE_VISITOR+","+GET_AVG_READ_TIME;
    /**
     * 查询数据概况
     *
     * @param param
     * @return
     */
    public Object queryDataProfile(Map<String, Object> param) throws Exception {
        //商户ID
        Object id = param.get("siteId");
        if (id == null) {
            id = param.get("site_id");
        }
        Integer siteId = (Integer) Integer.parseInt(id.toString());
        //时间
        String sTime = (String) param.get("sTime");
        Object advanceDays = param.get("advanceDays");
        Integer days = null;
        if (advanceDays != null) {
            days = (Integer) Integer.parseInt(advanceDays.toString());
        } else {
            days = 7;
        }

        //上面的常量类型
        String type = (String) param.get("type");
        String todayTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        //--
        boolean is_today = false;
        Map<String, Object> todayResult = new HashMap<String, Object>();
        if (todayTime.equals(sTime)) {//查询当天的数据
            is_today = true;
            Map<String, Date> proportion = new HashMap<String, Date>();
            proportion.put("start", StatisticsDate.processString2Date(sTime + " 00:00:00"));
            proportion.put("end", StatisticsDate.processString2Date(sTime + " 23:59:59"));
            Object todayValue = null;
            Object result = null;
            switch (type) {
                case WEB_VISITORS:
                    todayValue = webPageMapper.queryWebYesterDayAndLastWeek(siteId, proportion);
                    break;
                case GOODS_WEB_VISITORS:
                    todayValue = webPageMapper.queryGoodsWebYesterDayAndLastWeek(siteId, proportion);
                    break;
                case TRADES_MONEY_BYDATES: {
                    List<Map<String, Object>> list = dataProfileService.selectTradesMoneyBydates(siteId, sTime, 0, null);
                    todayValue = list.get(0).get("value");
                    break;
                }
                case MEMBER_COUNT: {
                    List<Map<String, Object>> list = dataProfileService.selectMemberCountBydays(siteId, sTime, 0);
                    todayValue = list.get(0).get("value");
                    break;
                }
                case ENERGY_CLERKS: {
                    List<Map<String, Object>> list = dataProfileService.selectEnergyClerks(siteId, sTime, 0);
                    todayValue = list.get(0).get("value");
                    break;
                }
                case MEM_Satisfaction: {
                    List<Map<String, Object>> list = dataProfileService.getServiceSatisfactionMap(siteId, sTime, 0);
                    todayValue = list.get(0).get("value");
                    break;
                }
                case PULL_NEW_COUNT: {
                    result = memberMapper.getPullNewCount(siteId, sTime);
                    break;
                }
                case PAYMENT_MONEY: {
                    result = tradesMapper.getPaymentMoney(siteId, sTime);
                    break;
                }
                case TOTAL_VISITORS:{
                    List<Map<String, Object>> list = dataProfileService.getAllVisitor(siteId, sTime, 0);
                    todayValue = list.get(0).get("value");
                    break;
                }
                case NEW_VISITORS:{
                    List<Map<String, Object>> allVisitor = dataProfileService.getAllVisitor(siteId, sTime, 0);
                    List<Map<String, Object>> list = dataProfileService.selectOldVisitors(siteId, sTime, 0);
                    Integer vl = Integer.parseInt(list.get(0).get("value").toString());
                    Integer all  = Integer.parseInt(allVisitor.get(0).get("value").toString());
                    todayValue=all-vl;
                    break;
                }
                case OLD_VISITORS:{
                    //List<Map<String, Object>> list = dataProfileService.getOldVisitor(siteId, sTime, 0);
                    List<Map<String, Object>> list = dataProfileService.selectOldVisitors(siteId, sTime, 0);
                    todayValue=list.get(0).get("value");
                    break;
                }
                case AVERAGE_VISITOR:{
                    List<Map<String, Object>> list = dataProfileService.getAllVisitor(siteId, sTime, 0);
                    //List<Map<String,Object>> avg =(List<Map<String,Object>>) dataProfileService.getAverageVisitor(siteId, sTime, 0);
                    Float todayWeb = Float.valueOf(webPageMapper.queryWebYesterDayAndLastWeek(siteId, proportion));
                    //Float value = (Float) avg.get(0).get("value");
                    Float v1 = Float.valueOf(Integer.parseInt(list.get(0).get("value").toString()));
                    BigDecimal b ;
                    if (0 == v1){
                        b =new BigDecimal(todayWeb);
                    }else {
                        b = new BigDecimal(todayWeb/v1);
                    }

                    todayValue = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    break;
                }
                case GET_AVG_READ_TIME:{
                    Map<String, Object> iMap = webPageMapper.getAvgReadTimeToday(siteId, sTime);
                    Object value = iMap.get("value");
                    todayValue=value;
                    break;
                }
            }
            if (result != null) {
                return result;//如果查询排行榜且记录表中有数据
            }
            todayResult.put("query_time", sTime);
            todayResult.put("value", todayValue);
        }
        String query_time = sTime + " 00:00:00";
        if (is_today) {//如果是当天
            query_time = DateUtils.getBeforeOrAfterDateString(query_time, -1);
            query_time+=" 00:00:00";
        }
        String json = null;
        if(types_flow_table.contains(type)){
            json=staticsRecordMapper.queryFlowAnalysisRecords(siteId, query_time, type);
        }else {
            json=staticsRecordMapper.queryStaticsRecords(siteId, query_time, type);
        }

        //没有查询到结果，不处理结果
        if (json == null) {
            if (is_today) {
                sTime = DateUtils.formatDate(DateUtils.getBeforeOrAfterDate(DateUtils.convert(sTime, "yyyy-MM-dd"), -1), "yyyy-MM-dd");
            }
            json = selectDataByType(siteId, days, sTime, type);
        }
        Map<String, Object> dbResult = JacksonUtils.json2map(json);
        boolean isReturn = false;
        switch (type) {
            case PULL_NEW_COUNT: {
                isReturn = true;
                break;
            }
            case PAYMENT_MONEY: {
                isReturn = true;
                break;
            }
        }
        //为true则是排行榜，立即返回
        if (isReturn) {
            return dbResult;
        }
        List<Map<String, Object>> PreDaydates = (List<Map<String, Object>>) dbResult.get("data");
        Map<String, Object> ratio = (Map<String, Object>) dbResult.get("ratio");
        //替换今日数据
        if (is_today) {
            LinkedList<Map<String, Object>> Predates = new LinkedList<Map<String, Object>>(PreDaydates);
            //去掉开头一个数据，尾部添加一个，保证30天数据
            Predates.removeFirst();
            Predates.addLast(todayResult);
            PreDaydates = Predates;
        }
        if (days == 7&&PreDaydates.size()>=30) {
            PreDaydates = PreDaydates.subList(22, PreDaydates.size());
        }
        Map<String, Object> data = new HashMap<String, Object>();
        //重新计算
        if (is_today) {
            Map<String, Object> proportion = dataProfileService.getProportion(PreDaydates);
            data.put("result", PreDaydates);
            data.put("proportion", proportion);
        } else {
            data.put("result", PreDaydates);
            data.put("proportion", ratio);
        }
        //对7天（8个结果）的结果集进行削减
        if (days == 7) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("result");
            LinkedList<Map<String, Object>> Predates = new LinkedList<Map<String, Object>>(list);
            Predates.removeFirst();
            data.put("result", Predates);
        }
        return data;
    }

    /**
     * 如果日志记录表中没有数据，从对应主表中查询结果
     *
     * @return
     */
    public String selectDataByType(Integer siteId, Integer advanceDays, String sTime, String type) {
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Date> dateMap = new HashMap<>();
        List<Map<String, Object>> resultMap = new ArrayList<>();
        Date endDate = DateUtils.parseDate(sTime, "yyyy-MM-dd");
        Date startDate = new Date();
        try {
            startDate = DateUtils.getBeforeOrAfterDate(DateUtils.convert(sTime, "yyyy-MM-dd"), -advanceDays);
            dateMap.put("start", startDate);
            dateMap.put("end", endDate);
        } catch (ParseException e) {
            logger.info("日期类型转换异常");
        }
        boolean isRank = false;
        switch (type) {
            case WEB_VISITORS:
                resultMap = dataProfileService.rearragement(webPageMapper.queryWebVisitors2(siteId, dateMap), startDate, sTime);
                break;
            case GOODS_WEB_VISITORS:
                resultMap = dataProfileService.rearragement(webPageMapper.queryGoodsWebVisitors2(siteId, dateMap), startDate, sTime);
                break;
            case TRADES_MONEY_BYDATES: {
                resultMap = dataProfileService.selectTradesMoneyBydates(siteId, sTime, advanceDays, null);
                break;
            }
            case MEMBER_COUNT: {
                resultMap = dataProfileService.selectMemberCountBydays(siteId, sTime, advanceDays);
                break;
            }
            case ENERGY_CLERKS: {
                resultMap = dataProfileService.selectEnergyClerks(siteId, sTime, advanceDays);
                break;
            }
            case MEM_Satisfaction: {//用户满意度
                resultMap = dataProfileService.getServiceSatisfactionMap(siteId, sTime, advanceDays);
                break;
            }
            case PULL_NEW_COUNT: {
                resultMap = memberMapper.getPullNewCount(siteId, sTime);
                isRank = true;
                break;
            }
            case PAYMENT_MONEY: {
                resultMap = tradesMapper.getPaymentMoney(siteId, sTime);
                isRank = true;
                break;
            }
            case TOTAL_VISITORS:{
                resultMap = dataProfileService.getAllVisitor(siteId, sTime, advanceDays);
                break;
            }
            case NEW_VISITORS:{
                List<Map<String, Object>> list1 = dataProfileService.getAllVisitor(siteId, sTime, advanceDays);
                List<Map<String, Object>> list2 = dataProfileService.selectOldVisitors(siteId, sTime, advanceDays);
                for(int i=0;i<list1.size();i++){
                    Map<String, Object> lmap1 = list1.get(i);
                    Map<String, Object> lmap2 = list2.get(i);
                    Integer v1 =  Integer.parseInt(lmap1.get("value").toString());
                    Integer v2 = Integer.parseInt(lmap2.get("value").toString());
                    lmap2.put("value",v1-v2);
                }
                resultMap=list2;
                break;
            }
            case OLD_VISITORS:{
                resultMap = dataProfileService.selectOldVisitors(siteId, sTime, advanceDays);
                break;
            }
            case AVERAGE_VISITOR:{
                resultMap = (List<Map<String, Object>>) dataProfileService.getAverageVisitor(siteId, sTime, advanceDays);
                break;
            }
            case GET_AVG_READ_TIME:{
                resultMap = dataProfileService.getAvgReadTime(siteId, sTime, advanceDays);
                break;
            }
        }
        String JSONResult = null;
        if (isRank) {//是排行榜
            try {
                resultObject.put("code", 0);
                resultObject.put("message", "查询员支付金额排行榜成功");
                resultObject.put("result", resultMap);
                Map<String, Object> radioMap = new HashMap<>();
                radioMap.put("ratio", resultObject);
                JSONResult = JSON.toJSONString(radioMap);
                return JSONResult;
            } catch (Exception e) {
                logger.info("排行榜查询结果不能转化为JSON对象{}", e);
            }
        }
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String,Object> temp:resultMap){
            Object value = temp.get("query_time");
            if(value instanceof Date){
                temp.put("query_time",df.format(value));
            }
        }
        Map<String, Object> prop = dataProfileService.getProportion(resultMap);//对应的比例
        resultObject.put("data", resultMap);
        resultObject.put("ratio", prop);
        try {
            JSONResult = JSON.toJSONString(resultObject);
            return JSONResult;
        } catch (Exception e) {
            logger.info("结果不能转化为JSON对象{}", e);
        }
        return null;
    }


}
