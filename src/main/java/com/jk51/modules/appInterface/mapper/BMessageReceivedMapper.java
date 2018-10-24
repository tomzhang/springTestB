package com.jk51.modules.appInterface.mapper;

import com.jk51.model.BMessageReceived;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BMessageReceivedMapper {
    int insert(BMessageReceived record);
    int insertSelective(BMessageReceived record);
    int updateByPrimaryKeySelective(BMessageReceived record);
    int updateByPrimaryKey(BMessageReceived record);

    List<Map<String,Object>> getLastMessages(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeAdminId") String storeAdminId);
    List<Map<String, Object>> getNoReadMessageNum(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeAdminId") String storeAdminId);
    List<Map<String,Object>> getMessageList(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeAdminId") String storeAdminId, @Param("messageType") String messageType);
    int updateReadMessage(@Param("siteId") String siteId, @Param("id") String id);
    int deleteByPrimaryKey(@Param("siteId") String siteId, @Param("id") String id);
    void insertByList(List<BMessageReceived> messageReceivedList);
    int readMessageAll(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeAdminId") String storeAdminId, @Param("messageType") String messageType);
    void delMessageList(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeAdminId") String storeAdminId, @Param("messageType") String messageType);
}
