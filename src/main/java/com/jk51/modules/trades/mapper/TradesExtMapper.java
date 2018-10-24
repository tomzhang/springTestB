package com.jk51.modules.trades.mapper;

import com.jk51.model.order.TradesExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  交易拓展
 * 作者: hulan
 * 创建日期: 2017-02-20
 * 修改记录:
 */
@Mapper
public interface TradesExtMapper {
    public TradesExt getByTradesId(long tradesId);

    int udpateIntegralAward(long tradesId, int integralAward);

    int updateTradesExt(TradesExt param);

    List<Map<String,Object>> selectTradesExtInfoList(Map<String, Object> params);

    String queryDisOrder(long tradesId);

    int updateDeliveryInfo(@Param("feeMt") int feeMt, @Param("feeEle")int feeEle, @Param("deliveryOrder")String deliveryOrder, @Param("tradesId")long tradesId);
}
