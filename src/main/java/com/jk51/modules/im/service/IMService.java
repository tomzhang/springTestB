package com.jk51.modules.im.service;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.im.RLTXManager;
import com.jk51.model.*;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.appInterface.util.ReturnMap;
import com.jk51.modules.im.mapper.*;
import com.jk51.modules.im.netease.Util;
import com.jk51.modules.im.netease.service.NeteaseService;
import com.jk51.modules.im.util.IMParameter;
import com.jk51.modules.im.util.RLMessageContent;
import com.jk51.modules.im.util.RLMessageParameter;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.service.ScreeningClerkService;
import com.jk51.modules.index.service.StoreAdminExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Future;


import static java.util.stream.Collectors.toList;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 */
@Service
public class IMService {

    private static final Logger logger = LoggerFactory.getLogger(IMService.class);
    @Autowired
    private RLTXManager rLTXManager;
    @Autowired
    private ScreeningClerkService screeningClerkService;
    @Autowired
    private SendRaceAnswerRecodeMapper sendRaceAnswerRecodeMapper;
    @Autowired
    private  ImRecodeMapper imRecodeMapper;
    @Autowired
    private RaceAnswerRecodeMapper raceAnswerRecodeMapper;
    @Autowired
    private StoreAdminExtService storeAdminExtService;
    @Autowired
    private ChAnswerRelationMapper chAnswerRelationMapper;
    @Autowired
    private  BIMServiceMapper bIMServiceMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private BIMFailLogMapper bimFailLogMapper;
    @Autowired
    private Util util;
    @Autowired
    private NeteaseService neteaseService;
    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    private static List<String> sources = null;
    // 用户token前缀
    static {
        sources = new ArrayList<String>();
        sources.add("wechat_");
        sources.add("h5_");
        sources.add("app_");
        sources.add("helper_");
    }

    /**
     *直接发送消息到对应的receiver，记录聊天记录
     * */
    public Map<String,Object> sendMsg(RLMessageParameter param){

        //记录聊天记录
        IMRecode imRecode =  saveIMRecode(param);

        //判断imServiceId是否为空
        if(StringUtil.isEmpty(param.getImServiceId())){

            Integer imServiceId = getImServiceId(param);
            if(StringUtil.isEmpty(imServiceId)){
                return ResultMap.errorResult("imServiceId是否为空");
            }

            param.setImServiceId(getImServiceId(param));

        }

        String im_create_time = DateUtils.toString(imRecode.getCreate_time(),"yyyy-MM-dd HH:mm:ss");

        //获取店员邀请码
        String invitationCode = param.getMsg_type()==14? getclerkInvitationCode(param.getSender(),param.getSite_id()):null;
        //json封装发送消息格式
        String msg = getMsg(param.getMsg_content(),imRecode.getId(),0,param.getMsg_type(), param.getImServiceId(),im_create_time,invitationCode );
        param.setMsgContent(msg);

       /* Map<String,Object> paramMap = rLTXManager.getParam(param);
        Future<Map<String,Object>> future =  rLTXManager.pushMsg(paramMap);
        return getFutureResult(future, null);*/

       return send(param);
    }


    private Integer getImServiceId(RLMessageParameter param){

        return chAnswerRelationMapper.findImServiceId(param.getSender(),param.getReceiver());
    }
    /**
     * 获取异步方法调用结果
     * @parem receiver  不为空时为抢答消息，返回发送给了多少人
     *
     * */
    private Map<String,Object> getFutureResult(Future<Map<String, Object>> future, String receiver){

        Map<String,Object> result = new HashMap();
        try {
            result = future.get();
        } catch (Exception e) {
            logger.error("获取发送消息异步结果失败,报错信息:{}",e);
            return ReturnMap.buildFailReturnMap("获取发送消息异步结果失败");
        }
        if((result.get("statusCode")).equals("000000")){

            //抢答人数
            if(!StringUtil.isEmpty(receiver)){
                result.put("receiverNum",getReceiverNum(receiver));
            }

            result.put("status","OK");
            return result;
        }else {
            return ReturnMap.buildFailReturnMap(result.get("statusMsg").toString());
        }

    }

