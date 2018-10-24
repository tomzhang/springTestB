package com.jk51.modules.clerkvisit.mapper;

import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.clerkvisit.BClerkVisitExample;
import java.util.List;
import java.util.Map;

import com.jk51.model.order.Trades;
import org.apache.ibatis.annotations.Param;

public interface BClerkVisitMapper {
    long countByExample(BClerkVisitExample example);

    int deleteByExample(BClerkVisitExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BClerkVisit record);

    int insertSelective(BClerkVisit record);

    List<BClerkVisit> selectByExample(BClerkVisitExample example);

    //获取店员执行中的任务列表
    List<Map<String,Object>> getStoreAdminVistList(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId);

    //会员最近一次咨询的店员
    String getLatelyBimService(@Param("siteId") Integer siteId,@Param("buyerId") Integer buyerId);

    int getLatelyBClerkVisit(@Param("siteId") Integer siteId,@Param("buyerId") Integer buyerId);

    List<BClerkVisit> queryTodayWaitForVisit();

    BClerkVisit selectByPrimaryKey(Integer id);

    List<BClerkVisit> queryByTrades(Trades trades);

    //批量修改回访状态
    int updateVisitStatus(@Param("list") List<BClerkVisit> list,@Param("status") Integer status);

    int updateByExampleSelective(@Param("record") BClerkVisit record, @Param("example") BClerkVisitExample example);

    int updateByExample(@Param("record") BClerkVisit record, @Param("example") BClerkVisitExample example);

    int updateByPrimaryKeySelective(BClerkVisit record);

    int updateByPrimaryKey(BClerkVisit record);

    int insertFeedBack(Map<String,Object> param);

    Map<String,Object> queryFeedBack(Map<String,Object> param);

    int updateFeedBack(Map<String,Object> param);

    int updateReturnTaskStatus();

    Map<String,Object> queryVisitInfos(@Param("bvsId") Integer bvsId,@Param("siteId") Integer siteId);

    String taskGoodsIdsList(@Param("id") Integer id);
}
