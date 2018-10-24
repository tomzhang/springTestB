package com.jk51.modules.trades.mapper;

import com.jk51.model.account.models.Finances;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.order.*;
import com.jk51.model.packageEntity.StoreAdminCloseIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Mapper
public interface TradesMapper {
    //通过订单id获取订单
    Trades getTradesByTradesId(long trades_id);

    //获取莫顾客最近消费的一笔订单
    Trades getClosestTrades(BClerkVisit bClerkVisit);

    // 订单类型
    Trades getTradesByPostStyle(long tradesId, int postStyle);

    /*start zw */
    //通过对账状态获取订单数据
    List<Trades> getTradesListByAccountCheckingStatus(@Param("siteId") Integer siteId);

    //通过划账状态获取订单数据
    List<Trades> getTradesListByRemitAccountStatus();

    //修改订单对账状态
    int updateAccountStatus(long trade_id);

    int updateRemitAccountStatus(long trade_id); //修改订单划账状态

    int updateSettlementStatusByTradesId(String trades_id);

    /*end zw*/
    //用户未付款 取消订单
    void updateUseToCLoseTrades(Trades trades);

    //更新直购订单状态
    void updateToDirectPurchaseStatus(Trades trades);

    //送货上门 更新已发货状态
    void updateStatusToSend(Trades trades);

    //送货上门 更新到待备货
    void updateStatusToStock(Trades trades);

    //送货上门 更新到待备货
    void updateStatusAlreadyStock(Trades trades);

    //送货上门 门店或用户确认收货
    void updateConfirmStatus(Trades trades);

    //门店自提 更新为已付款状态 并 生成随机数字串
    void updateGetToStoreForPayStatus(Trades trades);

    //门店自提 更新为待自提
    void updateWaitExtract(Trades trades);

    //门店自提  更新为已自提
    void updateAlreadyExtract(Trades trades);

    void updateIsRefund(Trades trades);

    //验证提货码
    Trades selectBarCode(int site_id, String self_taken_code);

    //已退款
    void updateRefundStatus(Trades trades);

    // 查询 未付款 超时的订单
    int selectTimeoutBySystemCanel(int systemCanelDay);

    // 超时未付款 系统取消订单
    void updateTimeoutBySystemCanel(Map<String, Object> map);

    // 查询 直购 | 门店自提 确认收货超时的订单
    int selectTimeoutBySystemConfirm(int systemCanelDay);

    // 直购 | 门店自提 系统确认收货
    void updateTimeoutBySystemConfirm(Map<String, Object> map);

    // 查询 送货上门 确认收货超时的订单
    int selectTimeoutBySystemDelivery(int systemCanelDay);

    // 送货上门 系统确认收货
    void updateTimeoutBySystemDelivery(Map<String, Object> map);

    // 送货上门 系统确认收货
    void tradesEnd(Map<String, Object> map);

    //获取各门店订单数量
    List<Map<String, Integer>> getStoreOrderQuantityList();

    List<StoreAdminCloseIndex> findStoreAdminCloseIndex(@Param("now") Date now, @Param("before") Date before);

    List<Trades> getStoreTrades(QueryOrdersReq queryOrdersReq);

    List<Trades> selectTradesByFinanceNo(String finance_no);

    List<Map<String, Object>> getStoreTradesReport(Map<String, Object> map);

    int updateBarcode(Trades trades);

    /**
     * 获取订单详情
     *
     * @param tradesId
     * @return
     */
    Trades getTradesDetails(long tradesId);

    int selectMeta(int siteId, String storeAuthCode, int storeId);

    int updateCoupon(int siteId, long tradesId);  //退还优惠券

    int selectTradesByActivityCreateTime(@Param(value = "createTime") Date createTime,
                                         @Param(value = "userId") Integer userId,
                                         @Param(value = "siteId") Integer siteId);

    int queryUserFirstOrder(Integer siteId, Integer buyerId);

    int queryUserCouponFirstOrder(Integer siteId, Integer buyerId);

    int queryUserPromotionsFirstOrder(Integer siteId, Integer buyerId);

    int queryUserPromotionsIsRefundOrder(Integer siteId, Integer buyerId);

    int updatePayCommission(@Param("siteId") Integer siteId, @Param("tradesId") Long tradesId, @Param("platSplit") Integer platSplit);