    /**
     *抢答
     * 客户请求receiver为空，需要后台筛选出店员账户
     *
     * 记录发送的抢答信息
     */
    public Map<String,Object> answerDevices(RLMessageParameter param){

        //获取receiver
        String receiver = getReceiver(param.getSite_id());

        if(StringUtil.isEmpty(receiver)){
            return ResultMap.errorResult("未查询到店员");
        }

        param.setReceiver(receiver);

        param.setIsRace(IMParameter.isRace);
        //保存聊天记录
        IMRecode imRecode =  saveIMRecode(param);

        //软删除该用户与店员建立的关系----------2017/08/15 这个删除是多余的
        String sender = param.getSender();
        //chAnswerRelationMapper.delete(sender);

        //将微信发起的抢答记录到聊天服务中，并返回聊天服务表的ID
        Integer imServiceId = saveImService(sender,param.getMsg_type(),param.getSite_id());
        param.setImServiceId(imServiceId);

        String im_create_time = DateUtils.toString(imRecode.getCreate_time(),"yyyy-MM-dd HH:mm:ss");
        //获取封装后的信息为字符串
        String msg = getMsg(param.getMsg_content(),imRecode.getId(),1,param.getMsg_type(),imServiceId, im_create_time,null);
        param.setMsgContent(msg);


      /*  //发送消息参数
        Map<String,Object> paramMap = rLTXManager.getParam(param);
        //发送消息
        Future<Map<String,Object>> future = rLTXManager.pushMsg(paramMap);
        return getFutureResult(future,receiver);*/

      return send(param);
    }

    //将微信发起的抢答记录到聊天服务中，并返回聊天服务表的ID
    private Integer saveImService(String sender, Integer im_type, Integer site_id){

        BIMService bimService = new BIMService();
        bimService.setSender(sender);
        bimService.setStart_time(new Date());
        bimService.setIm_type(im_type);
        bimService.setSite_id(site_id);
        bIMServiceMapper.insertSelective(bimService);
        return bimService.getId();
    }

    //获得app receiver
    public String getReceiver(Integer site_id) {

        String receiver = "";

        //筛选店员账户
        List<StoreAdmin> storeAdminList = screeningClerkService.getClerkId(site_id);

        if(StringUtil.isEmpty(storeAdminList)){
            return receiver;
        }

        StringBuilder builder = new StringBuilder();
        for(StoreAdmin sa:storeAdminList){

            Integer siteId = sa.getSite_id();
            Integer id = sa.getId();
            builder.append("helper_");
            builder.append(siteId);
            builder.append("_");
            builder.append(id);
            builder.append(",");
        }


        return builder.toString().substring(0,builder.toString().length()-1);
    }


    //封装发送的信息
    private String getMsg(String msgContent, int msg_id, int is_question, int type, Integer imServiceId,String create_time,String invitationCode){

        RLMessageContent content = new RLMessageContent();
        content.setIs_question(is_question);
        content.setMsg_content(msgContent);
        content.setType(type);
        content.setMsg_id(msg_id);
        content.setImServiceId(imServiceId);
        content.setCreate_time(create_time);
        content.setClerkInvitationCode(invitationCode);

        String msg = "";
        try {
            msg = JacksonUtils.obj2json(content);
        } catch (Exception e) {
            logger.error("RLMessageContent转json失败,content:{},报错信息:{}",content,e);
        }
        return msg;
    }
    private int getReceiverNum(String receiver){
       String[] receiverArray =  receiver.split(",");
       if(receiverArray ==null){
           return 0;
       }
       return receiverArray.length;
    }
    /**
     *保存聊天记录
     *
     * */
    @Transactional
    public IMRecode saveIMRecode(RLMessageParameter param){

        IMRecode imRecode = new IMRecode();
        imRecode.setAppid(param.getAppId());
        imRecode.setReceiver(param.getReceiver());
        imRecode.setSender(param.getSender());
        imRecode.setMsg(emojiConverter.toAlias(StringUtil.isEmpty(param.getMsg_content())?"":param.getMsg_content()));  //emoji 需要转码才能保存到mysql
        imRecode.setMsg_type(param.getMsg_type());
        imRecode.setIsRace(StringUtil.isEmpty(param.getIsRace())?IMParameter.isNotRace:param.getIsRace());
        imRecode.setIsSystemMessage(StringUtil.isEmpty(param.getIsSystemMessage())?IMParameter.isNotSystemMessage:param.getIsSystemMessage());
        imRecode.setSiteId(param.getSite_id());
        imRecode.setBuyerId(getBuyerId(param));
        imRecode.setCreate_time(new Date());
        //不是抢答消息时，解析APP容联账户,设置storeAdminId，抢答消息在店员抢答时根据app容联账户保存storeAdminId,没有抢答的抢答消息没有storeAdminId
        if(imRecode.getIsRace().equals(IMParameter.isNotRace)){
            imRecode.setStoreAdminId(getStoreAdminId(param));
        }

        int num = imRecodeMapper.insertSelective(imRecode);

        if(num!=1){
            logger.error("保存聊天记录失败");
        }
        return imRecode;
    }

