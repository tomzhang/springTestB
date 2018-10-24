package com.jk51.model.promotions.sequence.app;

import com.jk51.model.promotions.sequence.SequenceBlock;

/**
 * Created by javen73 on 2018/5/9.
 */
public class AppSequenceBlock extends SequenceBlock{

    public AppSequenceGoods sequenceGoods;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppSequenceBlock that = (AppSequenceBlock) o;

        return sequenceGoods != null ? sequenceGoods.equals(that.sequenceGoods) : that.sequenceGoods == null;
    }

    @Override
    public int hashCode() {
        return sequenceGoods != null ? sequenceGoods.hashCode() : 0;
    }
}
