package com.jk51.model.task;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class TExamination implements Serializable {
    private Integer id;

    /**
     * 商户id 默认0 51健康添加
     */
    private Integer siteId;

    private Byte adminType;

    private Integer adminId;

    private String adminName;

    /**
     * 标题
     */
    private String title;

    /**
     * 药品分类
     */
    private Integer drugCategory;

    /**
     * 药品分类名
     */
    private String categoryName;

    /**
     * 疾病分类
     */
    private String diseaseCategory;

    /**
     * 培训分类
     */
    private Integer trainedCategory;

    /**
     * 问题数量
     */
    private Byte questNum;

    /**
     * 答题时间
     */
    private Integer secondTotal;

    /**
     * 10 单选 20 多选 逗号分割
     */
    private String questTypes;

    /**
     * 品牌
     */
    private String brand;

    private String enterprise;

    /**
     * 状态 10有效 20无效
     */
    private Byte status;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

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

    public Byte getAdminType() {
        return adminType;
    }

    public void setAdminType(Byte adminType) {
        this.adminType = adminType;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDrugCategory() {
        return drugCategory;
    }

    public void setDrugCategory(Integer drugCategory) {
        this.drugCategory = drugCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDiseaseCategory() {
        return diseaseCategory;
    }

    public void setDiseaseCategory(String diseaseCategory) {
        this.diseaseCategory = diseaseCategory;
    }

    public Integer getTrainedCategory() {
        return trainedCategory;
    }

    public void setTrainedCategory(Integer trainedCategory) {
        this.trainedCategory = trainedCategory;
    }

    public Byte getQuestNum() {
        return questNum;
    }

    public void setQuestNum(Byte questNum) {
        this.questNum = questNum;
    }

    public Integer getSecondTotal() {
        return secondTotal;
    }

    public void setSecondTotal(Integer secondTotal) {
        this.secondTotal = secondTotal;
    }

    public String getQuestTypes() {
        return questTypes;
    }

    public void setQuestTypes(String questTypes) {
        this.questTypes = questTypes;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TExamination other = (TExamination) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSiteId() == null ? other.getSiteId() == null : this.getSiteId().equals(other.getSiteId()))
            && (this.getAdminType() == null ? other.getAdminType() == null : this.getAdminType().equals(other.getAdminType()))
            && (this.getAdminId() == null ? other.getAdminId() == null : this.getAdminId().equals(other.getAdminId()))
            && (this.getAdminName() == null ? other.getAdminName() == null : this.getAdminName().equals(other.getAdminName()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getDrugCategory() == null ? other.getDrugCategory() == null : this.getDrugCategory().equals(other.getDrugCategory()))
            && (this.getCategoryName() == null ? other.getCategoryName() == null : this.getCategoryName().equals(other.getCategoryName()))
            && (this.getDiseaseCategory() == null ? other.getDiseaseCategory() == null : this.getDiseaseCategory().equals(other.getDiseaseCategory()))
            && (this.getTrainedCategory() == null ? other.getTrainedCategory() == null : this.getTrainedCategory().equals(other.getTrainedCategory()))
            && (this.getQuestNum() == null ? other.getQuestNum() == null : this.getQuestNum().equals(other.getQuestNum()))
            && (this.getSecondTotal() == null ? other.getSecondTotal() == null : this.getSecondTotal().equals(other.getSecondTotal()))
            && (this.getQuestTypes() == null ? other.getQuestTypes() == null : this.getQuestTypes().equals(other.getQuestTypes()))
            && (this.getBrand() == null ? other.getBrand() == null : this.getBrand().equals(other.getBrand()))
            && (this.getEnterprise() == null ? other.getEnterprise() == null : this.getEnterprise().equals(other.getEnterprise()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSiteId() == null) ? 0 : getSiteId().hashCode());
        result = prime * result + ((getAdminType() == null) ? 0 : getAdminType().hashCode());
        result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
        result = prime * result + ((getAdminName() == null) ? 0 : getAdminName().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getDrugCategory() == null) ? 0 : getDrugCategory().hashCode());
        result = prime * result + ((getCategoryName() == null) ? 0 : getCategoryName().hashCode());
        result = prime * result + ((getDiseaseCategory() == null) ? 0 : getDiseaseCategory().hashCode());
        result = prime * result + ((getTrainedCategory() == null) ? 0 : getTrainedCategory().hashCode());
        result = prime * result + ((getQuestNum() == null) ? 0 : getQuestNum().hashCode());
        result = prime * result + ((getSecondTotal() == null) ? 0 : getSecondTotal().hashCode());
        result = prime * result + ((getQuestTypes() == null) ? 0 : getQuestTypes().hashCode());
        result = prime * result + ((getBrand() == null) ? 0 : getBrand().hashCode());
        result = prime * result + ((getEnterprise() == null) ? 0 : getEnterprise().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", siteId=").append(siteId);
        sb.append(", adminType=").append(adminType);
        sb.append(", adminId=").append(adminId);
        sb.append(", adminName=").append(adminName);
        sb.append(", title=").append(title);
        sb.append(", drugCategory=").append(drugCategory);
        sb.append(", categoryName=").append(categoryName);
        sb.append(", diseaseCategory=").append(diseaseCategory);
        sb.append(", trainedCategory=").append(trainedCategory);
        sb.append(", questNum=").append(questNum);
        sb.append(", secondTotal=").append(secondTotal);
        sb.append(", questTypes=").append(questTypes);
        sb.append(", brand=").append(brand);
        sb.append(", enterprise=").append(enterprise);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}