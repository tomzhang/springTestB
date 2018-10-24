package com.jk51.modules.trades.mapper;

import com.jk51.model.order.Stockup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 自提编码
 * 作者: hulan
 * 创建日期: 2017-03-23
 * 修改记录:
 */
@Mapper
public interface StockupMapper {
    String getStockupId(@Param("siteId") int siteId, @Param("storeId") int storeId, @Param("createTime") Timestamp createTime);
    int insert(Stockup stockup);
    Stockup findByTradesId(@Param("tradesId") Long tradesId);
    int update(Stockup stockup);

    Stockup findByTradesId2(@Param("siteId")Integer siteId, @Param("tradesId")Long tradesId);

    List<Map<String,Object>> getTradesStockupInfoList(Map<String, Object> params);

    Stockup getTradesStockup(@Param("tradesId") String tradesId, @Param("siteId") String siteId, @Param("storeId") String storeId);

    void cancelStockup(@Param("siteId") String siteId, @Param("tradesId") String tradesId);
}
