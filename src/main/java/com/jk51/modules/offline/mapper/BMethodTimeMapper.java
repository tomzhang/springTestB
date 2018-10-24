package com.jk51.modules.offline.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-09-08
 * 修改记录:
 */
@Mapper
public interface BMethodTimeMapper {

    int insertMethodStaticsTime(@Param("siteId") Integer siteId, @Param("methodName") String methodName, @Param("argsNames") String argsNames,
                                @Param("argsValues") String argsValues, @Param("returnValue") String return_value, @Param("staticsTime") Long staticsTime);
}
