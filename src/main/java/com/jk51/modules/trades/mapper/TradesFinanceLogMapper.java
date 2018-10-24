package com.jk51.modules.trades.mapper;

import com.jk51.model.order.TradesFinanceLog;
import org.apache.ibatis.annotations.Param;

public interface TradesFinanceLogMapper {
    int deleteByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    int insert(TradesFinanceLog record);

    int insertSelective(TradesFinanceLog record);

    TradesFinanceLog selectByPrimaryKey(@Param("id") Integer id, @Param("siteId") Integer siteId);

    int updateByPrimaryKeySelective(TradesFinanceLog record);

    int updateByPrimaryKey(TradesFinanceLog record);
}
