package com.jk51.modules.im.netease.service;

import com.jk51.modules.im.netease.mapper.SendMsgResMapper;
import com.jk51.modules.im.netease.response.SendMsgRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/4
 * 修改记录:
 */
@Service
public class SendMsgResMapperService {
    @Autowired
    private SendMsgResMapper sendMsgResMapper;

    public void asyncInsert(SendMsgRes res){

        sendMsgResMapper.insert(res.getCode(),res.getDesc(),res.getFrom(),res.getTo(),res.getData()!=null?res.getData().toString():"");

    }
}
