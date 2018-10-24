package com.jk51.modules.im.service;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.ChUserPushInfo;
import com.jk51.model.GeTuiNoticeMessage;
import com.jk51.modules.im.mapper.ChUserPushInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:个推API
 * 作者: gaojie
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Service
public class GeTuiPush {


    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置\
    @Value("${getui.appId}")
    private  String appId;
    @Value("${getui.appKey}")
    private  String appKey;
    @Value("${getui.AppSecret}")
    private  String AppSecret;
    @Value("${getui.masterSecret}")
    private  String masterSecret;

    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";

    Logger logger = LoggerFactory.getLogger(GeTuiPush.class);

    @Autowired
    private ChUserPushInfoMapper chUserPushInfoMapper;


    public void pushToClient(String client_id, String content,String deviceToken){

        IGtPush push = new IGtPush(url, appKey, masterSecret);
        IQueryResult userstatus = push.getClientIdStatus(appId, client_id);
        if(!StringUtil.isEmpty(deviceToken)){

            if(userstatus.getResponse().get("result").equals("Offline")){

                //登出通知不发送离线消息
                //ios离线单个推送
                //iOSOfflineSinglePush(push,deviceToken,content);

            }else{

                //ios在线单个推送
                singlePush(push,client_id,content);
            }
        }else if(userstatus.getResponse().get("result").equals("Offline")){

            //登出通知不发送离线消息
            //singlePush(push,client_id,content);
        }else{

            singlePush(push,client_id,content);
        }


    }


    private int num = 0;

    //ios个推在线/安卓个推单个消息推送
    private void singlePush(IGtPush push, String client_id, String content){

        //创建接收目标
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(client_id);

        //获取itemplate
        TransmissionTemplate iTemplate = getITemplate(content);

        //获取Massege
        SingleMessage message = getMessage(iTemplate);

        //发送消息
        IPushResult iPushResult = null;
        try {
            iPushResult =  push.pushMessageToSingle(message, target);
            ++num;
            System.out.println(message.getData().getTransmissionContent()+"---"+num);
        } catch (RequestException e) {

            //异常重发
            push.pushMessageToSingle(message,target,e.getRequestId());
            logger.error("个推推送消息异常",e);
        }
    }



    //ios 个推离线单个推送
    private void iOSOfflineSinglePush(IGtPush push, String deviceToken, String content){

        //获取itemplate
        TransmissionTemplate iTemplate = getITemplate(content);

        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        APNPayload payload = new APNPayload();
        payload.setAutoBadge("+1");
        payload.setContentAvailable(1);
        payload.setSound("default");

        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(content));

        //字典模式使用下者
        iTemplate.setAPNInfo(payload);

        //获取Massege
        SingleMessage message = getMessage(iTemplate);

