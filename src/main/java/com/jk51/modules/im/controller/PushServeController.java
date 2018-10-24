package com.jk51.modules.im.controller;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.gexin.rp.sdk.base.IQueryResult;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BMessageSender;
import com.jk51.model.order.Trades;
import com.jk51.modules.im.event.DelayedMessageProduce;
import com.jk51.modules.im.event.PaySuccessEvent;
import com.jk51.modules.im.service.InitialMessage;
import com.jk51.modules.im.service.PushServe;
import com.jk51.modules.im.service.PushServeService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.trades.service.TradesService;
import com.jk51.mq.mns.CloudQueueFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/push")
@RestController
public class PushServeController {
    private static Logger logger = LoggerFactory.getLogger(PushServeController.class);
    @Autowired
    private PushServeService pushServeService;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DelayedMessageProduce delayedMessageProduce;

    /**
     *给单个店员推送消息
     */
    @RequestMapping("/test")
    public Map<String, Object> pushMessageToSingle(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String messageType = request.getParameter("messageType");
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String tradesId = request.getParameter("tradesId");
        String oldStoreId = request.getParameter("oldStoreId");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String queueName = request.getParameter("queueName");

        try {
            if(PushType.ORDER_NEWORDER.getValue().equals(messageType)){
                Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradesId));
                applicationContext.publishEvent(new PaySuccessEvent(this, trades));
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date currentDate = new DateTime().toDate();

                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put("messageType", messageType);
                msgMap.put("siteId", siteId);
                msgMap.put("storeId", storeId);
                msgMap.put("storeAdminId", storeAdminId);

                if(!StringUtil.isEmpty(queueName)){
                    msgMap.put("queueName", queueName);
                }

                Date startTime = null;
                Date endTime = null;

                if(startTimeStr != null){
                    startTime = sdf.parse(startTimeStr);
                }
                if(endTimeStr != null){
                    endTime = sdf.parse(endTimeStr);
                }

                if(PushType.ORDER_NOTIFYSHIPMENT.getValue().equals(messageType)){
                    Trades trades = tradesService.getTradesByTradesId(Long.parseLong(tradesId));
                    msgMap.put("tradesId", tradesId);
                    msgMap.put("paySuccessTime", sdf.format(currentDate));
                    endTime = trades.getDelvTime();
                }

                delayedMessageProduce.delayedMessageProduce(msgMap, siteId, messageType, startTime, endTime);
            }
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }


    /**
     *消息首页 获取最新消息记录
     */
    @RequestMapping("/getLastMessages")
    public Map<String, Object> getLastMessages(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeAdminId)){
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        try {
            result = pushServeService.getLastMessages(siteId, storeId, storeAdminId);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    /**
     *获取店员指定类型消息列表
     */
    @RequestMapping("/getMessageList")
    public Map<String, Object> getMessageList(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String messageType = request.getParameter("messageType");
        int pageNum = StringUtil.isEmpty(request.getParameter("pageNum"))?1:Integer.parseInt(request.getParameter("pageNum"));
        int pageSize = StringUtil.isEmpty(request.getParameter("pageSize"))?15:Integer.parseInt(request.getParameter("pageSize"));

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeAdminId)){
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        try {
            result = pushServeService.getMessageList(siteId, storeId, storeAdminId, messageType, pageNum, pageSize);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    /**
     *删除店员指定类型消息列表
     */
    @RequestMapping("/delMessageList")
    public Map<String, Object> delMessageList(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String messageType = request.getParameter("messageType");

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeAdminId)){
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        try {
            pushServeService.delMessageList(siteId, storeId, storeAdminId, messageType);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    /**
     *消息设置为已读
     */
    @RequestMapping("/readMessage")
    public Map<String, Object> readMessage(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String id = request.getParameter("id");

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(id)){
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        try {
            result = pushServeService.readMessage(siteId, id);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    /**
     *店员某类型消息 全部设置为已读
     */
    @RequestMapping("/readMessageAll")
    public Map<String, Object> readMessageAll(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String storeAdminId = request.getParameter("storeAdminId");
        String messageType = request.getParameter("messageType");

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(storeId) || StringUtil.isEmpty(storeAdminId)){
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        try {
            result = pushServeService.readMessageAll(siteId, storeId, storeAdminId, messageType);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    /**
     *删除消息
     */
    @RequestMapping("/delMessage")
    public Map<String, Object> delMessage(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        String siteId = request.getParameter("siteId");
        String id = request.getParameter("id");

        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(id)){
            result.put("status", "ERROR");
            result.put("message", "缺少必要参数");
            return result;
        }

        try {
            result = pushServeService.delMessage(siteId, id);
            result.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", e.toString());
        }
        return result;
    }

    @RequestMapping("/copy")
    public Map<String, Object> copy(String siteId, String storeAdminId, Integer force) {
        Map<String, Object> result = pushServeService.copy(siteId, storeAdminId, force);
        return result;
    }

    @RequestMapping("/delTest")
    public Map<String, Object> delTest(String siteId, String storeAdminId, Integer force) {
        Map<String, Object> result = pushServeService.delTest(siteId, storeAdminId, force);
        return result;
    }

    @RequestMapping("/crTest")
    public Map<String, Object> crTest(String key, Integer cr) {
        Map<String, Object> result = pushServeService.crTest(key, cr);
        return result;
    }
}
