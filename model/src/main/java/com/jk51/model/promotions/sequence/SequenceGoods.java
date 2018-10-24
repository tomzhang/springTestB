package com.jk51.model.promotions.sequence;

/**
 * Created by Administrator on 2018/5/9.
 */

import com.jk51.model.Goods;

import java.util.Map;

/**
 * 排序商品
 */
public abstract class SequenceGoods implements Comparable<SequenceGoods>{
    //商品
    private Map goods;
    //排序 序号  越小优先级越高，默认最大值
    private Integer sequence = Integer.MAX_VALUE;

    public Map getGoods() {
        return goods;
    }

    public void setGoods(Map goods) {
        this.goods = goods;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public int compareTo(SequenceGoods sg) {
        return this.sequence - sg.getSequence();
    }


    public SequenceGoods(Map goods, Integer sequence) {
        this.goods = goods;
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SequenceGoods that = (SequenceGoods) o;

        return goods != null ? goods.equals(that.goods) : that.goods == null;
    }

    @Override
    public int hashCode() {
        return goods != null ? goods.hashCode() : 0;
    }
}
