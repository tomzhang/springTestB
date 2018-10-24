package com.jk51.model.promotions.sequence;

import com.jk51.model.promotions.sequence.app.AppSequenceBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javen73 on 2018/5/8.
 */

/**
 * 返回结果
 */
public abstract class SequenceResult {
    //商品和活动信息块=》有序
    public List<AppSequenceBlock> block = new ArrayList<>();

}
