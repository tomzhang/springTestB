package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/27.
 */
@Mapper
public interface CoordinateMapper {
    //添加坐标
    Integer insertCoordinate(Map<String, Object> params);

    //查询坐标
    List<Map<String,Object>> selectCoordinate(Map<String, Object> params);

    //修改坐标
    Integer updateCoordinate(Map<String, Object> params);

    //删除坐标
    Integer deleteCoordinate(Map<String, Object> params);

    //根据ID查询坐标
    Map<String,Object> selectCoordinateById(Map<String, Object> params);

    //分开查用户
    Map<String,Object> getUserIdsAll(Map<String, Object> params);

    //6公里内的会员总数
    Integer getMemberCountAll(Map<String, Object> params);

    //6公里内的门店总数
    Integer getStoreCountAll(Map<String, Object> params);

    //分开查门店
    Map<String,Object> getStoreIdsAll(Map<String, Object> params);
}
