package com.jk51.modules.pay.service;

import com.jk51.model.PayLogs;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Service
public class PayLogsService {
    //@Autowired
    //PayLogsMapper payLogsMapper;
   /* @Autowired
    DistributeOrderMapper distributeOrderMapper;
    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    TradesMapper tradesMapper;*/

    public List<PayLogs> findPayLogs(Map<String, Object> params) {
        //List<String> accountNos= StringUtil.convertToList(params.get("ybAccount")+"");
        //params.put("ybAccount","".equals(params.get("ybAccount")+"")||null==params.get("ybAccount")+""?null:accountNos);    // 将 List 数据再放回 params 参数中
        //return payLogsMapper.findPayLogs(params);
        return null;
    }

    public PayLogs findByTradesId(long tradesId) {
       // return payLogsMapper.findByTradesId(tradesId);
        return null;
    }

    /**
     * 添加一条日志
     * @param tradesId
     * @param payStyle
     * @param payStatus
     */
    @Async
    public void addByTrades(long tradesId, String payStyle, Integer payStatus) {
        /*Trades trades = tradesMapper.getTradesByTradesId(tradesId);
        if(trades == null)
            return;
        PayLogs payLogs = findByTradesId(trades.getTradesId());
        if(payLogs == null) {
            payLogs = new PayLogs();
        }
        payLogs.setYbAccount("51jk");
        payLogs.setSiteId(trades.getSiteId());
        payLogs.setTradesId(trades.getTradesId());
        payLogs.setItemsTotal(ordersMapper.getOrdersTotalPriceByTradesId(trades.getTradesId()));
        payLogs.setPostFee(trades.getPostFee());
        payLogs.setTradesTotal(trades.getTotalFee());
        payLogs.setPlatformTotal(trades.getPlatSplit());
        payLogs.setTradesSplit(trades.getTradesSplit());
        payLogs.setTotalFee(trades.getRealPay());
        payLogs.setPayStyle(payStyle);
        payLogs.setPayStatus(payStatus);
        Merchant merchant = distributeOrderMapper.getMerchant(trades.getSiteId());
        if(merchant != null)
            payLogs.setSellerAccount(merchant.getMerchantName());
        payLogs.setAssignedStores(trades.getAssignedStores());
        payLogs.setTradesStatus(trades.getTradesStatus());
        payLogs.setConfirmGoodsTime(trades.getConfirmGoodsTime());
        payLogs.setSellerId(trades.getSellerId());
        payLogs.setBuyerId(trades.getBuyerId());
        payLogs.setPrescriptionOrders(trades.getPrescriptionOrders());
        payLogs.setTradesStore(trades.getTradesStore());
        payLogs.setAccountCheckingStatus(trades.getAccountCheckingStatus());
        payLogs.setStoreUserId(trades.getStoreUserId());
        payLogs.setStoreShippingClerkId(trades.getStoreShippingClerkId());
        payLogs.setRecommendUserId(trades.getRecommendUserId());
        payLogs.setCashierId(trades.getCashierId());
        payLogs.setStockupUserId(trades.getStockupUserId());
        payLogs.setSettlementStatus(trades.getSettlementStatus());
        payLogs.setSettlementFinalTime(trades.getSettlementFinalTime());
        if(payLogs.getPayId() == null)
            payLogsMapper.insert(payLogs);
        else
            payLogsMapper.update(payLogs);*/
    }

    /**
     * 更行一条日志
     * @param tradesId
     * @param payStyle
     * @param payStatus
     * @param callbackTime
     * @param payResult
     * @param ybAccount
     * @param buyerAccount
     */
    public void updateByTrades(long tradesId, String payMenber, String payStyle, Integer payStatus, Date callbackTime, String payResult,String ybAccount, String buyerAccount) {
       /* PayLogs payLogs = findByTradesId(tradesId);
        if(payLogs == null)
            return ;
        payLogs.setPayMember(payMenber);
        payLogs.setPayStyle(payStyle);
        payLogs.setPayStatus(payStatus);
        payLogs.setCallbackTime(callbackTime);
        payLogs.setPayResult(payResult);
        payLogs.setYbAccount(ybAccount);
        payLogs.setBuyerAccount(buyerAccount);*/
        //payLogsMapper.update(payLogs);
    }

    public void updateByTrades(long tradesId, Integer payStatus) {
       /* PayLogs payLogs = findByTradesId(tradesId);
        if(payLogs == null)
            return ;
        payLogs.setPayStatus(payStatus);*/
        //payLogsMapper.update(payLogs);
    }
}
