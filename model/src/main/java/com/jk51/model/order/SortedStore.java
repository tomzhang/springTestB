package com.jk51.model.order;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-03-26
 * 修改记录:
 */
public class SortedStore implements Comparable{

    private Double distance = 0d;

    private Store store;


    @Override
    public int compareTo(Object t) {
        if(!(t instanceof SortedStore)){
            throw new RuntimeException("比较门店距离失败，对象类型不符合");
        }
        SortedStore target = (SortedStore) t;
        if(target.getDistance() < this.distance){
            return 1;
        }else if(target.getDistance() > this.distance){
            return -1;
        }
        return 0;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
