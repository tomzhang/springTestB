package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.requestParams.CouponDetailSqlParam;
import com.jk51.modules.coupon.request.OwnCouponParam;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chenpeng
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Mapper
public interface CouponDetailMapper {

    int insertCouponDetail(CouponDetail couponDetail);

    int insertCouponDetailBatch(List<CouponDetail> couponDetail);

    int insertCouponDetailBatchForReissure(List<CouponDetail> couponDetail);

    List<CouponDetail> findByParam(CouponDetailSqlParam param);
    List<CouponDetail> findByParamWithRuleIn(CouponDetailSqlParam param);

    List<Map<String, Object>> findCouponList(Map<String, Object> param);

    //单独查时间是相对时间的优惠券
    List<Map<String, Object>> findCouponListForRelativeTimeRule(Map<String, Object> param);

    List<Map<String, Object>> findUserCouponList(@Param("siteId") Integer siteId, @Param("userId") Integer userId);

    Integer getUseBuyGoodsNum(@Param("siteId") Integer siteId, @Param("userId") Integer userId,
                              @Param("ruleId") Integer ruleId);

    Integer getUseBuyedGoodsNum(@Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId,
                                @Param("userId") Integer userId, @Param("ruleId") Integer ruleId);

    int updateCouponDetail(@Param("siteId") Integer siteId, @Param("managerId") Integer managerId,
                           @Param("ruleId") Integer ruleId);

    CouponDetail getCouponDetail(Integer siteId, Integer managerId, Integer ruleId);

    List<CouponDetail> getCouponDetailByRuleId(Integer siteId, Integer ruleId, Integer userId);

    void updateStatusById(Integer siteId, Integer couponDetailId, String orderId);

    int updateDiscountAmountById(@Param("siteId") Integer siteId,
                                 @Param("id") Integer id,
                                 @Param("discountAmount") Integer discountAmount);

    Integer updateStatusByCouponNo(@Param("siteId") Integer siteId,
                                   @Param("list") String[] couponNos,
                                   @Param("status") Integer status);


    Integer findSendMemberAmount(@Param("siteId") Integer siteId,
                                 @Param("activityId") String activityId,
                                 @Param("ruleId") Integer ruleId);

    Integer findSendAmount(@Param("siteId") Integer siteId,
                           @Param("activityId") Integer activityId,
                           @Param("ruleId") Integer ruleId);

    Integer findUsedMemberAmount(@Param("siteId") Integer siteId,
                                 @Param("activityId") String activityId,
                                 @Param("ruleId") Integer ruleId);

    Integer findUsedAmount(@Param("siteId") Integer siteId,
                           @Param("activityId") Integer activityId,
                           @Param("ruleId") Integer ruleId);

    Integer findOwnCouponCount(@Param("siteId") Integer siteId,
                               @Param("activityId") Integer activityId,
                               @Param("ruleId") Integer ruleId, @Param("userId") Integer userId);


    CouponDetail getMAxCouponDetail(Integer ruleId, Integer siteId);

    List<Map<String, Object>> findActiveCount(@Param("siteId") Integer siteId, @Param("activityId") Integer activityId);

    Map<String, Object> findCouponByOrderId(String orderId);

    List<Map<String, Object>> findCouponBySendOrderId(String sendOrderId);

    void updateCouponToReturn(String orderId);

    CouponDetail findCouponDetailByOrderId(String orderId);

    List<CouponDetail> findCouponDetailBySendOrderId(String sendOrderId);

    List<Integer> findCouponDetailStatusBySiteIdAndActivityId(@Param("siteId") Integer siteId,
                                                              @Param("activityId") Integer activityId);

    @MapKey("source")
    Map<String, Map<String, Object>> findCouponDetailStatusBySiteIdAndActivityIds(@Param("siteId") Integer siteId,
                                                                                  @Param("activityIds") List<Integer> activityIds);


    void updateCouponToRecovery(String orderId);

    Map<String, Object> findWxCouponDetailById(Integer siteId, Integer id);

