package com.jk51.modules.pandian.dto;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
public class NotHasRemindCountRate {

    private Integer nextChecked;
    private Integer nextNotInInventory;
    private Integer nextNotInRedis;
    private Integer currentNotInRedis;
    private Integer hasRemind;

    public Integer getNextChecked() {
        return nextChecked;
    }

    public void setNextChecked(Integer nextChecked) {
        this.nextChecked = nextChecked;
    }

    public Integer getNextNotInInventory() {
        return nextNotInInventory;
    }

    public void setNextNotInInventory(Integer nextNotInInventory) {
        this.nextNotInInventory = nextNotInInventory;
    }

    public Integer getNextNotInRedis() {
        return nextNotInRedis;
    }

    public void setNextNotInRedis(Integer nextNotInRedis) {
        this.nextNotInRedis = nextNotInRedis;
    }

    public Integer getCurrentNotInRedis() {
        return currentNotInRedis;
    }

    public void setCurrentNotInRedis(Integer currentNotInRedis) {
        this.currentNotInRedis = currentNotInRedis;
    }

    public Integer getHasRemind() {
        return hasRemind;
    }

    public void setHasRemind(Integer hasRemind) {
        this.hasRemind = hasRemind;
    }
}
