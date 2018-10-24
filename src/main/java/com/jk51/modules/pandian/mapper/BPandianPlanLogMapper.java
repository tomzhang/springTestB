package com.jk51.modules.pandian.mapper;

import com.jk51.model.BPandianPlanLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BPandianPlanLogMapper {
    int insertSelective(BPandianPlanLog record);
}
