package com.jk51.modules.health.mapper;

import com.jk51.model.health.YbMemberHealthFile;
import com.jk51.model.health.YbMemberHealthFileExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface YbMemberHealthFileMapper {
    long countByExample(YbMemberHealthFileExample example);

    int deleteByExample(YbMemberHealthFileExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(YbMemberHealthFile record);

    int insertSelective(YbMemberHealthFile record);

    List<YbMemberHealthFile> selectByExample(YbMemberHealthFileExample example);

    YbMemberHealthFile selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") YbMemberHealthFile record, @Param("example") YbMemberHealthFileExample example);

    int updateByExample(@Param("record") YbMemberHealthFile record, @Param("example") YbMemberHealthFileExample example);

    int updateByPrimaryKeySelective(YbMemberHealthFile record);

    int updateByPrimaryKey(YbMemberHealthFile record);

    List<Map<String, Object>> healthList (Map<String, Object> param);

    int updateMobileOrIdCardNum(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("mobile") String mobile, @Param("memberId") Integer memberId, @Param("idCardNum") String idCardNum);
}
