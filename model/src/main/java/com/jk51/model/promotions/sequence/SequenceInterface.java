package com.jk51.model.promotions.sequence;

import java.util.List;

/**
 * Created by javen73 on 2018/5/8.
 */
public interface SequenceInterface<T> {

    /**
     * 收集活动
     */
    T collection() throws Exception;

    /**
     * 将现有活动所参加的商品进行处理
     */
    T processGoods() throws Exception;

    /**
     * 处理商品进行排序
     * @return
     */
    T processSequence(SequenceHandler handler) throws Exception;
}
