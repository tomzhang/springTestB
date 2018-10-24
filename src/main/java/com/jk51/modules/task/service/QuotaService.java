package com.jk51.modules.task.service;

import com.jk51.model.task.TQuota;
import com.jk51.model.task.TQuotaExample;
import com.jk51.modules.task.mapper.TQuotaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-08-17
 * 修改记录:
 */
@Service
public class QuotaService {
    @Autowired
    TQuotaMapper tQuotaMapper;

    public List<TQuota> quotaList(){
        TQuotaExample tQuota = new TQuotaExample();
        return tQuotaMapper.selectByExample(tQuota);
    }
}
