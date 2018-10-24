package com.jk51.modules.merchant.Timer;

import com.aliyun.mns.model.Message;
import com.jk51.modules.balance.service.BalanceService;
import com.jk51.modules.merchant.service.TimerInsertService;
import com.jk51.modules.tpl.service.EleService;
import com.jk51.mq.mns.MessageWorker;
import com.jk51.mq.mns.RunMsgWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/19.
 */
@Component
@RunMsgWorker(queueName = "DataProfileEvent")
public class DataTimerSynce implements MessageWorker {

    @Autowired
    private TimerInsertService timerInsertService;
    @Autowired
    private EleService eleService;
    @Autowired
    private BalanceService balanceService;
    private static Logger logger = LoggerFactory.getLogger(DataTimerSynce.class);
    public static final String DateProfile= "DataProfileEvent";

    @Override
    public void consume(Message message){
        logger.info("===数据统计定时任务===");
        // 处理消息
        String content = message.getMessageBodyAsString();
        logger.info("{} {} {} {}",message.getReceiptHandle(),content, message.getMessageId(), message.getMessageId());

        //获取时间(昨天时间)
        Map<String, Object> formatMap = formatDayTime();
        String endTime = formatMap.get("date").toString();
        try {
            timerInsertService.dataInsert(endTime);//数据概况
            timerInsertService.dataInsertFlow(endTime);//流量分析
            timerInsertService.dataInsertTransaction(endTime);//交易分析
            timerInsertService.updateStruts(endTime);//修改数据状态（区分今天数据与今天之前的数据）
            timerInsertService.updUserInvitecode();//处理用户邀请码

            eleService.timerAutoQueryo2o();//蜂鸟订单状态

//            balanceService.balanceTiming();//执行昨天的余额结算

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
    //时间格式转换(日期)
    public Map<String,Object> formatDayTime(){
        Map<String,Object> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date d = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(d);
        String dateCount = date+" 00:00:00";
        map.put("date",date);
        map.put("dateCount",dateCount);
        return map;
    }

}
