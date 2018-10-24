package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/8/7
 * 修改记录:
 */
@Mapper
public interface LoginLogMapper {

   int recordLog(@Param("siteId")String siteId, @Param("mobile")String mobile, @Param("buyerId")String buyerId, @Param("inviteCode")String inviteCode, @Param("ip")String ip);

    int recordLoginLog2(Map<String, Object> params);

    String getTokenByMemberId(Map<String, Object> params);
}
