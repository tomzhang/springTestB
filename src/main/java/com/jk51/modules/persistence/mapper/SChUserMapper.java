package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SChUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SChUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SChUser record);

    int insertSelective(SChUser record);

    SChUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SChUser record);

    int updateByPrimaryKey(SChUser record);

    List<SChUser> getUserInfoByPhone(@Param("phone") String phone, @Param("userType") Integer userType, @Param("deletedAt") Date deletedAt);

    List<SChUser> findUserByPhone(String phone);

    SChUser selectByMobile(String mobile);


}
