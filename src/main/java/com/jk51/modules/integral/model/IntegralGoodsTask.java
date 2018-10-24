package com.jk51.modules.integral.model;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-06-01 11:26
 * 修改记录:
 */
public class IntegralGoodsTask {

    /**
     * 商品id组
     */
    private String goodsIds;

    /**
     * 间隔时间
     */
    private Integer timeInterval;

    /**
     * 数据库当时的时间
     */
    private Date databaseTime;

    public IntegralGoodsTask() {
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Date getDatabaseTime() {
        return databaseTime;
    }

    public void setDatabaseTime(Date databaseTime) {
        this.databaseTime = databaseTime;
    }
}
