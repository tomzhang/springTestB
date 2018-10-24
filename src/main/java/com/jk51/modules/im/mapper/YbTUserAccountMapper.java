package com.jk51.modules.im.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@Mapper
public interface YbTUserAccountMapper {

    List<String> findUserIdByStoreAdminId(@Param("storeadmin_id") int storeadmin_id, @Param("siteId") String siteId);
}
