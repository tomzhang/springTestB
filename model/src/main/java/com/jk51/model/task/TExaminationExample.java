package com.jk51.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TExaminationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public TExaminationExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return offset;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andSiteIdIsNull() {
            addCriterion("site_id is null");
            return (Criteria) this;
        }

        public Criteria andSiteIdIsNotNull() {
            addCriterion("site_id is not null");
            return (Criteria) this;
        }

        public Criteria andSiteIdEqualTo(Integer value) {
            addCriterion("site_id =", value, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdNotEqualTo(Integer value) {
            addCriterion("site_id <>", value, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdGreaterThan(Integer value) {
            addCriterion("site_id >", value, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("site_id >=", value, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdLessThan(Integer value) {
            addCriterion("site_id <", value, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdLessThanOrEqualTo(Integer value) {
            addCriterion("site_id <=", value, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdIn(List<Integer> values) {
            addCriterion("site_id in", values, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdNotIn(List<Integer> values) {
            addCriterion("site_id not in", values, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdBetween(Integer value1, Integer value2) {
            addCriterion("site_id between", value1, value2, "siteId");
            return (Criteria) this;
        }

        public Criteria andSiteIdNotBetween(Integer value1, Integer value2) {
            addCriterion("site_id not between", value1, value2, "siteId");
            return (Criteria) this;
        }

        public Criteria andAdminTypeIsNull() {
            addCriterion("admin_type is null");
            return (Criteria) this;
        }

        public Criteria andAdminTypeIsNotNull() {
            addCriterion("admin_type is not null");
            return (Criteria) this;
        }

        public Criteria andAdminTypeEqualTo(Byte value) {
            addCriterion("admin_type =", value, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeNotEqualTo(Byte value) {
            addCriterion("admin_type <>", value, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeGreaterThan(Byte value) {
            addCriterion("admin_type >", value, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("admin_type >=", value, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeLessThan(Byte value) {
            addCriterion("admin_type <", value, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeLessThanOrEqualTo(Byte value) {
            addCriterion("admin_type <=", value, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeIn(List<Byte> values) {
            addCriterion("admin_type in", values, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeNotIn(List<Byte> values) {
            addCriterion("admin_type not in", values, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeBetween(Byte value1, Byte value2) {
            addCriterion("admin_type between", value1, value2, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("admin_type not between", value1, value2, "adminType");
            return (Criteria) this;
        }

        public Criteria andAdminIdIsNull() {
            addCriterion("admin_id is null");
            return (Criteria) this;
        }

        public Criteria andAdminIdIsNotNull() {
            addCriterion("admin_id is not null");
            return (Criteria) this;
        }

        public Criteria andAdminIdEqualTo(Integer value) {
            addCriterion("admin_id =", value, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdNotEqualTo(Integer value) {
            addCriterion("admin_id <>", value, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdGreaterThan(Integer value) {
            addCriterion("admin_id >", value, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("admin_id >=", value, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdLessThan(Integer value) {
            addCriterion("admin_id <", value, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdLessThanOrEqualTo(Integer value) {
            addCriterion("admin_id <=", value, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdIn(List<Integer> values) {
            addCriterion("admin_id in", values, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdNotIn(List<Integer> values) {
            addCriterion("admin_id not in", values, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdBetween(Integer value1, Integer value2) {
            addCriterion("admin_id between", value1, value2, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminIdNotBetween(Integer value1, Integer value2) {
            addCriterion("admin_id not between", value1, value2, "adminId");
            return (Criteria) this;
        }

        public Criteria andAdminNameIsNull() {
            addCriterion("admin_name is null");
            return (Criteria) this;
        }

        public Criteria andAdminNameIsNotNull() {
            addCriterion("admin_name is not null");
            return (Criteria) this;
        }

        public Criteria andAdminNameEqualTo(String value) {
            addCriterion("admin_name =", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameNotEqualTo(String value) {
            addCriterion("admin_name <>", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameGreaterThan(String value) {
            addCriterion("admin_name >", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameGreaterThanOrEqualTo(String value) {
            addCriterion("admin_name >=", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameLessThan(String value) {
            addCriterion("admin_name <", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameLessThanOrEqualTo(String value) {
            addCriterion("admin_name <=", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameLike(String value) {
            addCriterion("admin_name like", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameNotLike(String value) {
            addCriterion("admin_name not like", value, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameIn(List<String> values) {
            addCriterion("admin_name in", values, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameNotIn(List<String> values) {
            addCriterion("admin_name not in", values, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameBetween(String value1, String value2) {
            addCriterion("admin_name between", value1, value2, "adminName");
            return (Criteria) this;
        }

        public Criteria andAdminNameNotBetween(String value1, String value2) {
            addCriterion("admin_name not between", value1, value2, "adminName");
            return (Criteria) this;
        }

        public Criteria andTitleIsNull() {
            addCriterion("title is null");
            return (Criteria) this;
        }

        public Criteria andTitleIsNotNull() {
            addCriterion("title is not null");
            return (Criteria) this;
        }

        public Criteria andTitleEqualTo(String value) {
            addCriterion("title =", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotEqualTo(String value) {
            addCriterion("title <>", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThan(String value) {
            addCriterion("title >", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThanOrEqualTo(String value) {
            addCriterion("title >=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThan(String value) {
            addCriterion("title <", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThanOrEqualTo(String value) {
            addCriterion("title <=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLike(String value) {
            addCriterion("title like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotLike(String value) {
            addCriterion("title not like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleIn(List<String> values) {
            addCriterion("title in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotIn(List<String> values) {
            addCriterion("title not in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleBetween(String value1, String value2) {
            addCriterion("title between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotBetween(String value1, String value2) {
            addCriterion("title not between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryIsNull() {
            addCriterion("drug_category is null");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryIsNotNull() {
            addCriterion("drug_category is not null");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryEqualTo(Integer value) {
            addCriterion("drug_category =", value, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryNotEqualTo(Integer value) {
            addCriterion("drug_category <>", value, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryGreaterThan(Integer value) {
            addCriterion("drug_category >", value, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryGreaterThanOrEqualTo(Integer value) {
            addCriterion("drug_category >=", value, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryLessThan(Integer value) {
            addCriterion("drug_category <", value, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryLessThanOrEqualTo(Integer value) {
            addCriterion("drug_category <=", value, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryIn(List<Integer> values) {
            addCriterion("drug_category in", values, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryNotIn(List<Integer> values) {
            addCriterion("drug_category not in", values, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryBetween(Integer value1, Integer value2) {
            addCriterion("drug_category between", value1, value2, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andDrugCategoryNotBetween(Integer value1, Integer value2) {
            addCriterion("drug_category not between", value1, value2, "drugCategory");
            return (Criteria) this;
        }

        public Criteria andCategoryNameIsNull() {
            addCriterion("category_name is null");
            return (Criteria) this;
        }

        public Criteria andCategoryNameIsNotNull() {
            addCriterion("category_name is not null");
            return (Criteria) this;
        }

        public Criteria andCategoryNameEqualTo(String value) {
            addCriterion("category_name =", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameNotEqualTo(String value) {
            addCriterion("category_name <>", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameGreaterThan(String value) {
            addCriterion("category_name >", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameGreaterThanOrEqualTo(String value) {
            addCriterion("category_name >=", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameLessThan(String value) {
            addCriterion("category_name <", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameLessThanOrEqualTo(String value) {
            addCriterion("category_name <=", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameLike(String value) {
            addCriterion("category_name like", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameNotLike(String value) {
            addCriterion("category_name not like", value, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameIn(List<String> values) {
            addCriterion("category_name in", values, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameNotIn(List<String> values) {
            addCriterion("category_name not in", values, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameBetween(String value1, String value2) {
            addCriterion("category_name between", value1, value2, "categoryName");
            return (Criteria) this;
        }

        public Criteria andCategoryNameNotBetween(String value1, String value2) {
            addCriterion("category_name not between", value1, value2, "categoryName");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryIsNull() {
            addCriterion("disease_category is null");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryIsNotNull() {
            addCriterion("disease_category is not null");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryEqualTo(String value) {
            addCriterion("disease_category =", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryNotEqualTo(String value) {
            addCriterion("disease_category <>", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryGreaterThan(String value) {
            addCriterion("disease_category >", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryGreaterThanOrEqualTo(String value) {
            addCriterion("disease_category >=", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryLessThan(String value) {
            addCriterion("disease_category <", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryLessThanOrEqualTo(String value) {
            addCriterion("disease_category <=", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryLike(String value) {
            addCriterion("disease_category like", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryNotLike(String value) {
            addCriterion("disease_category not like", value, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryIn(List<String> values) {
            addCriterion("disease_category in", values, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryNotIn(List<String> values) {
            addCriterion("disease_category not in", values, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryBetween(String value1, String value2) {
            addCriterion("disease_category between", value1, value2, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryNotBetween(String value1, String value2) {
            addCriterion("disease_category not between", value1, value2, "diseaseCategory");
            return (Criteria) this;
        }

        public Criteria andDiseaseCategoryFindInSet(Integer diseaseCategory) {
            addCriterion("FIND_IN_SET(" + diseaseCategory +", disease_category)");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryIsNull() {
            addCriterion("trained_category is null");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryIsNotNull() {
            addCriterion("trained_category is not null");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryEqualTo(Integer value) {
            addCriterion("trained_category =", value, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryNotEqualTo(Integer value) {
            addCriterion("trained_category <>", value, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryGreaterThan(Integer value) {
            addCriterion("trained_category >", value, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryGreaterThanOrEqualTo(Integer value) {
            addCriterion("trained_category >=", value, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryLessThan(Integer value) {
            addCriterion("trained_category <", value, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryLessThanOrEqualTo(Integer value) {
            addCriterion("trained_category <=", value, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryIn(List<Integer> values) {
            addCriterion("trained_category in", values, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryNotIn(List<Integer> values) {
            addCriterion("trained_category not in", values, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryBetween(Integer value1, Integer value2) {
            addCriterion("trained_category between", value1, value2, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andTrainedCategoryNotBetween(Integer value1, Integer value2) {
            addCriterion("trained_category not between", value1, value2, "trainedCategory");
            return (Criteria) this;
        }

        public Criteria andQuestNumIsNull() {
            addCriterion("quest_num is null");
            return (Criteria) this;
        }

        public Criteria andQuestNumIsNotNull() {
            addCriterion("quest_num is not null");
            return (Criteria) this;
        }

        public Criteria andQuestNumEqualTo(Byte value) {
            addCriterion("quest_num =", value, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumNotEqualTo(Byte value) {
            addCriterion("quest_num <>", value, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumGreaterThan(Byte value) {
            addCriterion("quest_num >", value, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumGreaterThanOrEqualTo(Byte value) {
            addCriterion("quest_num >=", value, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumLessThan(Byte value) {
            addCriterion("quest_num <", value, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumLessThanOrEqualTo(Byte value) {
            addCriterion("quest_num <=", value, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumIn(List<Byte> values) {
            addCriterion("quest_num in", values, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumNotIn(List<Byte> values) {
            addCriterion("quest_num not in", values, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumBetween(Byte value1, Byte value2) {
            addCriterion("quest_num between", value1, value2, "questNum");
            return (Criteria) this;
        }

        public Criteria andQuestNumNotBetween(Byte value1, Byte value2) {
            addCriterion("quest_num not between", value1, value2, "questNum");
            return (Criteria) this;
        }

        public Criteria andSecondTotalIsNull() {
            addCriterion("second_total is null");
            return (Criteria) this;
        }

        public Criteria andSecondTotalIsNotNull() {
            addCriterion("second_total is not null");
            return (Criteria) this;
        }

        public Criteria andSecondTotalEqualTo(Integer value) {
            addCriterion("second_total =", value, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalNotEqualTo(Integer value) {
            addCriterion("second_total <>", value, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalGreaterThan(Integer value) {
            addCriterion("second_total >", value, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("second_total >=", value, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalLessThan(Integer value) {
            addCriterion("second_total <", value, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalLessThanOrEqualTo(Integer value) {
            addCriterion("second_total <=", value, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalIn(List<Integer> values) {
            addCriterion("second_total in", values, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalNotIn(List<Integer> values) {
            addCriterion("second_total not in", values, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalBetween(Integer value1, Integer value2) {
            addCriterion("second_total between", value1, value2, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andSecondTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("second_total not between", value1, value2, "secondTotal");
            return (Criteria) this;
        }

        public Criteria andQuestTypesFindInSet(Integer questType) {
            addCriterion("FIND_IN_SET(" + questType +", quest_types)");
            return (Criteria) this;
        }

        public Criteria andQuestTypesIsNull() {
            addCriterion("quest_types is null");
            return (Criteria) this;
        }

        public Criteria andQuestTypesIsNotNull() {
            addCriterion("quest_types is not null");
            return (Criteria) this;
        }

        public Criteria andQuestTypesEqualTo(String value) {
            addCriterion("quest_types =", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesNotEqualTo(String value) {
            addCriterion("quest_types <>", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesGreaterThan(String value) {
            addCriterion("quest_types >", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesGreaterThanOrEqualTo(String value) {
            addCriterion("quest_types >=", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesLessThan(String value) {
            addCriterion("quest_types <", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesLessThanOrEqualTo(String value) {
            addCriterion("quest_types <=", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesLike(String value) {
            addCriterion("quest_types like", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesNotLike(String value) {
            addCriterion("quest_types not like", value, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesIn(List<String> values) {
            addCriterion("quest_types in", values, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesNotIn(List<String> values) {
            addCriterion("quest_types not in", values, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesBetween(String value1, String value2) {
            addCriterion("quest_types between", value1, value2, "questTypes");
            return (Criteria) this;
        }

        public Criteria andQuestTypesNotBetween(String value1, String value2) {
            addCriterion("quest_types not between", value1, value2, "questTypes");
            return (Criteria) this;
        }

        public Criteria andBrandIsNull() {
            addCriterion("brand is null");
            return (Criteria) this;
        }

        public Criteria andBrandIsNotNull() {
            addCriterion("brand is not null");
            return (Criteria) this;
        }

        public Criteria andBrandEqualTo(String value) {
            addCriterion("brand =", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotEqualTo(String value) {
            addCriterion("brand <>", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandGreaterThan(String value) {
            addCriterion("brand >", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandGreaterThanOrEqualTo(String value) {
            addCriterion("brand >=", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandLessThan(String value) {
            addCriterion("brand <", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandLessThanOrEqualTo(String value) {
            addCriterion("brand <=", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandLike(String value) {
            addCriterion("brand like", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotLike(String value) {
            addCriterion("brand not like", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandIn(List<String> values) {
            addCriterion("brand in", values, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotIn(List<String> values) {
            addCriterion("brand not in", values, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandBetween(String value1, String value2) {
            addCriterion("brand between", value1, value2, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotBetween(String value1, String value2) {
            addCriterion("brand not between", value1, value2, "brand");
            return (Criteria) this;
        }

        public Criteria andEnterpriseIsNull() {
            addCriterion("enterprise is null");
            return (Criteria) this;
        }

        public Criteria andEnterpriseIsNotNull() {
            addCriterion("enterprise is not null");
            return (Criteria) this;
        }

        public Criteria andEnterpriseEqualTo(String value) {
            addCriterion("enterprise =", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseNotEqualTo(String value) {
            addCriterion("enterprise <>", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseGreaterThan(String value) {
            addCriterion("enterprise >", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseGreaterThanOrEqualTo(String value) {
            addCriterion("enterprise >=", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseLessThan(String value) {
            addCriterion("enterprise <", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseLessThanOrEqualTo(String value) {
            addCriterion("enterprise <=", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseLike(String value) {
            addCriterion("enterprise like", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseNotLike(String value) {
            addCriterion("enterprise not like", value, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseIn(List<String> values) {
            addCriterion("enterprise in", values, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseNotIn(List<String> values) {
            addCriterion("enterprise not in", values, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseBetween(String value1, String value2) {
            addCriterion("enterprise between", value1, value2, "enterprise");
            return (Criteria) this;
        }

        public Criteria andEnterpriseNotBetween(String value1, String value2) {
            addCriterion("enterprise not between", value1, value2, "enterprise");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Byte value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Byte value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Byte value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Byte> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    /**
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
