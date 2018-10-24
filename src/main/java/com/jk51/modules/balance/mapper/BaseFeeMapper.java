package com.jk51.modules.balance.mapper;

import com.jk51.model.balance.BaseFeeSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
@Mapper
public interface BaseFeeMapper {


    BaseFeeSet getBaseFee(@Param("siteId") Integer siteId,@Param("id") Integer id);

    List<BaseFeeSet> getBaseFeeLst(@Param("siteId") Integer siteId);

    int addBaseFee(BaseFeeSet record);

    int updBaseFee(BaseFeeSet record);

    Integer delBaseFee(@Param("siteId") Integer siteId,@Param("id") Integer id);
}
