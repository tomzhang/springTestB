package com.jk51.model.concession.result;

import com.jk51.model.concession.ConcessionDesc;
import com.jk51.model.concession.GiftMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ztq on 2018/1/6
 * Description:
 */
public class GiftResult {
    /**
     * 单个列表的最大赠送数量
     */
    private Integer maxSendNum = 0;

    private ConcessionDesc concessionDesc;

    private List<GiftMsg> giftList = new ArrayList<>();


    /* -- setter & getter -- */

    public Integer getMaxSendNum() {
        return maxSendNum;
    }

    public void setMaxSendNum(Integer maxSendNum) {
        this.maxSendNum = maxSendNum;
    }

    public ConcessionDesc getConcessionDesc() {
        return concessionDesc;
    }

    public void setConcessionDesc(ConcessionDesc concessionDesc) {
        this.concessionDesc = concessionDesc;
    }

    public List<GiftMsg> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<GiftMsg> giftList) {
        this.giftList = giftList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftResult)) return false;
        GiftResult that = (GiftResult) o;
        return Objects.equals(getMaxSendNum(), that.getMaxSendNum()) &&
            Objects.equals(getConcessionDesc(), that.getConcessionDesc()) &&
            Objects.equals(getGiftList(), that.getGiftList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaxSendNum(), getConcessionDesc(), getGiftList());
    }

    @Override
    public String toString() {
        return "GiftResult{" +
            "maxSendNum=" + maxSendNum +
            ", concessionDesc=" + concessionDesc +
            ", giftList=" + giftList +
            '}';
    }
}
