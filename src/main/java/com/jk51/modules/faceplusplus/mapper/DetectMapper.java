package com.jk51.modules.faceplusplus.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DetectMapper {

    int detectLog(@Param("siteId") String siteId, @Param("param") String param,@Param("result") String result,@Param("remark") String remark);

}
