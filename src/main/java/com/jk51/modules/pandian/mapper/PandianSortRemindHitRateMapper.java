package com.jk51.modules.pandian.mapper;

import com.jk51.model.PandianSortRemindHitRate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
@Mapper
public interface PandianSortRemindHitRateMapper {

    int insert(PandianSortRemindHitRate pandianSortRemindHitRate);
}
