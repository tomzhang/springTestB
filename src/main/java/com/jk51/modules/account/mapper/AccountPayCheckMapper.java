package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.AccountCheckData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: chen
 * @Description:
 * @Date: created in 2018/9/27
 * @Modified By:
 */
@Mapper
public interface AccountPayCheckMapper {


    List<AccountCheckData> getPayCheck(Map<String, Object> parms);//付款对账
    List<Map<String,Object>> getPayCheckReport(Map<String, Object> parms);//付款对账

    List<AccountCheckData> getRefundPayCheck(Map<String, Object> parms);//退款对账
    List<Map<String,Object>> getRefundPayCheckReport(Map<String, Object> parms);//退款对账

}
