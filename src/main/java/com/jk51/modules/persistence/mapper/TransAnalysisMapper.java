package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/3.
 */
@Mapper
public interface TransAnalysisMapper {
    int insertTrans(Map<String, Object> params);
}
