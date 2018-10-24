package com.jk51.modules.pay.mapper;

import com.jk51.model.PayLogs;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Mapper
public interface PayLogsMapper {

    public List<PayLogs> findPayLogs(Map<String, Object> params);

    public void insert(PayLogs payLogs);

    public void update(PayLogs payLogs);

    public PayLogs findByTradesId(long tradesId);
}
