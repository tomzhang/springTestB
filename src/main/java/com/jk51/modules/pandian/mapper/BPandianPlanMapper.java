package com.jk51.modules.pandian.mapper;

import com.jk51.model.BPandianPlan;
import com.jk51.modules.pandian.Response.PandianPlanDetail;
import com.jk51.modules.pandian.Response.PandianPlanInfo;
import com.jk51.modules.pandian.Response.PandianUploadStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BPandianPlanMapper {
    int insertSelective(BPandianPlan record);
    int updateByPrimaryKeySelective(BPandianPlan record);
    BPandianPlan selectByPrimaryKey(@Param("siteId") Integer siteId, @Param("id") Integer id);
    List<Map<String,Object>> getExistPlans(Map<String, Object> params);
    int stopOrDelPlan(BPandianPlan pandianPlan);
    List<BPandianPlan> getPlanListBySite(Integer siteId);
    List<BPandianPlan> getPlanListByStore(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId);
    List<BPandianPlan> getUnCreateOrderPlanList();
    String getStoreNum(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId);
    List<PandianPlanInfo> getPlanList(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("source") Integer source, @Param("id") Integer id, @Param("planType") Integer planType, @Param("startTime") String startTime, @Param("endTime") String endTime);
    List<Map<String,Object>> getPDStoreList(Map<String, Object> params);
    List<Map<String,Object>> getPDClerkList(Map<String, Object> params);
    BPandianPlan findByPandianNum(@Param("pandianNum")String pandianNum,@Param("siteId") Integer siteId);
    PandianPlanDetail getPlanDetail(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("id") Integer id);
    Integer planOrderNum(Integer id);

    PandianUploadStatus getStorePandianStatus(@Param("pdNum") String pdNum, @Param("siteId") Integer siteId, @Param("storeId") Integer storeId);
}
