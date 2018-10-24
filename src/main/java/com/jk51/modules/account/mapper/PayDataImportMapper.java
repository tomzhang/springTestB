package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.requestParams.PayDataImportParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zhangduoduo
 * date     :2017/2/15
 * Update   :
 */
@Mapper
public interface PayDataImportMapper {
    /**
     * 根据订单号查询第三方订单
     *
     * @param trades_id
     */
    List<PayDataImport> getImportListByTradesId(@Param("trades_id") String trades_id);

    public int updateImportStatus(@Param("trade_id") String trade_id,
                                  @Param("data_type") Integer data_type ,@Param("account_status") Integer account_status);

    /*public int updateImportStatusByTradesIdAnd(@Param("id") Integer id,
                                  @Param("account_checking_status") Integer account_checking_status);*/

    /**
     * 批量保存第三方订单
     * @param payDataImportList
     */
    void savePayDataImport(@Param(value="payDataImportList") PayDataImport payDataImportList);

  //  int add(@Param(value="payDataImportList") PayDataImport payDataImport);

    /**
     * 根据check_status是否处理字段查询第三方订单
     */
    List<PayDataImport> getPayDataImportListByCheckStatus();

    List<PayDataImport> getPayDataImportListByRemitAccountStatus();    //取导入表中划账金额

    List<PayDataImport> getPayLogList(@Param("payDataImportParams") PayDataImportParams payDataImport);


    List<Map<String,Object>> getPayDataImportList (Map<String,Object> params);

    Map getPayDataImportStastic(Map<String,Object> params);//统计付款(退款)对账表的汇总sql语句

    int updateCheckStatusById(Map<String,Object> params);

    List<Map<String,Object>>  getPayDataImportExcel(Map<String, Object> params);

    PayDataImport getPayDataImportByTradesIdAndStatus(@Param("tradesId") String tradesId,@Param("tradesStatus") String tradesStatus);

    int updateImportList(@Param("payDataImport") PayDataImport payDataImport);

     List<PayDataImport> selectPayDataByTradesId(@Param("trades_id") String tradesId);

    PayDataImport selectPayDataByTradesIdAndAccountBalance(@Param("tradesId")String tradesId, @Param("tradesStatus")String tradesStatus,@Param("accountBalance")String accountBalance);

    List<PayDataImport> selectPayDataForCheck();

    void updateLostTradesId();
}
