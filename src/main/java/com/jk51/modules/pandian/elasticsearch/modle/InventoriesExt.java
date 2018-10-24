package com.jk51.modules.pandian.elasticsearch.modle;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/28
 * 修改记录:
 */
public class InventoriesExt {

    private String checkerName;
    private Integer plan_stock_show;
    private Integer goodsId;

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public Integer getPlan_stock_show() {
        return plan_stock_show;
    }

    public void setPlan_stock_show(Integer plan_stock_show) {
        this.plan_stock_show = plan_stock_show;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
}