    private Integer getStoreAdminId(RLMessageParameter param){
        String appRL = param.getSender().startsWith("helper")?param.getSender():param.getReceiver();
        String[] strList = appRL.split("_");
        String storeAdminIdStr = strList[2];
        return Integer.valueOf(storeAdminIdStr);
    }

    private Integer getBuyerId(RLMessageParameter param){

        String wechatRL = param.getSender().startsWith("wechat")?param.getSender():param.getReceiver();
        String[] strList = wechatRL.split("_");
        String buyerId = strList[2];
        return Integer.valueOf(buyerId);
    }

    /**
     *保存发送给店员的抢答信息
     *
     * */
    @Transactional
    public int saveSendRaceAnswerRecode(RLMessageParameter param){

        SendRaceAnswerRecode sendRaceAnswerRecode = new SendRaceAnswerRecode();
        sendRaceAnswerRecode.setAppid(param.getAppId());
        sendRaceAnswerRecode.setReceiver(param.getReceiver());
        sendRaceAnswerRecode.setSender(param.getSender());
        sendRaceAnswerRecode.setMsg(param.getMsg_content());
        sendRaceAnswerRecode.setMsgType(param.getMsg_type());
        int num = sendRaceAnswerRecodeMapper.insertSelective(sendRaceAnswerRecode);

        if(num!=1){
            logger.error("保存发送给店员的抢答信息失败");
        }

        return sendRaceAnswerRecode.getId();
    }

    //判断消息是否有抢答过
    private  boolean checkReaced(Integer imServiceId){

        BIMService bimService = bIMServiceMapper.selectByPrimaryKey(imServiceId);
        if(StringUtil.isEmpty(bimService)){
            return false;
        }
        if(StringUtil.isEmpty(bimService.getRace_status())){
            return false;
        }
        if(bimService.getRace_status()==1){
            return true;
        }

        return false;
    }

    //咨询已经结束，不能抢答
    private  boolean checkIMEndAndIMReced(Integer imServiceId){
        Integer racedOrIMEnd = bIMServiceMapper.checkIMEndAndIMReced(imServiceId);
        if(racedOrIMEnd==1){
            return true;
        }
        return false;
    }


