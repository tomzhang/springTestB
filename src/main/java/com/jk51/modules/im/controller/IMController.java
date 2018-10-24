package com.jk51.modules.im.controller;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.configuration.SessionConfig;
import com.jk51.model.BIMFailLog;
import com.jk51.model.ChAnswerRelation;
import com.jk51.model.order.Orders;
import com.jk51.model.order.Trades;
import com.jk51.modules.appInterface.service.AppGoodsService;
import com.jk51.modules.appInterface.util.ReturnMap;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.im.controller.request.CreateAccidParam;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.mapper.ChAnswerRelationMapper;
import com.jk51.modules.im.netease.Util;
import com.jk51.modules.im.netease.service.NeteaseService;
import com.jk51.modules.im.service.IMExpireRedisKeyService;
import com.jk51.modules.im.service.IMService;
import com.jk51.modules.im.util.MsgType;
import com.jk51.modules.im.util.RLMessageParameter;
import com.jk51.modules.trades.mapper.TradesMapper;
import com.jk51.modules.trades.service.TradesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 * 聊天
 */
@Controller
@ResponseBody
@RequestMapping("/im")
public class IMController {


    @Autowired
    private IMService imService;
    @Autowired
    private IMExpireRedisKeyService iMExpireRedisKeyService;
    @Autowired
    private ChAnswerRelationMapper chAnswerRelationMapper;
    @Autowired
    private BIMServiceMapper bimServiceMapper;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private NeteaseService neteaseService;
    @Autowired
    GoodsMapper goodsMapper;

    private static final Logger logger = LoggerFactory.getLogger(IMController.class);


    /**
     *客户发起的聊天请求，如果请求中带receiver，直接发送消息到对应的receiver，如果请求中不带receiver，走抢答的流程
     *如果请求中不带receiver,添加返回参数 isAnswerDevices = true ，供客户端判断发送的消息是抢答类型
     *
     * return  receiverNum 发送给抢答店员的数量
     * return  isAnswerDevices = true 为抢答信息
     *
     *
     * param appid
     * param sender  客户账户
     * param receiver 店员账户
     * param site_id  商家ID
     * param msgType 消息类型
     * param msgContent 消息内容
     * */
    @RequestMapping("/advisor")
    public Map<String,Object> advisor(@Valid RLMessageParameter param){

        Map<String,Object> result = new HashMap<String,Object>();

        //替换空格换行特殊字符
        String msg_content = param.getMsg_content().replaceAll("&nbsp;","\\\t").replaceAll("<br>","\\\n").replaceAll("<div>", "").replaceAll("</div>", "");
        param.setMsg_content(msg_content);


        //图片信息，不抢答
        if(param.getMsg_type()== MsgType.IMAGE.getIndex()&&!checkRelation(param.getReceiver(),param.getSender())){
            result.put("status","ERROR");
            result.put("errorMessage","请建立聊天关系后再发送图片或语音");
            imService.saveImFaillog(param,"请建立聊天关系后再发送图片或语音");
            return result;
        }


        //发送的消息为商品或订单消息时，查询对应的信息替换ID
        updateMsgContent2GoosInfoOr2Tradesdetial(param);


        if(isAnswerDevices(param.getReceiver(),param.getSender())){

            //抢答流程
            result = imService.answerDevices(param);
            result.put("isAnswerDevices","true");
        }else{

            //直接发送消息到对应的receiver
            result = imService.sendMsg(param);

        }


        //消息发送成功后删除和添加过期事件(抢答消息不添加过期键)
        if(result.get("status").equals("OK")){

            //删除会员过期
            iMExpireRedisKeyService.delExpireForMember(param);

            if((result.get("isAnswerDevices")== null)){
                //添加店员过期提醒键
                iMExpireRedisKeyService.addExpireForClerkTimeoutRemind(param);

                //添加店员过期键
                iMExpireRedisKeyService.addExpireForClerkTimeout(param);
            }


        }else{

            //记录发送消息失败记录
            imService.saveImFaillog(param,result);
        }

        return result;
    }



    private void updateMsgContent2GoosInfoOr2Tradesdetial(RLMessageParameter param){

        if(param.getMsg_type().equals(MsgType.SEND_GOODS.getIndex())){
            updateMsgContent2GoosInfo(param);
        }

        if(param.getMsg_type().equals(MsgType.SEND_TRADES.getIndex())){
            updateMsgContent2Tradesdetial(param);
        }

    }


    //消息类型为发送商品消息时，查询商品信息，并替换聊天消息中商品Id
    private void updateMsgContent2GoosInfo(RLMessageParameter param){

       Map<String,Object> goodsInfo = goodsMapper.queryGoodsDetailByGoodId(Integer.valueOf(param.getMsg_content()), param.getSite_id());

       try{

           param.setMsg_content(JacksonUtils.mapToJson(shortGoodsInfo(goodsInfo)));
       }catch (Exception e){
           logger.error("JackSon处理异常，异常信息：{},参数：{}",e,param);
       }

    }

    //消息类型为发送订单消息时，查询订单信息，并替换聊天消息中订单Id
    private void updateMsgContent2Tradesdetial(RLMessageParameter param){


        try {
            Trades trades  = tradesService.getTradesDetial(Long.valueOf(param.getMsg_content()));
            param.setMsg_content(JacksonUtils.mapToJson(shortTradesinfo(trades)));

        } catch (Exception e) {
            logger.error("查询订单异常，异常信息：{},参数：{}",e,param);
        }
    }

