package com.jk51.model.promotions.sequence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jk51.model.Goods;
import com.jk51.model.promotions.PromotionsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javen73 on 2018/5/8.
 */

/**
 * 活动与商品
 */
 public abstract class SequenceBlock {
    //活动
    @JsonIgnore
    public PromotionsActivity activity;


    public PromotionsActivity getActivity() {
        return activity;
    }

    public void setActivity(PromotionsActivity activity) {
        this.activity = activity;
    }

}

