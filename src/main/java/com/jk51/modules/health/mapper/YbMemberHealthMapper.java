package com.jk51.modules.health.mapper;

import com.jk51.model.health.YbMemberHealth;
import com.jk51.model.health.YbMemberHealthExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface YbMemberHealthMapper {
    long countByExample(YbMemberHealthExample example);

    int deleteByExample(YbMemberHealthExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(YbMemberHealth record);

    int insertSelective(YbMemberHealth record);

    List<YbMemberHealth> selectByExample(YbMemberHealthExample example);

    YbMemberHealth selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") YbMemberHealth record, @Param("example") YbMemberHealthExample example);

    int updateByExample(@Param("record") YbMemberHealth record, @Param("example") YbMemberHealthExample example);

    int updateByPrimaryKeySelective(YbMemberHealth record);

    int updateByPrimaryKey(YbMemberHealth record);
}
