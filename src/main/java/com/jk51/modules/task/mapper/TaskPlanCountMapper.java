package com.jk51.modules.task.mapper;

import com.jk51.model.order.BMember;
import com.jk51.modules.task.domain.StoreAndAdminCombId;
import com.jk51.modules.task.domain.TradesSale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskPlanCountMapper {
    List<Integer> selectAdminIdByStoreList(@Param("siteId") Integer siteId, @Param("storeIds") List<Integer> storeIds);

    List<Integer> selectStoreIdByAdminList(@Param("siteId")Integer siteId, @Param("adminIds") List<Integer> adminIds);

    Integer selectStoreIdByAdminId(@Param("siteId") Integer siteId, @Param("adminId") Integer adminId);

    int countTradesMeta(@Param("filterCondition") String filterCondition,
                        @Param("rewardObject") Byte rewardObject,
                        @Param("joinId") Integer joinId,
                        @Param("siteId") Integer siteId,
                        @Param("goodsIds") List<Integer> goodsIds,
                        @Param("countType") Byte countType,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

    /**
     * 订单量统计
     * @param filterCondition
     * @param rewardObject
     * @param joinId
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @return
     */
    int countTradesNum(@Param("filterCondition") String filterCondition,
                  @Param("rewardObject") Byte rewardObject,
                  @Param("joinId") Integer joinId,
                  @Param("siteId") Integer siteId,
                  @Param("goodsIds") List<Integer> goodsIds,
                  @Param("startTime") LocalDateTime startTime,
                  @Param("endTime") LocalDateTime endTime);

    int countRegisterMemberNum(@Param("filterCondition") String filterCondition,
                               @Param("rewardObject") Byte rewardObject,
                               @Param("joinId") Integer joinId,
                               @Param("siteId") Integer siteId,
                               @Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime);

    int countGoodsSaleNum(@Param("filterCondition") String filterCondition,
                          @Param("rewardObject") Byte rewardObject,
                          @Param("joinId") Integer joinId,
                          @Param("siteId") Integer siteId,
                          @Param("goodsIds") List<Integer> goodsIds,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    List<BMember> selectBmemberBySiteIdAndCreateTime(@Param("siteId") Integer siteId,
                                                     @Param("filterCondition") String filterCondition,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    List<TradesSale> selectBTradesBySiteIdAndCreateTime(@Param("siteId") Integer siteId,
                                                     @Param("filterCondition") String filterCondition,
                                                     @Param("goodsIds") List<Integer> goodsIds,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    List<StoreAndAdminCombId> selectAdminId(@Param("siteId") Integer siteId);

    List<TradesSale> selectBOrdersBySiteIdAndCreateTime(@Param("siteId") Integer siteId,
                                                        @Param("filterCondition") String filterCondition,
                                                        @Param("goodsIds") List<Integer> goodsIds,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
}
