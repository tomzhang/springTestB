package com.jk51.modules.account.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.account.models.AccountCheckData;
import com.jk51.modules.account.mapper.AccountPayCheckMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: chen
 * @Description:
 * @Date: created in 2018/9/27
 * @Modified By:
 */
@Service
public class AccountPayCheckService {
    private static Logger logger = LoggerFactory.getLogger(AccountPayCheckService.class);

    @Autowired private AccountPayCheckMapper payCheckMapper;

    public PageInfo getPayCheck(Map<String, Object> parms) {
        logger.info("获取对账数据参数:{}", JacksonUtils.mapToJson(parms));
        PageHelper.startPage(Integer.parseInt(parms.get("pageNum").toString()),Integer.parseInt(parms.get("pageSize").toString()));
        List<AccountCheckData> getPayCheck = null;
        if("0".equals(parms.get("isRefund"))){
            //付款对账
            getPayCheck = payCheckMapper.getPayCheck(parms);
        }else {
            getPayCheck = payCheckMapper.getRefundPayCheck(parms);
        }
        return new PageInfo(getPayCheck);
    }

    public List<Map<String,Object>> accountCheckPayExport(Map<String, Object> params){
        checkParms(params);
        List<Map<String,Object>> getPayCheck = payCheckMapper.getPayCheckReport(params);

        return getPayCheck;
    }

    public List<Map<String,Object>> accountCheckRefundExport(Map<String, Object> params){
        checkParms(params);
        List<Map<String,Object>> getPayCheck = payCheckMapper.getRefundPayCheckReport(params);

        return getPayCheck;
    }

    private Map<String, Object> checkParms(Map<String, Object> params){
        if (params.containsKey("realPayA") && StringUtil.isNotEmpty(params.get("realPayA").toString())){
            Double   f3   =    (new BigDecimal(Double.parseDouble( params.get("realPayA").toString()) * 100).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
            int realPayA = f3.intValue();
            params.put("realPayA",realPayA);
        }
        if (params.containsKey("realPayB") && StringUtil.isNotEmpty(params.get("realPayB").toString())){
            Double   f3   =    (new BigDecimal(Double.parseDouble( params.get("realPayB").toString()) * 100).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
            int realPayB = f3.intValue();
            params.put("realPayB",realPayB);
        }
        return params;
    }

}
