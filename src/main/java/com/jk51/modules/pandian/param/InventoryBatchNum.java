package com.jk51.modules.pandian.param;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:批号、规格、数量、质量封装类
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
public class InventoryBatchNum {


    private Integer inventoryId; //库存Id
    private String batchNum;    //批号
    @NotNull
    private String specif_cation; //规格
    @NotNull
    private Double num; //数量
    private String quality;//质量

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getSpecif_cation() {
        return specif_cation;
    }

    public void setSpecif_cation(String specif_cation) {
        this.specif_cation = specif_cation;
    }

    public Double getNum() {
        return num;
    }

    public void setNum(Double num) {
        this.num = num;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryBatchNum that = (InventoryBatchNum) o;
        return Objects.equals(batchNum, that.batchNum);
    }

    @Override
    public int hashCode() {

        return Objects.hash(batchNum);
    }
}
