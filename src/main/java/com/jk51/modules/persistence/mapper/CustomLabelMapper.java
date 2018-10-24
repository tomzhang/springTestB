package com.jk51.modules.persistence.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/28.
 */
@Mapper
public interface CustomLabelMapper {
    Integer insertCustomLabel(Map<String,Object> customLabel);

    Map<String,Object> getClickName(@Param("siteId") Integer siteId, @Param("username") String username);

    List<Map<String,Object>> selectCustomAll(Map<String, Object> customLabel);

    Integer updateCustom(Map<String, Object> customLabel);

    Integer deleteCustom(Map<String, Object> customLabel);

    Map<String,Object> selectCrowdSortmById(Map<String, Object> customLabel);

    List<Map<String,Object>> booleanCustom(Map<String, Object> customLabel);

    List<Map<String,Object>> selectMemberByMemberId(@Param("siteId") Integer siteId,@Param("list") List<Integer> list);

    List<Map<String,Object>> getDimByCustom(Map<String, Object> customLabel);

    String getMemberID(Map<String, Object> customLabel);

    List<Map<String,Object>> getCrowdAll(@Param("siteId") Integer siteId);

    Map<String,Object> getBasicsMap(Map<String, Object> customLabel);

    Map<String,Object> getTrandsCount(Map<String, Object> customLabel);

    Map<String,Object> getTime(Map<String, Object> customLabel);

    List<Map<String,Object>> getCustomLabelAll(@Param("siteId") Integer siteId);

    Integer updateCustomByName(@Param("siteId") Integer siteId,@Param("name") String name,@Param("ids") String ids);

    String getIDSByName(@Param("siteId") Integer siteId,@Param("name") String name);

    Map<String,Object> getIDSAndSortByName(@Param("siteId") Integer siteId,@Param("customName") String customName);

    Integer updateCustomSort(@Param("siteId") Integer siteId,@Param("customName") String customName,@Param("crowdname") String crowdname);

    String getLabels(@Param("siteId") Integer siteId,@Param("buyerId") Integer buyerId);

    Integer updateLabels(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("nameList") String nameList);

    Integer updateTagLabels(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("nameList") String nameList);

    String getIDSById(Map<String, Object> customLabel);

    String getLabelsByMemberId(@Param("siteId") Integer siteId,@Param("memberId") Integer memberId);

    Integer getBuyerIdByMember(@Param("siteId") Integer siteId,@Param("memberId") String memberId);

    List<Map<String,Object>> getAllMember(Map<String, Object> params);

    Map<String,Object> getLabelSlowAndCustom(Map<String, Object> customLabel);

    List<String> getCustomLabelBySiteId(Map<String, Object> customLabel);

    Integer updateSlowLabel(Map<String, Object> customLabel);

    String getFirstOrder(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    Integer updateFirstOrderToMember(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("date") String date);

    List<Integer> getBuyerIdBySiteId(@Param("siteId") Integer siteId);

    String selectOrderFirst(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    Map<String,Object> selectCustomById(Map<String, Object> mapCustom);

    String getNameById(@Param("siteId") Integer siteId,@Param("id")  Integer id);

    String getCustomIds(@Param("siteId") Integer siteId, @Param("name") String name);

    Integer updataLabelsByMemberId(@Param("siteId") Integer siteId,@Param("buyerId") Integer buyerId,@Param("labelsName") String labelsName);

    Map<String,String> getLabelsByBuyerIdAndSiteId(@Param("siteId")int siteId, @Param("buyerId")int buyerId);

    String getSlowLabel(Map<String, Object> customLabel);
}
