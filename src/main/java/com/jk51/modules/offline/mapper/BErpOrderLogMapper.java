package com.jk51.modules.offline.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-06-11
 * 修改记录:
 */
@Mapper
public interface BErpOrderLogMapper {

    int insertSelectErpLog(@Param("siteId") Integer siteId, @Param("url") String url, @Param("sendMsg") String sendMsg, @Param("returnMsg") String returnMsg);

    List<Map<String, Object>> selectErpOrdersByTradesId(@Param("siteId") Integer siteId, @Param("tradesId") Long tradesId);
}
