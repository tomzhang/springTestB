package com.jk51.modules.promotions.mapper;

import com.jk51.model.promotions.PromotionsDetail;
import com.jk51.model.promotions.detail.PromotionsDetailSqlParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@Mapper
public interface PromotionsDetailMapper {
    /* -- insert start -- */
    int insert(@Param(value = "promotionsDetail") PromotionsDetail promotionsDetail);
    /* -- insert end -- */

    /* -- update start -- */

    /* -- update end -- */

    /* -- select start -- */
    List<PromotionsDetail> findByParam(PromotionsDetailSqlParam param);

    List<PromotionsDetail> queryPromotionsDetailListByOrderId(String orderId);

    List<Map<String, Object>> queryPromotionsDetailListByRuleIdAndSiteId(@Param("siteId") int siteId, @Param("ruleId") int ruleId, @Param("params") Map<String, Object> params);

    List<Map<String, Object>> queryDetailAndRuleListByOrderId(@Param(value = "orderId") String orderId);

    PromotionsDetail getMAxPromotionsDetail(Integer promotionsId, Integer siteId);


    Integer getUseBuyedGoodsNum(@Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId,
                                @Param("userId") Integer userId, @Param("proActivityId") Integer promotionsActivityId, @Param("proRuleId") Integer promotionsruleId);


    int queryPromotionsDetailCount(@Param("siteId") int siteId, @Param("ruleId") int ruleId, @Param("params") Map<String, Object> params);

    List<Map<String, Object>> getPromotionsStatus(@Param("siteId") int siteId, @Param("ruleId") int ruleId, @Param("status") int status, @Param("params") Map<String, Object> params);

    Integer updateByCancel(@Param(value = "promotionsDetail") PromotionsDetail promotionsDetail);


    List<Map<String, Object>> queryGiftByOrderId(@Param(value = "orderId") String orderId, @Param(value = "siteId") Integer siteId);

    List<Map<String, Integer>> queryUseNumAndSendNumForActivity(@Param("siteId") Integer siteId, @Param("activityIds") List<Integer> idList);
    /* -- select end -- */
}
