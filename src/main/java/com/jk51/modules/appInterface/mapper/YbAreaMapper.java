package com.jk51.modules.appInterface.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: admin
 * 创建日期: 2017-03-15
 * 修改记录:
 */
@Mapper
public interface YbAreaMapper {
    Map queryAreaByAreaId(Integer cityId);

    List<Map<String, Object>> queryAreaByParentId(@Param("parentId") Integer parentId, @Param("type") Integer type);

    List<Map<String, Object>> queryAreaIdByName(@Param("type") Integer type, @Param("name") String name, @Param("parentId") Integer parentId);

    List<Map<String, Object>> getcitys();

    Integer getParentId(@Param("areaId") Integer area_id, @Param("type") Integer type);

    List<Map<String,String>> queryAreaByTree(@Param("areaIds") List<String> areaIds);

    List<Map> queryCommonArea(@Param("pid")String parentId);
}
