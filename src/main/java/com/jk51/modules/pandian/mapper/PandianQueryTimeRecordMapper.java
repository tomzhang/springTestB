package com.jk51.modules.pandian.mapper;

import com.jk51.modules.pandian.dto.PandianTimeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018-04-09
 * 修改记录:
 */
@Mapper
public interface PandianQueryTimeRecordMapper {

    int insertRecord(@Param("timeDto") PandianTimeDto timeDto);
}
