package com.jk51.modules.pandian.mapper;

import com.jk51.model.BPandianOrder;
import com.jk51.model.BPandianOrderExt;
import com.jk51.modules.pandian.Response.BPandianOrderList;
import com.jk51.modules.pandian.Response.OrderInfo;
import com.jk51.modules.pandian.Response.PandainPlanMap;
import com.jk51.modules.pandian.param.ClerkParam;
import com.jk51.modules.pandian.param.PandianOrderStatusParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BPandianOrderMapper {
    int insertSelective(BPandianOrder record);
    int updateByPrimaryKeySelective(BPandianOrder record);
    BPandianOrder getLatestPandianOrder(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("type") Integer type);

    List<BPandianOrderList> getBPandianOrderExtList(PandianOrderStatusParam param);

    BPandianOrder getNowMonthOrder(@Param("siteId") Integer siteId, @Param("planId") Integer planId);

    BPandianOrder getBPandianOrder(@Param("pandian_num")String pandian_num,@Param("siteId") Integer siteId);

    Map<String,Object> getPlanOrder(@Param("siteId") int siteId, @Param("id") int id);

    List<PandainPlanMap> getBPandianOrders(ClerkParam param);

    int updateIsUpSite(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("isUpSite") Integer isUpSite);

    Integer getUploadByStatus(@Param("siteId") Integer siteId, @Param("orderId") Integer orderId, @Param("isUpSite") Integer isUpSite, @Param("storeId") Integer storeId);

    OrderInfo getBPandianOrderByPandianNumAndStoreId(@Param("pandian_num")String pandian_num, @Param("storeId")Integer storeId);

    BPandianOrder selectByPrimaryKey(@Param("siteId") Integer siteId, @Param("id") Integer id);
}
