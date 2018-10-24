package com.jk51.modules.pandian.job;

import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:盘点插入
 * 作者: dumingliang
 * 创建日期: 2018-05-18
 * 修改记录:
 */
@Component
@RunMsgWorker(queueName = PandianErpInsertMessage.QUEUE_NAME)
public class PandianErpInsertMessage implements MessageWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PandianErpInsertMessage.class);

    public static final String QUEUE_NAME = "PandianErpInsertQueue";

    @Autowired
    private OfflineCheckService offlineCheckService;

    @Override
    public void consume(Message message) throws Exception {
        try {
            LOGGER.info("获取盘点信息回传!, {}", message);
            String messageBodyAsString = message.getMessageBodyAsString();
            Map<String, Object> insertObj = JacksonUtils.json2map(messageBodyAsString);
            LOGGER.info("信息回传:{}", insertObj.toString());
            int siteId = Integer.parseInt(insertObj.get("siteId").toString());
            String uid = insertObj.get("uid").toString();
            String pandianNum = insertObj.get("pandianNum").toString();
            offlineCheckService.updateOfflinetqty(siteId, pandianNum, uid, null);
        } catch (Exception e) {
            LOGGER.info("class[PandianErpInsertMessage],消费盘点信息回传问题:{}", e);
        }
    }
}
