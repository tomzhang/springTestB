package com.jk51.modules.persistence.mapper;

import com.jk51.model.BTrades;
import com.jk51.modules.appInterface.util.OfflineQRCodeParam;
import com.jk51.modules.im.service.iMRecode.response.StatMemberTrades;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface BTradesMapper {

    List<BTrades> getOtherOrderList(@Param("siteId") String siteId, @Param("storeId") String storeId);

    List<BTrades> getOrderList(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeShippingClerkId") String storeShippingClerkId);

    Map<String, Object> getTradesInformation(@Param("tradesId") String tradesId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    Map<String, Object> getOrderDetailByDeliveryCode(@Param("deliveryCode") String deliveryCode, @Param("siteId") String siteId, @Param("storeId") String storeId);

    List<BTrades> getBuyerTrades(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("memberId") String memberId);

    List<Map<String, Object>> getBuyerTradesInformation(@Param("memberId") String memberId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    BTrades getBTradesByTradesId(@Param("siteId") String siteId, @Param("tradesId") String tradesId);

    BTrades getBTradesByCode(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("selfTakenCode") String selfTakenCode);

    void updateByPrimaryKey(BTrades bTrades);

    Integer getSumRealPay(@Param("storeUserId") int storeUserId, @Param("time") String time, @Param("siteId") int siteId);

    Integer getCountOrderNum(@Param("storeId") int storeId, @Param("storeUserId") int storeUserId, @Param("time") String time, @Param("siteId") int siteId);

    Integer getCountOtherNumber(@Param("siteId") int siteId, @Param("storeId") int storeId, @Param("time") String time);

    Integer getCountPickNumber(@Param("storeId") int storeId, @Param("time") String time, @Param("siteId") int siteId);

    Integer getSaleNum(@Param("storeUserId") int storeUserId, @Param("time") String time, @Param("siteId") int siteId);

    List<BTrades> findBTradesList(@Param("storeUserId") int storeUserId, @Param("time") String time, @Param("siteId") int siteId);

    Integer getSaleAmount(@Param("storeUserId") int storeUserId, @Param("time") String time, @Param("siteId") int siteId);

    List<Date> getBTradesDate(@Param("storeUserId") int storeUserId, @Param("month") String month, @Param("siteId") int siteId);

    List<Map<String, Object>> getOtherOrderListMap(@Param("siteId") String siteId, @Param("storeId") String storeId);

    List<Map<String, Object>> getOrderListMap(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeShippingClerkId") String storeShippingClerkId);

    Map<String, Object> getTradesInformationMap(@Param("tradesId") String tradesId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    Map<String, Object> getOrderDetailByDeliveryCodeMap(@Param("deliveryCode") String deliveryCode, @Param("siteId") String siteId, @Param("storeId") String storeId);

    List<Map<String, Object>> getTradesOrdersInfo(@Param("tradesId") Long tradesId, @Param("siteId") int siteId);

    List<Map<String, Object>> getBuyerTradesInformationMap(@Param("memberId") String memberId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    Integer queryTradesByBuyId(int buyer_id);

    Integer queryCountByBuyId(@Param("buyer_id") Integer buyer_id, @Param("site_id") Integer site_id);

    int queryUserFirstOrder(@Param("siteId") String siteId, @Param("buyerId") String buyerId);

    int getStoreOrderAll(@Param("siteId") String siteId, @Param("storeId") Integer storeId, @Param("flag") String flag);

    Integer getWeChatTradesAll(@Param("siteId") Integer siteId, @Param("buyerId") String buyerId);

    Integer deployAble(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("adminId") Integer adminId);

    Integer todaySalesByStores(@Param("siteId") int siteId, @Param("storeId") int storeId);

    Integer todaySalesRealPayByStores(@Param("siteId") int siteId, @Param("storeId") int storeId);

    Integer monthSalesByStores(@Param("siteId") int siteId, @Param("storeId") int storeId);

    Integer monthSalesRealPayByStores(@Param("siteId") int siteId, @Param("storeId") int storeId);

    Integer salesByStores(@Param("siteId") int siteId, @Param("storeId") int storeId);

    Integer salesRealPayByStores(@Param("siteId") int siteId, @Param("storeId") int storeId);

    List<Map<String, Object>> everydayMonthSalesByStores(@Param("siteId") int siteId, @Param("storeId") int storeId, @Param("month") String month);

    List<BTrades> findBTradesListByDate(@Param("siteId") int siteId, @Param("storeId") int storeId, @Param("date") String date);

    /**
     * 统计 门店/店员  当月/当日 销售订单量
     *
     * @param siteId
     * @param storeId
     * @param storeUserId          促销员
     * @param storeShippingClerkId 送货员
     * @param flag                 date：当日； month：当月；
     * @return
     */
    Integer todaySalesByStoresClerk(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("storeShippingClerkId") Integer storeShippingClerkId, @Param("flag") String flag);

    Integer todaySalesRealPayByStoresClerk(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("storeShippingClerkId") Integer storeShippingClerkId, @Param("flag") String flag);

    List<Map<String, Object>> everydayMonthSalesByStoresClerk(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("storeShippingClerkId") Integer storeShippingClerkId, @Param("month") String month);

    List<BTrades> findBTradesListByDateClerk(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("storeShippingClerkId") Integer storeShippingClerkId, @Param("date") String date);

    StatMemberTrades statMemberTrades(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);


    Integer todayTradesNumByIsPay(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeUserId") String storeUserId);

    Integer todayTradesRealPayByIsPay(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeUserId") String storeUserId);

    Integer getTradesNumByStoreIsPaying(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeUserId") String storeUserId);

    Integer getTradesNumByStoreWaitProcess(@Param("siteId") String siteId, @Param("storeId") String storeId);

    long myDeliveryOrderNum(Map<String, Object> params);

    List<Map<String, Object>> appselectTradespay(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("isPayment") Integer isPayment, @Param("start") String start, @Param("end") String end);

    List<Map<String, Object>> appselectTradenum(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("storeUserId") Integer storeUserId, @Param("isPayment") Integer isPayment, @Param("start") String start, @Param("end") String end);

    List<Map<String,Object>> selectConsumerList(Map<String, Object> parameterMap);

    int  getStoreAdminTradeNum(@Param("siteId")String siteId,@Param("activityId") String activityId);

    List<Map<String,Object>> getStoreAdminTrade(@Param("siteId")String siteId, @Param("activityId")String activityId);

    OfflineQRCodeParam getOfflineQRCodeParam(@Param("tradesId") String tradesId);
}
