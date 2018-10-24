package com.jk51.modules.sms.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.balance.Balance;
import com.jk51.modules.balance.mapper.BalanceMapper;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.offline.mapper.SMSLogMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:短信记录插入
 * 作者: dumingliang
 * 创建日期: 2018-01-05
 * 修改记录:
 */
@Service
public class SmsService {
    @Autowired
    private SMSLogMapper smsLogMapper;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceMapper balanceMapper;

    public void insertLog(Integer siteId, Integer extId, Integer storeId, Integer type, String sendMsg, String return_msg) {
        try {
            if (type == 1){type = 100;}//避免类型为1的验证码短信
            smsLogMapper.insertLog(siteId, extId, storeId, type, sendMsg, return_msg);
            Balance balanceObj = balanceMapper.getBalanceBySiteIdForBool(siteId);//获取余额对象
            Map map = JacksonUtils.json2map(sendMsg);
            if (map.containsKey("phone") && !StringUtil.isEmpty(map.get("phone")) && !StringUtil.isEmpty(type)){
                map.put("type",type);
                int msgNum = 1;
                if (!StringUtil.isEmpty(balanceObj)){
                    map.put("storeId",storeId);
                    if (!StringUtil.isEmpty(map.get("Fee")) && Integer.parseInt(String.valueOf(map.get("Fee"))) > 0){
                        msgNum = Integer.parseInt(String.valueOf(map.get("Fee")));
                    }
                    balanceService.insertBalanceDetail(siteId, 4, null,JacksonUtils.mapToJson(map) , null,type,msgNum);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //给商户发信息，不调用短信费率
    public void insertLogToMerchant(Integer siteId, Integer extId, Integer storeId, Integer type, String sendMsg, String return_msg) {
        smsLogMapper.insertLog(siteId, extId, storeId, type, sendMsg, return_msg);
    }
}
