package com.jk51.modules.treat.mapper;

import com.jk51.model.BLogisticsOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-03-19
 * 修改记录:
 */

@Mapper
public interface LogisticsOrderMapper {
    List<BLogisticsOrder> getLogisticsOrder(Map<String, Object> map);
    Integer  insertLogistics (BLogisticsOrder bLogisticsOrder);
    List<String> getLogisticsName();

    List<BLogisticsOrder> getLogisticsOrderAccount(Map<String, Object> map);
}
