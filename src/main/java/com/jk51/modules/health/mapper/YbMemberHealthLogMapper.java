package com.jk51.modules.health.mapper;

import com.jk51.model.health.YbMemberHealthLog;
import com.jk51.model.health.YbMemberHealthLogExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface YbMemberHealthLogMapper {
    long countByExample(YbMemberHealthLogExample example);

    int deleteByExample(YbMemberHealthLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(YbMemberHealthLog record);

    int insertSelective(YbMemberHealthLog record);

    List<YbMemberHealthLog> selectByExampleWithBLOBs(YbMemberHealthLogExample example);

    List<YbMemberHealthLog> selectByExample(YbMemberHealthLogExample example);

    YbMemberHealthLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") YbMemberHealthLog record, @Param("example") YbMemberHealthLogExample example);

    int updateByExampleWithBLOBs(@Param("record") YbMemberHealthLog record, @Param("example") YbMemberHealthLogExample example);

    int updateByExample(@Param("record") YbMemberHealthLog record, @Param("example") YbMemberHealthLogExample example);

    int updateByPrimaryKeySelective(YbMemberHealthLog record);

    int updateByPrimaryKeyWithBLOBs(YbMemberHealthLog record);

    int updateByPrimaryKey(YbMemberHealthLog record);
}
