package com.jk51.modules.pandian.mapper;

import com.jk51.model.BPandianPlanExecutor;
import com.jk51.modules.pandian.Response.JoinInventoryUserResp;
import com.jk51.modules.pandian.Response.StoresResponse;
import com.jk51.modules.pandian.param.PlandJoinUsersParam;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.param.StoresParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface BPandianPlanExecutorMapper {
    int insertSelective(BPandianPlanExecutor record);
    int updateByPrimaryKeySelective(BPandianPlanExecutor record);
    int insertByList(@Param("planExecutors") List<BPandianPlanExecutor> pandianPlanExecutors, @Param("planId") Integer planId);
    List<Map<String, Object>> getPandianExecutorList(@Param("siteId") Integer siteId, @Param("planId") Integer planId, @Param("storeId") Integer storeId);
    List<String> getClerkNameList(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("storeId") Integer storeId);
    String getClerkName(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("id") Integer id);
    String getMerchantName(@Param("siteId") Integer siteId, @Param("id") Integer id);
    String getStoreName(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId);
    List<StoresResponse> getStores(StoresParam param);
    List<Map<String,Object>> getStoreNumList(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("planId") Integer planId);
    List<String> getGoodsString(@Param("siteId") Integer siteId, @Param("storeNumSet") Set<String> storeNumSet);
    List<Integer> getExecStoreIdList(@Param("siteId") Integer siteId, @Param("planId") Integer planId);
    Long getPlanCheckNum(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId,@Param("planId") Integer planId);

    BPandianPlanExecutor findBPandianPlanExecutor(@Param("siteId")Integer siteId,@Param("storeId") Integer storeId,@Param("planId")  Integer planId);
}
