package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/11.
 */
@Mapper
public interface UserStoresDistanceMapper {
    //添加数据到日志表
    Integer insertUserStoresDistanceLog(Map<String, Object> map);

    //查询记录表是否有该会员信息
    Map<String,Object> booleanUser(Map<String, Object> params);

    //添加数据到记录表
    Integer insertUserStoresDistance(Map<String, Object> params);

    //修改记录表
    Integer updateUserStoresDistance(Map<String, Object> params);

    //查询商户下所有的门店
    List<Map<String,Object>> getAllStore(Map<String, Object> params);

    //根据区域ID查询区域名称及父级ID
    Map<String,Object> getArea(@Param("regoinId") Integer regoinId);

    //根据ID获取区域父级名称
    String getParentName(@Param("parentId") Integer parentId);

    //根据门店ID或者门店名称
    String getStoreNameByStoreId(Map<String, Object> params);

    //判断记录表中是否有该用户
    Integer getBooleanMember(Map<String, Object> params);

    //查询会员离门店的距离
    String getDistanceByMember(Map<String, Object> params);

    //查询会员离门店的信息
    Map getDistanceByMemberMap(Map<String, Object> params);
}
