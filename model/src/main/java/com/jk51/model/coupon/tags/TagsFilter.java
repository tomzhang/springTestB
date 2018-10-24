package com.jk51.model.coupon.tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javen73 on 2018/3/23.
 */
public interface TagsFilter<T> {



    /**
     * 收集优惠券或活动
     */
    T collection() throws Exception;

    /**
     * 对收集的数据进行分组
     */
    T grouping()throws Exception;

    /**
     * 对优惠券或活动进行规则标签解析
     */
    T resolve()throws Exception;

}
