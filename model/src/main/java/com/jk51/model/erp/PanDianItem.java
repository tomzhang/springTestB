package com.jk51.model.erp;

import java.util.Date;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-05-08
 * 修改记录:
 */
public class PanDianItem {

    private String billid;//任务号
    private Integer itemid;//序号
    private Integer gid;//商品id（erp）
    private String goodsno;//商品编码
    private String barcode;//商品条码
    private String batchchid;//商品批次
    private String batchno;//商品批号
    private Double ackquty;//账目库存数量
    private Double actquty;//实际盘点数量
    private String mname;//商品名称
    private String spec;//规格
    private String productor;//生产厂家

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public Integer getItemid() {
        return itemid;
    }

    public void setItemid(Integer itemid) {
        this.itemid = itemid;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getGoodsno() {
        return goodsno;
    }

    public void setGoodsno(String goodsno) {
        this.goodsno = goodsno;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBatchchid() {
        return batchchid;
    }

    public void setBatchchid(String batchchid) {
        this.batchchid = batchchid;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public Double getAckquty() {
        return ackquty;
    }

    public void setAckquty(Double ackquty) {
        this.ackquty = ackquty;
    }

    public Double getActquty() {
        return actquty;
    }

    public void setActquty(Double actquty) {
        this.actquty = actquty;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }
}