    //app只需要返回结果的一部分参数
    private Map<String,Object> shortTradesinfo(Trades trades) {

        Map<String,Object> info = new HashMap<String,Object>();
        info.put("createTime",trades.getCreateTime());
        //info.put("ordersList",trades.getOrdersList());
        info.put("tradesId",StringUtil.isEmpty(trades.getOrdersList().get(0))?"":Long.toString(trades.getOrdersList().get(0).getTradesId()));
        info.put("postStyle",trades.getPostStyle());
        info.put("tradesStatus",trades.getTradesStatus());
        info.put("realPay",trades.getRealPay());
        info.put("storeName",StringUtil.isEmpty(trades.getStore())?"":trades.getStore().getName());
        info.put("hash",StringUtil.isEmpty(trades.getOrdersList().get(0))?"":trades.getOrdersList().get(0).getHash());
        info.put("goodsNum",getGoodsNum(trades.getOrdersList()));
        info.put("goods_title",StringUtil.isEmpty(trades.getOrdersList().get(0))?"":trades.getOrdersList().get(0).getGoodsTitle());
        return info;
    }


    //查询订单中的商品数量
    private int getGoodsNum(List<Orders> ordersList){

        return ordersList.stream().map((order)->order.getGoodsNum()).reduce(0,(a,b)->a+b);
    }

    //app只需要返回结果的一部分参数
    private Map<String,Object> shortGoodsInfo(Map<String,Object> goodsInfo){

        Map<String,Object> info = new HashMap<String,Object>();
        info.put("drugName",goodsInfo.get("drugName"));
        info.put("shopPrice",goodsInfo.get("shopPrice"));
        info.put("specifCation",goodsInfo.get("specifCation"));
        info.put("defUrl",goodsInfo.get("defUrl"));
        info.put("goodsId",goodsInfo.get("goods_id"));
        return info;

    }

    //判断是否为抢答
    private boolean isAnswerDevices(String receiver,String sender){
        if(StringUtil.isEmpty(receiver)){
            return true;
        }

        if(!checkRelation(receiver,sender)){
            return true;
        }

        return false;
    }


    //判断receiver与sender是否为绑定的关系
    private boolean checkRelation(String receiver, String sender) {

        //查询是否为绑定关系
        ChAnswerRelation chAnswerRelation =
                chAnswerRelationMapper.findByUserOpenIdAndharmacistUserid(sender,receiver);
        if(StringUtil.isEmpty(chAnswerRelation)){
            return false;
        }else{
            return true;
        }

    }

    /**
     *店员抢答
     * */
    @RequestMapping("/raceFirstAnswer")
    public Map<String,Object> raceFirstAnswer(@RequestBody @Valid RLMessageParameter param){

        return imService.raceFirstAnswer(param);

    }



    /**
     *店员回复
     *
     * */
    @RequestMapping("/answer")
    public Map<String,Object> answer(@Valid RLMessageParameter param){


        //判断店员和会员绑定是否有建立绑定关系
        if(!checkRelation(param.getSender(),param.getReceiver())){
            return ReturnMap.buildFailReturnMap("没有建立绑定关系");
        }

        //记录b_im_service第一次回复时间（first_reply_time）
        bimServiceMapper.updateFirstReplyTimeIFFirstReplyTimeISNULL(param.getImServiceId());

        //删除店员过期，店员过期提醒键
        iMExpireRedisKeyService.delExpireForClerkTimeOutKey(param);

        //会员过期评价键
        iMExpireRedisKeyService.addExpireForMemberTimeoutEvaluate(param);

        //会员过期断开关系键
        iMExpireRedisKeyService.addExpireForMemberTimeoutOverConversation(param);

        Map<String,Object> result =  imService.sendMsg(param);


        if(!result.get("status").equals("OK")){
            //记录发送消息失败记录
            imService.saveImFaillog(param,result);
        }

        return  result;

    }

    /**
     *一键呼叫
     * 软删除该用户与店员建立的关系
     * */
    @RequestMapping("/call")
    public Map<String,Object> call(@Valid RLMessageParameter param){


        //添加一键呼叫的redi键过期事件,处理超时未回复
        iMExpireRedisKeyService.addExpireForCall(param);

        Map<String,Object> result = imService.aKeyToCall(param);

        if(!result.get("status").equals("OK")){
            //记录发送消息失败记录
            imService.saveImFaillog(param,result);
        }

        return result;

    }

    /**
     *一键呼叫抢答
     *
     * */
    @RequestMapping("/reachFirstCall")
    public Map<String,Object> reachFirstCall(@RequestBody @Valid RLMessageParameter param){

        //删除一键呼叫的redi键
        iMExpireRedisKeyService.delExpireForCall(param);

        return imService.raceFirstAnswer(param);
    }

    /**
     * 查询网易是否有效，如果有效返回对应的accid、token
     */
    @RequestMapping("createAccid")
    public Result createAccid(@RequestBody CreateAccidParam param){

        try{
            return neteaseService.createAccid(param);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }

    }

}
