package com.jk51.model.promotions;

/**
 * Created by Administrator on 2017/11/21.
 */
public class EasyToSeeConstants {
    // 50-> 限价 60->拼团
    public static final Integer[] BETTER_ACTIVITY_ID= {60,50};
    //活动顺序 打折->20  满减->40  满赠->10 包邮->30
    public static final Integer[] PROMOTIONS_ACTIVITY_ID = {20,40,10,30};
    //优惠券顺序 限价->300  打折->200  满减->100 满赠->500
    public static final Integer[] COUPONS_ACTIVITY_ID = {300,200,100,500};
    //高级活动解析的类型
    public static final Integer[] BEST_ACTIVITY = {60,50,20,40,10,30};
    //普通活动解析的类型  没有类型60 拼团活动不具有普通活动
    public static final Integer[] ORDINARY_ACTIVITY = {50,20,40,10,30};
    //搜索商品 最大标签数
    public static final Integer SEARCH_GOODS=3;
    //商品详情 最大标签数
    public static final Integer GOODS_DETAIL=5;

}
