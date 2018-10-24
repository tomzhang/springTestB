package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.account.requestParams.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/17
 * Update   :
 */
@Mapper
public interface SettlementDetailAndTradesMapper {

     List<SettlementDetailAndTrades> getSettlementListByTradesId(@Param("seller_id")Integer seller_id, @Param("startDate")Timestamp startDate, @Param("endDate")Timestamp endDate);

     List<Map<String,Object>> getSettlementListByObj(@Param("account_params") AccountParams accountParams);

     List<Map<String,Object>> getSettlementListByObjByexport(Map <String,Object> map);

     Map<String,Object> getTotalAwardByTradesId(String trades_id);

     Map<String,Object> findSettlementDetailById(String tradesId);

     List<Map<String,Object>>  getStoreSettlementListByObj(@Param("account_params") AccountParams accountParams);

     List<Map<String,Object>>  getStoreSettlementListByObjs(Map<String,Object> map);

     List<Map<String,Object>>  getClerkSettlementListByObj(@Param("account_params") AccountParams accountParams);

     List<Map<String,Object>>  getClerkSettlementListByObjs(@Param("account_params")Map<String,Object> map);

     List<AccountException>  getAccountByException(@Param("account_exception") AccountException accountException);

     List<Map<String,Object>>  getAccountRemit(@Param("account_remit") Map<String, Object> accountRemit);//划账明细sql语句

     List<Map<String,Object>> getAccountRemitStatic(@Param("account_remit") AccountRemit accountRemit);   //划账明细的合计

     List<Map<String,Object>>  getAccountRemitSum(@Param("account_remitsum") AccountRemit accountRemit);//划账汇总sql语句

     Map<String,Object>  getAccountRemitStastic(@Param("account_remitsum") AccountRemit accountRemit);//统计sql语句

     Long getTotalClerkSettlementListByObj(@Param("account_params")AccountParams accountParams);

     Long getTotalTrades();

     Map<String,Object> getRefundByTradesId(String trades_id);

     List<Map<String,Object>> getSettlementListByObjForExport(@Param("account_params") AccountParams accountParams);

     List<Map<String,Object>> getPreSettlementLst(@Param("parm") PreSettlementParam parm);

     List<Map<String,Object>> getAccountRun(@Param("accountRun") AccountRun accountRun);

    int getAccountCollectCount(@Param("accountCollect") AccountCollect accountCollect);  //商家汇总count

     List<Map<String,Object>> getAccountCollect(@Param("pageNum") Integer pageNum, @Param("pageSize")Integer pageSize, @Param("accountCollect") AccountCollect accountCollect);   //商家汇总

     Long getAccountRunTotal(@Param("accountRun") AccountRun accountRun);

     Map<String,Object>  getRemitMoney(String trades_id);

    List<SettlementDetailAndTrades> getTradesForFinances(@Param("seller_id")Integer seller_id, @Param("startDate")Timestamp startDate, @Param("endDate")Timestamp endDate);

    List<SettlementDetailAndTrades> getNoSettlementPostfee(@Param("seller_id")Integer seller_id);

    List<Map<String,Object>> getSettlementList(@Param("account_params") Map<String, Object> map);

    List<SettlementDetailAndTrades> getNoSettlementTrades(@Param("seller_id")Integer seller_id,@Param("startDate") Timestamp start_date);

    List<Map<String,Object>> exportAccountRemitList(@Param("account_remitsum") Map params);   // 划账汇总表导出
}
