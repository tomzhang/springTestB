package com.jk51.modules.es.mapper;


import com.jk51.model.BSearchLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BSearchLogMapper {


    int insertSelective(BSearchLog record);

    List<BSearchLog> queryList(@Param("siteId") String siteId,@Param("mobile") String buyerId);

    int batchUpdate(@Param("siteId") String siteId,@Param("mobile") String buyerId);

    Map<String,Object> queryLastRecord(@Param("siteId") Integer siteId,@Param("mobile") String phoneNum);
}
