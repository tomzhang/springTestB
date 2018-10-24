package com.jk51.modules.trades.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @Author: chenpengtao
 * @Description:
 * @Date: created in 2018/8/13
 * @Modified By:
 */
@Mapper
public interface DeliverypriceMapper {


    int getPrice(Map parm);


}
