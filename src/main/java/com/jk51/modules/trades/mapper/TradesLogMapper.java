package com.jk51.modules.trades.mapper;

import com.jk51.model.order.TradesLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Mapper
public interface TradesLogMapper {
    public int addTradesLog(TradesLog tradesLog);
    // 超时未付款 系统取消
    public void  batchInsertToSystemCanel(Map<String,Object> map);

    //系统确认收货
    public void  batchInsertToEnd(Map<String,Object> map);

    public void batchInsertToSystemDelivery(Map<String,Object> map);


    TradesLog selectbTradeslogs(@Param("tradesId") Long tradesId);
}
