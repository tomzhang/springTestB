package com.jk51.modules.marketing.mapper;

import com.jk51.model.BMarketingLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BMarketingLogMapper {
    int insertSelective(BMarketingLog record);
}