    /**
     *店员抢答
     *查询抢答记录表中是否有抢答成功的记录，如果有（记录抢答失败），如果没有（记录抢答成功）
     * 在记录抢答时，记录抢答发送的时间
     * */
    public Map<String,Object> raceFirstAnswer(RLMessageParameter param) {

        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> results = new HashMap<String,Object>();


        //查询是否已经有其他店员建立抢答关系
        ChAnswerRelation chr = chAnswerRelationMapper.findByUserOpenId(param.getSender());
        if(!StringUtil.isEmpty(chr)){

            if(!chr.getPharmacist_userid().equals(param.getReceiver())){

                //记录抢答失败记录
                saveNotGetRaceAnswerRecode(param);

                result.put("errorMessage","已被别人抢走");
                result.put("status","ERROR");
                return result;
            }

        }

        //查询是否已经是抢答关系
        ChAnswerRelation chr2 =
            chAnswerRelationMapper.findByUserOpenIdAndharmacistUserid(param.getSender(),param.getReceiver());

        Integer imServiceId = 0;
        //没有建立过抢答关系
        if(StringUtil.isEmpty(chr2)){

            imServiceId = param.getImRecodeIdAndServiceIds().stream().filter((imRecodeIdAndServiceId)->filterCanRaceIMServiceID(imRecodeIdAndServiceId)).map(a->a.getImServiceId()).reduce(Integer::max).get();
            Integer imRecodeId = param.getImRecodeIdAndServiceIds().stream().filter((imRecodeIdAndServiceId)->filterCanRaceIMServiceID(imRecodeIdAndServiceId)).map(a->a.getMsg_id()).reduce(Integer::max).get();

            //建立过抢答关系
            ChAnswerRelation cr = new ChAnswerRelation();
            cr.setDisable(0);
            cr.setPharmacist_userid(param.getReceiver());
            cr.setUser_openid(param.getSender());
            cr.setIm_service_id(imServiceId);
            cr.setIm_recode_id(imRecodeId);
            int num = chAnswerRelationMapper.insert(cr);

            if(num!=1){
                result.put("errorMessage","建立好友关系失败");
                result.put("status","ERROR");
                return result;
            }

            //记录抢答成功信息
            saveIsGetRaceAnswerRecode(param);

            //发送抢答成功的店员信息给客户
            sendStoreAdminInfoToCustomer(param);

        }

        //获取符合抢答的imRecodeIds
        List<Integer> imRecodeIds = param.getImRecodeIdAndServiceIds().stream()
            .filter((imRecodeIdAndServiceId)->filterCanRaceIMServiceID(imRecodeIdAndServiceId))
            .map(imRecodeIdAndServiceId-> imRecodeIdAndServiceId.getMsg_id())
            .collect(toList());



        //遍历ImRecodeIdAndServiceIds执行业务需求
        param.getImRecodeIdAndServiceIds().stream()
            .filter((imRecodeIdAndServiceId)->filterCanRaceIMServiceID(imRecodeIdAndServiceId))
            .forEach((imRecodeIdAndServiceId)->updateIMServiceAndIMRecode(imRecodeIdAndServiceId,param));


        if(StringUtil.isEmpty(imRecodeIds)){

            result.put("errorMessage","已被别人抢走");
            result.put("status","ERROR");
            return result;
        }

        //返回参数
        result.put("status","OK");
        results.put("memberId",param.getSender());
        results.put("statusCode",0);
        results.put("msg_ids",imRecodeIds);
        results.put("imServiceId",imServiceId);
        result.put("results",results);

        return result;
    }


    private void updateIMServiceAndIMRecode(ImRecodeIdAndServiceIds ids,RLMessageParameter param){

        //抢答时在聊天服务中更新imServiceID对应的race_time,rece_status设置为1
        try{
            bIMServiceMapper.updateRaceTimeAndReceiver(ids.getImServiceId(),new Date(),param.getReceiver());
        }catch (Exception e){
            logger.error("更新imServiceID对应的race_time失败,imServiceId={},receiver={},报错信息：{}",ids.getImServiceId(),param.getReceiver(),e);
        }


        //抢答时根据msg_id更新对应b_im_recode中的storeAdminId
        Integer storeAdminId = getStoreAdminId(param);
        imRecodeMapper.updateStoreAdminIdByPrimaryKey(storeAdminId,ids.getMsg_id());
    }


    //获取符合抢答的消息ID
    private boolean filterCanRaceIMServiceID(ImRecodeIdAndServiceIds ids){

        if(checkIMEndAndIMReced(ids.getImServiceId())){
            return false;
        }

        return true;
    }

    //发送抢答成功的店员信息给客户
    private void sendStoreAdminInfoToCustomer(RLMessageParameter param){

        //根据app的RLToken查询user_id
        String user_id = "";
        if(!StringUtil.isEmpty(param.getReceiver())){
            user_id = getUserIdByToken(param.getReceiver());
        }else {
            logger.error("发送抢答成功的店员信息给客户失败，RLMessageParameter：{}",param);
            return;
        }


        String[] store_admin_priamryKey = user_id.split("_");
        if(store_admin_priamryKey.length!=2){

            logger.error("发送抢答成功的店员信息给客户失败，RLMessageParameter：{}",param);
            return;
        }

        //获取用户信息
        Map<String,String> info = getUserInfo(store_admin_priamryKey[0],store_admin_priamryKey[1]);
        info.put("type","2");
        info.put("imServiceId", param.getImRecodeIdAndServiceIds().stream().filter((imRId)->filterCanRaceIMServiceID(imRId)).map(a->a.getImServiceId()).reduce(Integer::max).get().toString());


        //发送抢答成功的店员信息给客户
        String msgContent = getStoreAdminInfomation(info);
        sendSystemInformation(param.getSender(),param.getReceiver(),msgContent,param.getAppId());
    }


