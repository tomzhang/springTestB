package com.jk51.modules.trades.mapper;

import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.account.requestParams.RefundParams;
import com.jk51.model.order.Refund;
import com.jk51.model.order.Trades;
import com.jk51.model.order.response.RefundQueryReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 退款记录
 * 作者: hulan
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Mapper
public interface RefundMapper {
    public int addRefund(Refund refund);
    public Refund getByTradesId(int siteId,String tradesId);
    /* start 获取退款金额zw*/
    public Refund getRefundListByTradesId(@Param(value = "trade_id") String trade_id);
    public List<Refund> getRefundListByAccountCheckingStatus(@Param("siteId") Integer siteId);//修改订单对账状态

    public List<Refund> getRefundListByRemitAccountStatus();//修改订单划账状态

    public int updateAccountStatus(long trade_id);
    /*end zw*/
    public void updateStatus(String tradeId,int status,String refundSerialNo,int is_coupon, int is_integral, int money);
    public void refusedStatus(String tradeId,int status);

    List<Refund> queryRefundList(@Param("refundParams") RefundParams refundParams);

    Refund getByTradesId2(@Param("siteId")Integer siteId, @Param("tradesId")String tradesId);

    Refund getRefundByTradeId(@Param("siteId")Integer siteId, @Param("tradesId")String tradesId);

    List<Map<String,Object>> getTradesRefundList(Map<String, Object> params);



    public List<Refund> getRefundList(@Param("refundQueryReq") RefundQueryReq req);

    public List<Refund> refundList(@Param("refundQueryReq") RefundQueryReq req);

    public Refund getRefundById(@Param("refundQueryReq") RefundQueryReq req);

    Trades getTradesRealPayTotal(@Param("siteId") Integer siteId); //校验退款金额与支付总金额对比

    List<SettlementDetailAndTrades> getRefunds(@Param("seller_id")Integer seller_id, @Param("startDate")Timestamp start_date, @Param("endDate")Timestamp end_date);

    int updateRefundStoreId(@Param("trades_id")Long tradesId, @Param("store_id")Integer storeId);

    Refund getRefundByTradesId(@Param("tradesId") Long tradesId);
}
