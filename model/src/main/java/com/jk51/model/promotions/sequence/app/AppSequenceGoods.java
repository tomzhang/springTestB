package com.jk51.model.promotions.sequence.app;

import com.jk51.model.promotions.sequence.SequenceGoods;

import java.util.Map;

/**
 * Created by javen73 on 2018/5/9.
 */
public class AppSequenceGoods extends SequenceGoods{

    private String tag;
    private String qrURL;
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getQrURL() {
        return qrURL;
    }

    public void setQrURL(String qrURL) {
        this.qrURL = qrURL;
    }

    public AppSequenceGoods(Map goods, Integer sequence) {
        super(goods, sequence);
    }
}