    long updateO2OCommission(@Param("siteId") Integer siteId, @Param("tradesId") Long tradesId, @Param("O2OFreight") Integer O2OFreight);

    Store selectStoreInfo(int siteId, int id);

    void clerkToSend(long tradesId, int storeShippingClerkId);

    void logisticsToSend(long tradesId, String postName, String postNumber);

    int updateTradeRank(@Param("rank") Integer rank, @Param("trade_id") Long trankId);

    /**
     * 更新订单的服务形式和服务门店
     *
     * @param trades
     * @return
     */
    Integer updateAssignStoreAndPostStyle(Trades trades);

    /**
     * 更新订单的商户标记和商户评价
     *
     * @param trades
     * @return
     */
    Integer updateSellerFlagAndMemo(Trades trades);

    int selectTradesCount(Map<String, Object> map);

    List<Merchant> getMerchantInfo();

    int updateTradePayStyle(@Param("payStyle") String payStyle, @Param("tradesId") long tradesId);

    int updateRefuseRefundStatus(long tradesId, int status);

    Integer selectIsRefundByTradesId(long tradesId);

    Integer insertSelective(Trades trades);

    List<Trades> getDocetorServceTrades(Trades trades);

    List<Trades> selectBySystemDelivery(Map<String, Object> mapTrades);

