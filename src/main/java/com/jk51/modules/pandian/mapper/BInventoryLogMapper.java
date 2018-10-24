package com.jk51.modules.pandian.mapper;

import com.jk51.model.BInventoryLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: liang
 * 创建日期: 2018-06-07
 * 修改记录:
 */
@Mapper
public interface BInventoryLogMapper {

    int insert(BInventoryLog i);

    List<BInventoryLog> findLog(@Param("storeId")Integer storeId, @Param("pandianNum") String pandianNum, @Param("goodsCode") String goodsCode);

    int updateLog(BInventoryLog log);
}
