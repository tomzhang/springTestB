package com.jk51.modules.grouppurchase.response;


import com.jk51.model.order.GoodsInfo;

import java.util.List;

/**
 * Created by mqq on 2017/11/23.
 */
public class GroupPurchaseResponseForBeforeQuery {
    private Integer groupPurchasestatus; //拼团状态判定能否参与拼团

    private Integer getGroupProActivityStatus;//拼团活动状态

    private Integer resultStatus;//判定能否参与

    private Integer groupPrice;//拼团对应价格

    private List<GoodsInfo> listGoodsInfo;

    public Integer getGetGroupProActivityStatus () {
        return getGroupProActivityStatus;
    }

    public Integer getGroupPrice () {
        return groupPrice;
    }

    public Integer getGroupPurchasestatus () {
        return groupPurchasestatus;
    }

    public Integer getResultStatus () {
        return resultStatus;
    }

    public void setGetGroupProActivityStatus (Integer getGroupProActivityStatus) {
        this.getGroupProActivityStatus = getGroupProActivityStatus;
    }

    public void setGroupPrice (Integer groupPrice) {
        this.groupPrice = groupPrice;
    }

    public void setGroupPurchasestatus (Integer groupPurchasestatus) {
        this.groupPurchasestatus = groupPurchasestatus;
    }

    public void setResultStatus (Integer resultStatus) {
        this.resultStatus = resultStatus;
    }

    public List<GoodsInfo> getListGoodsInfo () {
        return listGoodsInfo;
    }

    public void setListGoodsInfo (List<GoodsInfo> listGoodsInfo) {
        this.listGoodsInfo = listGoodsInfo;
    }
}
