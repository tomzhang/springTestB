package com.jk51.modules.pandian.service;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.pandian.dto.PandianTimeDto;
import com.jk51.modules.pandian.job.PandianTimeRecoderJob;
import com.jk51.modules.pandian.mapper.PandianQueryTimeRecordMapper;
import com.jk51.modules.pandian.param.InventoryParam;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018-03-30
 * 修改记录:
 */
@Service
public class PandianTimeService {

    private Logger logger = LoggerFactory.getLogger(PandianTimeService.class);

    @Autowired
    private PandianQueryTimeRecordMapper mapper;
    /**
     * 保存盘各步骤的时间点
     *
     * */
    public void insert(PandianTimeDto timeDto) {

        mapper.insertRecord(timeDto);

    }


    public void sendMessageToMQ(InventoryParam param) throws Exception {

        CloudQueue queue = CloudQueueFactory.create(PandianTimeRecoderJob.MQ_TOPIC_PANDIAN_TIME);

        PandianTimeDto pandianTimeDto = new PandianTimeDto.Builder()
            .mobileType(param.getMobile_type())
            .clickScanerTime(param.getClick_scaner_date())
            .sendRequestTime(param.getSend_request_date())
            .getRequestTime(param.getGet_request_time())
            .responseTime(param.getResponse_time())
            .build();

        Message message = new Message();
        message.setMessageBody(JacksonUtils.obj2json(pandianTimeDto).getBytes());
        try {
            Message result = queue.putMessage(message);

        }catch (Exception e){
            logger.debug("发送到盘点时间消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }
    }
}
