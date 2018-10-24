package com.jk51.model.official;

import java.util.Date;

/**
 * @author 
 */
public class YbCollaborate {
    private Integer id;
    private String companyName;
    private String companyAddr;
    private String manageProvince;
    private Integer storeNum;
    private Integer directStoreNum;
    private String name;
    private String phone;
    private Integer status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private Date endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public String getManageProvince() {
        return manageProvince;
    }

    public void setManageProvince(String manageProvince) {
        this.manageProvince = manageProvince;
    }

    public Integer getStoreNum() {
        return storeNum;
    }

    public void setStoreNum(Integer storeNum) {
        this.storeNum = storeNum;
    }

    public Integer getDirectStoreNum() {
        return directStoreNum;
    }

    public void setDirectStoreNum(Integer directStoreNum) {
        this.directStoreNum = directStoreNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
