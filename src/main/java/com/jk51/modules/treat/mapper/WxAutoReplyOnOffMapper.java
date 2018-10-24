package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.WxAutoReplyOnOff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WxAutoReplyOnOffMapper {

    int creReplyOnOff(WxAutoReplyOnOff record);

    int updReplyOnOff(@Param("onOff") Integer onOff,@Param("siteId") Integer siteId);

    WxAutoReplyOnOff getWxReplyOnOffBySiteId(@Param("siteId") Integer siteId);

}
