package com.jk51.modules.pandian.Response;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-06
 * 修改记录:
 */
public class PandianOrderDetailResponse {

    private Integer plan_id;
    private String pandian_num;
    private String storeName;
    private String stores_number;
    private String goods_code; //'商品编号，b_goods.goods_code'
    private String drug_name; //商品名称
    private String batch_number;//批号
    private String specif_cation;   //药品规格
    private String goods_company;  //'生产厂家'
    private Double inventory_accounting;  //账目库存
    private Double actual_store; //实际库存
    private Integer inventory_checker; //盘点人storeAdminId
    private String checkerName;  //盘点人名字
    private String audit_checker_name; //审批人
    private String confirm_checker_name; //监盘人
    private Date create_time;
    private Date update_time;
    private String quality;
    private Double profitAndLossNum; //盈亏数
    private String profitAndLossStatus; //盈亏状态
    private Integer inventory_confirm;


    public Integer getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(Integer plan_id) {
        this.plan_id = plan_id;
    }

    public String getPandian_num() {
        return pandian_num;
    }

    public void setPandian_num(String pandian_num) {
        this.pandian_num = pandian_num;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStores_number() {
        return stores_number;
    }

    public void setStores_number(String stores_number) {
        this.stores_number = stores_number;
    }

    public String getGoods_code() {
        return goods_code;
    }

    public void setGoods_code(String goods_code) {
        this.goods_code = goods_code;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getBatch_number() {
        return batch_number;
    }

    public void setBatch_number(String batch_number) {
        this.batch_number = batch_number;
    }

    public String getSpecif_cation() {
        return specif_cation;
    }

    public void setSpecif_cation(String specif_cation) {
        this.specif_cation = specif_cation;
    }

    public String getGoods_company() {
        return goods_company;
    }

    public void setGoods_company(String goods_company) {
        this.goods_company = goods_company;
    }

    public Double getInventory_accounting() {
        return inventory_accounting;
    }

    public void setInventory_accounting(Double inventory_accounting) {
        this.inventory_accounting = inventory_accounting;
    }

    public Double getActual_store() {
        return actual_store;
    }

    public void setActual_store(Double actual_store) {
        this.actual_store = actual_store;
    }

    public Integer getInventory_checker() {
        return inventory_checker;
    }

    public void setInventory_checker(Integer inventory_checker) {
        this.inventory_checker = inventory_checker;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getAudit_checker_name() {
        return audit_checker_name;
    }

    public void setAudit_checker_name(String audit_checker_name) {
        this.audit_checker_name = audit_checker_name;
    }

    public String getConfirm_checker_name() {
        return confirm_checker_name;
    }

    public void setConfirm_checker_name(String confirm_checker_name) {
        this.confirm_checker_name = confirm_checker_name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Double getProfitAndLossNum() {
        return profitAndLossNum;
    }

    public void setProfitAndLossNum(Double profitAndLossNum) {
        this.profitAndLossNum = profitAndLossNum;
    }

    public String getProfitAndLossStatus() {
        return profitAndLossStatus;
    }

    public void setProfitAndLossStatus(String profitAndLossStatus) {
        this.profitAndLossStatus = profitAndLossStatus;
    }

    public Integer getInventory_confirm() {
        return inventory_confirm;
    }

    public void setInventory_confirm(Integer inventory_confirm) {
        this.inventory_confirm = inventory_confirm;
    }
}
