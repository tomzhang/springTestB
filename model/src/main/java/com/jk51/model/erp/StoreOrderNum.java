package com.jk51.model.erp;

import java.util.Date;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-05-08
 * 修改记录:
 */
public class StoreOrderNum {

    private String unit_no;//门店编码
    private String billid;//任务号
    private String third_no;//盘点单号
    private Date add_time;//插入时间

    public String getUnit_no() {
        return unit_no;
    }

    public void setUnit_no(String unit_no) {
        this.unit_no = unit_no;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public String getThird_no() {
        return third_no;
    }

    public void setThird_no(String third_no) {
        this.third_no = third_no;
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    @Override
    public String toString() {
        return "StoreOrderNum{" +
            "unit_no='" + unit_no + '\'' +
            ", billid='" + billid + '\'' +
            ", third_no='" + third_no + '\'' +
            ", add_time=" + add_time +
            '}';
    }
}
