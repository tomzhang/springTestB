package com.jk51.modules.promotions.mapper;

import com.jk51.model.goods.PageData;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.activity.ProActivityBomb;
import com.jk51.model.promotions.activity.PromotionsActivitySqlParam;
import com.jk51.model.promotions.rule.PromotionsRuleSqlParam;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForProActivityParam;
import com.jk51.modules.promotions.job.StatusTask;
import com.jk51.modules.promotions.request.ProActivityDto;
import com.jk51.modules.promotions.request.ProActivityDtoForPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@Mapper
public interface PromotionsActivityMapper {
    /* -- insert start -- */
    int create (@Param("promotionsActivity") PromotionsActivity promotionsActivity);

    /* -- insert end -- */

    /* -- update start -- */
    int update (@Param("promotionsActivity") PromotionsActivity promotionsActivity);

    int updateStatusByIdAndSiteId (@Param("id") Integer id, @Param("siteId") Integer siteId, @Param("status") int status);

    /* -- update end -- */

    /* -- select start -- */
    List<PromotionsActivity> findByParamWithRuleIn(PromotionsActivitySqlParam param);

    List<Map<String, Object>> centerOfProActivityList (ProActivityDto proActivityDto);

    PromotionsActivity getPromotionsActivity (ProActivityDto proActivityDto);

    Map<String, Object> getPromotionsActivityMap (ProActivityDto proActivityDto);

    List<Map<String, Object>> getGroupPurchaseProActivity(GroupPurchaseForProActivityParam groupPurchaseForProActivityParam);

    Map<String, Object> getGroupPurchaseOneProActivity(GroupPurchaseForProActivityParam groupPurchaseForProActivityParam);

    PromotionsActivity getPromotionsActivityDetail (@Param("siteId") Integer siteId,
                                                    @Param("id") Integer id);

    PromotionsActivity getPromotionsAndPromotionsRule (@Param("siteId") Integer siteId,
                                                       @Param("id") Integer id);


    List<PageData> proActivityList (ProActivityDtoForPage proActivityDtoForPage);

    List<PageData> proActivityListNew (ProActivityDtoForPage proActivityDtoForPage);

    List<PageData> couponActivityList (ProActivityDtoForPage proActivityDtoForPage);

    List<PageData> promotionsActivityList (ProActivityDtoForPage proActivityDtoForPage);


    List<PageData> promotionsActivityList2 (ProActivityDtoForPage proActivityDtoForPage);

    List<PromotionsActivity> getPromotionsActivitiesByRuleIdAndSiteId (@Param("siteId") Integer siteId,
                                                                       @Param("ruleId") Integer ruleId);
    /*查询所有正常状态的活动*/
    List<PromotionsActivity> getPromotionsActivitiesByStatusAndSiteId (@Param("siteId") Integer siteId);

    List<PromotionsActivity> getPromotionsActivitiesByStatusTwoAndSiteId (@Param("siteId") Integer siteId);

    List<PromotionsActivity> getPromotionsActivitiesBySiteIdAndIds(@Param("siteId") Integer siteId,
                                                                   @Param("ids") List<Integer> promotionsActivityIds,
                                                                   @Param("isReleased") boolean isReleased);

    List<Map<String, Object>> getproActivityBomb (@Param("proActivityBomb") ProActivityBomb proActivityBomb);

    List<Map<String, Object>> findAllReleasePromotions (@Param("siteId") int siteId, @Param("fields") String[] fields);

    List<PromotionsActivity> findAllReleasePromotionsActivity(@Param("siteId") Integer siteId,@Param("activityId") Integer activityId);

    List<PromotionsActivity> findAllReleasePromotionsActivity2(@Param("siteId") Integer siteId);
    List<PromotionsActivity> findAllReleasePromotionsActivityForBuyer(@Param("siteId") Integer siteId,@Param("activityId") Integer activityId);

    List<PromotionsActivity> findAllReleasePromotionsActiviting(@Param("siteId") Integer siteId,@Param("activityId") Integer activityId);

    /*判断是否有主题活动*/
    int getForcePopupCounts (@Param("siteId") Integer siteId);

    List<PromotionsActivity> getThemaActivityList (Integer siteId);

    int countRuleReleaseNum (@Param("siteId") Integer siteId, @Param("ruleId") Integer ruleId);

    /**
     * 获取一些信息用于{@link StatusTask#groupPurchaseStatus()}
     * @return
     */
    List<Map<String, Object>> getSomeInfoForTask(@Param("promotionsActivityIds") Set<Integer> promotionsActivityIds);

    List<PromotionsActivity> findShowAd2App(@Param("siteId") Integer siteId);

    PromotionsActivity getPromotionsActivityByPromotionNo(@Param("siteId")Integer siteId,@Param("promotionsNo") String promotionsNo);

    List<PromotionsActivity> getPromotionsActivitiesWithRuleIn(@Param("siteId") Integer siteId);

    List<PromotionsActivity> getIndependentPromotionsActivityBySiteId(Integer siteId);
    /* -- select end -- */
}
