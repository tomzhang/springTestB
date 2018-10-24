package com.jk51.modules.schedule.service;

import com.jk51.model.schedule.ScheduleMeta;
import com.jk51.modules.schedule.mapper.ScheduleMetaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Service
public class ScheduleMetaService {

    @Autowired
    private ScheduleMetaMapper mapper;


    public ScheduleMeta queryOne(Integer id){
        return mapper.queryOne(id);
    }

    public void updateStatus(Integer id, Integer status){
        mapper.updateStatus(id,status);
    }

}
