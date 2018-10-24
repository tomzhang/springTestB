package com.jk51.model.promotions.sequence.wechat;

import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.model.promotions.sequence.app.AppSequenceBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javen73 on 2018/5/12.
 */
public class WechatSequenceResult extends SequenceResult{
    public WechatSequenceBlock endBlock = new WechatSequenceBlock();
    public WechatSequenceBlock startBlock = new WechatSequenceBlock();
    public List<WechatSequenceBlock> block = new ArrayList<>();
}
