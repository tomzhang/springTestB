package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.Finances;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/22
 * Update   :
 */
@Mapper
public interface FinancesMapper {

    int addFinances(@Param(value = "finances") Finances finances);

    Finances getStartDay(@Param(value = "seller_id") Integer seller_id);

    Finances getStartDayAndEndDay(@Param(value = "seller_id") Integer seller_id,
                                  @Param(value = "start_time") Date start_time,
                                  @Param(value = "end_time") Date end_time);

    Map<String,Object> findByFinancesNo(Integer siteId,String financeNo);

    Map<String,Object> findBeforeData(Integer siteId,String createTime);

    List<Map<String,Object>> findStatisticById(Integer siteId,String financeNo);
    LinkedHashMap<String,Object> findSettlementDetailByTradeId(Integer siteId, String tradesId);
    Map<String,Object> getOfficialAccount(String sellerId);

    int updateFinanceByNo(Finances finances);

    List<Map<String,String>> findStatisticFinancesBalance(Map param);

    List<Map<String,String>> getFinancesBalanceStatic(Map param);  // 财务余额核对的合计
}
