package com.jk51.modules.im.service;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.collect.Lists;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.modules.appInterface.mapper.BMessageSenderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jk51.modules.im.util.PushConstant.ALL;
import static com.jk51.modules.im.util.PushConstant.DEFAULT;
import static com.jk51.modules.im.util.PushConstant.DONT_SAVE_OFFLINE;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/10/11
 * 修改记录:
 */
@Service
public class MultiThreadPushMessage {

    private  Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    @Qualifier("storeHelpPush")
    private PushServe storeHelpPush;
    @Resource
    @Qualifier("storeXiaoWuPush")
    private PushServe storeXiaoWuPush;
    @Autowired
    private BMessageSenderMapper bMessageSenderMapper;
    @Autowired
    private PushServeService pushServeService;


    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void pushMessageToAll(String title,String text,String msgType){

        Map<String,String> content = new HashMap<>();
        content.put("messageType",msgType);

        send(title, text, content, storeHelpPush);
        send(title, text, content, storeXiaoWuPush);

    }

    public void  send(String title,String text, Map<String,String> content, PushServe pushServe) {
        List<Target> targets = getAllTargets(pushServe);
        TransmissionTemplate template = pushServeService.getTransmissionTemplateAPNS(title, text, JacksonUtils.mapToJson(content), pushServe.getAppId(), pushServe.getAppKey(), DEFAULT, DEFAULT);//设置透传模板
        ListMessage message = pushServeService.getListMessage(template,DONT_SAVE_OFFLINE, ALL);//设置列表推送消息的消息体

        String taskId = pushServe.getContentId(message);// taskId用于在推送时去查找对应的message

        if(StringUtil.isEmpty(targets)){
            return;
        }

        List<List<Target>> relist = Lists.partition(targets,50);

        for(List<Target> list:relist){
            executorService.execute(()->{

                IPushResult ret = null;//发送消息
                try {
                    ret = pushServe.pushMessageToList(taskId, list);
                } catch (RequestException e) {
                    logger.error("pushMessageToAll",ExceptionUtil.exceptionDetail(e));
                }
                if (ret != null) {
                    logger.info("pushMessageToAll 推送结果：{}",ret.getResponse());
                }

                logger.info("pushMessageToAll targets:{},title:{},text:{},content:{}",getClientId(list),title,text,content);
            });
        }
    }



    private List<Target> getAllTargets(PushServe pushServe) {
        List<Target> result = new ArrayList<>();
        List<String> clientIds = bMessageSenderMapper.findAllClientId();
        for(String cid:clientIds){
            Target target = new Target();
            target.setAppId(pushServe.getAppId());
            target.setClientId(cid);
            result.add(target);
        }

        return result;
    }

    private List<String> getClientId(List<Target> targets){
        List<String> result = new ArrayList<>();
        for(Target t:targets){
            result.add(t.getClientId());
        }
        return result;
    }
}
