package com.jk51.modules.appInterface.mapper;

import com.jk51.model.BMessageSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BMessageSettingMapper {
    int insertSelective(BMessageSetting record);
    BMessageSetting getPushSetting(@Param("siteId") String siteId, @Param("messageType") String messageType);
    String getDelaySeconds(@Param("siteId") String siteId, @Param("messageType") String messageType);
    Integer updateDelayTime(@Param("delayTime") String delayTime,@Param("siteId") String siteId, @Param("messageType") String messageType);
}
