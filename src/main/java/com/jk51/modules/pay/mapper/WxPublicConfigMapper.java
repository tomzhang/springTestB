package com.jk51.modules.pay.mapper;

import com.jk51.model.pay.WxPublicConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-10
 * 修改记录:
 */
@Mapper
public interface WxPublicConfigMapper {

    public WxPublicConfig findConfigBySiteId(@Param("siteId") Integer siteId);
}
