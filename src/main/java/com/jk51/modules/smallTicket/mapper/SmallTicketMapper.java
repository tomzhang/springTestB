package com.jk51.modules.smallTicket.mapper;

import com.jk51.model.TradesInvoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Administrator on 2018/6/5.
 */
@Mapper
public interface SmallTicketMapper {

    //
    void insertOfTradesInvoice(TradesInvoice tradesInvoice);

    void updateBrandesInvoiceBySiteIdAndOpenIdOrMobile(@Param("siteId") Integer siteId, @Param("ticketIdsList")List<Integer> ticketIdsList);

    List<Map<String, Object>> getBradesInvoiceBySiteIdAndOpenIdOrMobile(@Param("siteId") Integer siteId, @Param("openId")String openId, @Param("mobile")String mobile);

    Map<String,Object> getTradesBySiteIdAndTradesIdOrMobile(@Param("siteId")Integer siteId, @Param("tradesId")String tradesId, @Param("mobile")String mobile);

    List<Map<String, Object>> getOrdersBySiteIdAndTradesId(@Param("siteId")Integer siteId, @Param("tradesId")String tradesId);

    Map<String,Object> getStoresBySiteIdAndTradesStore(@Param("siteId")Integer siteId, @Param("tradesStore")Integer tradesStore);

    String getYBMerchantBySiteId(@Param("siteId")Integer siteId);

    Integer getSmallTicketsCountBySiteIdAndOpenIdOrMobile(@Param("siteId")Integer siteId, @Param("openId")String openId, @Param("mobile")String mobile);
    //按手机号查询小票信息
    List<Map<String,Object>> getTradesInvoiceCondition(@Param("site_id") Integer site_id, @Param("mobile") String mobile);
    //根据moblie和openid查询小票数量
    Integer getSmallTicketsCountSiteIdAndOpenIdOrMobile(@Param("siteId")Integer siteId, @Param("mobile") String mobile, @Param("openId") String openId);

    List<String> getDateOfBradesInvoiceBySiteIdAndOpenIdOrMobile(@Param("siteId") Integer siteId, @Param("openId") String openId, @Param("mobile") String mobile);

    List<Map<String,Object>> getBradesInvoiceFreshBySiteIdAndDateAndOpenIdOrMobile(@Param("siteId") Integer siteId, @Param("date") String date, @Param("openId") String openId, @Param("mobile") String mobile);

    String getOpenIdOfMemberBySiteIdAndMobile(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    /*商品上架改造  接口*/
    Integer updateGoodsInfoBySiteIdAndCodeAndAppStatus(@Param("siteId") Integer siteId, @Param("goodsCode")String goodsCode, @Param("appGoodsStatus")Integer appGoodsStatus);


    Integer updateGoodsInfoBySiteIdAndCodeAndStatus(@Param("siteId") Integer siteId, @Param("goodsCode")String goodsCode, @Param("goodsStatus")Integer goodsStatus);

    List<HashMap> getGoodsInfoListBySiteIdAndCode(@Param("siteId") Integer siteId, @Param("goodsCodeList")List<String> goodsCodeList);
    Map<String,Object> getGoodsInfoBySiteIdAndCode(@Param("siteId") Integer siteId,  @Param("goodsCode") String goodsCode);

    Integer updateAppLowerGoodsBySiteIdAndCode(@Param("siteId")Integer siteId, @Param("appLowerCodeList")List<String> appLowerCodeList);

    Integer updateAppUpperGoodsBySiteIdAndCode(@Param("siteId")Integer siteId, @Param("appUpperCodeList")List<String> appUpperCodeList);

    Integer updateLowerGoodsBySiteIdAndCode(@Param("siteId")Integer siteId, @Param("lowerCodeList")List<String> lowerCodeList);

    Integer updateUpperGoodsBySiteIdAndCode(@Param("siteId")Integer siteId, @Param("upperCodeList")List<String> upperCodeList);

    String getByMerchantId(@Param("siteId")Integer siteId);

    List<Map<String,Object>> queryGoodsClassify(@Param("siteId") String siteId);
}
