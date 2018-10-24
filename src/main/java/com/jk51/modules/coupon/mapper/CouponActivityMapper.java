package com.jk51.modules.coupon.mapper;

import com.jk51.model.coupon.CouponActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Mapper
public interface CouponActivityMapper {

    Integer insert(CouponActivity couponActivity);

    int update(CouponActivity couponActivity);

    List<Map<String, Object>> findActivityByType(Map<String, Object> param);

    CouponActivity getCouponActivity(Integer siteId, Integer activityId);

    CouponActivity getCouponActivityInIssued(Integer siteId, Integer activityId);
    /**
     * 获取该商家下正在发布中的活动
     *
     * @param siteId 商家id
     * @return
     */

    List<CouponActivity> getCouponActivityList(Integer siteId);

    List<CouponActivity> getCouponActivityListBySendWay(Integer siteId);

    List<CouponActivity> findCouponActivityList(CouponActivity couponActivity);

    void updateCouponActivity(Date time, Integer siteId, Integer id);

    void updateCouponActivityStatus(Integer siteId, Integer id);

    Integer updateStatusByTime(Integer siteId, Integer id, Integer status);

    List<CouponActivity> selectStatus();

    List<CouponActivity> selectActivityStatus();

    Map<String, Object> findWxUrlBySiteId(Integer siteId);

    void updateStatus(Integer siteId, Integer id);

    List<CouponActivity> findActivityByCouponRule(Integer siteId, Integer ruleId);

    List<CouponActivity> findActivityFixedSend(@Param(value = "nowTime") Date nowTime);

    void updateCouponActivityByUse(CouponActivity couponActivity);

    Map<String, Object> selectActiveById(Integer activeId);

    List<CouponActivity> getAllCouponActivity();

    void updateSendStatus(CouponActivity couponActivity);

    int updateActiveForStopStatus(CouponActivity couponActivity);

    List<Map<String,Object>> getMembersList(Integer siteId,String[] mamberArray);

    List<CouponActivity> getCouponActivityListBySendWayUnAuth(Integer siteId);
}