    /**
     * @param siteId
     * @param buyerId
     * @return
     */
    List<Trades> selectByBuyerId(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    List<Trades> getTradesList(QueryOrdersReq queryOrdersReq);

    List<Map> selectTradesListfromERP(@Param("start_time") String sTime, @Param("endTime") String eTime, @Param("site_id") Integer site_id, @Param("status") Integer status);

    List<Map> selectTradesListfromERP2(@Param("start_time") String sTime, @Param("endTime") String eTime, @Param("site_id") Integer site_id, @Param("status") Integer status);

    Map<String, Object> selectTradesListfromERPtoJZ(@Param("site_id") Integer site_id, @Param("trades_id") Long tradesId);

    Map<String, Object> selectTradesListfromERP_new(@Param("site_id") Integer site_id, @Param("trades_id") Long tradesId);

    List<Trades> queryTradesEndList(Map<String, Object> mapTrades);

    Integer updateDistributorId(@Param("tradesId") Long tradesId, @Param("id") Integer id);

    Integer getPayMoney(@Param("buyer_id") int buyer_id);

    //查询是否是第一笔分销订单
    Map<String, Object> isFirstDistributorOrder(@Param("tradesId") String tradesId);


    List<Map<String, Object>> getOtherOrderListMap(@Param("siteId") String siteId, @Param("storeId") String storeId);

    List<Map<String, Object>> getOrderListMap(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeShippingClerkId") String storeShippingClerkId);

    Map<String, Object> getTradesInformationMap(@Param("tradesId") String tradesId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    Map<String, Object> getOrderDetailByDeliveryCodeMap(@Param("deliveryCode") String deliveryCode, @Param("siteId") String siteId, @Param("storeId") String storeId);

    List<Map<String, Object>> getBuyerTradesInformationMap(@Param("memberId") String memberId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    int updateFinancesNoByTradesId(@Param("financesNo") String financesNo, @Param("tradesId") String tradesId);

    /**
     * 获取一定天数内每天的订单总额
     *
     * @param siteId
     * @return
     */
    List<Map<String, Object>> selectTradesMoneyBydates(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("isPayment") Integer isPayment, @Param("postStyle") Integer post_style);

    //统计右侧商品支付金额
    List<Map<String, Object>> getPaymentMoney(@Param("siteId") Integer siteId, @Param("nowDay") String nowDay);

    List<Map<String, Object>> get_trades_list(Trades trades);

    String getFinanceNoByTradesId(String tradesId);

    void o2oToSend(Trades tradesId);

    /**
     * 获得一定天数内订单数量
     *
     * @param siteId
     * @param startTime
     * @param endTime
     * @param post_style
     * @return
     */
    List<Map<String, Object>> selectTradesCountBydates(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("isPayment") Integer isPayment, @Param("postStyle") Integer post_style);

    /**
     * 获得一定天数内客单价
     *
     * @param siteId
     * @param startTime
     * @param endTime
     * @param post_style
     * @return
     */
    List<Map<String, Object>> selectunitPriceByDates(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("postStyle") Integer post_style);

    /**
     * 获得一定天数内订单的支付人数，去重
     *
     * @param siteId
     * @param startTime
     * @param endTime
     * @param post_style
     * @return
     */
    List<Map<String, Object>> selectPayMemberNum(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("isPayment") Integer isPayment, @Param("postStyle") Integer post_style);

    /**
     * 获得一定天数内订单的促销员人数，去重
     *
     * @param siteId
     * @param startTime
     * @param endTime
     * @param post_style
     * @return
     */
    List<Map<String, Object>> selectStoreUserNum(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("postStyle") Integer post_style);

    List<Map<String, Object>> funnelPaymemberCount(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("isPayment") Integer isPayment, @Param("poststyles") List<Integer> postStyles);

    List<Map<String, Object>> funnelPayBefoCount(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("isPayment") Integer isPayment, @Param("poststyles") List<Integer> postStyles);

    List<Map<String, Object>> funnelUnitPay(@Param("siteId") Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("isPayment") Integer isPayment, @Param("poststyles") List<Integer> postStyles);

    //品类支付排行榜
    List<Map<String, Object>> getCategory(@Param("siteId") Integer siteId, @Param("nowDay") String nowDay);

    //门店支付排行榜
    List<Map<String, Object>> getStorePay(@Param("siteId") Integer siteId, @Param("sTime") String sTime, @Param("eTime") String eTime);

    List<Map<String, Object>> getAeraPayPackUp(@Param("siteId") Integer siteId, @Param("sTime") String sTime, @Param("eTime") String eTime);

    List<Map<String, Object>> getAeraPayDistribution(@Param("siteId") Integer siteId, @Param("sTime") String sTime, @Param("eTime") String eTime);


    Trades selectTradesByType();

    List<Trades> selectTradesByMigrate();

    Finances getFinancesRecalculate(String financesNo);

    List<Store> selectStoreInfoList(Map<String, Object> params);

    long getTradesListCount(QueryOrdersReq queryOrdersReq);

    int cancelStockup(@Param("siteId") String siteId, @Param("tradesId") String tradesId);

    Trades selectBarCode2(int siteId, String self_taken_code);

    int updateRealPay(@Param("siteId") Integer siteId, @Param("tradesId") Long tradeId, @Param("price") Integer price);

    int updateIsUpPrice(@Param("siteId") Integer siteId, @Param("tradesId") Long tradeId);

    void cancelShipping(Trades trades);

    //查询订单佣金比例
    Map<String, Object> selectCommissionRate(Integer siteId);

    //修改订单佣金
    int updateTradesSplit(@Param("purchase_rate") Integer purchase_rate, @Param("tradesId") Long tradeId);

    int updateTradesForFinances(Trades trades);

    //查询未付款 订单
    List<Map<String, Object>> selectTimeoutBySystemCanelNew(@Param("siteId") Integer siteId, @Param("systemCanel") Integer systemCanel);

    //查询未付款 订单
    Map<String, Object> getUserByTrade(@Param("tradesId") Long tradeId);

    int insertTradesAssignLog(@Param("site_id") Integer site_id, @Param("order_number") long order_number, @Param("before_store") Integer before_store
        , @Param("before_post_style") Integer before_post_style, @Param("store") Integer store, @Param("post_style") Integer post_style, @Param("user") String userName);

    String selectTradesIdbyPostStyle(@Param("siteId") Integer siteId, @Param("buyerId") String buyerId, @Param("postStyle") Integer postStyle);

    int insertSelfTakenLog(@Param("site_id") Integer site_id, @Param("store_id") Integer storeId, @Param("clerk_id") Integer clerkId, @Param("code") String code);

    int insertExceptionLog(Map<String, Object> params);

    String getTradesIdByPayNum(@Param("payNum") String payNum);

    List<Map<String, Object>> getGoodsInfoReport(@Param("siteId") Integer siteId, @Param("tradesIdArray") long[] tradesIdArray);

    int insertOrderPayLog(Map<String, Object> params);
    Integer selectOrderPayLog(Map<String, Object> params);

    List<Trades> getTradesListBySiteId(@Param("siteId")Integer siteId,@Param("startTime") String startTime,@Param("endTime") String endTime);

    Map<String,Object> getYonjinAndPost(@Param("tradesId")Long tradesId);

    Integer getAccountResult(@Param("tradesId")Long tradesId);
}
