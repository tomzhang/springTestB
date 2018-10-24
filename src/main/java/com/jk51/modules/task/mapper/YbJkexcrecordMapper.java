package com.jk51.modules.task.mapper;

import com.jk51.model.task.YbJkexcrecord;
import com.jk51.model.task.YbJkexcrecordExample;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface YbJkexcrecordMapper {
    long countByExample(YbJkexcrecordExample example);

    int deleteByExample(YbJkexcrecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(YbJkexcrecord record);

    int insertSelective(YbJkexcrecord record);

    List<YbJkexcrecord> selectByExample(YbJkexcrecordExample example);

    YbJkexcrecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") YbJkexcrecord record, @Param("example") YbJkexcrecordExample example);

    int updateByExample(@Param("record") YbJkexcrecord record, @Param("example") YbJkexcrecordExample example);

    int updateByPrimaryKeySelective(YbJkexcrecord record);

    int updateByPrimaryKey(YbJkexcrecord record);

    Integer surplusBeans(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId,@Param("storeAdminId") Integer storeAdminId);

    List<Map<String,Object>> selectByMap(HashMap queryMap);

    int changeStatusById(Integer id);

    Integer updateStatusById(Integer id);
}
