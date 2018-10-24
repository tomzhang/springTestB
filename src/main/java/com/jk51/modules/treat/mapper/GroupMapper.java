package com.jk51.modules.treat.mapper;

import com.jk51.model.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);

    List<Group> selectBySiteId(@Param("siteId") Integer siteId);

    int insertList(@Param("groups") List<Group> groups, @Param("merchantId") Integer merchantId);
}
