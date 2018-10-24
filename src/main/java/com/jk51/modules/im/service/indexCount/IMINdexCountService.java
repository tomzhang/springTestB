package com.jk51.modules.im.service.indexCount;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.service.indexCount.response.IMCount;
import com.jk51.modules.im.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class IMINdexCountService {


    @Autowired
    private BIMServiceMapper bimServiceMapper;


    /**
    *  根据商家和日期查询咨询次数查询（统计当天会员与店员完成咨询关系建立的次数（不算店员不抢单但是会员发起的咨询次数））
     * @param site_id
     * @param start_day
     * @param end_day
    */
    private List<CountResult> countAdvisory(int site_id,String start_day,String end_day){

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
     *  根据商家和日期查询服务满意度平均分（没有评价的不参与计算，满意10分，一般6分，不满意1分，除以每天有效评价的次数）
     *
     *  没有数据试返回 null
     * @param site_id
     * @param start_day
     * @param end_day
     */
    private List<CountResult> countServiceSatisfaction(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult> evaluateList = bimServiceMapper.findEvaluateList(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,evaluateList);



    }



    /**
     * @param start_day 等于null时，表示查询的时间是某一天，不是时间段，直接返回countResultList
     * @param end_day
     * @param countResultList 查询结果，为null是直接返回
     * 返回结果以day_str转换为Date降序排序
     * */
    private List<CountResult> chekcCountResultList(String start_day,String end_day,List<CountResult> countResultList){


        if(start_day==null){
            return countResultList;
        }

        //获取start_day至end_day每天的时间天数
        List<String> dayList = DateUtils.getContinuousDayStr(start_day,end_day);


        for(String dayStr:dayList){

            boolean has_value = false;

            if(!StringUtil.isEmpty(countResultList)){
                for(CountResult countResult:countResultList){

                    if(countResult.getDay_str().equals(dayStr)){
                        has_value = true;
                    }
                }
            }

            if(!has_value){
                countResultList.add(new CountResult(dayStr,0));
            }
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
     * 会员咨询流失人次（统计当天会员发起咨询却无人抢答响应的次数）
     * @param site_id
     * @param start_day
     * @param end_day
     * */
    private List<CountResult> countLostNum(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult> countLostNumList =  bimServiceMapper.countLostNum(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countLostNumList);
    }

    /**
     * 店员回复人数（参加聊天的店员人数）
     *
     * @param site_id
     * @param start_day
     * @param end_day
     * */
    private List<CountResult> countClerkNum(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult> countClerkNumList = bimServiceMapper.countClerkNum(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countClerkNumList);
    }

    /**
     * 顾客平均等待时间（会发送时间，和抢答时间的差值）
     * @param site_id
     * @param start_day
     * @param end_day
     * */
    private List<CountResult> countMemberWaitAverageTime(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult> countMemberWaitAverageTimeList =  bimServiceMapper.findBySiteIdAndCreateTiemAndHasRace(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countMemberWaitAverageTimeList);
    }

    /**
     * 店员平均回复时间（会发送时间，第一次回复时间的差值）
     * @param site_id
     * @param start_day
     * */
    private List<CountResult> countClerkReplyAverageTime(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult> countClerkReplyAverageTimeList =  bimServiceMapper.findBySiteIdAndCreateTiemAndHasReply(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countClerkReplyAverageTimeList);

    }


    /**
     * 店员平均服务时长（每段咨询从店员抢答到咨询结束的时刻的时间间隔为平均服务时长）
     * @param site_id
     * @param start_day
     *
     * */
    private List<CountResult> countServiceAverageTime(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult> countServiceAverageTimeList =  bimServiceMapper.findBySiteIdAndCreateTiemAndHasClosed(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countServiceAverageTimeList);
    }


    /**
     * 店员超时断开次数
     * @param site_id
     * @param start_day
     *
     * */
    private List<CountResult> countClerkTimeOutTime(int site_id,String start_day,String end_day){

        String endDay = end_day + " 23:59:59";
        String startDay = getStartDay(start_day,end_day);

        List<CountResult>  countClerkTimeOutTimeList = bimServiceMapper.countClerkTimeOutTime(site_id,startDay,endDay);

        return chekcCountResultList(start_day,end_day,countClerkTimeOutTimeList);
    }


    /**
     * 带下单成功率   TBD
     *
     * */


    /**
     *查询各指标指定时间段对应的值
     * @param body
     * */
    public ReturnDto queryIndex(IMIndexRequestBody body){

        if(!DateUtils.isValidDate(body.getEnd_day(),"yyyy-MM-dd")){
            return ReturnDto.buildFailedReturnDto("start_day: "+body.getEnd_day()+" 格式错误,正确格式为,YYYY-MM-dd");
        }

        String start_day = "";
        if(body.getTime_gap_type()== IMIndexConstant.seven){
            start_day = DateUtils.getBeforeOrAfterDateString(body.getEnd_day(),-6);
        }else if(body.getTime_gap_type()== IMIndexConstant.thirty){
            start_day = DateUtils.getBeforeOrAfterDateString(body.getEnd_day(),-29);
        }else {
            return  ReturnDto.buildFailedReturnDto("time_gap_type错误");
        }

        List<CountResult> countResults = null;

        switch (body.getIndex_type()){
            case IMIndexConstant.serviceSatisfaction:

                countResults = countServiceSatisfaction(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.advisoryNum:

                countResults = countAdvisory(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.lostNum:

                countResults = countLostNum(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.clerkNum:

                countResults = countClerkNum(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.memberWaitAverageTime:

                countResults = countMemberWaitAverageTime(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.clerkReplyAverageTime:

                countResults = countClerkReplyAverageTime(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.serviceAverageTime:

                countResults = countServiceAverageTime(body.getSite_id(),start_day,body.getEnd_day());
                break;
            case IMIndexConstant.clerkTimeOutTimeNum:

                countResults = countClerkTimeOutTime(body.getSite_id(),start_day,body.getEnd_day());
                break;
            default:
                return  ReturnDto.buildFailedReturnDto("指标类型错误");
        }

        if(StringUtil.isEmpty(countResults)){
            return ReturnDto.buildFailedReturnDto("没有数据");
        }else{
            return ReturnDto.buildSuccessReturnDto(countResults);
        }


    }



    /**
     * 查询服务质量指标,以及较前天波动Top3
     * @param site_id
     * @param end_day "格式2017-06-20"
     * */
    public ReturnDto queryServiceIndex(Integer site_id, String end_day){

        if(StringUtil.isEmpty(site_id)){
            return ReturnDto.buildFailedReturnDto("site_id为空");
        }

        if(StringUtil.isEmpty(end_day)){
            return ReturnDto.buildFailedReturnDto("end_day为空");
        }

        if(!DateUtils.isValidDate(end_day,"yyyy-MM-dd")){
            return ReturnDto.buildFailedReturnDto("start_day: "+end_day+" 格式错误,正确格式为,YYYY-MM-dd");
        }


        //查询服务满意度指标,及较前一天比较
        Map<String,Float> serviceSatisfactionMap = getServiceSatisfactionMap(site_id,end_day,-1);

        //查询咨询次数，及较前一天比较
        Map<String,Float> advisoryMap = getAdvisoryMap(site_id,end_day,-1);

        //会员流失人次，及较前一天比较
        Map<String,Float> countLostMap = getCountLostMap(site_id,end_day,-1);

        //回复店员人数
        Map<String,Float> countClerkNumMap = getCountClerkNumMap(site_id,end_day,-1);

        //顾客平均等待时间，及较前一天比较
        Map<String,Float> countMemberWaitAverageTimeMap = getCountMemberWaitAverageTimeMap(site_id,end_day,-1);

        //平均回复时间，及较前一天比较
        Map<String,Float> countClerkReplyAverageTimeMap = getCountClerkReplyAverageTime(site_id,end_day,-1);

        //店员平均服务时长
        Map<String,Float> countServiceAverageTimeMap = getCountServiceAverageTime(site_id,end_day,-1);

        //店员超时关闭次数
        Map<String,Float> countClerkTimeOutTimeMap = getCountClerkTimeOutTime(site_id,end_day,-1);


        IMCount imCount =  new IMCount();

        imCount.setServiceSatisfaction(serviceSatisfactionMap.get("serviceSatisfaction"));
        imCount.setServiceSatisfactionProportion(serviceSatisfactionMap.get("serviceSatisfactionProportion"));

        imCount.setAdvisoryNum(advisoryMap.get("advisoryNum").intValue());
        imCount.setAdvisoryNumProportion(advisoryMap.get("advisoryNumProportion"));

        imCount.setLostNum(countLostMap.get("lostNum").intValue());
        imCount.setLostNumProportion(countLostMap.get("lostNumProportion"));

        imCount.setClerkNum(countClerkNumMap.get("clerkNum").intValue());
        imCount.setClerkNumProportion(countClerkNumMap.get("clerkNumProportion"));

        imCount.setMemberWaitAverageTime(countMemberWaitAverageTimeMap.get("memberWaitAverageTime"));
        imCount.setMemberWaitAverageTimeProportion(countMemberWaitAverageTimeMap.get("memberWaitAverageTimeProportion"));

        imCount.setClerkReplyAverageTime(countClerkReplyAverageTimeMap.get("clerkReplyAverageTime"));
        imCount.setClerkReplyAverageTimeProportion(countClerkReplyAverageTimeMap.get("clerkReplyAverageTimeProportion"));

        imCount.setServiceAverageTime(countServiceAverageTimeMap.get("serviceAverageTime"));
        imCount.setServiceAverageTimeProportion(countServiceAverageTimeMap.get("serviceAverageTimeProportion"));

        imCount.setClerkTimeOutTimeNum( countClerkTimeOutTimeMap.get("clerkTimeOutTimeNum").intValue());
        imCount.setClerkTimeOutTimeNumProportion(countClerkTimeOutTimeMap.get("clerkTimeOutTimeNumProportion"));

        //波动绝对值排序（降序）
        imCount.orderProportions();

        return ReturnDto.buildSuccessReturnDto(imCount);
    }

    /**
     * 查询店员超时关闭次数，及较前一天比较值
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getCountClerkTimeOutTime(Integer site_id, String end_day,int day_num) {

        Map<String,Float> result = new HashMap();

        List<CountResult> clerkTimeOutTimeNumList = countClerkTimeOutTime(site_id,null,end_day);
        float clerkTimeOutTimeNum = (float) getCountResultFirstValue(clerkTimeOutTimeNumList);

        String beforeDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeClerkTimeOutTimeNumList = countClerkTimeOutTime(site_id,null,beforeDayStr);
        float beforeClerkTimeOutTimeNum = (float) getCountResultFirstValue(beforeClerkTimeOutTimeNumList);

        Float clerkTimeOutTimeNumProportion = getProportion(clerkTimeOutTimeNum,beforeClerkTimeOutTimeNum);

        result.put("clerkTimeOutTimeNum",clerkTimeOutTimeNum);
        result.put("clerkTimeOutTimeNumProportion",clerkTimeOutTimeNumProportion);
        return result;
    }

    /**
     * 查询店员平均服务时长,及较前一天比较值
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getCountServiceAverageTime(Integer site_id, String end_day,int day_num) {

        Map<String,Float> result = new HashMap();

        List<CountResult> serviceAverageTimeList = countServiceAverageTime(site_id,null,end_day);
        Float serviceAverageTime = (Float) getCountResultFirstValue(serviceAverageTimeList);

        String beforeDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeServiceAverageTimeList = countServiceAverageTime(site_id,null,beforeDayStr);
        Float beforeServiceAverageTime = (Float) getCountResultFirstValue(beforeServiceAverageTimeList);

        float serviceAverageTimeProportion = getProportion(serviceAverageTime,beforeServiceAverageTime);

        result.put("serviceAverageTime",serviceAverageTime);
        result.put("serviceAverageTimeProportion",serviceAverageTimeProportion);
        return result;
    }


    /**
     *查询店员平均回复间隔时间，及较前一天比较波动
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getCountClerkReplyAverageTime(Integer site_id,String end_day,int day_num) {

        Map<String,Float> result = new HashMap();

        List<CountResult> clerkReplyAverageTimeList = countClerkReplyAverageTime(site_id,null,end_day);
        Float clerkReplyAverageTime = (Float) getCountResultFirstValue(clerkReplyAverageTimeList);

        String beforeDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeClerkReplyAverageTimeList = countClerkReplyAverageTime(site_id,null,beforeDayStr);
        Float beforeClerkReplyAverageTime = (Float) getCountResultFirstValue(beforeClerkReplyAverageTimeList);

        float clerkReplyAverageTimeProportion = getProportion(clerkReplyAverageTime,beforeClerkReplyAverageTime);

        result.put("clerkReplyAverageTime",clerkReplyAverageTime);
        result.put("clerkReplyAverageTimeProportion",clerkReplyAverageTimeProportion);
        return result;
    }

    /**
     *查询顾客平均等待时间，及较前一天比较波动
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getCountMemberWaitAverageTimeMap(Integer site_id, String end_day,int day_num) {

        Map<String,Float> result = new HashMap();

        List<CountResult> memberWaitAverageTimeList =  countMemberWaitAverageTime(site_id,null,end_day);
        Float memberWaitAverageTime = (Float) getCountResultFirstValue(memberWaitAverageTimeList);

        String beforeDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeMemberWaitAverageTimeList = countMemberWaitAverageTime(site_id,null,beforeDayStr);
        Float beforeMemberWaitAverageTime = (Float) getCountResultFirstValue(beforeMemberWaitAverageTimeList);

        float memberWaitAverageTimeProportion = getProportion(memberWaitAverageTime,beforeMemberWaitAverageTime);

        result.put("memberWaitAverageTime",memberWaitAverageTime);
        result.put("memberWaitAverageTimeProportion",memberWaitAverageTimeProportion);
        return result;

    }


    /**
     *查询回复店员人数，及较前一天比较波动
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getCountClerkNumMap(Integer site_id, String end_day,int day_num) {

        Map<String,Float> result = new HashMap();

        List<CountResult> clerkNumList = countClerkNum(site_id,null,end_day);
        float clerkNum = (float) getCountResultFirstValue(clerkNumList);

        String beforDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeClerkNumList = countClerkNum(site_id,null,beforDayStr);
        float beforeClerkNum = (float) getCountResultFirstValue(beforeClerkNumList);

        float clerkNumProportion = getProportion(clerkNum,beforeClerkNum);

        result.put("clerkNum",clerkNum);
        result.put("clerkNumProportion",clerkNumProportion);
        return result;
    }


    /**
     * 查询满意度和前一天的波动(前一天的波动：用当日的数据指标除以前一日的数据指标，然后结果-1)
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getServiceSatisfactionMap(Integer site_id, String end_day,int day_num){

        Map<String,Float> result = new HashMap();

        List<CountResult> serviceSatisfactionList = countServiceSatisfaction(site_id,null,end_day);
        float serviceSatisfaction = (float)getCountResultFirstValue(serviceSatisfactionList);

        String beforeDay = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforServiceSatisfactionList = countServiceSatisfaction(site_id,null,beforeDay);
        float beforServiceSatisfaction = (float)getCountResultFirstValue(beforServiceSatisfactionList);

        float serviceSatisfactionProportion = getProportion(serviceSatisfaction,beforServiceSatisfaction);

        result.put("serviceSatisfaction",serviceSatisfaction);
        result.put("serviceSatisfactionProportion",serviceSatisfactionProportion);
        return result;
    }


    /**
     *查询咨询次数，及较前一天比较波动
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getAdvisoryMap(Integer site_id, String end_day,int day_num){

        Map<String,Float> result = new HashMap();

        List<CountResult> advisoryNumList = countAdvisory(site_id,null,end_day);
        float advisoryNum = (float) getCountResultFirstValue(advisoryNumList);

        String beforDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeAdvisoryNumList = countAdvisory(site_id,null,beforDayStr);
        float beforeAdvisoryNum = (float) getCountResultFirstValue(beforeAdvisoryNumList);

        float advisoryNumProportion = getProportion(advisoryNum,beforeAdvisoryNum);

        result.put("advisoryNum",advisoryNum);
        result.put("advisoryNumProportion",advisoryNumProportion);
        return result;

    }

    /**
     *查询流失人次，及较前一天比较波动
     * @param site_id
     * @param end_day 结束时间
     * @param day_num 间隔天数，如果是之前的日期加负号，例如  -1（前一天），-7（前一周当天）
     * */
    public Map<String,Float> getCountLostMap(Integer site_id, String end_day,int day_num){

        Map<String,Float> result = new HashMap();

        List<CountResult> lostNumList = countLostNum(site_id,null,end_day);
        float lostNum = (float) getCountResultFirstValue(lostNumList);

        String beforDayStr = DateUtils.getBeforeOrAfterDateString(end_day,day_num);
        List<CountResult> beforeLostNumList = countLostNum(site_id,null,beforDayStr);
        float beforeLostNum = (float) getCountResultFirstValue(beforeLostNumList);

        float lostNumProportion = getProportion(lostNum,beforeLostNum);

        result.put("lostNum",lostNum);
        result.put("lostNumProportion",lostNumProportion);
        return result;
    }


    /**
     * 获取当天数据与前一天数据的比例，保留2位小数
     **/
    private Float getProportion(Float tadayValue,Float beforeDayValue){

        float proportion = 0f;
        if(tadayValue == 0){
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

        Object value = 0f;
        if(!StringUtil.isEmpty(countResults)&&!(countResults.get(0).getValue()==null)){
            value = countResults.get(0).getValue();
        }

        return value;
    }
}
