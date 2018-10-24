package com.jk51.modules.checkin.service;


import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.checkin.mapper.CheckinMapper;
import com.jk51.modules.integral.mapper.IntegralMapper;
import com.jk51.modules.integral.service.IntegralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CheckinService {

    private static final Logger logger = LoggerFactory.getLogger(CheckinService.class);

    @Autowired
    private CheckinMapper mapper;

    @Autowired
    private IntegralMapper integralMapper;

    @Autowired
    private IntegralService integralService;

    public Integer queryToday(Integer siteId){
        return mapper.queryToday(siteId);
    }

    @Transactional
    public JSONObject checkinAction(Map param) {
        Integer siteId = getMapInt(param, "siteId");
        Integer buyerId = getMapInt(param, "buyerId");
        Map exist = integralService.checkinToday(param);
        if (!StringUtil.isEmpty(exist)){
            return resultHelper(false, "你已签到");
        }
        JSONObject item = integralService.integralAddForChicken(param, true);
        logger.info("签到时调用签到送积分接口，返回结果{}", item);

        mapper.checkinAction(siteId, buyerId);
        Map yesterday = integralMapper.checkinYesterday(param);
        param.put("checkinNum", StringUtil.isEmpty(yesterday) ? 1 : getInteger(yesterday, "checkin_num") + 1);//昨天没有签到,则连续签到总数为1
        integralMapper.updateChickenData(param);

        if(item.getString("status").equals("error")){
            return resultHelper(true, item.getString("msg"));
        }
        return resultHelper(true);
    }

    public JSONObject resultHelper(boolean flag) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "fail");
        return result;
    }

    public int getMapInt(Map param, String key) {
        return (param.containsKey(key) && !StringUtil.isEmpty(param.get(key))) ? Integer.valueOf(param.get(key) + "") : 0;
    }

    public JSONObject resultHelper(boolean flag, String str) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        result.put("msg", str);
        return result;
    }

    public Integer getInteger(Map map, String str) {
        if (StringUtil.isEmpty(map)) return 0;
        return Integer.valueOf(StringUtil.isEmpty(map.get(str)) ? "0" : map.get(str).toString());
    }
}