    /**
     * 查找该用户已经接收的优惠券
     *
     * @param siteId
     * @param userId
     * @param activeId
     * @return
     */
    List<CouponDetail> findIsReceive(Integer siteId, Integer userId, Integer activeId);

    Integer findIsReceiveCount(Integer siteId, Integer userId, Integer activeId);

    int updateStatusToOffLine(Integer siteId, Integer id, Integer userId);

    CouponDetail getCouponDetailByUserId(Integer siteId, Integer id);

    CouponDetail getCouponDetailByCouponId(Integer siteId, Integer id);

    int updateUseStatus(@Param(value = "siteId") Integer siteId, @Param(value = "id") Integer id);

    CouponDetail getByRuleIdAndActivityId(@Param("userId") Integer userId, @Param("siteId") Integer siteId,
                                          @Param("ruleId") Integer ruleId, @Param("activityId") Integer activityId);

    List<CouponDetail> getRuleBySiteId(@Param("siteId") Integer siteId, @Param("activityId") String activityId,
                                       @Param("begin") Integer begin);

    /**
     * 统计用户参加过几次该活动
     *
     * @param memberId
     * @param siteId
     * @param activityId
     * @return
     */
    Long countActivityTimes(@Param("userId") Integer memberId, @Param("siteId") Integer siteId,
                            @Param("activityId") Integer activityId);

    /**
     * 查询用户在该活动下获取过该优惠券几张
     *
     * @param userId
     * @param ruleId     优惠券规则id
     * @param activityId 活动id
     * @param siteId
     * @return
     */
    Integer countCouponByUserRuleAndActivity(@Param("userId") String userId, @Param("ruleId") Integer ruleId,
                                             @Param("activityId") Integer activityId, @Param("siteId") Integer siteId);

    Long findConponDetailListForMemberAndActiveNum(@Param("memberId") Integer memberId, @Param("activityId") Integer activityId, @Param("siteId") Integer siteId);

    List<Map<String, Object>> centerOfOwnCoupon(@Param("ownCouponParam") OwnCouponParam ownCouponParam);

    Map<String, Object> centerOfOwnCouponDetail(@Param("ownCouponParam") OwnCouponParam ownCouponParam);


    List<Map<String, Integer>> useAmountBySiteIdAndRuleIdForRuleList(@Param("siteId") int siteId, @Param("ruleIds") List<Integer> ruleIds);

    List<Map<String, Integer>> useAmountBySiteIdAndRuleIdForActivityList(@Param("siteId") int siteId, @Param("activityIds") List<Integer> activityIds);

    List<Map<String, Object>> findCouponDetailList(@Param("params") Map<String, Object> params);

    int findCouponDetailStatusBySiteIdAndRuleId(@Param("siteId") int siteId, @Param("ruleId") int ruleId, @Param("params") Map<String, Object> params, @Param("status") int status);

    CouponDetail findCouponDetailByCouponNo(@Param("couponNo") String couponNo, @Param("siteId") Integer siteId);

    List<Map<String, Object>> findCouponListByStoreAdmin(@Param("siteId") Integer siteId, @Param("activityId") Integer activityId, @Param("ruleId") Integer ruleId, @Param("storeId") Integer storeId, @Param("managerId") Integer managerId);

    Map<String, Object> getCouponToBuyer(Map<String,Object> param);

    List<Map<String,Object>> findUserCoupon(@Param("siteId") Integer siteId,@Param("userId") Integer userId);

    int findCouponDetailBySiteIdAndSendOrder(@Param("siteId")Integer siteId,@Param("orderId") String orderId,@Param("ruleId") int ruleId,@Param("source") String source);

    List<Map<String,Object>> combineCouponActivityDetail(@Param("siteId") Integer siteId,@Param("ruleIds") List<Integer> ruleIds,@Param("activeId") String activeId);
    List<Map<String,Object>> findGetCouponNum(@Param("siteId") Integer siteId,@Param("activityIds") List<String> activityIds,@Param("userId") Integer userId);
}
