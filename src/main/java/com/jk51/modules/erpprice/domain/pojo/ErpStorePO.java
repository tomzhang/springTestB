package com.jk51.modules.erpprice.domain.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * @author
 */
public class ErpStorePO {
    private Byte type;
    private Integer siteId;
    private Byte isJoint;
    List<ErpSettingPO> erpSettings;

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Byte getIsJoint() {
        return isJoint;
    }

    public void setIsJoint(Byte isJoint) {
        this.isJoint = isJoint;
    }

    public List<ErpSettingPO> getErpSettings() {
        return erpSettings;
    }

    public void setErpSettings(List<ErpSettingPO> erpSettings) {
        this.erpSettings = erpSettings;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
