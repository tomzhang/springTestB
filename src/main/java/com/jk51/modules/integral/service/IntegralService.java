package com.jk51.modules.integral.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.integral.IntegralRule;
import com.jk51.model.order.OrderGoods;
import com.jk51.modules.integral.mapper.IntegralMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class IntegralService {

    private static final Logger logger = LoggerFactory.getLogger(IntegralService.class);

    @Autowired
    private IntegralMapper mapper;

    public Map query(Map m) {
        return mapper.query(m);
    }

    public JSONObject integralAddForRegister(Map param) {
        param.put("useCase", CommonConstant.USE_CASE_REGIST);
        param.put("integralDesc", CommonConstant.LOG_DESC_REGIST);
        param.put("integralDiff", 0);
        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        logger.info("注册送积分接口，参数为：{}", JacksonUtils.mapToJson(param));
        Map ruleMap = query(param);//是否开启送积分
        if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
            return resultHelper(false, CommonConstant.MSG_STATUS_REGISTER);
        }

        List<Map> exist = logQuery(param);//判断是否送过积分
        if (exist != null && exist.size() > 0) {
            return resultHelper(false, CommonConstant.MSG_EXIST);
        }

        Map overPlusMap = getOverPlus(param);
        if (StringUtil.isEmpty(overPlusMap)) {
            return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
        }

        JSONObject rulesTemp;
        JSONObject result = new JSONObject();
        try {
            JSONArray arrTmp = (JSONArray) JSONArray.parse(ruleMap.get("value").toString());
            rulesTemp = arrTmp.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            return result;
        }
        BigInteger addCount = new BigInteger(rulesTemp.getString("value"));
        BigInteger limit = getTodayLimit(param);//赠送上限
        BigInteger sumTodayTemp = queryAddSum(param);
        BigInteger sumToday = StringUtil.isEmpty(sumTodayTemp) ? new BigInteger("0") : sumTodayTemp;
        if (sumToday.compareTo(limit) == 0) {
            result.put("status", "error");
            result.put("msg", "已达到赠送上限");
            return result;
        }
        if (sumToday.add(addCount).compareTo(limit) == 1) {
            addCount = limit.subtract(sumToday);
            BigInteger zero = new BigInteger("0");
            if (addCount.compareTo(zero) == -1) {
                result.put("status", "error");
                result.put("msg", "已达到赠送上限，可赠送积分为0");
                return result;
            }
            result.put("status", "success");
            result.put("msg", "已达到赠送上限，可赠送积分为" + limit.subtract(sumToday));
        }

        param.put("type",10);
        checkinDbOpt(param, addCount, false, overPlusMap);
        result.put("status", "success");
        return result;
    }

    //增加积分
    @Transactional
    public void checkinDbOpt(Map m, BigInteger addCount, boolean flag, Map overPlusMap) {
        m.put("integralAdd", addCount);
//        if (flag) {//更新签到数据（b_member_info）
//            Map yesterday = mapper.checkinYesterday(m);
//            m.put("checkinNum", StringUtil.isEmpty(yesterday) ? 1 : getInteger(yesterday, "checkin_num") + 1);//昨天没有签到,则连续签到总数为1
//            mapper.updateChickenData(m);
//        }

        BigInteger overPlusNum = getBigInteger(overPlusMap, "integrate");
        BigInteger totalGetIntegrate = getBigInteger(overPlusMap, "total_get_integrate");

        m.put("integralOverplus", addCount.add(overPlusNum));
        mapper.logAdd(m);

        m.put("totalGetIntegrate", addCount.add(totalGetIntegrate));
        m.put("integrate", addCount.add(overPlusNum));
        mapper.updateMemberData(m);

    }

    public JSONObject integralAddForChicken(Map param, boolean flag) {
        param.put("useCase", CommonConstant.USE_CASE_CHECKIN);
        param.put("integralDesc", CommonConstant.LOG_DESC_CHECKIN);
        param.put("integralDiff", 0);
        logger.info("签到送积分接口，参数为：{}", JacksonUtils.mapToJson(param));

        //Map today = checkinToday(param);
        param.put("createTime", "today");//判断当天是否已送过积分
        List<Map> today = logQuery(param);
        Map ruleMap = query(param);//是否开启送积分
        Map overPlusMap = getOverPlus(param);
        if (flag) {
            if (!StringUtil.isEmpty(today) || today.size() > 0) {
                return resultHelper(false, CommonConstant.MSG_CHECKIN_HASACTION);
            }

            if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
                return resultHelper(false, CommonConstant.MSG_STATUS_CHECKIN_T);
            }

            if (StringUtil.isEmpty(overPlusMap)) {
                return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
            }
        }

        JSONObject rulesTemp = new JSONObject();
        JSONObject result = new JSONObject();
        result.put("status", "success");
        try {
            JSONArray arrTmp = (JSONArray) JSONArray.parse(getStringVal(ruleMap, "value"));
            if(StringUtil.isEmpty(arrTmp)){
                result.put("series_value", 0);
                result.put("series_day", 0);
                result.put("value", 0);
                return result;
            }
            rulesTemp = arrTmp.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            return result;
        }

        String addType = getStringVal(ruleMap, "add_type");
        BigInteger rulesMaxNum = getBigInteger(rulesTemp, "max_num");
        BigInteger rulesAddValue = getBigInteger(rulesTemp, "add_value");//连续签到额外赠送积分数
        BigInteger rulesValue = getBigInteger(rulesTemp, "value");

        BigInteger iCount = new BigInteger("0");
        Map seriesMap = mapper.checkinYesterday(param);
        Integer seriesDays = getInteger(seriesMap, "checkin_num");//连续签到天数(数据库中，昨天必须签到)
        if (addType.equals("110")) {//每日获取固定积分
            iCount = rulesValue;
        } else if (addType.equals("120")) {//每日获取自定义积分
            List arr = rulesTemp.getJSONArray("valuearr");
            int index = seriesDays.intValue() % arr.size();
            iCount = new BigInteger(arr.get(index).toString());
        }

        if (!flag) {
            result.put("series_value", rulesAddValue);
            result.put("series_day", rulesMaxNum);
        }

        JSONObject tmpLimit = handleTodayLimit(iCount, param);
        if (!flag) {
            tmpLimit.put("series_value", rulesAddValue);
            tmpLimit.put("series_day", rulesMaxNum);
            tmpLimit.put("value", 0);
        }
        if (tmpLimit.getString("status").equals("error")) {
            //if (flag) checkinDbOpt(param, iCount, true, overPlusMap);//达到积分赠送上限，但仍需要签到
            return tmpLimit;//不能超出每日赠送上限
        }
        iCount = tmpLimit.getBigInteger("hasHandleVal");

        param.put("type",20);
        if (flag) checkinDbOpt(param, iCount, true, overPlusMap);
        result.put("value", iCount);
        if (!StringUtil.isEmpty(rulesAddValue) && rulesAddValue.compareTo(new BigInteger("0")) == 1) {//连续签到获取额外积分
            if (seriesDays.intValue() >= 0 && (seriesDays.intValue() + 1) % rulesMaxNum.intValue() == 0) {
                param.put("integralDesc", CommonConstant.LOG_DESC_CHECKIN_SERIES);//覆盖integralDesc
                if (flag) {

                    JSONObject tmpLimit_ = handleTodayLimit(rulesAddValue, param);
                    if (tmpLimit_.getString("status").equals("error")) return tmpLimit_;//不能超出每日赠送上限
                    rulesAddValue = tmpLimit_.getBigInteger("hasHandleVal");

                    Map overPlusMap_ = mapper.getOverPlus(param);//获取最新数据
                    checkinDbOpt(param, rulesAddValue, false, overPlusMap_);//只修改积分，不修改签到数据
                }
                result.put("value", iCount.add(rulesAddValue));//如果满足连续签到条件，当天可获得积分数
            }
        }

        if (param.containsKey("torFlag")) {
            Map seriesMap_ = mapper.checkinToday(param);//取最新值
            result.put("tor_value", checkinTomorrowVal(ruleMap, param, seriesMap_));
        }
        return result;
    }

    public JSONObject checkinTomorrowVal(Map ruleMap, Map param, Map checkinDataMap) {
        JSONObject rulesTemp = new JSONObject();
        JSONObject result = new JSONObject();
        result.put("status", "success");
        try {
            JSONArray arrTmp = (JSONArray) JSONArray.parse(getStringVal(ruleMap, "value"));
            if(StringUtil.isEmpty(arrTmp)){
                result.put("tor_value", 0);
                return result;
            }
            rulesTemp = arrTmp.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            return result;
        }

        String addType = getStringVal(ruleMap, "add_type");
        BigInteger rulesMaxNum = getBigInteger(rulesTemp, "max_num");
        BigInteger rulesAddValue = getBigInteger(rulesTemp, "add_value");//连续签到额外赠送积分数
        BigInteger rulesValue = getBigInteger(rulesTemp, "value");

        BigInteger iCount = null;
        Integer seriesDays = getInteger(checkinDataMap, "checkin_num");//连续签到天数

        if (addType.equals("110")) {
            iCount = rulesValue;
        } else if (addType.equals("120")) {
            List arr = rulesTemp.getJSONArray("valuearr");
            int index = seriesDays.intValue() % arr.size();
            iCount = new BigInteger(arr.get(index).toString());
        }
        iCount = StringUtil.isEmpty(iCount) ? new BigInteger("0") : iCount;

        if (!StringUtil.isEmpty(rulesAddValue) && rulesAddValue.compareTo(new BigInteger("0")) == 1) {//连续签到获取额外积分
            if (seriesDays.intValue() >= 0 && (seriesDays.intValue() + 1) % rulesMaxNum.intValue() == 0) {
                iCount = iCount.add(rulesAddValue);//如果满足连续签到条件，当天可获得积分数
            }
        }
        BigInteger limit = getTodayLimit(param);//查询明日可赠送积分时，判断是否超过赠送上限
        iCount = iCount.compareTo(limit) == 1 ? limit : iCount;

        if (StringUtil.isEmpty(checkinDataMap)) iCount = new BigInteger("0");//今天未签到

        result.put("tor_value", iCount);
        return result;
    }

    public JSONObject handleTodayLimit(BigInteger addCount, Map param) {
        JSONObject result = new JSONObject();
        BigInteger limit = getTodayLimit(param);
        BigInteger sumTodayTemp = queryAddSum(param);
        BigInteger sumToday = StringUtil.isEmpty(sumTodayTemp) ? new BigInteger("0") : sumTodayTemp;
        if (sumToday.compareTo(limit) == 0) {
            result.put("status", "error");
            result.put("msg", "已达到赠送上限");
            return result;
        }

        if (sumToday.add(addCount).compareTo(limit) == 1) {
            addCount = limit.subtract(sumToday);
            BigInteger zero = new BigInteger("0");
            if (addCount.compareTo(zero) == -1) {
                result.put("status", "error");
                result.put("msg", "已达到赠送上限，可赠送积分为0");
                return result;
            }
            result.put("msg", "已达到赠送上限，可赠送积分为" + limit.subtract(sumToday));
        }
        result.put("hasHandleVal", addCount);
        result.put("status", "success");
        return result;
    }

    //添加签到记录，不更改积分
    @Transactional
    public void chickenLogAddOnly(Map m) {
        m.put("type",20);
        mapper.logAdd(m);
        Map yesterday = mapper.checkinYesterday(m);
        m.put("checkinNum", StringUtil.isEmpty(yesterday) ? 1 : getInteger(yesterday, "checkin_num") + 1);//昨天没有签到,则连续签到总数为1
        mapper.updateChickenData(m);//更新连续签到数，签到总数加一
    }

    public Integer getInteger(Map map, String str) {
        if (StringUtil.isEmpty(map)) return 0;
        return Integer.valueOf(StringUtil.isEmpty(map.get(str)) ? "0" : map.get(str).toString());
    }

    public String getStringVal(Map map, String str) {
        if (StringUtil.isEmpty(map)) return "";
        return StringUtil.isEmpty(map.get(str)) ? "" : map.get(str).toString();
    }

    public BigInteger getBigInteger(Map map, String str) {
        if (map == null) return new BigInteger("0");
        return new BigInteger(StringUtil.isEmpty(map.get(str)) ? "0" : map.get(str).toString());
    }

    public List<Map> logQuery(Map m) {
        return mapper.logQuery(m);
    }

    public Map getOverPlus(Map m) {
        return mapper.getOverPlus(m);
    }

    public void updateMemberData(Map m) {
        mapper.updateMemberData(m);
    }

    public BigInteger queryDiffSum(Map m) {
        return mapper.queryDiffSum(m);
    }

    @Transactional
    public JSONObject integralDiff(Map param) {
        param.put("useCase", CommonConstant.USE_CASE_DIFF);
        //param.put("integralDesc", CommonConstant.LOG_DESC_DIFF);
        param.put("integralAdd", 0);
        String checkParam = mapKeyHelper(param, "siteId", "orderAmount", "buyerId", "useNum", "integralDesc");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        param.put("integralDesc", getStringVal(param, "integralDesc"));
        logger.info("积分抵现金接口，参数为：{}", JacksonUtils.mapToJson(param));
        Map ruleMap = mapper.query(param);//是否开启送积分
        if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
            return resultHelper(false, CommonConstant.MSG_STATUS_DIFF);
        }
        Map overPlusMap = mapper.getOverPlus(param);
        if (StringUtil.isEmpty(overPlusMap)) {
            return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
        }

        JSONObject result = new JSONObject();
        BigInteger orderAmount = getBigInteger(param, "orderAmount");
        BigInteger limit = getBigInteger(ruleMap, "subtract_max");
        BigInteger overPlus = getBigInteger(overPlusMap, "integrate");//剩余积分
        BigInteger useNum = getBigInteger(param, "useNum");//用户打算使用的积分
        BigInteger totalSub = null;//用户实际最多可以抵扣的钱
        BigDecimal proportionWrap = queryIntegralProportion(param);
        if (StringUtil.isEmpty(proportionWrap)) {
            result.put("status", "error");
            result.put("msg", "获取积分比例异常");
            return result;
        }
        BigInteger proportion = proportionWrap.toBigInteger();//积分比例
        BigInteger useSub = useNum.multiply(proportion);//用户打算抵扣的钱

        if (useNum.compareTo(overPlus) == 1) {
            result.put("status", "error");
            result.put("msg", "积分不足");
            return result;
        }

        JSONObject totalSubWrap = calcTotalSub(ruleMap, orderAmount, proportion, useNum);
        if (!totalSubWrap.getBoolean("tempFlag")) return totalSubWrap;
        totalSub = totalSubWrap.getBigInteger("totalSub");

        //是否超出最多可抵用钱数
        useSub = useSub.compareTo(totalSub) == 1 ? totalSub : useSub;

        //是否超出每天抵扣上限
        BigInteger sumTodayTemp = mapper.queryDiffSum(param);
        BigInteger sumToday = StringUtil.isEmpty(sumTodayTemp) ? new BigInteger("0") : sumTodayTemp.multiply(proportion);//积分转换为钱
        if (sumToday.compareTo(limit) == 0) {
            result.put("status", "error");
            result.put("msg", "已达到抵扣上限");
            return result;
        }
        if (useSub.add(sumToday).compareTo(limit) == 1) {
            useSub = limit.subtract(sumToday);
            BigInteger zero = new BigInteger("0");
            if (useSub.compareTo(zero) == -1) {
                result.put("status", "error");
                result.put("msg", "已达到抵扣上限，可抵扣钱数为0");
                return result;
            }
            result.put("msg", "已达到抵扣上限，可抵扣钱数为" + useSub);
        }

        //最多只能抵扣订单金额大小
        useSub = useSub.compareTo(orderAmount) == 1 ? orderAmount : useSub;

        //修改积分数据并记录log
        BigInteger diffCount = useSub.divide(proportion);//最终需要减少的积分
        BigInteger totalConsumeIntegrate = getBigInteger(overPlusMap, "total_consume_integrate");

        param.put("integralOverplus", overPlus.subtract(diffCount));
        param.put("integralDiff", diffCount);
        param.put("type",70);
        mapper.logAdd(param);

        param.put("totalConsumeIntegrate", diffCount.add(totalConsumeIntegrate));
        param.put("integrate", overPlus.subtract(diffCount));
        mapper.updateMemberData(param);

        result.put("status", "success");
        result.put("integralPrice", useSub);
        result.put("integralUsed", diffCount);
        return result;
    }


    public JSONObject calcTotalSub(Map ruleMap, BigInteger orderAmount, BigInteger proportion, BigInteger useNum) {
        JSONObject result = new JSONObject();
        result.put("tempFlag", true);
        BigInteger totalSub = null;//用户实际最多可以抵扣的钱
        JSONObject rulesTemp = new JSONObject();
        String diffType = getStringVal(ruleMap, "subtract_type");//110 抵扣一次，120 累加抵扣，130直接抵扣
        Object valueTemp = ruleMap.get("value");
        if (!StringUtil.isEmpty(valueTemp)) {

            try {
                JSONArray arrTmp = (JSONArray) JSONArray.parse(valueTemp.toString());
                rulesTemp = arrTmp.getJSONObject(0);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("status", "error");
                result.put("msg", "无法获取积分抵现金规则");
                result.put("tempFlag", false);
                return result;
            }
        }
        BigInteger ruleMin = rulesTemp.getBigInteger("min");
        BigInteger ruleMax = rulesTemp.getBigInteger("max");
        BigInteger ruleValue = rulesTemp.getBigInteger("value");

        if (orderAmount.compareTo(ruleMin) == -1) {
            result.put("status", "error");
            result.put("msg", "不满足积分抵现金条件");
            result.put("tempFlag", false);
            return result;
        }
        if (diffType.equals("130")) {//直接抵扣
            totalSub = useNum.multiply(proportion);//直接抵扣时使用用户输入的积分数量
        } else if (diffType.equals("110")) {//抵扣一次
            totalSub = ruleValue;
        } else if (diffType.equals("120")) {//每满
            totalSub = orderAmount.divide(ruleMin).multiply(ruleValue);
        }
        result.put("totalSub", totalSub);
        return result;
    }

    @Transactional
    public JSONObject integralAddForBuy(Map param) {
        param.put("useCase", CommonConstant.USE_CASE_BUY);
        param.put("integralDesc", CommonConstant.LOG_DESC_BUY);
        param.put("integralDiff", 0);
        String checkParam = mapKeyHelper(param, "siteId", "buyerId", "orderAmount");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        logger.info("购物送积分接口，参数为：{}", JacksonUtils.mapToJson(param));
        Map overPlusMap = mapper.getOverPlus(param);
        if (StringUtil.isEmpty(overPlusMap)) {
            return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
        }

        Map ruleMap = mapper.query(param);//是否开启送积分
        if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
            return resultHelper(true, CommonConstant.MSG_STATUS_BUY);
        }
        JSONObject result = new JSONObject();
        BigInteger addCount;
        BigInteger orderAmount = getBigInteger(param, "orderAmount");
        BigInteger limit = getBigInteger(ruleMap, "add_max");//赠送上限

        JSONObject addCountTemp = calcAddNum(ruleMap, orderAmount);
        if (!addCountTemp.getBoolean("tempFlag")) return addCountTemp;
        addCount = addCountTemp.getBigInteger("addCountTemp");

        BigInteger sumTodayTemp = mapper.queryAddSum(param);
        BigInteger sumToday = StringUtil.isEmpty(sumTodayTemp) ? new BigInteger("0") : sumTodayTemp;
        if (sumToday.compareTo(limit) == 0) {
            result.put("status", "error");
            result.put("msg", "已达到赠送上限");
            return result;
        }

        if (sumToday.add(addCount).compareTo(limit) == 1) {
            addCount = limit.subtract(sumToday);
            BigInteger zero = new BigInteger("0");
            if (addCount.compareTo(zero) == -1) {
                result.put("status", "error");
                result.put("msg", "已达到赠送上限，可赠送积分为0");
                return result;
            }
            result.put("status", "success");
            result.put("msg", "已达到赠送上限，可赠送积分为" + limit.subtract(sumToday));
        }

        BigInteger overPlusCount = getBigInteger(overPlusMap, "integrate");
        BigInteger totalGetIntegrate = getBigInteger(overPlusMap, "total_get_integrate");

        param.put("integralAdd", addCount);
        param.put("integralOverplus", addCount.add(overPlusCount));
        param.put("type",40);
        param.put("mark","购物送积分老接口");
        mapper.logAdd(param);

        param.put("totalGetIntegrate", addCount.add(totalGetIntegrate));
        param.put("integrate", addCount.add(overPlusCount));
        mapper.updateMemberData(param);

        result.put("status", "success");
        result.put("integrate", addCount);
        return result;
    }

    public JSONObject calcAddNum(Map ruleMap, BigInteger orderAmount) {
        JSONObject result = new JSONObject();
        result.put("tempFlag", true);
        BigInteger addCount = null;
        JSONObject rulesTemp;
        JSONArray arrTmp;
        try {
            arrTmp = (JSONArray) JSONArray.parse(getStringVal(ruleMap, "value"));
            rulesTemp = arrTmp.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("msg", "无法获取购物送积分规则");
            result.put("tempFlag", false);
            return result;
        }
        String addType = getStringVal(ruleMap, "add_type");//110 赠送一次，120 累计赠送，130 不同条件赠送
        BigInteger ruleMin = rulesTemp.getBigInteger("min");
        BigInteger ruleMax = rulesTemp.getBigInteger("max");
        BigInteger ruleValue = rulesTemp.getBigInteger("value");
        if (orderAmount.compareTo(ruleMin) == -1) return result4CalcAddNum();

        if (addType.equals("110")) {
            addCount = ruleValue;
        } else if (addType.equals("120")) {
            addCount = orderAmount.divide(ruleMin).multiply(ruleValue);
        } else if (addType.equals("130")) {
            addCount = getStageMaxIntergral(arrTmp, orderAmount);
            if (addCount.compareTo(new BigInteger("0")) == 0) return result4CalcAddNum();
        }
        result.put("addCountTemp", addCount);
        return result;
    }

    public BigInteger getStageMaxIntergral(JSONArray stage, BigInteger orderAmount) {
        List<BigInteger> arr = new ArrayList();
        for (int i = 0; i < stage.size(); i++) {
            arr.add(stage.getJSONObject(i).getBigInteger("min"));
        }
        Collections.sort(arr);
        for (int i = arr.size() - 1; i >= 0; i--) {
            if (orderAmount.compareTo(arr.get(i)) != -1) {
                return stage.getJSONObject(i).getBigInteger("value");
            }
        }
        return new BigInteger("0");
    }

    public JSONObject result4CalcAddNum() {
        JSONObject result = new JSONObject();
        result.put("status", "error");
        result.put("msg", "不满足送积分条件");
        result.put("tempFlag", false);
        return result;
    }

    public JSONObject integralMaxDiff(Map param) {

        param.put("useCase", CommonConstant.USE_CASE_DIFF);
        param.put("integralDesc", CommonConstant.LOG_DESC_DIFF);
        param.put("integralAdd", 0);

        String checkParam = mapKeyHelper(param, "siteId", "orderAmount", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        logger.info("根据订单金额查询可抵用积分数接口，参数为：{}", JacksonUtils.mapToJson(param));
        Map ruleMap = mapper.query(param);//是否开启送积分
        if (StringUtil.isEmpty(ruleMap) || (ruleMap != null && ruleMap.get("status").equals(CommonConstant.STATUS_SETTING))) {
            return resultHelper(false, CommonConstant.MSG_STATUS_DIFF);
        }

        Map overPlusMap = mapper.getOverPlus(param);
        if (StringUtil.isEmpty(overPlusMap)) {
            return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
        }

        JSONObject result = new JSONObject();
        BigInteger orderAmount = getBigInteger(param, "orderAmount");
        BigInteger limit = getBigInteger(ruleMap, "subtract_max");//单位为元
        BigInteger overPlus = getBigInteger(overPlusMap, "integrate");
        BigInteger maxDiff = null;
        BigDecimal proportionWrap = queryIntegralProportion(param);
        if (StringUtil.isEmpty(proportionWrap)) {
            result.put("status", "error");
            result.put("msg", "获取积分比例异常");
            return result;
        }
        BigInteger proportion = proportionWrap.toBigInteger();//积分比例

        limit = limit.divide(proportion);//单位转换为积分

        JSONObject totalSubWrap = calcTotalSub(ruleMap, orderAmount, proportion, overPlus);//直接抵扣时使用会员剩余积分
        if (!totalSubWrap.getBoolean("tempFlag")) return totalSubWrap;

        //最多可以抵扣订单金额
        BigInteger totalSub = totalSubWrap.getBigInteger("totalSub");
        totalSub = totalSub.compareTo(orderAmount) == 1 ? orderAmount : totalSub;

        maxDiff = totalSub.divide(proportion);//钱转换为积分

        //不能超出会员的剩余积分
        maxDiff = maxDiff.compareTo(overPlus) == 1 ? overPlus : maxDiff;

        //是否超出每天抵扣上限
        BigInteger sumTodayTemp = mapper.queryDiffSum(param);
        BigInteger sumToday = StringUtil.isEmpty(sumTodayTemp) ? new BigInteger("0") : sumTodayTemp;
        if (sumToday.compareTo(limit) == 0) {
            result.put("status", "error");
            result.put("msg", "已达到抵扣上限");
            return result;
        }

        if (maxDiff.add(sumToday).compareTo(limit) == 1) {
            maxDiff = limit.subtract(sumToday);
            BigInteger zero = new BigInteger("0");
            if (maxDiff.compareTo(zero) == -1) {
                result.put("status", "error");
                result.put("msg", "已达到抵扣上限，可抵用积分数量为0");
                return result;
            }
            result.put("msg", "已达到抵扣上限，可抵用积分数量为" + maxDiff);
        }

        result.put("status", "success");
        result.put("maxDiff", maxDiff);
        return result;
    }

    public JSONObject integralCalcMoney(Map param) {

        param.put("useCase", CommonConstant.USE_CASE_DIFF);
        param.put("integralDesc", CommonConstant.LOG_DESC_DIFF);
        param.put("integralAdd", 0);
        String checkParam = mapKeyHelper(param, "siteId", "orderAmount", "buyerId", "useNum");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }

        logger.info("计算用户输入的积分数的可抵扣钱数接口，参数为：{}", JacksonUtils.mapToJson(param));
        Map status = mapper.query(param);//是否开启
        if (StringUtil.isEmpty(status) || (status != null && status.get("status").equals(CommonConstant.STATUS_SETTING))) {
            return resultHelper(false, CommonConstant.MSG_STATUS_DIFF);
        }
        JSONObject check = integralMaxDiff(param);
        if (!check.getString("status").equals("success")) {
            return check;
        }
        JSONObject result = new JSONObject();
        try {
            BigDecimal proportionWrap = queryIntegralProportion(param);
            BigInteger money = getBigInteger(param, "useNum").multiply(proportionWrap.toBigInteger());
            result.put("status", "success");
            result.put("useNum", getBigInteger(param, "useNum"));
            result.put("useMoney", money);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("msg", e);
        }
        return result;
    }

    public List<Map> queryMemberi(Map item) {
        return mapper.queryMemberi(item);
    }

    @Transactional
    public List<Map> queryRules(Map item) {
        Map tmp = new HashMap();
        tmp.put("siteId", StringUtil.isEmpty(item.get("siteId")) ? "" : item.get("siteId").toString());
        List<Map> result = mapper.queryRules(item);
        if (!StringUtil.isEmpty(result) && result.size() > 0) {
            return mapper.queryRules(item);
        }
        String[] titles = {"签到送积分", "积分抵现金", "注册送积分", "购物送积分"};
        String[] types = {"1", "2", "1", "1"};
        String[] values = {"[{}]", "[{}]", "[{}]", "[{}]"};
        String[] descs = {"签到送积分", "用户支付货款时，可以用积分抵扣现金。", "用户在商城完成注册后，就可以自动获得相应积分。", "用户下单并付款后，就可以自动获得相应的积分。"};
        String[] useCases = {"140", "130", "110", "120"};
        for (int i = 0; i < titles.length; i++) {
            if (!titles[i].equalsIgnoreCase("积分抵现金")) {  //先过滤掉积分抵现金这条数据
                Map param = new HashMap();
                param.put("siteId", item.get("siteId"));
                param.put("title", titles[i]);
                param.put("type", types[i]);
                param.put("value", values[i]);
                param.put("integralDesc", descs[i]);
                param.put("useCase", useCases[i]);
                mapper.init(param);
            }
        }
        return mapper.queryRules(item);
    }

    public void setProportion(Map param) {
        mapper.setProportion(param);

    }

    /**
     * 获取积分比例
     * 默认为1，即1个积分1分钱
     *
     * @param param
     * @return
     */
    public BigDecimal queryIntegralProportion(Map param) {
        BigDecimal val = mapper.queryIntegralProportion(param);
        if (StringUtil.isEmpty(val) || val.intValue() == 0) return new BigDecimal("1");
        return val;
    }

    public void updateRule(Map map) {
        mapper.updateRule(map);
    }

    /**
     * 强制修改积分
     *
     * @param param
     */
    @Transactional
    public void updateIntegralForce(Map param, Map overPlusMap) {
        BigInteger editCount = getBigInteger(param, "value");
        boolean flag = Boolean.valueOf(param.get("type").toString());
        if (StringUtil.isEmpty(overPlusMap)) return;//找不到该会员，直接返回
        BigInteger overPlus = getBigInteger(overPlusMap, "integrate");
        String integralDesc = "";
        if (flag) {//加
            param.put("integralAdd", editCount);
            param.put("integralDiff", 0);
            param.put("integralOverplus", overPlus.add(editCount));
            integralDesc = "商城加积分";

            param.put("integrate", overPlus.add(editCount));
            param.put("totalGetIntegrate", getBigInteger(overPlusMap, "total_get_integrate").add(editCount));
        }
        if (!flag) {//减
            param.put("integralDiff", editCount);
            param.put("integralAdd", 0);
            param.put("integralOverplus", overPlus.subtract(editCount));
            integralDesc = "商城减积分";

            param.put("integrate", overPlus.subtract(editCount));
            param.put("totalConsumeIntegrate", getBigInteger(overPlusMap, "total_consume_integrate").add(editCount));
        }
        if (StringUtil.isEmpty(getStringVal(param, "integralDesc"))) param.put("integralDesc", integralDesc);

        param.put("useCase", 0);
        /*String type = String.valueOf(param.get("type"));
        if(type.equals("true")) {
            param.put("type",0);
        }*/
        //直接将类型覆盖掉
        param.put("type",0);
        mapper.logAdd(param);
        mapper.updateMemberData(param);
    }

    public Map checkinToday(Map param) {
        return mapper.checkinToday(param);
    }

    public BigInteger queryAddSum(Map param) {
        return mapper.queryAddSum(param);
    }

    /**
     * 获取每日赠送上限
     *
     * @param param
     * @return
     */
    public BigInteger getTodayLimit(Map param) {
        param.put("useCase", CommonConstant.USE_CASE_BUY);
        return new BigInteger(mapper.query(param).get("add_max").toString());
    }

    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
    }

    public JSONObject resultHelper(boolean flag, String str) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        result.put("msg", str);
        return result;
    }

    public JSONObject storeUpdateIntegral(Map param) {
        String checkParam = mapKeyHelper(param, "siteId", "type", "value", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }
        if (StringUtil.isEmpty(param.get("siteId"))) {
            return resultHelper(false, "参数 siteId 不能为空！");
        }
        Map overPlusMap = getOverPlus(param);
        if (StringUtil.isEmpty(overPlusMap)) {
            return resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
        }
        updateIntegralForce(param, overPlusMap);
        return resultHelper(true);
    }

    public JSONObject resultHelper(boolean flag) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        return result;
    }

    public List<IntegralRule> rules(Map param){
        List<IntegralRule> ruleList = mapper.rules(param);
        // 10 注册送 20 签到送 30 完善信息送 40 下单满额送 50 咨询评价送 60 订单评价送
        for (IntegralRule rule:ruleList) {
            try{
                Map map = JacksonUtils.json2map(rule.getRule());
                switch (rule.getType()){
                    case 10:
                        rule.setDesc("+"+map.get("firstRegister").toString()+"积分");
                        break;
                    case 20:
                        rule.setDesc("+"+map.get("value").toString()+"积分");
                        break;
                    case 40:
                        String msg = "无上限";
                        if(!StringUtil.isEmpty(rule.getLimit())){
                            msg = "上限" + rule.getLimit()+"积分";
                        }
                        rule.setDesc("购物送积分，"+msg);
                        break;
                    case 50:
                        rule.setDesc("+"+map.get("evaluate").toString()+"积分");
                        break;
                    case 60:
                        rule.setDesc("+"+map.get("orderEvaluate").toString()+"积分");
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return ruleList;
    }

    public int calcIntegral(int siteId, List<OrderGoods> orderGoods) {
        return mapper.calcIntegral(siteId, orderGoods);
    }
}
