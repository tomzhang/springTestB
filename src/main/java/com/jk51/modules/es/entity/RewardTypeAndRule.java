package com.jk51.modules.es.entity;

/**
 * 保存优惠券和活动的类型和规则
 *
 * @auhter zy
 * @create 2017-09-06 18:12
 */
public class RewardTypeAndRule<T> {

    private String rewardType; //奖励类型

    private T obj;

    public RewardTypeAndRule(String rewardType, T obj) {
            this.rewardType = rewardType;
            this.obj = obj;
    }

    public RewardTypeAndRule() {
        super();
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
