package com.jk51.modules.storedelivery.mapper;

import com.jk51.model.storedelivery.StoreDelivery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-29
 * 修改记录:
 */
@Mapper
public interface StoreDeliveryMapper {

    List<StoreDelivery> getStoreDeliveryByParam(Map<String, Object> params);
}
