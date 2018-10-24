package com.jk51.modules.integral.timingtask.thread;

import com.jk51.modules.goods.mapper.BIntegralGoodsMapper;
import com.jk51.modules.integral.model.IntegralGoodsTask;
import com.jk51.modules.integral.timingtask.IntegralGoodsTimingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-06-01 14:32
 * 修改记录:
 */
public class IntegralGoodsTimingOpenThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(IntegralGoodsTimingService.class);

    private BIntegralGoodsMapper integralGoodsMapper;

    //开启任务类型
    private final static Integer OPEN_TYPE = 0;

    //关闭任务类型
    private final static Integer CLOSE_TYPE = 1;

    private IntegralGoodsTask task;

    private int type;

    public IntegralGoodsTimingOpenThread(IntegralGoodsTask task,int type,BIntegralGoodsMapper integralGoodsMapper) {
        this.task = task;
        this.type = type;
        this.integralGoodsMapper = integralGoodsMapper;
    }

    @Override
    public void run() {

        try {
            if(task.getTimeInterval() > 0){
                LOGGER.info("积分商品定时任务执行休眠开始,商品id组:" + task.getGoodsIds()+",  time:" + new Date());
                Thread.sleep(task.getTimeInterval() * 1000);
                LOGGER.info("积分商品定时任务执行休眠结束,商品id组:" + task.getGoodsIds()+",  time:" + new Date());
            }

            if(OPEN_TYPE == type){
                integralGoodsMapper.updateIntegralGoodsStartStatus(getGoodsIdList(task.getGoodsIds()));
            }else if(CLOSE_TYPE == type){
                integralGoodsMapper.updateIntegralGoodsEndStatus(getGoodsIdList(task.getGoodsIds()));
            }else {
                return;
            }

            LOGGER.info("积分商品定时任务执行成功，类型:" + type + ",商品id组:" + task.getGoodsIds() + " ,时间差:" + task.getTimeInterval() + ",查出时的数据库时间:" + task.getDatabaseTime());
        } catch (Exception e) {
            LOGGER.error("积分商品定时任务执行失败，类型:" + type + ",商品id组:" + task.getGoodsIds() + " ,时间差:" + task.getTimeInterval() + ",查出时的数据库时间:" + task.getDatabaseTime(),e);
        }

    }

    public List<String> getGoodsIdList(String goodsIds) {

        if(goodsIds != null){
            return Arrays.asList(goodsIds.split(","));
        }

        return new LinkedList<String>();

    }

}
