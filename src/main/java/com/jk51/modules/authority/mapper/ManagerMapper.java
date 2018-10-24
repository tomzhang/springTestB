package com.jk51.modules.authority.mapper;

import com.jk51.model.BManager;
import com.jk51.model.role.Manager;
import com.jk51.model.role.Role;
import com.jk51.model.role.RoleKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ManagerMapper {

    BManager findUserAndPasswordById(Integer siteId, String username, String password2);

    int deleteByPrimaryKey(Integer id, Integer site_id);

    int insertSelective(Manager record);

    Manager selectByPrimaryKey(@Param("id") Integer id,@Param("site_id") Integer site_id);
    String selectByNamePrimaryKey(@Param("id") Integer id,@Param("site_id") Integer site_id);

    int updateByPrimaryKeySelective(Manager record);

    Manager selectByName(String name);

}
