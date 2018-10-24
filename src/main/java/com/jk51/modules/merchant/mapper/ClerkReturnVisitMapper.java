package com.jk51.modules.merchant.mapper;

import com.jk51.model.clerkvisit.BClerkVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/12.
 */
@Mapper
public interface ClerkReturnVisitMapper {

    List<Map<String,Object>> getVisitList(Map<String,Object> map);

    int changeVisitStatus(Map<String,Object> map);

    List<Map<String,Object>> getMerchantClerkList(Map<String,Object> map);

    List<Map<String,Object>> getadminInfo(Map<String,Object> map);

    void addChangeClerkLog(Map<String,Object> map);

    int changeClerk(Map<String,Object> map);

    List<Map<String,Object>> getStoreList(@Param("siteId") Integer siteId);

    List<Map<String,Object>> queryGoodsInfoById(@Param("siteId") Integer siteId, @Param("goodsIds") String[] goodsIds,@Param("goodsIdsType") int goodsIdsType);

    List<Map<String,Object>> queryStoreInfo(@Param("siteId") Integer siteId,@Param("storeIds") String[] storeIds);

    Integer queryCoincideCustomer(@Param("siteId") Integer siteId, @Param("goodsIds") String[] goodsIds, @Param("days")Integer days, @Param("goodsType")Integer goodsType,@Param("memberIds")String[] memberIds);

    Integer queryCoincideCustomerNum(@Param("siteId") Integer siteId, @Param("goodsIds") String[] goodsIds, @Param("days")Integer days, @Param("memberIds")String[] memberIds);

    Integer queryMemberNums(@Param("siteId") Integer siteId);

    List<Map<String,Object>> getActivityList(Map<String,Object> map);

    Integer getAllGoodsNum(@Param("siteId") Integer siteId);

    int queryLeftGoodsNum(@Param("siteId")Integer siteId, @Param("goodsIds")String[] goodsIds);

    List<String> getActivityName(@Param("siteId")String siteId, @Param("activityIds")String[] activityIds);

    List<Map<String,Object>> getPIgoodsList(Map<String,Object> map);

    List<Map<String,Object>> getStoreClerkList(@Param("siteId") Integer siteId,@Param("storeCode") String storeCode,@Param("clerkName") String clerkName);

    List<Map<String,Object>> queryMemberInfo(@Param("siteId") Integer siteId,@Param("memberIds") String[] memberIds);

    List<Map<String,Object>> queryAccordMember(@Param("siteId") Integer siteId, @Param("goodsIds") String[] goodsIds, @Param("days")Integer days, @Param("goodsType")Integer goodsType,@Param("memberIds")String[] memberIds);

    List<BClerkVisit> queryVisitLog(@Param("siteId") Integer siteId);

    List<String> queryGoodsId(@Param("siteId") Integer siteId,@Param("buyerId") Integer buyerId, @Param("split") String[] split, @Param("days")Integer days);

    int updataVisitLog(BClerkVisit bClerkVisit);

    int insertNewTask(BClerkVisit bClerkVisit);

    int insertActivityTask(Map<String, Object> bvstatistics);

    List<Map<String,Object>> getAllStores(@Param("siteId") Integer siteId);

    List<String> queryGoodsIdsByType(@Param("siteId") Integer siteId,@Param("gdsIds") String[] gdsIds,@Param("goodsType") Integer goodsType);

    List<Map<String,Object>> getActicityWithActivityId(@Param("siteId") String siteId,@Param("activityId")String activityId);

    int updataVisitDesc(BClerkVisit bClerkVisit);

    int insertNewLog(BClerkVisit bClerkVisit);

    int getCouponNum(@Param("siteId")String siteId,@Param("activityId") String activityId,
                     @Param("startTime") String startTime,@Param("endTime") String endTime);

    List<Map<String,Object>> getCouponList(@Param("siteId")String siteId,@Param("activityId") String activityId,
                     @Param("startTime") String startTime,@Param("endTime") String endTime);

    List<Map<String,Object>> getSumForActivity(Map<String, Object> parameterMap);

    List<Map<String,Object>> getSumForActivity2(Map<String, Object> parameterMap);
}
