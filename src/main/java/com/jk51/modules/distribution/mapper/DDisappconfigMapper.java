package com.jk51.modules.distribution.mapper;

import com.jk51.model.distribute.DDisappconfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DDisappconfigMapper {
    /**
     * 根据siteId查询配置信息
     * @param siteId
     * @return
     */
    DDisappconfig findBySiteId(@Param("siteId") Integer siteId);
}