package com.jk51.modules.trades.mapper;

import com.jk51.model.order.TradesUpdatePriceLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7.
 */
@Mapper
public interface TradesUpdatePriceLogMapper {

    int insertTradesUpdatePriceLog(TradesUpdatePriceLog log);

    List<TradesUpdatePriceLog> selectTradesUpProceLog(@Param("siteId") Integer siteId,@Param("tradeId") Long tradeId);
}
