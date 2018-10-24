package com.jk51.modules.appInterface.mapper;

import com.jk51.model.BMessageSender;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface BMessageSenderMapper {
    int insert(BMessageSender record);
    int insertSelective(BMessageSender record);
    int updateByPrimaryKeySelective(BMessageSender record);
    int updateByPrimaryKey(BMessageSender record);

    String getPushClientId(@Param("siteId") String siteId, @Param("storeAdminId") String storeAdminId);
    void updateClinetId(@Param("siteId") String siteId, @Param("storeAdminId") String storeAdminId, @Param("clientId") String clientId, @Param("deviceToken") String deviceToken);
    List<Map<String, Object>> getPushClientIdList(@Param("siteId") String siteId, @Param("storeId") String storeId);

    //app接受参数日志
    Integer insertPhoneLog(Map<String, Object> params);

    List<String> findAllClientId();
}
