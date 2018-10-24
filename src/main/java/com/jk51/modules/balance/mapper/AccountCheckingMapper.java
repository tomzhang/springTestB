package com.jk51.modules.balance.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/21.
 */
@Mapper
public interface AccountCheckingMapper {

    List<Map<String,Object>> getMerchantTradesChecking(Map<String, Object> params);

    Integer boolStoreData(Map<String, Object> params);

    Integer addStoreData(Map<String, Object> params);

    Integer updateFundsData(Map<String, Object> params);

    List<Map<String,Object>> getMerchantFunds(Map<String, Object> params);

    Map<String,Object> getMerchantFundsBySiteIdAndTime(Map<String, Object> map);

    void insertFundsBySiteId(Map<String, Object> merchantfundsMap);

    List<Map<String,Object>> getStoreMaoList(@Param("siteId") Integer siteId);

    List<Integer> getSiteIdAll();
}
