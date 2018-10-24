package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.PayPlatform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * filename :com.jk51.modules.account.mapper.
 * author   :zw
 * date     :2017/3/16
 * Update   :
 */
@Mapper
public interface PayPlatformMapper {
  int addPayPlatform(@Param(value = "payPlatform") PayPlatform payPlatform);
  PayPlatform getPayPlatformById(@Param(value = "site_id") Integer site_id,@Param(value = "pay_type") String pay_type);
  int updatePayPlatform(@Param(value = "payPlatform") PayPlatform payPlatform);
}
