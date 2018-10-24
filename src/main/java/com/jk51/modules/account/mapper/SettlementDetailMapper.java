package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.AccountCheckException;
import com.jk51.model.account.models.AccountImportData;
import com.jk51.model.account.models.ClassifiedStatistic;
import com.jk51.model.account.models.SettlementDetail;
import com.jk51.model.account.requestParams.AccountParams;
import com.jk51.model.account.requestParams.QueryOrderBill;
import com.jk51.model.account.requestParams.QuerySettlementLog;
import com.jk51.model.account.requestParams.QueryStatement;
import com.jk51.model.goods.PageData;
import com.jk51.model.order.Trades;
import com.jk51.model.settle.FinancialSettleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/16
 * Update   :
 */
@Mapper
public interface SettlementDetailMapper {

    public SettlementDetail getSettlementListByTradesId(@Param("trades_id") long trades_id);

    public int addSettlementDetail(@Param(value = "settlementDetail") SettlementDetail settlementDetail);

    public int updateSettlementById(@Param(value = "settlementDetail") SettlementDetail settlementDetail);

    public int updateSettlementByCh(@Param(value = "settlementDetail") SettlementDetail settlementDetail);

    public int updateSettlementRefundById(@Param(value = "settlementDetail") SettlementDetail settlementDetail);

    public int updateSettlementByRemitData(@Param(value = "settlementDetail") SettlementDetail settlementDetail);   //更新settlementDetail表的划账服务费及费率

    public int updateRefundSettlementById(@Param(value = "settlementDetail") SettlementDetail settlementDetail);
    public int updateAccountImportById(@Param(value = "settlementDetail") AccountImportData settlementDetail);
    public int updateSettlementByTradeId(@Param(value = "trades_id") long trades_id,
                                         @Param(value = "finance_no") String finance_no);
    public int updateWechatRefundBankById(@Param(value = "settlementDetail") SettlementDetail settlementDetail);
    List<PageData> findSettlementDetail(QueryStatement queryStatement);

    List<PageData> findSettlementList(QueryOrderBill queryOrderBill);

    List<Map<String,Object>> findsSettlementListExcel(QueryStatement queryStatement);

    List<Map<String,Object>> findSettlementLog(QuerySettlementLog log);

    Map<String,Object> findDetails(String orderId,Integer siteId);

    Map<String,Object> findUserNameById(String orderId,Integer siteId);

    List<FinancialSettleDetail> queryOrderSettleDetail(Map<String,Object> params);

    Map queryOrderSettleDetailStastic(Map<String,Object> params);//统计订单对账情况的支付汇总sql语句

    Map queryImportSettleDataStastic(Map<String,Object> params);//统计订单对账情况的导入数据支付金额的汇总sql语句

    Map queryImportDataStastic(Map<String,Object> params);//统计订单对账情况的导入数据退款金额的汇总sql语句


    Long queryTotalOrderSettleDetail(Map<String,Object> params);


    List<Map<String,Object>> queryOrderSettleDetailForExport(@Param("account_params") AccountParams accountParams);



    /*List<PageData> queryPaySettleDetail(Map<String,Object> params);

    List<PageData> queryRefundSettleDetail(Map<String,Object> params);*/

    List<SettlementDetail> findSettlementDetailsByFinancesId(String financesNo);

    List<SettlementDetail> findTradesByFinance(String financesNo);

    int updateStatusByTradesId(Map<String,Object> param);

    List<ClassifiedStatistic> findCheckedMoney(String financesNo);

    List <Trades> selectTradesByMigrate();

    int addAccountException(@Param(value = "accountcheckException") AccountCheckException accountcheckException);

    List<Map<String,Object>> queryOrderSettleDetailByobj(Map <String,Object> map);

    List<SettlementDetail> selectRefunCheckStatus();

    int updateRefunCheckStatus(@Param(value = "trades_id") long trades_id);

}
