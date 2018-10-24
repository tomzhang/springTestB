package com.jk51.modules.appInterface.mapper;


import com.jk51.model.BOrders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BOrdersMapper {

    List<BOrders> getTradesOrdersList(@Param("tradesId") String tradesId, @Param("siteId") String siteId);


    List<Map<String,Object>> getOrderInfo(@Param("tradesId") Long tradesId, @Param("siteId") int siteId);

    List<Map<String,Object>> getTradesOrdersListMap(@Param("tradesId") String tradesId, @Param("siteId") String siteId);
}
