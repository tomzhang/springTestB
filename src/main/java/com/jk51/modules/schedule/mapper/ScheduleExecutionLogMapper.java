package com.jk51.modules.schedule.mapper;

import com.jk51.model.schedule.ScheduleExecutionLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Mapper
public interface ScheduleExecutionLogMapper {

    void insert(ScheduleExecutionLog log);

}
