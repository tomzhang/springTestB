package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/27.
 */
@Mapper
public interface FlowAnalysisMapper {


    int insertRemain(Map<String, Object> params);
}
