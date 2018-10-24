package com.jk51.modules.offline.mapper;

import com.jk51.commons.sms.SmsTemplate;
import org.apache.ibatis.annotations.Param;


public interface SMSTplMapper {
    SmsTemplate selectSmsTplById(@Param("id") int id);
}
