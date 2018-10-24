package com.jk51.modules.erpprice.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jk51.modules.task.domain.AddGroup;
import com.jk51.modules.task.domain.JsonStringSerialize;
import com.jk51.modules.task.domain.UpdateGroup;
import com.jk51.modules.task.domain.validation.AllowValue;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron
 * 创建日期: 2017-10-31 14:02
 * 修改记录:
 */
public class BErpSettingDTO {

    @NotNull(groups = {UpdateGroup.class}, message = "商户ERP价格类型实体id不能为空")
    private Integer id;

    @NotNull(groups = {AddGroup.class},message = "ERP价格类型实体商户编号不能为空")
    private Integer siteId;

    /**
     * 10 总部基础价 20 市级价 30 区级价 40 门店价
     */
    @NotNull(groups = {AddGroup.class, UpdateGroup.class},message = "erp价格类型不能为空")
    @AllowValue(value = {10, 20, 30, 40}, groups = {AddGroup.class, UpdateGroup.class}, message = "不能设置未知的价格类型")
    private Byte type;

    /**
     * 10 对接 20 不对接
     */
    @NotNull(groups = {AddGroup.class, UpdateGroup.class},message = "erp价格对接不能为空")
    @AllowValue(value = {10, 20}, groups = {AddGroup.class, UpdateGroup.class}, message = "不能设置未知的对接状态")
    private Byte isJoint;

    @Valid
    @Size(groups ={AddGroup.class, UpdateGroup.class},min=0,message = "商户ERP区域价格设置失败")
    private ErpPriceSettingDTO[] erpPriceSettingDTOs;

    public static class ErpPriceSettingDTO{

        @NotNull(groups = {UpdateGroup.class}, message = "区域指定门店实体id不能为空")
        private Integer id;

        private Integer siteId;

        /**
         * 10 总部基础价 20 市级价 30 区级价 40 门店价
         */
        @NotNull(groups = {AddGroup.class},message = "erp价格类型不能为空")
        @AllowValue(value = {10, 20, 30, 40}, groups = {AddGroup.class}, message = "不能设置未知的价格类型")
        private Byte type;

        /**
         * 基准价的门店ID
         */
        @NotNull(groups = {AddGroup.class},message = "区域指定价格的门店id不能为空")
        private Integer storeId;

        /**
         * 根据type记录行政区划代码 40 不需要记录
         */
        @NotNull(groups = {AddGroup.class},message = "区域指定区域的code不能为空")
        private Integer areaCode;

        /**
         * 优先级
         */
        @NotNull(groups = {AddGroup.class},message = "优先级不能为空")
        private Byte priority;

        public ErpPriceSettingDTO() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public Byte getType() {
            return type;
        }

        public void setType(Byte type) {
            this.type = type;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public Integer getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(Integer areaCode) {
            this.areaCode = areaCode;
        }

        public Byte getPriority() {
            return priority;
        }

        public void setPriority(Byte priority) {
            this.priority = priority;
        }
    }

    public BErpSettingDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getIsJoint() {
        return isJoint;
    }

    public void setIsJoint(Byte isJoint) {
        this.isJoint = isJoint;
    }

    public ErpPriceSettingDTO[] getErpPriceSettingDTOs() {
        return erpPriceSettingDTOs;
    }

    public void setErpPriceSettingDTOs(ErpPriceSettingDTO[] erpPriceSettingDTOs) {
        this.erpPriceSettingDTOs = erpPriceSettingDTOs;
    }
}
