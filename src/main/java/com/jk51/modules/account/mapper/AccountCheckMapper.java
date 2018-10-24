package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.AccountCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/2/14
 * Update   :
 */
@Mapper
public interface AccountCheckMapper {

    /**
     * 根据订单号查询订单
     *
     * @param trades_id
     */
    Map<AccountCheck, Object> getTradesListByTradesId(@Param("trades_id") String trades_id);

}