    //获取用户信息
    private Map<String,String> getUserInfo(String site_id,String store_admin_id){

        List<Map<String,String>> infoList = storeAdminExtService.getSoreAdminInfo(site_id,store_admin_id);
        Map<String,String> info = new HashMap<>();
        if(!StringUtil.isEmpty(infoList)&&infoList.size()!=0){
            info = infoList.get(0);
        }

        return info;
    }

    /**
     * 根据荣联token去获取user_id
     * @param token string
     * @return string
     */
    public String  getUserIdByToken(String token){

        String userId = "";
        for(String str:sources){
            if(token.indexOf(str)!=-1){
                userId = token.replaceAll(str,"");
            }
        }
        return userId;
    }

    //获取系统信息
    private String getStoreAdminInfomation(Map<String,String> info){

        StringBuilder builder = new StringBuilder();
       /* builder.append("systemInformation:");*/

        if(info==null || info.isEmpty()){
            return builder.toString();
        }

        builder.append(JacksonUtils.mapToJson(info));
        return builder.toString();

    }

    //发送系统消息
    public void sendSystemInformation(String receiver,String sender,String msgContent,String appId){

      /*  Map<String,Object> paramMap = rLTXManager.getParam(receiver,sender,msgContent,appId);
        rLTXManager.pushMsg(paramMap);*/

        RLMessageParameter parameter = new RLMessageParameter();
        parameter.setReceiver(receiver);
        parameter.setSender(sender);
        parameter.setMsgContent(msgContent);
        parameter.setAppId(appId);

        send(parameter);
    }

    /**
     *查询抢答记录
     * */
    private RaceAnswerRecode getRaceAnswerRecode(Map<String, Object> param){

        String msg_id = (String) param.get("msg_id");
        return  raceAnswerRecodeMapper.getByPrimarykey(msg_id);

    }

    /**
     * 保存抢答成功记录
     * */
    @Transactional
    public void  saveIsGetRaceAnswerRecode(RLMessageParameter param){

        //查询发送抢答记录
        SendRaceAnswerRecode sendRaceAnswerRecode = getSendRaceAnswerRecode(param);

        RaceAnswerRecode raceAnswerRecode = new RaceAnswerRecode();
        raceAnswerRecode.setAppid(param.getAppId());
        raceAnswerRecode.setReceiver(param.getReceiver());
        raceAnswerRecode.setSender(param.getSender());
        raceAnswerRecode.setMsg(emojiConverter.toAlias(StringUtil.isEmpty(param.getMsg_content())?"":param.getMsg_content()));
        raceAnswerRecode.setIsGet("Y");
        raceAnswerRecode.setIsBreak("N");
        raceAnswerRecode.setSend_race_answer_recode_time((sendRaceAnswerRecode!=null)?sendRaceAnswerRecode.getCreate_time():null);
       int num =  raceAnswerRecodeMapper.insertSelective(raceAnswerRecode);
        if(num!=1){
            logger.error("保存抢答成功记录失败");
        }
    }

    /**
     *根据msg，sender，appId查询发送抢答记录
     *
     * **/
    private SendRaceAnswerRecode getSendRaceAnswerRecode(RLMessageParameter param){

        List<SendRaceAnswerRecode> sendRaceAnswerRecodeList = sendRaceAnswerRecodeMapper.getSendRaceAnswerRecode(param);
        SendRaceAnswerRecode sendRaceAnswerRecode = null;
        if(sendRaceAnswerRecodeList.size()!=0){
            sendRaceAnswerRecode =  sendRaceAnswerRecodeList.get(0);
        }
        return sendRaceAnswerRecode;
    }

