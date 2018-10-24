package com.jk51.modules.pandian.param;

import com.jk51.commons.string.StringUtil;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
public class InventoryConfirmParam {

    @NotNull(message = "商品编号不能为空")
    private String  goods_code; //商品编码

    private Integer storeAdminId;
    @NotNull(message="盘点编号不能为空")
    private String pandian_num;

    private String drug_name; //商品名称
    private String goods_company; //生产企业

    @NotEmpty(message = "batchNums不能为空")
    private List<InventoryBatchNum> batchNums ;

    private int enableOrder;

    private String authToken;

    private Integer storeId;

    private Integer siteId;

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public List<InventoryBatchNum> getBatchNums() {
        return batchNums;
    }

    public void setBatchNums(List<InventoryBatchNum> batchNums) {
        this.batchNums = batchNums;
    }

    public Integer getStoreAdminId() {
        return storeAdminId;
    }

    public void setStoreAdminId(Integer storeAdminId) {
        this.storeAdminId = storeAdminId;
    }

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public int getEnableOrder() {
        return enableOrder;
    }

    public void setEnableOrder(int enableOrder) {
        this.enableOrder = enableOrder;
    }

    /**
     * 判断盘点确认中是否有新增的批号
     *
     * */
    public boolean hasNewBatch(){

        for(InventoryBatchNum b:batchNums){

            if(StringUtil.isEmpty(b.getInventoryId())){
                return true;
            }
        }

        return false;
    }



}
