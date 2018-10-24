package com.jk51.modules.appInterface.mapper;

import com.jk51.model.ChUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ChUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChUser record);

    int insertSelective(ChUser record);

    ChUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChUser record);

    int updateByPrimaryKey(ChUser record);

    List<ChUser> getUserInfoByPhone(@Param("phone") String phone, @Param("userType") Integer userType, @Param("deletedAt") Date deletedAt);

    List<ChUser> findUserByPhone(String phone);


}
