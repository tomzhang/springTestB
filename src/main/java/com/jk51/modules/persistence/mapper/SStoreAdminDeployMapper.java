package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SStoreAdminDeploy;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SStoreAdminDeployMapper {
    int deleteByPrimaryKey(Integer id, Integer site_id);

    int insert(SStoreAdminDeploy record);

    int insertSelective(SStoreAdminDeploy record);

    SStoreAdminDeploy selectByPrimaryKey(Integer id, Integer site_id);

    int updateByPrimaryKeySelective(SStoreAdminDeploy record);

    int updateByPrimaryKey(SStoreAdminDeploy record);

    List<SStoreAdminDeploy> selectByStore(@Param("site_id") Integer site_id, @Param("store_id") Integer store_id, @Param("clerkName") String clerkName, @Param("mobile") String mobile);

    List<Map<String,Object>> selectClerkVisitList(Map<String,Object> map);

//    List<Map<String,Object>> selectClerkListLog(Map<String,Object> map);

    int changeClerkStatus(Map<String,Object> map);
}
