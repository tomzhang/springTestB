package com.jk51.modules.appInterface.controller;



import com.jk51.commons.date.DateUtils;
import com.jk51.commons.date.StatisticsDate;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.im.mapper.ImRecodeMapper;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.util.CountResult;
import com.jk51.modules.merchant.service.DataProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:聊天服务质量指标统计
 * 作者: gaojie
 * 创建日期: 2017-06-19
 * 修改记录:
 */
@Service
public class IMIndexCountService {

    @Autowired
    private ImRecodeMapper imRecodeMapper;
    @Resource
    private BIMServiceMapper bimServiceMapper;


    /**
    *  根据商家和日期查询咨询次数查询（统计当天会员与店员完成咨询关系建立的次数（不算店员不抢单但是会员发起的咨询次数））
     * @param site_id
     * @param start_day
     * @param end_day
    */
    private List<CountResult> countAdvisory(int site_id, String start_day, String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);
        List<CountResult> countAdvisoryList =  bimServiceMapper.findBIMServiceCountBySiteIdAndQueryTime(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countAdvisoryList);


    }


    //获取开始时间，如果开始时间为null,开始时间等于结束时间+" 00:00:00"
    public String getStartDay(String start_day,String end_day){

        String startDay = "";
        if(StringUtil.isEmpty(start_day)){
            startDay = end_day + " 00:00:00";
        }else{
            startDay = start_day + " 00:00:00";
        }

        return startDay;
    }






    /**
     * @param start_day 等于null时，表示查询的时间是某一天，不是时间段，直接返回countResultList
     * @param end_day
     * @param countResultList 查询结果，为null是直接返回
     * 返回结果以day_str转换为Date降序排序
     * */
    private List<CountResult> chekcCountResultList(String start_day,String end_day,List<CountResult> countResultList){

        if(StringUtil.isEmpty(countResultList)){
            return null;
        }

        if(start_day==null){
            return countResultList;
        }

        //获取start_day至end_day每天的时间天数
        List<String> dayList = DateUtils.getContinuousDayStr(start_day,end_day);


        for(String dayStr:dayList){

            boolean has_value = false;
            for(CountResult countResult:countResultList){

                if(countResult.getDay_str().equals(dayStr)){
                    has_value = true;
                }
            }

            countResultList.add(new CountResult(dayStr,0L));
        }

        countResultList.sort(new Comparator<CountResult>() {
            @Override
            public int compare(CountResult o1, CountResult o2) {

                return DateUtils.parseDate(o1.getDay_str(),"yyy-MM-dd").compareTo(DateUtils.parseDate(o2.getDay_str(),"yyy-MM-dd"));
            }
        });

        return countResultList;
    }




    /**
     *查询咨询次数，及较前一天比较波动
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Object> getAdvisoryMap(Integer site_id, String end_day,int day_num){

        Map<String,Object> result = new HashMap();
        List<CountResult> advisoryNumList = countAdvisory(site_id,null,end_day);
        Float advisoryNum = (Float) getCountResultFirstValue(advisoryNumList);

        String beforDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeAdvisoryNumList = countAdvisory(site_id,null,beforDayStr);
        Integer beforeAdvisoryNum = (Integer) getCountResultFirstValue(beforeAdvisoryNumList);

        float advisoryNumProportion = getProportion(advisoryNum.floatValue(),beforeAdvisoryNum.floatValue());

        result.put("advisoryNum",advisoryNum);
        result.put("advisoryNumProportion",advisoryNumProportion);
//
        return result;

    }




    /**
     * 获取当天数据与前一天数据的比例，保留2位小数
     **/
    private Float getProportion(Float tadayValue,Float beforeDayValue){

        float proportion = 0f;
        if(tadayValue==0&&beforeDayValue==0){
            proportion=0f;
        }else if(tadayValue == 0){
            proportion = -1f;
        }else if(beforeDayValue == 0){
            proportion = 1f;
        }else{
            proportion = tadayValue/beforeDayValue-1;
        }

        BigDecimal bigDecimal = new BigDecimal((double)proportion);
        bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);

        return bigDecimal.floatValue();
    }

    private Object getCountResultFirstValue(List<CountResult> countResults){

        Object value = 0;
        if(!StringUtil.isEmpty(countResults)){
            value = countResults.get(0).getValue();
        }

        return value;
    }

    public Map<String, Object> getAdvisoryHistory(Integer site_id, String sdate, Integer days) {
        String eTime = sdate + " 23:59:59";
        Date endDate = StatisticsDate.processString2Date(eTime);
        Date startDate = DateUtils.getBeforeOrAfterDate(endDate, -days);
        String sTime = DateUtils.formatDate(startDate, "yyyy-MM-dd") + " 00:00:00";
        Map<String, Date> datesMap = new HashMap<>();
        datesMap.put("start", DateUtils.parseDate(sTime, "yyyy-MM-dd hh:mm:ss"));
        datesMap.put("end", DateUtils.parseDate(eTime, "yyyy-MM-dd hh:mm:ss"));
        List<Map<String,Object>> rs=bimServiceMapper.getAdvisoryByDays(site_id,datesMap);
        DataProfileService a=new DataProfileService();
        List<Map<String, Object>> list = a.rearragement(rs, startDate, sdate);
        Map<String, Object> proportion = a.getProportion(list);
        Map<String,Object> resultObj=new HashMap<String,Object>();
        resultObj.put("result", list.subList(1,list.size()));
        resultObj.put("proportion", proportion);
        return resultObj;
    }
}
