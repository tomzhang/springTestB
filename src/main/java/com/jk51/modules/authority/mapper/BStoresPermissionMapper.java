package com.jk51.modules.authority.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-10-20
 * 修改记录:
 */
@Mapper
public interface BStoresPermissionMapper {

    int insertPermission(@Param("siteId") Integer site_id, @Param("storeIds") String store_ids, @Param("PermissionIds") String permission_ids,
                         @Param("desc") String desc, @Param("status") Integer status, @Param("is_del") Integer is_del);

    Map<String, Object> selectPermission(@Param("siteId") Integer site_id, @Param("desc") String desc, @Param("status") Integer status);

    int updateStorePermission(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("storeIds") String storesIds,
                              @Param("permissionIds") String permissionIds, @Param("desc") String desc, @Param("status") Integer status);

    /**
     * 修改记录的状态，包括状态和软删除
     *
     * @param id
     * @param site_id
     * @param status
     * @param is_del
     * @return
     */
    int updateStatusByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer site_id, @Param("status") Integer status,
                                 @Param("isDel") Integer is_del);
}
