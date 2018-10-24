package com.jk51.modules.im.mapper;

import com.jk51.model.ChOrderRemind;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChOrderRemindMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ChOrderRemind record);

    int insertSelective(ChOrderRemind record);

    ChOrderRemind selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ChOrderRemind record);

    int updateByPrimaryKey(ChOrderRemind record);

    List<ChOrderRemind> getChOrderRemindByOrderId(@Param("tradesId") Long tradesId);

    void updateByOrderId(@Param("tradesId")Long tradesId, @Param("postStyle")Integer postStyle, @Param("extraStr")String extraStr);


    Integer findNotReaded(@Param("siteId")Integer siteId, @Param("storeId")int storeId);

    //查询已读提醒的所有OrderId
    List<String> fingIsReadedOrderId(Integer pharmacistId);

    List<Map<String, Object>> getOrderRemindList(String pharmacistId);

    List<ChOrderRemind> getOrderReminds(String orderId);

    ChOrderRemind getOrderRemindById(String id);

    void setOrderRemindRead(@Param("orderId") String orderId, @Param("storeAdminId") String storeAdminId);

    List<Map<String,Object>> getOrderRemindListMap(@Param("siteId")String siteId, @Param("storeId")String storeId);

    ChOrderRemind getOrderRemindById2(String id);
    List<ChOrderRemind> getOrderReminds2(String orderId);

    List<Map<String,Object>> getOrderRemindListMap2(@Param("siteId")String siteId, @Param("storeId")String storeId);

    long getOrderRemindListCount2(@Param("siteId")String siteId, @Param("storeId")String storeId);
    List<Map<String,Object>> getOrderRemindList2(@Param("siteId")String siteId, @Param("storeId")String storeId, @Param("startRow")int startRow, @Param("pageSize")int pageSize);
}