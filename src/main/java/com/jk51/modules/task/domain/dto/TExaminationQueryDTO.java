package com.jk51.modules.task.domain.dto;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.TExaminationExample;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TExaminationQueryDTO {
    private Integer id;

    private Integer siteId;

    /**
     * 标题
     */
    private String title;

    /**
     * 药品分类
     */
    private Integer drugCategory;

    /**
     * 疾病分类
     */
    private Integer diseaseCategory;

    /**
     * 培训分类
     */
    private Integer trainedCategory;

    /**
     * 题型
     */
    private Integer questType;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 厂家
     */
    private String enterprise;

    private Byte adminType;

    private Date startTime;

    private Date endTime;

    /**
     * 页数
     */
    private Integer pageno;

    /**
     * 每页多少条
     */
    private Integer pageSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getDiseaseCategory() {
        return diseaseCategory;
    }

    public void setDiseaseCategory(Integer diseaseCategory) {
        this.diseaseCategory = diseaseCategory;
    }

    public Integer getTrainedCategory() {
        return trainedCategory;
    }

    public void setTrainedCategory(Integer trainedCategory) {
        this.trainedCategory = trainedCategory;
    }

    public Integer getQuestType() {
        return questType;
    }

    public void setQuestType(Integer questType) {
        this.questType = questType;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageno() {
        return pageno;
    }

    public void setPageno(Integer pageno) {
        this.pageno = pageno;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Byte getAdminType() {
        return adminType;
    }

    public void setAdminType(Byte adminType) {
        this.adminType = adminType;
    }

    public String getLikeTitle() {
        return "%" + title + "%";
    }

    public String getLikeBrand() {
        return "%" + brand + "%";
    }

    public String getLikeEnterprise() {
        return "%" + enterprise + "%";
    }

    public int getPageNo(int defaultValue){
        return pageno == null ? defaultValue : pageno;
    }

    public int getPageSize(int defaultValue){
        return pageSize == null ? defaultValue : pageSize;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public TExaminationExample buildExample() {
        TExaminationExample example = new TExaminationExample();
        example.setOrderByClause("id DESC");
        TExaminationExample.Criteria criteria = example.createCriteria().andStatusEqualTo((byte) 10);
        if (Objects.nonNull(getId())) {
            // id
            criteria.andIdEqualTo(getId());
        }

        if (Objects.nonNull(getSiteId())) {
            criteria.andSiteIdEqualTo(getSiteId());
        }

        if (StringUtil.isNotBlank(getTitle())) {
            // 标题
            criteria.andTitleLike(getLikeTitle());
        }
        if (StringUtil.isNotBlank(getBrand())) {
            // 品牌
            criteria.andBrandLike(getLikeBrand());
        }
        if (StringUtil.isNotBlank(getEnterprise())) {
            // 厂家
            criteria.andEnterpriseLike(getLikeEnterprise());
        }
        if (Objects.nonNull(getDiseaseCategory())) {
            // 疾病分类
            criteria.andDiseaseCategoryFindInSet(getDiseaseCategory());
        }
        if (Objects.nonNull(getDrugCategory())) {
            // 药品分类
            criteria.andDrugCategoryEqualTo(getDrugCategory());
        }
        if (Objects.nonNull(getQuestType())) {
            // 题型
            criteria.andQuestTypesFindInSet(getQuestType());
        }

        if (Objects.nonNull(getStartTime()) && Objects.nonNull(getEndTime())) {
            criteria.andCreateTimeBetween(getStartTime(), getEndTime());
        } else if (Objects.nonNull(getStartTime())) {
            criteria.andCreateTimeGreaterThanOrEqualTo(getStartTime());
        } else if (Objects.nonNull(getEndTime())) {
            criteria.andCreateTimeLessThanOrEqualTo(getEndTime());
        }
        if (Objects.nonNull(getAdminType())) {
            // adminType
            criteria.andAdminTypeEqualTo(getAdminType());
        }
        return example;
    }

    public TExaminationExample buildExample(Byte adminType,List<Integer> adminIds) {
        TExaminationExample example = new TExaminationExample();
        example.setOrderByClause("id DESC");
        TExaminationExample.Criteria criteria = example.createCriteria().andStatusEqualTo((byte) 10).andAdminTypeEqualTo(adminType);
        if (Objects.nonNull(getId())) {
            // id
            criteria.andIdEqualTo(getId());
        }

        if (Objects.nonNull(getSiteId())) {
            criteria.andSiteIdEqualTo(getSiteId());
        }

        if (StringUtil.isNotBlank(getTitle())) {
            // 标题
            criteria.andTitleLike(getLikeTitle());
        }
        if (StringUtil.isNotBlank(getBrand())) {
            // 品牌
            criteria.andBrandLike(getLikeBrand());
        }
        if (StringUtil.isNotBlank(getEnterprise())) {
            // 厂家
            criteria.andEnterpriseLike(getLikeEnterprise());
        }
        if (Objects.nonNull(getDiseaseCategory())) {
            // 疾病分类
            criteria.andDiseaseCategoryFindInSet(getDiseaseCategory());
        }
        if (Objects.nonNull(getDrugCategory())) {
            // 药品分类
            criteria.andDrugCategoryEqualTo(getDrugCategory());
        }
        if (Objects.nonNull(getQuestType())) {
            // 题型
            criteria.andQuestTypesFindInSet(getQuestType());
        }

        if (Objects.nonNull(getStartTime()) && Objects.nonNull(getEndTime())) {
            criteria.andCreateTimeBetween(getStartTime(), getEndTime());
        } else if (Objects.nonNull(getStartTime())) {
            criteria.andCreateTimeGreaterThanOrEqualTo(getStartTime());
        } else if (Objects.nonNull(getEndTime())) {
            criteria.andCreateTimeLessThanOrEqualTo(getEndTime());
        }

        if(Objects.nonNull(adminIds)){
            //门店下的店员id
            criteria.andAdminIdIn(adminIds);
        }

        return example;
    }

    @Override
    public String toString() {
        return "TExaminationQueryDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", drugCategory=" + drugCategory +
            ", diseaseCategory=" + diseaseCategory +
            ", trainedCategory=" + trainedCategory +
            ", questType=" + questType +
            ", brand='" + brand + '\'' +
            ", enterprise='" + enterprise + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", pageno=" + pageno +
            ", pageSize=" + pageSize +
            '}';
    }
}
