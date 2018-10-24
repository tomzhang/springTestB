package com.jk51.modules.im.netease.impl;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.netease.NeteaseAccid;
import com.jk51.modules.im.netease.NeteaseIm;
import com.jk51.modules.im.netease.Util;
import com.jk51.modules.im.netease.param.SendMsgParam;
import com.jk51.modules.im.netease.response.CreatAccidRes;
import com.jk51.modules.im.netease.response.RefreshTokenRes;
import com.jk51.modules.im.netease.response.SendMsgRes;
import jdk.jfr.events.ExceptionThrownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static com.jk51.modules.im.netease.Constant.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@Service
public class NeteaseImImpl implements NeteaseIm {

    private Logger logger = LoggerFactory.getLogger(NeteaseImImpl.class);

    @Autowired
    private Util util;


    @Override
    public NeteaseAccid creatAccid(String accid){

        NeteaseAccid result = new NeteaseAccid();
        Map<String,Object> body = new HashMap<>();
        body.put("accid",accid);

        try{

            String str = OkHttpUtil.postMap(CREATE_ACCID,body,util.getHeaderMap());
            CreatAccidRes res = JacksonUtils.json2pojo(str,CreatAccidRes.class);
            if(res.getCode() == SUCCESS){
                result = res.getNeteaseAccid();
            }else{

                result = refreshToken(accid);
                logger.info("creatAccid报错：{}",res);
                throw new RuntimeException(res.getDesc());
            }

        }catch (Exception e){
            logger.error("creatAccid报错：{}",ExceptionUtil.exceptionDetail(e));
            throw new RuntimeException(e.getMessage());
        }

        return result;
    }


    @Override
    public NeteaseAccid refreshToken(String accid) {
        NeteaseAccid result = new NeteaseAccid();
        Map<String,Object> body = new HashMap<>();
        body.put("accid",accid);

        try{
            String str = OkHttpUtil.postMap(REFRESH_TOKEN,body,util.getHeaderMap());
            RefreshTokenRes res = JacksonUtils.json2pojo(str,RefreshTokenRes.class);
            if(res.getCode() == SUCCESS){
                result = res.getNeteaseAccid();
            }else{
                logger.error("refreshToken报错：{}",res);
            }

        }catch (Exception e){
            logger.error("refreshToken报错：{}",ExceptionUtil.exceptionDetail(e));
        }

        return result;
    }


    @Override
    public SendMsgRes sendMsg(SendMsgParam param){

        SendMsgRes result = new SendMsgRes();

        Map<String,Object> body = new HashMap<>();
        body.put("from",param.getFrom());
        body.put("to",param.getTo());
        body.put("ope",param.getOpe());
        body.put("type",param.getType());
        body.put("body",param.getBody());

        String str = OkHttpUtil.postMap(SEND_MSG,body,util.getHeaderMap());
        try {
            result = JacksonUtils.json2pojo(str,SendMsgRes.class);
        } catch (Exception e) {
            result.setCode(FAILT);
            result.setDesc(e.getMessage());
            logger.error("sendMsg 解析异常：{}",ExceptionUtil.exceptionDetail(e));
        }

        result.setFrom(param.getFrom());
        result.setTo(param.getTo());

        return result;
    }

    @Override
    @Async
    public Future<SendMsgRes> asyncSendMsg(SendMsgParam param){
        return new AsyncResult(sendMsg(param));
    }


}