    /**
     * 保存抢答失败记录
     * */
    @Transactional
    public void saveNotGetRaceAnswerRecode(RLMessageParameter param){

        //查询发送抢答记录
        SendRaceAnswerRecode sendRaceAnswerRecode = getSendRaceAnswerRecode(param);

        RaceAnswerRecode raceAnswerRecode = new RaceAnswerRecode();
        raceAnswerRecode.setAppid(param.getAppId());
        raceAnswerRecode.setReceiver(param.getReceiver());
        raceAnswerRecode.setSender(param.getSender());
        raceAnswerRecode.setMsg(emojiConverter.toAlias(StringUtil.isEmpty(param.getMsg_content())?"":param.getMsg_content()));
        raceAnswerRecode.setIsGet("N");
        raceAnswerRecode.setIsBreak("N");
        raceAnswerRecode.setSend_race_answer_recode_time((sendRaceAnswerRecode!=null)?sendRaceAnswerRecode.getCreate_time():null);
        int num =  raceAnswerRecodeMapper.insertSelective(raceAnswerRecode);
        if(num!=1){
            logger.error("保存抢答失败记录失败");
        }
    }

    public int findIMRecodeNumByTiemScope(Date now, Date beforeDate, String user_name) {
        return imRecodeMapper.findIMRecodeByTiemScope(now,beforeDate,user_name);
    }

    //一键呼叫
    public Map<String,Object> aKeyToCall(RLMessageParameter param) {

        //筛选店员账户
        String receiver = getReceiver(param.getSite_id());

        if(StringUtil.isEmpty(receiver)){
            return ResultMap.errorResult("没有符合条件的店员");
        }
        param.setReceiver(receiver);

        int msg_id = saveSendRaceAnswerRecode(param);


        //将微信发起的抢答记录到聊天服务中，并返回聊天服务表的ID
        Integer imServiceId = saveImService(param.getSender(), IMParameter.CALLTYPE, param.getSite_id());
        param.setImServiceId(imServiceId);

        //json封装发送消息格式
        String msg = getMsg(param.getMsg_content(),msg_id,1,param.getMsg_type(), imServiceId,DateUtils.getCurrentTimeByType("yyyy-MM-dd HH:mm:ss"),null);
        param.setMsgContent(msg);

        //软删除该用户与店员建立的关系
        //chAnswerRelationMapper.delete(param.getSender());



        //发送消息
       /* Map<String,Object> paramMap = rLTXManager.getParam(param);
        Future<Map<String,Object>> future = rLTXManager.pushMsg(paramMap);
        return getFutureResult(future, receiver);*/
       return send(param);
    }

    //获取邀请码
    private String  getclerkInvitationCode(String sender,int siteId){


        if(StringUtil.isEmpty(sender)||sender.split("_").length!=3){
            logger.error("获取storeAdminID失败，sender:{}",sender);
            return "";
        }
        String storeAdminId = sender.split("_")[2];

        String invitationCode = storeAdminExtMapper.findClerkInvitationCode(siteId,storeAdminId);

        if(StringUtil.isEmpty(invitationCode)){
            logger.error("获取获取邀请码失败，storeAdminId:{},siteId:{}",storeAdminId,siteId);
        }

        return invitationCode;

    }


    public void saveImFaillog(RLMessageParameter param,Map<String,Object> result){
        saveImFaillog(param,StringUtil.isEmpty(result.get("errorMessage"))?"":result.get("errorMessage").toString());
    }


    public void saveImFaillog( RLMessageParameter param,String errorMessage){

        BIMFailLog log = new BIMFailLog();
        log.setSender(param.getSender());
        log.setReceiver(param.getReceiver());
        log.setMsg_id(param.getMsg_id());
        log.setMsg_type(param.getMsg_type());
        log.setImServiceId(param.getImServiceId());
        log.setSite_id(param.getSite_id());
        log.setMsgContent(emojiConverter.toAlias(StringUtil.isEmpty(param.getMsg_content())?"":param.getMsg_content()));
        log.setIsRace(param.getIsRace());
        log.setIsSystemMessage(param.getIsSystemMessage());
        log.setErrorMessage(errorMessage);
        bimFailLogMapper.insertLog(log);
    }

    public Map<String,Object> send(RLMessageParameter param){

      if(util.neteaseActive()){
          return neteaseService.sendMsg(param.getSender(),param.getReceiver(),param.getMsgContent());
      }
       return  rlSendMsg(param);
    }

    public Map<String,Object> rlSendMsg(RLMessageParameter param){
        //发送消息
        Map<String,Object> paramMap = rLTXManager.getParam(param);
        Future<Map<String,Object>> future = rLTXManager.pushMsg(paramMap);


        return getFutureResult(future, param.getReceiver());
    }



}
