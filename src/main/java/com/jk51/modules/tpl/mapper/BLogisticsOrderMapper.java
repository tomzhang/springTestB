package com.jk51.modules.tpl.mapper;

import com.jk51.model.BLogisticsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BLogisticsOrderMapper {

    public int insertSelective(BLogisticsOrder bLogisticsOrder);
    public BLogisticsOrder selectByTradesId(String orderNumber);
    public int updateByPrimaryKey(BLogisticsOrder bLogisticsOrder);
    public List<BLogisticsOrder> selectByTradesIdList(String orderNumber);
    BLogisticsOrder queryByOrderNumber(@Param("orderNumber")Long orderNumber);

    int insertLog(Map map);
    public List<Map> selectByTradesIdLog(String orderNumber);
    String queryTrade(String tradesId);

    List<Map<String,Object>> getTradesLogisticsList(Map<String, Object> params);

    int updateByWayBill(BLogisticsOrder item);

    int hideRecord(@Param("siteId")Integer siteId, @Param("orderNumber")Long orderNumber);
    int hideRecordLog(@Param("siteId")Integer siteId, @Param("orderNumber")Long orderNumber);

    int updateTradeso2o(@Param("tradesId")Long tradesId);

    int insertLogWithTime(Map map);

    List<Map> queryYesterdayBadLogisticOrder();
}
