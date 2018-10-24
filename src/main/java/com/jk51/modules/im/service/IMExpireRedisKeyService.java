package com.jk51.modules.im.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.communal.redis.RedisUtil;
import com.jk51.model.NoBodyAnswerIMRecode;
import com.jk51.modules.im.expires.ExpireRedisKeyManager;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.mapper.ChAnswerRelationMapper;
import com.jk51.modules.im.mapper.NoBodyAnswerIMRecodeMapper;
import com.jk51.modules.im.queue.ExpireProduce;
import com.jk51.modules.im.util.ExpireType;
import com.jk51.modules.im.util.IMParameter;
import com.jk51.modules.im.util.IMRedisKey;
import com.jk51.modules.im.util.RLMessageParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.jk51.modules.im.util.ImExpireConstant.MSG_CONTENT;
import static com.jk51.modules.im.util.ImExpireConstant.RECEIPT_HEANDLE;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 聊天过期事件键添加和删除
 * 作者: gaojie
 * 创建日期: 2017-02-27
 * 修改记录:
 *
 * 处理聊天中咨询与一键呼叫的超时未答复处理
 */
@Service
public class IMExpireRedisKeyService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private NoBodyAnswerIMRecodeMapper noBodyAnswerIMRecodeMapper;

    @Autowired
    private ExpireRedisKeyManager expireKey;

    private Logger logger = LoggerFactory.getLogger(IMExpireRedisKeyService.class);


    //添加店员过期事件键
    public void addExpireForClerkTimeout(RLMessageParameter param) {

        String key = getRedisKey(param,ExpireType.clerkTimeout.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.clerkTimeout.getExpireType());

        if(expireKey.exits(key)){
            return;
        }

        String receiptHandle = ExpireProduce.sendMessage(key,ExpireProduce.FIVE_MINUTE);

        expireKey.addKey(key,getMap(receiptHandle,value));



        //判断店员过期键是否存在,解决（添加多次过期事件键时存在多次相同的事件处理）
       /* if(checkKeyExists(param,ExpireType.clerkTimeout.getExpireType(),true)){
            return;
        }
*/
        //获取过期键
     /*   String key = getRedisKey(param,ExpireType.clerkTimeout.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.clerkTimeout.getExpireType());
        if(StringUtil.isEmpty(key)){
            return;
        }*/

        //添加过期键
        /*redisAddExpire(key,value,IMParameter.clerkTimeoutTime);*/
    }

    //添加店员过期提醒键
    public void addExpireForClerkTimeoutRemind(RLMessageParameter param) {

        String key = getRedisKey(param,ExpireType.clerkTimeoutRemind.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.clerkTimeoutRemind.getExpireType());
        if(expireKey.exits(key)){
            return;
        }

        String receiptHandle = ExpireProduce.sendMessage(key,ExpireProduce.FOUR_MINUTE);
        expireKey.addKey(key,getMap(receiptHandle,value));



        //判断店员过期提醒键是否存在,解决（添加多次过期事件键时存在多次相同的事件处理）
       /* if(checkKeyExists(param,ExpireType.clerkTimeoutRemind.getExpireType(),true)){
            return;
        }*/

        //获取过期键
       /* String key = getRedisKey(param,ExpireType.clerkTimeoutRemind.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.clerkTimeoutRemind.getExpireType());
        if(StringUtil.isEmpty(key)){
            return;
        }*/

        //添加过期键
        //redisAddExpire(key,value,IMParameter.clerkTimeoutTimeRemind);
    }




    //添加会员过期评价键
    public void addExpireForMemberTimeoutEvaluate(RLMessageParameter param) {

        String key = getRedisKey(param,ExpireType.memberTimeoutEvaluate.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.memberTimeoutEvaluate.getExpireType());
        if(expireKey.exits(key)){
            return;
        }

        String receiptHandle = ExpireProduce.sendMessage(key,ExpireProduce.FOUR_MINUTE);
        expireKey.addKey(key,getMap(receiptHandle,value));



        //判断会员过期评价键是否存在,解决（添加多次过期事件键时存在多次相同的事件处理）
     /*   if(checkKeyExists(param,ExpireType.memberTimeoutEvaluate.getExpireType(),false)){
            return;
        }*/

        //获取键
      /*  String key = getRedisKey(param,ExpireType.memberTimeoutEvaluate.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.memberTimeoutEvaluate.getExpireType());
        if(StringUtil.isEmpty(key)){
            return;
        }*/

        //添加键
        //redisAddExpire(key,value,IMParameter.memberTimeoutTimeEvaluateTiem);

    }

    //添加会员过期断开关系键
    public void addExpireForMemberTimeoutOverConversation(RLMessageParameter param) {

        String key = getRedisKey(param,ExpireType.memberTimeOutOverConversation.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.memberTimeOutOverConversation.getExpireType());

        if(expireKey.exits(key)){
            return;
        }

        String receiptHandle = ExpireProduce.sendMessage(key,ExpireProduce.FIVE_MINUTE);
        expireKey.addKey(key,getMap(receiptHandle,value));



        //添加会员过期断开关系键是否存在,解决（添加多次过期事件键时存在多次相同的事件处理）
       /* if(checkKeyExists(param,ExpireType.memberTimeOutOverConversation.getExpireType(),false)){
            return;
        }*/

        //获取键
      /*  String key = getRedisKey(param,ExpireType.memberTimeOutOverConversation.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.memberTimeOutOverConversation.getExpireType());
        if(StringUtil.isEmpty(key)){
            return;
        }*/

        //添加键
        //redisAddExpire(key,value,IMParameter.memberTimeoutTimeOverConversationTime);
    }





    //添加一键呼叫过期键
    public void addExpireForCall(RLMessageParameter param) {

        String key = getRedisKey(param,ExpireType.memberCall.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.memberCall.getExpireType());

        if(expireKey.exits(key)){
            return;
        }

        String receiptHandle = ExpireProduce.sendMessage(key,ExpireProduce.FIVE_MINUTE);
        expireKey.addKey(key,getMap(receiptHandle,value));



        //添加一键呼叫过期键是否存在,解决（添加多次过期事件键时存在多次相同的事件处理）
       /* if(checkKeyExists(param,ExpireType.memberCall.getExpireType(),true)){
            return;
        }*/

        //获取键
     /*   String senderCallKey = getRedisKey(param,ExpireType.memberCall.getExpireType());
        String value = getRedisKeyValue(param,ExpireType.memberCall.getExpireType());
        if(StringUtil.isEmpty(senderCallKey)){
            return;
        }*/

        //保存键
       /* redisAddExpire(senderCallKey,value,IMParameter.callTimeoutTime);*/
    }



    //删除店员过期键
    public void delExpireForClerkTimeOutKey(RLMessageParameter param) {


        //店员过期键
        String clerkTimeOutkey = getRedisKey(param,ExpireType.clerkTimeout.getExpireType());
        //店员过期提醒
        String clerkTimeOutRemindkey = getRedisKey(param,ExpireType.clerkTimeoutRemind.getExpireType());

        deleteKey(clerkTimeOutkey);
        deleteKey(clerkTimeOutRemindkey);
        //批量店员过期键
      /*  Set<String> clerkTimeOutkeys = RedisUtil.scan(clerkTimeOutkey+"*");
        if(!StringUtil.isEmpty(clerkTimeOutkeys)){
            for(String str:clerkTimeOutkeys){
                stringRedisTemplate.delete(str);
            }
        }

        //批量店员过期提醒键
        Set<String> clerkTimeOutRemindkeys = RedisUtil.scan(clerkTimeOutRemindkey+"*");

        if(!StringUtil.isEmpty(clerkTimeOutRemindkeys)){
            for(String str:clerkTimeOutRemindkeys){
                stringRedisTemplate.delete(str);
            }
        }*/



    }



    //删除一键呼叫的redis键
    public void delExpireForCall(RLMessageParameter param) {

        String callTimeOutkey = getRedisKey(param,ExpireType.memberCall.getExpireType());

        deleteKey(callTimeOutkey);
        //批量删除一键呼叫键
        /*Set<String> callTimeOutkeys = RedisUtil.scan(callTimeOutkey+"*");
        if(!StringUtil.isEmpty(callTimeOutkeys)){
            for(String str:callTimeOutkeys){
                stringRedisTemplate.delete(str);
            }
        }*/
    }

    //删除会员过期评价键、删除会员过期断开关系键
    public void delExpireForMember(RLMessageParameter param) {

        String memberTimeoutEvaluateKey = getRedisKey(param,ExpireType.memberTimeoutEvaluate.getExpireType());
        String memberTimeOutOverConversationKey = getRedisKey(param,ExpireType.memberTimeOutOverConversation.getExpireType());


        deleteKey(memberTimeoutEvaluateKey);
        deleteKey(memberTimeOutOverConversationKey);
        //批量删除会员过期评价键
       // Set<String> memberTimeoutEvaluateKeys = RedisUtil.scan(memberTimeoutEvaluateKey+"*");

      /*  if(!StringUtil.isEmpty(memberTimeoutEvaluateKeys)){
            for(String str:memberTimeoutEvaluateKeys){
                stringRedisTemplate.delete(str);
            }
        }*/

        //批量删除会员过期断开关系键
        /*Set<String> memberTimeOutOverConversationKeys = RedisUtil.scan(memberTimeOutOverConversationKey+"*");
        if(!StringUtil.isEmpty(memberTimeOutOverConversationKeys)){
            for(String str:memberTimeOutOverConversationKeys){
                stringRedisTemplate.delete(str);
            }
        }*/
    }


    private void deleteKey(String key){

        String receiptHandle = expireKey.getValue(key,RECEIPT_HEANDLE);
        if(receiptHandle != null){

            try{
                ExpireProduce.deleteMsg(receiptHandle);
            }catch (Exception e){
                logger.info("删除消息队列消息报错提示：{}",e.getMessage());
            }

        }
        expireKey.delete(key);
       /* stringRedisTemplate.delete(key);
        stringRedisTemplate.delete(key+"-list");*/
    }



    private boolean isCall(String key) {
        return key.startsWith("call");
    }

    //保存三次重发后再超时的聊条记录
    private void saveNoBodyAnsweIM(String key,String msgContent) {
        List<String> param = StringUtil.toList(key,"-");
        if(param ==null || param.isEmpty()){
            return;
        }

        NoBodyAnswerIMRecode recode = new NoBodyAnswerIMRecode();
        recode.setApp_id(param.get(1));
        recode.setSender(param.get(2));
        recode.setSite_id(StringUtil.convertToInt(param.get(3)));
        recode.setMsgContent(msgContent);
        noBodyAnswerIMRecodeMapper.insertSelective(recode);

    }

    //获取聊天内容
    private String getMsgContent(List<String> list) {
        String msgContent = "";
        if(list==null || list.size()!=3){
            return msgContent;
        }
        return list.get(1);
    }

    //获取重发的次数，没有的时候返回0
    private int getReSendNum(List<String> list) {
        int reSendNum = 0;
        if(list==null || list.size()!=3){
            return reSendNum;
        }
        return StringUtil.convertToInt(list.get(2)) ;

    }

    //判断是否为咨询
    private boolean isAdvisor(String key) {
        return key.startsWith("advisor");
    }

    //解析key 成IM发送的参数
    private Map<String,Object> getIMParam(String key,String msgContent){

        Map<String,Object> paramMap = new HashMap<String,Object>();
        List<String> param = StringUtil.toList(key,"-");
        if(param ==null || param.isEmpty()){
            return paramMap;
        }
        paramMap.put("appId",param.get(1));
        paramMap.put("sender",param.get(2));
        paramMap.put("site_id",param.get(3));
        paramMap.put("msg_content",msgContent);
        return paramMap;
    }


    //判断店员过期键是否存在
    private boolean checkKeyExists(RLMessageParameter param,String title,boolean isClerkExpireKey){

        boolean keyExists = false;

        IMRedisKey redisKey = new IMRedisKey();
        redisKey.setTitle(title);
        redisKey.setAppId(param.getAppId());
        if(isClerkExpireKey){
            redisKey.setSender(param.getSender().startsWith("wechat")?param.getSender():param.getReceiver());
        }else{
            redisKey.setSender(param.getSender().startsWith("helper")?param.getSender():param.getReceiver());
            //redisKey.setReceiver(param.getSender().startsWith("wechat")?param.getSender():param.getReceiver());
        }

        redisKey.setSiteId(param.getSite_id());
        //redisKey.setiMServiceId(param.getImServiceId());
        try {
            String key =  JacksonUtils.obj2json(redisKey);
            //String key2 =  key.substring(0,key.length()-1).replaceAll("\\\"","\\\\\\\"");

            //批量店员过期键
            //Set<String> clerkTimeOutkeys = stringRedisTemplate.keys(key2+"*");
            // Set<String> clerkTimeOutkeys = RedisUtil.scan(key2+"*");

            String value = stringRedisTemplate.opsForValue().get(key);
            if(!StringUtil.isEmpty(value)){
                keyExists =  true;
            }

        } catch (Exception e) {
            logger.error("生成redis key失败,param:{},报错信息:{}",param,e);
        }

        return keyExists;
    }

    //添加过期时间
    private void redisAddExpire(String key,String value,Integer tiemoutTime){

        //redis中添加键和对应的过期时间
        stringRedisTemplate.opsForValue().set(key,value, tiemoutTime,TimeUnit.MINUTES);

        //redis中添加键对相应的list，在list中报错一个数据，过期监听器在处理时先获取list中的数据，获取到的服务器节点处理（解决多个服务节点重复处理同一个键的过期事件）
        stringRedisTemplate.opsForList().leftPush(key+"-"+"list",value);
    }

    /**
     *由IMRedisKey属性组成的redis key value,用于redis键添加
     * @param param 消息参数
     * */
    private String getRedisKeyValue(RLMessageParameter param,String title){


        IMRedisKey redisKey = new IMRedisKey();
        redisKey.setTitle(title);
        redisKey.setAppId(param.getAppId());
        redisKey.setSender(param.getSender());
        redisKey.setSiteId(param.getSite_id());
        redisKey.setMsgContent(param.getMsg_content());
        redisKey.setiMServiceId(param.getImServiceId());
        redisKey.setReceiver(param.getReceiver());

        try {
            return JacksonUtils.obj2json(redisKey);
        } catch (Exception e) {
            logger.error("生成redis key失败,报错信息:{}",e);
        }

        return null;

    }

    /**
     *由IMRedisKey属性组成的redis key ,用于redis键添加
     * @param param 消息参数
     * */
    private String getRedisKey(RLMessageParameter param,String title){


        StringBuilder sb = new StringBuilder("imKey");
        sb.append("-");
        sb.append(title);
        sb.append("-");
        if(param.getSender().startsWith("wechat_")){
            sb.append(param.getReceiver());
        }else {
            sb.append(param.getSender());
        }
        sb.append("-");
        if(param.getSender().startsWith("wechat_")){
            sb.append(param.getSender());
        }else {
            sb.append(param.getReceiver());
        }

      /*  IMRedisKey redisKey = new IMRedisKey();
        redisKey.setTitle(title);
        redisKey.setAppId(param.getAppId());
        redisKey.setSender(param.getSender());
        redisKey.setSiteId(param.getSite_id());*/
     /*   redisKey.setMsgContent(param.getMsg_content());
        redisKey.setiMServiceId(param.getImServiceId());
        redisKey.setReceiver(param.getReceiver());*/

     /*   try {
            return JacksonUtils.obj2json(redisKey);
        } catch (Exception e) {
            logger.error("生成redis key失败,报错信息:{}",e);
        }*/

        return sb.toString();

    }

    //由IMRedisKey属性组成的redis key,用于redis键删除
    private String getRedisDeleteKeyForCall(RLMessageParameter param,String title){

        IMRedisKey redisKey = new IMRedisKey();
        redisKey.setTitle(title);
        redisKey.setAppId(param.getAppId());
        redisKey.setSender(param.getSender());
        redisKey.setSiteId(param.getSite_id());

        try {
            return   JacksonUtils.obj2json(redisKey);
            //return key.replaceAll("\\\"","\\\\\\\"");
        } catch (Exception e) {
            logger.error("生成redis key失败,报错信息:{}",e);
        }

        return null;
    }

    private String getRedisDeleteKeyForCallClerk(RLMessageParameter param,String title){

        IMRedisKey redisKey = new IMRedisKey();
        redisKey.setTitle(title);
        redisKey.setAppId(param.getAppId());
        redisKey.setSender(param.getReceiver());
        redisKey.setSiteId(param.getSite_id());
        //redisKey.setiMServiceId(param.getImServiceId());
        try {
            return  JacksonUtils.obj2json(redisKey);
        } catch (Exception e) {
            logger.error("生成redis key失败,报错信息:{}",e.getMessage());
        }

        return null;
    }

    private Map<String,String> getMap(String reciptHandle,String msgContent){

        Map<String,String> result = new HashMap<>();
        result.put(RECEIPT_HEANDLE,reciptHandle);
        result.put(MSG_CONTENT,msgContent);
        return result;

    }
}
