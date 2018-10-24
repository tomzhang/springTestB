package com.jk51.model.erp;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-05-08
 * 修改记录:
 */
public class PanDianHead {

    private Integer siteId;
    private String panDianNum;
    private long goodsNum;//需要盘点的商品总数

    List<StoreOrderNum> storeOrderNums;//任务序号

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPanDianNum() {
        return panDianNum;
    }

    public void setPanDianNum(String panDianNum) {
        this.panDianNum = panDianNum;
    }

    public long getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(long goodsNum) {
        this.goodsNum = goodsNum;
    }

    public List<StoreOrderNum> getStoreOrderNums() {
        return storeOrderNums;
    }

    public void setStoreOrderNums(List<StoreOrderNum> storeOrderNums) {
        this.storeOrderNums = storeOrderNums;
    }

    @Override
    public String toString() {
        return "PanDianHead{" +
            "siteId=" + siteId +
            ", panDianNum='" + panDianNum + '\'' +
            ", goodsNum=" + goodsNum +
            ", storeOrderNums=" + storeOrderNums +
            '}';
    }
}
