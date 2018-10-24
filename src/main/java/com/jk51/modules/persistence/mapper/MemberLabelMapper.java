package com.jk51.modules.persistence.mapper;

import com.jk51.model.merchant.MemberLabel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/9.
 */
@Mapper
public interface MemberLabelMapper {

    Integer insertLabel(MemberLabel memberLabel);
    List<MemberLabel> getLabelAll(@Param("siteId") Integer siteId,@Param("crowdSort") Integer crowdSort);
    List<MemberLabel> getLabelById(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("crowdSort") Integer crowdSort);
    List<MemberLabel> getLabelByName(@Param("siteId") Integer siteId,@Param("crowdName") String crowdName,@Param("crowdSort") Integer crowdSort);
    Integer updateLabel(MemberLabel memberLabel);
    Integer deleteLabel(@Param("siteId") Integer siteId,@Param("id") Integer id,@Param("crowdSort") Integer crowdSort);

    Integer getLabelCount(Map params);
    List<Integer> getLabelCountInsert(Map params);


    List<MemberLabel> getBooleanByName(@Param("siteId") Integer siteId, @Param("crowdSort") Integer crowdSort, @Param("crowdName") String crowdName);

    List<Map<String,Object>> getLabelReport (Map<String,Object> map);
    List<MemberLabel> getLabelAllForCouponActive(@Param("siteId") Integer siteId,@Param("lablelist") String[] lablelist);

    Integer deleteLabelByName(Map<String, Object> customLabel);

    Map<String,Object> getMemberIDs(Map<String, Object> customLabel);

    Integer updateCrowdCount(Map<String, Object> customLabel);

    List<Map<String,Object>> getIdAndName(Map<String, Object> customLabel);

    Integer updateCrowdLabelGroup(@Param("siteId") Integer siteId,@Param("id") Integer id,@Param("labelgroup") String labelgroup);

    List<String> getCrowdNameAll(@Param("siteId") Integer siteId);

    String getCrowdOpenIdAll(@Param("siteId") Integer siteId, @Param("labelName") String labelName);

    List<Map<String,Object>> getOpenIdByMemberId(@Param("siteId") Integer siteId, @Param("idsList") List<String> idsList);

    List<String> queryMemberInfoById(@Param("siteId")Integer siteId, @Param("memberIds")String[] memberIds);
}
