package com.jk51.modules.merchant.mapper;


import com.jk51.model.treat.YbSettlementdayConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-17
 * 修改记录:
 */
@Mapper
public interface YbSettlementdayConfigMapper {

     List<YbSettlementdayConfig> findSettlementday(String siteId);

}
