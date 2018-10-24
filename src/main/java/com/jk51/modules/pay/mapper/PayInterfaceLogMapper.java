package com.jk51.modules.pay.mapper;

import com.jk51.model.pay.PayInterfaceLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Mapper
public interface PayInterfaceLogMapper {
    public void insert(PayInterfaceLog payInterfaceLog);

    public List<PayInterfaceLog> findByTradesId(@Param("tradesId") String tradesId, @Param("payStyle") String payStyle, @Param("payInterface") String payInterface, @Param("siteId") Integer siteId);
    public List<PayInterfaceLog> findByTradesIdALL(@Param("tradesId") String tradesId);
    List<PayInterfaceLog> findByTradesIdquery(@Param("tradesId") String tradesId);
    void updateByTradesId(@Param("id") Integer id);
}
