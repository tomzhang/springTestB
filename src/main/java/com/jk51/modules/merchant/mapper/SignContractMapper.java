package com.jk51.modules.merchant.mapper;

import com.jk51.model.merchant.SignContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignContractMapper {
    SignContract getById(@Param("id") Integer id);
}
