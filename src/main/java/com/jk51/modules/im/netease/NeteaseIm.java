package com.jk51.modules.im.netease;

import com.jk51.model.netease.NeteaseAccid;
import com.jk51.modules.im.netease.param.SendMsgParam;
import com.jk51.modules.im.netease.response.SendMsgRes;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
public interface NeteaseIm {

    NeteaseAccid creatAccid(String accid);

    SendMsgRes sendMsg(SendMsgParam param);

    NeteaseAccid refreshToken(String accid);

    @Async
    Future<SendMsgRes> asyncSendMsg(SendMsgParam param);
}
