package com.jk51.modules.appInterface.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Mapper
public interface ChAccountLogMapper {

    Long countTotalIncome(@Param("userId") Integer userId, @Param("month") String month);
}
