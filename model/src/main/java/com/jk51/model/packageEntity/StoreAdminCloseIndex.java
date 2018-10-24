package com.jk51.model.packageEntity;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-20
 * 修改记录:
 *
 * 店员与用户亲密度类
 */
public class StoreAdminCloseIndex {

    private int site_id;
    private int storeadmin_id;
    private String storeadmin_user_name;
    private String customer_user_name;
    private int times;
    private double index;
    private int orderTimes;
    private double orderNumIndex;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreAdminCloseIndex that = (StoreAdminCloseIndex) o;

        if (site_id != that.site_id) return false;
        if (storeadmin_id != that.storeadmin_id) return false;
        if (!storeadmin_user_name.equals(that.storeadmin_user_name)) return false;
        return customer_user_name.equals(that.customer_user_name);
    }

    @Override
    public int hashCode() {
        int result = site_id;
        result = 31 * result + storeadmin_id;
        result = 31 * result + storeadmin_user_name.hashCode();
        result = 31 * result + customer_user_name.hashCode();
        return result;
    }

    public double getOrderNumIndex() {
        return orderNumIndex;
    }

    public void setOrderNumIndex(double orderNumIndex) {
        this.orderNumIndex = orderNumIndex;
    }

    public int getOrderTimes() {
        return orderTimes;
    }

    public void setOrderTimes(int orderTimes) {
        this.orderTimes = orderTimes;
    }

    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getStoreadmin_id() {
        return storeadmin_id;
    }

    public void setStoreadmin_id(int storeadmin_id) {
        this.storeadmin_id = storeadmin_id;
    }

    public String getStoreadmin_user_name() {
        return storeadmin_user_name;
    }

    public void setStoreadmin_user_name(String storeadmin_user_name) {
        this.storeadmin_user_name = storeadmin_user_name;
    }

    public String getCustomer_user_name() {
        return customer_user_name;
    }

    public void setCustomer_user_name(String customer_user_name) {
        this.customer_user_name = customer_user_name;
    }
}
