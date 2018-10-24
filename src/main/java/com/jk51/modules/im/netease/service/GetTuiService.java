package com.jk51.modules.im.netease.service;


import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.im.service.MultiThreadPushMessage;
import com.jk51.modules.im.service.PushServeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jk51.modules.im.util.PushConstant.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/3
 * 修改记录:
 */
@Service
public class GetTuiService {

    @Autowired
    private MultiThreadPushMessage multiThreadPushMessage;


    public Result noticeAllAppOffline(){

        try{
            multiThreadPushMessage.pushMessageToAll(HANDOVER_IM_TITLE, HANDOVER_IM_TEXT, HANDOVER_IM_TYPE);
        }catch (Exception e){
            Result.fail(e.getMessage());
        }

        return Result.success();
    }

}