        //推送消息到苹果
        try {
            push.pushAPNMessageToSingle(appId, deviceToken, message);
        }catch (Exception e){
            logger.error("个推推送消息异常",e);
        }
    }


    //返回个推类---设置离线是不保存信息，如果需要离线是保存信息，请设置message.setOffline(true);
    private SingleMessage getMessage(ITemplate iTemplate) {

        SingleMessage message = new SingleMessage();
        message.setData(iTemplate);
        message.setOffline(false);
        //message.setOfflineExpireTime(24 * 1000 * 3600);
        return message;
    }


    //返回个推模板
    private TransmissionTemplate getITemplate(String content) {

        TransmissionTemplate templet = new TransmissionTemplate();
        templet.setAppId(appId);
        templet.setTransmissionContent(content);

        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        templet.setTransmissionType(2);
        templet.setAppkey(appKey);
        return templet;
    }


    /**
     * 通知app下线
     * @param storeAdminId b_store_admin表主键
     */

    public void noticeOtherAppQuit(Integer storeAdminId){

        //查询storeAdminId的登录记录
        ChUserPushInfo chUserPushInfo = chUserPushInfoMapper.findByStoreAdminId(storeAdminId);

        if(StringUtil.isEmpty(chUserPushInfo)){
            return;
        }

        //设置个推的提醒信息
        GeTuiNoticeMessage geTuiNoticeMessage = new GeTuiNoticeMessage();
        geTuiNoticeMessage.setAnchor("QUIT");
        /*geTuiNoticeMessage.setAnchor("orderRemind");
        geTuiNoticeMessage.setMsg("测试定单提醒");*/
        pushToClient(chUserPushInfo.getPush_id(),geTuiNoticeMessage.getContent(),chUserPushInfo.getDevice_token());
    }




    /**
     * 买家付款成功后，要推送 新订单提醒 到门店助手APP 该订单对应门店下所有店员 ；
     * 若该已付款订单，并没有被指定某个门店，在商家后台指定门店时 再推送
     * @param storeAdminIds
     * @param message
     */
    public void noticeOtherAppOrderRemind(List<String> storeAdminIds, String message, String messageType){//messageType:orderRemind
        if(storeAdminIds == null || storeAdminIds.size() == 0 || StringUtil.isEmpty(messageType)){
            return;
        }

        GeTuiNoticeMessage geTuiNoticeMessage = new GeTuiNoticeMessage();
        geTuiNoticeMessage.setAnchor(messageType);
        if(!StringUtil.isEmpty(message)){
            geTuiNoticeMessage.setMsg(message);
        }

        IGtPush push = new IGtPush(url, appKey, masterSecret);

        List<Target> targets = new ArrayList<>();
        List<String> deviceTokenListAPN = new ArrayList<>();
        for (String id : storeAdminIds){
            ChUserPushInfo chUserPushInfo = chUserPushInfoMapper.findByStoreAdminId(Integer.parseInt(id));

            if(StringUtil.isEmpty(chUserPushInfo)){
                continue;
            }

            IQueryResult userstatus = push.getClientIdStatus(appId, chUserPushInfo.getPush_id());
            if(!StringUtil.isEmpty(chUserPushInfo.getDevice_token()) && !"NULL".equals(chUserPushInfo.getDevice_token()) && userstatus.getResponse().get("result").equals("Offline")){//iOS 离线
                deviceTokenListAPN.add(chUserPushInfo.getDevice_token());
            }else{
                Target target = new Target();
                target.setAppId(appId);
                target.setClientId(chUserPushInfo.getPush_id());
                targets.add(target);
            }
        }

        if(targets.size() != 0){
            TransmissionTemplate template = getITemplate(geTuiNoticeMessage.getContent());
            ListMessage listMessage = getListMessage(template);

            // taskId用于在推送时去查找对应的message
            String taskId = push.getContentId(listMessage);
            //发送消息
            IPushResult ret = null;
            try {
                ret = push.pushMessageToList(taskId, targets);
            } catch (RequestException e) {
                logger.error("个推推送消息异常",e);
            }
            if (ret != null) {
                System.out.println(ret.getResponse().toString());
            } else {
                logger.error("个推服务响应异常");
            }
        }

        if(deviceTokenListAPN.size() != 0){
            TransmissionTemplate templateAPN = getTransmissionTemplateAPN(geTuiNoticeMessage.getContent());
            ListMessage listMessageAPN = getListMessage(templateAPN);

            // taskId用于在推送时去查找对应的message
            String taskIdAPN = push.getContentId(listMessageAPN);
            //发送消息
            IPushResult retAPN = null;
            try {
                retAPN = push.pushAPNMessageToList(appId, taskIdAPN, deviceTokenListAPN);
            } catch (RequestException e) {
                logger.error("个推推送消息异常",e);
            }
            if (retAPN != null) {
                System.out.println(retAPN.getResponse().toString());
            } else {
                logger.error("个推服务响应异常");
            }
        }
    }

    private TransmissionTemplate getTransmissionTemplateAPN(String content) {
        //获取itemplate
        TransmissionTemplate iTemplate = new TransmissionTemplate();
        iTemplate.setAppId(appId);
        iTemplate.setAppkey(appKey);
        iTemplate.setTransmissionContent(content);
        iTemplate.setTransmissionType(2);

        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("+1");
        payload.setContentAvailable(1);
        payload.setSound("default");
        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(content));
        //字典模式使用下者
        //payload.setAlertMsg(getDictionaryAlertMsg());

        iTemplate.setAPNInfo(payload);
        return iTemplate;
    }

    private ListMessage getListMessage(TransmissionTemplate transmissionTemplate) {
        ListMessage message = new ListMessage();
        message.setData(transmissionTemplate);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 1000 * 3600);
        return message;
    }

}
