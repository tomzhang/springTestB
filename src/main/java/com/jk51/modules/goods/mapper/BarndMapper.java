package com.jk51.modules.goods.mapper;

import com.jk51.model.Barnd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BarndMapper {

    int insert(Barnd record);

    int insertSelective(Barnd record);

    Barnd findByName(@Param("barndName") String barndName, @Param("siteId") int siteId);

    Barnd findById(@Param("barndId") int barndId, @Param("siteId") int siteId);
}