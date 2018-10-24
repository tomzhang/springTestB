package com.jk51.modules.im.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.mapper.ChAnswerRelationMapper;
import com.jk51.modules.im.util.*;
import com.jk51.modules.im.util.PushType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:聊天过期事件处理
 * 作者: gaojie
 * 创建日期: 2017-06-14
 * 修改记录:
 */

@Service
public class IMExpireService {

    @Autowired
    private IMService imService;
    @Autowired
    private ChAnswerRelationMapper chAnswerRelationMapper;
    @Autowired
    private BIMServiceMapper bimServiceMapper;

    private Logger logger = LoggerFactory.getLogger(IMExpireService.class);


    public void handleEvent(String redisKey){

        IMRedisKey key = redisKeyParse(redisKey);

        if(key.getTitle().equals(ExpireType.clerkTimeout.getExpireType())){
            iMClerkTimeout(key);
        }

        if(key.getTitle().equals(ExpireType.memberCall.getExpireType())){

            //.软删除会员与店员的绑定关系
            chAnswerRelationMapper.deleteBySender(key.getSender());

        }

        if(key.getTitle().equals(ExpireType.memberTimeoutEvaluate.getExpireType())){
            imMemberTimeoutEvaluate(key);
        }

        if(key.getTitle().equals(ExpireType.memberTimeOutOverConversation.getExpireType())){
            imMemberTiemoutAndOverConversation(key);
        }

        if(key.getTitle().equals(ExpireType.clerkTimeoutRemind.getExpireType())){
            iMClerkTimeoutRemind(key);
        }

    }

    /**
     * 会员超时评价事件
     *
     * 向会员发起评价
     *
     * */
    public void imMemberTimeoutEvaluate(IMRedisKey imRedisKey){


        //发送评价到会员
        sendEvaluate2Member(imRedisKey);

    }

    /**
     * 会员超时断开聊天关系事件
     *1.向会员发送断开提醒
     * 4.向店员发送断开提醒
     * 3.断开会员与店员的绑定关系
     * 4.记录聊天服务的断开类型和时间
     *
     *
     * */
    public void imMemberTiemoutAndOverConversation(IMRedisKey imRedisKey){


        //1向会员发送断开提醒
        sendRemind2Member(imRedisKey, IMParameter.MEMBERBUSY);

        //2向店员发送断开提醒
        sendRemind2Clerk(imRedisKey,IMParameter.MEMBER_BUSY_TO_CLERK);

        //3断开会员与店员的绑定关系
        chAnswerRelationMapper.deleteBySender(imRedisKey.getReceiver());

        //4.记录聊天服务的断开类型和时间
        recodeTimeout(imRedisKey.getiMServiceId(),IMEndType.memberTimeout.getIndex());
    }


    /**
     *
     * 店员超时事件
     *
     * 1.记录聊天结束类型，和结束时间   3(会员超时)
     * 2.软删除会员与店员的绑定关系
     * 3.发送提示信息给会员
     * 4.发送提示消息给店员
     */
    public void iMClerkTimeout(IMRedisKey imRedisKey) {



        //1记录聊天结束类型，和结束时间   店员超时)
        recodeTimeout(imRedisKey.getiMServiceId(),IMEndType.clerkTimeout.getIndex());

        //2.软删除会员与店员的绑定关系
        chAnswerRelationMapper.deleteBySender(imRedisKey.getSender());

        //发送提示信息给会员
        sendRemind2Member(imRedisKey,IMParameter.ALLCLERKBUSY);

        //4发送提示消息给店员，如果为抢答消息超时，不发送提示信息给店员
        if(isQuestion(imRedisKey)){
            return;
        }
        sendRemind2Clerk(imRedisKey,IMParameter.CLERK_REMIND_DID_CLOSE);


    }


    //判断店员超时消息是否为抢答消息
    private boolean isQuestion(IMRedisKey imRedisKey){

        String[] receivers = imRedisKey.getReceiver().split(",");
        if(receivers.length>1){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 店员超时提醒事件
     * 1.发送提醒信息给店员
     *
     * */
    public void iMClerkTimeoutRemind(IMRedisKey imRedisKey) {

        //1发送提示信息给店员
        if(isQuestion(imRedisKey)){
            return;
        }
        sendRemind2Clerk(imRedisKey,IMParameter.CLERK_REMIND_WILL_CLOSE);

    }



    //发送评价到会员
    private void sendEvaluate2Member(IMRedisKey imRedisKey){
        RLMessageParameter param = new RLMessageParameter();
        param.setAppId(imRedisKey.getAppId());
        param.setSite_id(imRedisKey.getSiteId());
        param.setReceiver(imRedisKey.getReceiver().startsWith("wechat")?imRedisKey.getReceiver():imRedisKey.getSender());
        param.setSender("51jk");
        param.setImServiceId(imRedisKey.getiMServiceId());
        param.setMsg_type(MsgType.EVALUATR.getIndex());
        param.setPush_type(PushType.PERSON.getIndex());
        param.setIsSystemMessage(IMParameter.isSystemMessage);
        param.setMsg_content(imRedisKey.getiMServiceId().toString());
        imService.sendMsg(param);
    }


    //解析redis键
    public IMRedisKey redisKeyParse(String redisKey){

        IMRedisKey imRedisKey = null;
        try {
            imRedisKey = JacksonUtils.json2pojo(redisKey,IMRedisKey.class);
        } catch (Exception e) {
            logger.error("聊天超时键解析失败，key:{},报错信息:{}",redisKey,e);
        }

        return imRedisKey;
    }



    //记录聊天结束类型，和结束时间   3(会员超时)
    private void recodeTimeout(int im_service_id,int im_end_type){

        bimServiceMapper.updateImEndType(im_service_id,im_end_type);
    }

    //店员超时发送提示信息给会员
    public void sendRemind2Member(IMRedisKey imRedisKey,String remind){

        RLMessageParameter param = new RLMessageParameter();
        param.setAppId(imRedisKey.getAppId());
        param.setSite_id(imRedisKey.getSiteId());
        param.setReceiver(imRedisKey.getReceiver().startsWith("wechat")?imRedisKey.getReceiver():imRedisKey.getSender());
        param.setSender("51jk");
        param.setImServiceId(imRedisKey.getiMServiceId());
        param.setMsg_type(MsgType.SYSTEM_REMIND.getIndex());
        param.setPush_type(PushType.PERSON.getIndex());
        param.setMsg_content(remind);
        param.setIsSystemMessage(IMParameter.isSystemMessage);

        imService.sendMsg(param);
    }

    //发送提示消息给店员
    public void sendRemind2Clerk(IMRedisKey imRedisKey, String remind) {

        RLMessageParameter param = new RLMessageParameter();
        param.setAppId(imRedisKey.getAppId());
        param.setSite_id(imRedisKey.getSiteId());
        param.setReceiver(imRedisKey.getReceiver().startsWith("helper")?imRedisKey.getReceiver():(StringUtil.isEmpty(imRedisKey.getReceiver())?"":imRedisKey.getSender()));
        param.setSender(imRedisKey.getSender().startsWith("wechat")?imRedisKey.getSender():imRedisKey.getReceiver());
        param.setImServiceId(imRedisKey.getiMServiceId());
        param.setMsg_type(MsgType.SYSTEM_REMIND.getIndex());
        param.setPush_type(PushType.PERSON.getIndex());
        param.setMsg_content(remind);
        param.setIsSystemMessage(IMParameter.isSystemMessage);

        imService.sendMsg(param);
    }
}
