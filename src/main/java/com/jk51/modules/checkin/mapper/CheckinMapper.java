package com.jk51.modules.checkin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CheckinMapper {

    int queryToday(@Param("siteId")Integer siteId);

    int checkinTodayIs(@Param("siteId")Integer siteId, @Param("buyerId")Integer buyerId);

    int checkinAction(@Param("siteId")Integer siteId, @Param("buyerId")Integer buyerId);

}
