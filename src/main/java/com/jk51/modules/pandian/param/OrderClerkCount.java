package com.jk51.modules.pandian.param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/20
 * 修改记录:
 */
public class OrderClerkCount {

    private Integer storeAdminId;
    private String name;
    private Double sumActual;
    private Long countInventoryed;
    private Long same;
    private Long more;
    private Long less;
    private boolean isConfirm;

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSumActual() {
        return sumActual;
    }

    public void setSumActual(Double sumActual) {
        this.sumActual = sumActual;
    }


    public Long getCountInventoryed() {
        return countInventoryed;
    }

    public void setCountInventoryed(Long countInventoryed) {
        this.countInventoryed = countInventoryed;
    }

    public Long getSame() {
        return same;
    }

    public void setSame(Long same) {
        this.same = same;
    }

    public Long getMore() {
        return more;
    }

    public void setMore(Long more) {
        this.more = more;
    }

    public Long getLess() {
        return less;
    }

    public void setLess(Long less) {
        this.less = less;
    }

    public boolean getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(boolean isConfirm) {
        this.isConfirm = isConfirm;
    }
}
