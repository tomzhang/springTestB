package com.jk51.model.concession;

/**
 * Created by ztq on 2017/12/20
 * Description: 优惠装饰类，抽取过滤和计算方法
 * important!!!! 该类下的优惠Concession解释为：优惠券Coupon 和 活动Promotions
 */
public interface ConcessionDecorator {
    /**
     * 过滤该优惠能否使用
     * @return
     */
    boolean filter();

    /**
     * 计算优惠了什么，包括优惠了多少金额或赠送了哪些礼物
     */
    void concession();
}
