package com.jk51.modules.offline.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-01-04
 * 修改记录:
 */
@Mapper
public interface SMSLogMapper {

    int insertLog(@Param("site_id") Integer siteId, @Param("ext_id") Integer extId, @Param("store_id") Integer storeId,
                  @Param("type") Integer type, @Param("send_msg") String sendMsg, @Param("return_msg") String return_msg);
}
