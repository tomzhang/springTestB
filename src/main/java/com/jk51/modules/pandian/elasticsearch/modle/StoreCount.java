package com.jk51.modules.pandian.elasticsearch.modle;


import com.jk51.commons.string.StringUtil;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/6/6
 * 修改记录:
 */

public class StoreCount {

    private Double sumInventory;
    private Double sumActual;
    private Integer storeId;
    private long countInventoryed;
    private long totalGoods;
    private long checkerNum;
    private Long planCheckerNum;
    private Double profitAndLossNum;

    public long getNotInventoryed(){

        return totalGoods - countInventoryed;
    }

    public long getCountInventoryed() {
        return countInventoryed;
    }

    public void setCountInventoryed(long countInventoryed) {
        this.countInventoryed = countInventoryed;
    }

    public long getTotalGoods() {
        return totalGoods;
    }

    public void setTotalGoods(long totalGoods) {
        this.totalGoods = totalGoods;
    }

    public long getCheckerNum() {
        return checkerNum;
    }

    public void setCheckerNum(long checkerNum) {
        this.checkerNum = checkerNum;
    }

    public String getProfitAndLossStatus(){

        Double loss = getProfitAndLossNum();
        if(StringUtil.isEmpty(loss)){
            return "未开始盘点";
        }

        int i = loss.compareTo(0D);

        String result = "";
        switch (i) {
            case 0:
                result = "盘平";
                break;
            case 1:
                result = "盘盈";
                break;
            case -1:
                result = "盘亏";
                break;
        }

        return result;
    }

    /*public Double getProfitAndLossNum(){

        if(StringUtil.isEmpty(sumActual) || sumActual==0){

            return null;
        }

        if(StringUtil.isEmpty(sumInventory)){
           return sumActual - 0;
        }

        return new BigDecimal(sumActual -sumInventory).setScale(4,RoundingMode.HALF_UP).doubleValue();
    }*/

    public Double getSumInventory() {
        return sumInventory;
    }

    public void setSumInventory(Double sumInventory) {
        this.sumInventory = sumInventory;
    }

    public Double getSumActual() {

        return sumActual;
    }

    public void setSumActual(Double sumActual) {
        this.sumActual = sumActual;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Long getPlanCheckerNum() {
        return planCheckerNum;
    }

    public void setPlanCheckerNum(Long planCheckerNum) {
        this.planCheckerNum = planCheckerNum;
    }

    public void setProfitAndLossNum(Double profitAndLossNum) {
        this.profitAndLossNum = profitAndLossNum;
    }

    public Double getProfitAndLossNum() {
        return profitAndLossNum;
    }
}
