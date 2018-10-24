package com.jk51.model.promotions.sequence;

import com.jk51.model.promotions.sequence.app.AppSequenceBlock;

import java.util.List;

/**
 * Created by javen73 on 2018/5/8.
 */
public interface SequenceHandler {

    /**
     * 商品排序器
     * 具体的商品排序实现
     */
    void sequence(SequenceInterface sequence) throws Exception;


}
