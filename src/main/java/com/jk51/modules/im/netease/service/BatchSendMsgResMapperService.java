package com.jk51.modules.im.netease.service;

import com.jk51.modules.im.netease.mapper.BatchSendMsgResMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/4
 * 修改记录:
 */
@Service
public class BatchSendMsgResMapperService {

    @Autowired
    private BatchSendMsgResMapper batchSendMsgResMapper;

    @Async
    public void asyncInSert(String success,String fail){
        batchSendMsgResMapper.insert(success,fail);
    }
}
