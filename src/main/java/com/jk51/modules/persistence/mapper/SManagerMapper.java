package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-02-28
 * 修改记录:
 */
@Mapper
public interface SManagerMapper {

    int insertManager(SManager manager);

    int deleteByPrimaryKey(@Param("id") Integer id, @Param("site_id") Integer siteId);

    int updateManager(SManager manager);

    List<SManager> selectAll(@Param("siteId") Integer siteId, @Param("username") String username, @Param("realname") String realname, @Param("isActive") Integer isActive);

    SManager selectBySelective(@Param("id") Integer id, @Param("username") String username, @Param("site_id") Integer site_id);

    List<SManager> selectByUsername(@Param("id") Integer id, @Param("username") String username, @Param("site_id") Integer site_id);

    String getUserNamebyPrimaryKey(@Param("id") Integer id, @Param("site_id") Integer site_id);

    List<SManager> getUserName(@Param("siteId") Integer siteId, @Param("username") String username, @Param("password") String password);

    int updatePassword(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("password") String password);

    Integer updateLoginCount(@Param("siteId") Integer siteId, @Param("username") String username, @Param("password") String password);

}
