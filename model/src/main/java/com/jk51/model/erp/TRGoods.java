package com.jk51.model.erp;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-12
 * 修改记录:
 */
public class TRGoods implements Serializable {
    private String spbh;//商品编号
    private String jigid;//门店编号
    private String yhshj;//商品单价
    private String shl;//数量
    private String finalPrice;//最终单价

    public String getSpbh() {
        return spbh;
    }

    public void setSpbh(String spbh) {
        this.spbh = spbh;
    }

    public String getJigid() {
        return jigid;
    }

    public void setJigid(String jigid) {
        this.jigid = jigid;
    }

    public String getYhshj() {
        return yhshj;
    }

    public void setYhshj(String yhshj) {
        this.yhshj = yhshj;
    }

    public String getShl() {
        return shl;
    }

    public void setShl(String shl) {
        this.shl = shl;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    @Override
    public String toString() {
        return "TRGoods{" +
            "spbh='" + spbh + '\'' +
            ", jigid='" + jigid + '\'' +
            ", yhshj='" + yhshj + '\'' +
            ", shl='" + shl + '\'' +
            ", finalPrice='" + finalPrice + '\'' +
            '}';
    }
}
