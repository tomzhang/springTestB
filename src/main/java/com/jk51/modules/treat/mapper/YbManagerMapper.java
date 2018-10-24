package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.YBManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:51后台管理员
 * 作者: dumingliang
 * 创建日期: 2017-03-03
 * 修改记录:
 */
@Mapper
public interface YbManagerMapper {

    Integer addYBManager(YBManager ybManager);

    Integer delYBManager(@Param("id") Integer id);

    Integer updateYBManager(YBManager ybManager);

    List<YBManager> selectAll(@Param("username") String username, @Param("realname") String realname, @Param("isActive") Integer isActive);

    YBManager selectBySelective(@Param("id") Integer id);

    String getUserNamebyPrimaryKey(@Param("id") Integer id);

    String getRealNamebyPrimaryKey(@Param("id") Integer id);

    List<YBManager> selectByUserName(@Param("username") String name, @Param("pwd") String pwd);

    Integer updateLoginCount(@Param("username") String name, @Param("pwd") String pwd);


}
