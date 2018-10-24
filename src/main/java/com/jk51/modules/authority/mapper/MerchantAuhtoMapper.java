package com.jk51.modules.authority.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/4.
 */
@Mapper
public interface MerchantAuhtoMapper {

    List<Map<String,Object>> selectMerchantList(Map<String,Object> map);

    Integer insertAuthoLog(Map<String,Object> map);

    String queryAuthoLog(@Param("siteId") Integer siteId);

    Map<String,Object> getAuthoLog(@Param("pwd")String pwd);

    List<Map<String,Object>> getStoresListBySiteId(@Param("siteId") Integer siteId,@Param("storeName") String storeName);
}
