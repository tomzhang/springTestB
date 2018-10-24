package com.jk51.modules.order.mapper;

import com.jk51.model.order.*;
import com.jk51.model.order.response.UpdateOrderPayStyleReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单
 * 作者: baixiongfei
 * 创建日期: 2017-02-24
 * 修改记录:
 */
@Mapper
public interface OrdersMapper {

    public BMember getMemberById(Integer siteId, String mobile);

    public BMemberInfo getMemberInfoById(@Param("siteId") Integer siteId, @Param("memberId") Integer memberId);

    public YBMember getYBMemberByMobile(@Param("mobile") String mobile);

    public int addYBMember(@Param("ybMember") YBMember ybMember);

    public int updateYBMember(@Param("ybMember") YBMember ybMember);

    public BMember getMemberByBuyerId(Integer siteId, Integer buyerId);

    public YBAccount getYBAccountById(Integer sellerId, Integer payPlatformId);

    public int addDirectOrderTrades(@Param("trades") Trades trades);

    public int addOrders(@Param("orders") Orders orders);

    public int updateBShopMember(BMember bMember);

    public int updateOrderMember(BMember bMember);

    public int addTradesExt(@Param("tradesExt") TradesExt tradesExt);

    public int addDistributorDiscount(Map<String,Object> param);

    public Map<String,Object> getDistributorDiscount(@Param("siteId") Integer siteId,@Param("tradesId") long tradesId);

    public List<Orders> getOrdersByTradesId(@Param("tradesId") long trades_id);

    public int updateOrdersStatus(@Param("list") List<Orders> orderss, @Param("ordersStatus") int orders_status);

    public int updateTradesExt(@Param("tradesId") String tradesId, @Param("cashPaymentPay") Integer cashPaymentPay, @Param("medicalInsuranceCardPay") Integer medicalInsuranceCardPay, @Param("lineBreaksPay") Integer lineBreaksPay, @Param("cashReceiptNote") String cashReceiptNote);

    public List<Store> getStoresBySiteId(@Param("siteId") Integer siteId, @Param("cityId") Integer cityId);

    public int getOrdersTotalPriceByTradesId(@Param("tradesId") long tradesId);

    public int updateOrderPayStyle(@Param("updateOrderPayStyleReq") UpdateOrderPayStyleReq req);

    public String getAreaId(@Param("areaId") String areaId);

    public String getParentId(@Param("areaId") String areaId);

    Integer addBMemberInfo(BMemberInfo bMemberInfo);

    Store getStore(@Param("storeId") int storeId, @Param("siteId") int siteId);

    List<Orders> getOrdersListByTradesId(@Param("siteId") String siteId, @Param("tradesId") Long tradesId);

    List<Map> selectOrderListFromERP(@Param("siteId") Integer siteId, @Param("tradesId") Long tradesId);

    boolean updateStatusByTradesId(@Param("tradesId") @NotNull Long tradesId, @Param("tradesStatus") @NotNull Integer tradesStatus);

    List<Orders> getOrdersInfoList(Map<String, Object> params);

    void cancelStockup(@Param("siteId") String siteId, @Param("tradesId") String tradesId);

    int memberUseIntegral(@Param("siteId")Integer siteId, @Param("memberId")Integer memberId, @Param("needIntegral") Integer needIntegral);

    List<Orders> getordersByCouponDetail(@Param("siteId") Integer siteId,@Param("userId") Integer userId, @Param("activityId") String activityId, @Param("ruleId") int ruleId, @Param("orderId") String orderId, @Param("goods") List<Integer> goods);

    int getGoodsNumByTime(Map<String,Object> param);

    //获取订单内商品以map结果返回,跟上面以订单对象区分开
    List<Map<String,Object>> getOrdersListByTradesIdAndSiteId(@Param("siteId")String site_id,@Param("tradesId")Long trades_id);


    List<Map<String,Object>> getOrdersListByActivity(Map<String,Object> param);

    Map<String,Object> queryshoppingNum(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    Map<String,Object> queryshoppingLog(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("goodsId") Integer goodsId);

    Map<String,Object> queryDealAnalyze(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    long queryIntervalTime(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    List<Map<String,Object>> queryReVisitLog(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    List<Orders> queryOrdersUsePromotions(@Param("siteId") Integer siteId, @Param("ruleId") Integer ruleId,
                                          @Param("activityId") Integer activityId,@Param("memberId")Integer memberId,@Param("goodsId")Integer goodsId);

    List<Integer> findHistoryGoods(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId);

    int addpaInfo(Map<String,Object> param);

    Map<String,Object> findPingAnOrderId(@Param("siteId")Integer siteId,@Param("tradesId") long tradesId);
}
