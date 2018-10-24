package com.jk51.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BTaskExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public BTaskExample() {
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

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andTargetIdIsNull() {
            addCriterion("target_id is null");
            return (Criteria) this;
        }

        public Criteria andTargetIdIsNotNull() {
            addCriterion("target_id is not null");
            return (Criteria) this;
        }

        public Criteria andTargetIdEqualTo(Integer value) {
            addCriterion("target_id =", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotEqualTo(Integer value) {
            addCriterion("target_id <>", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdGreaterThan(Integer value) {
            addCriterion("target_id >", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("target_id >=", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLessThan(Integer value) {
            addCriterion("target_id <", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdLessThanOrEqualTo(Integer value) {
            addCriterion("target_id <=", value, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdIn(List<Integer> values) {
            addCriterion("target_id in", values, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotIn(List<Integer> values) {
            addCriterion("target_id not in", values, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdBetween(Integer value1, Integer value2) {
            addCriterion("target_id between", value1, value2, "targetId");
            return (Criteria) this;
        }

        public Criteria andTargetIdNotBetween(Integer value1, Integer value2) {
            addCriterion("target_id not between", value1, value2, "targetId");
            return (Criteria) this;
        }

        public Criteria andTypeIdsIsNull() {
            addCriterion("type_ids is null");
            return (Criteria) this;
        }

        public Criteria andTypeIdsIsNotNull() {
            addCriterion("type_ids is not null");
            return (Criteria) this;
        }

        public Criteria andTypeIdsEqualTo(String value) {
            addCriterion("type_ids =", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsNotEqualTo(String value) {
            addCriterion("type_ids <>", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsGreaterThan(String value) {
            addCriterion("type_ids >", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsGreaterThanOrEqualTo(String value) {
            addCriterion("type_ids >=", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsLessThan(String value) {
            addCriterion("type_ids <", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsLessThanOrEqualTo(String value) {
            addCriterion("type_ids <=", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsLike(String value) {
            addCriterion("type_ids like", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsNotLike(String value) {
            addCriterion("type_ids not like", value, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsIn(List<String> values) {
            addCriterion("type_ids in", values, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsNotIn(List<String> values) {
            addCriterion("type_ids not in", values, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsBetween(String value1, String value2) {
            addCriterion("type_ids between", value1, value2, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTypeIdsNotBetween(String value1, String value2) {
            addCriterion("type_ids not between", value1, value2, "typeIds");
            return (Criteria) this;
        }

        public Criteria andTimeTypeIsNull() {
            addCriterion("time_type is null");
            return (Criteria) this;
        }

        public Criteria andTimeTypeIsNotNull() {
            addCriterion("time_type is not null");
            return (Criteria) this;
        }

        public Criteria andTimeTypeEqualTo(Byte value) {
            addCriterion("time_type =", value, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeNotEqualTo(Byte value) {
            addCriterion("time_type <>", value, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeGreaterThan(Byte value) {
            addCriterion("time_type >", value, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("time_type >=", value, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeLessThan(Byte value) {
            addCriterion("time_type <", value, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeLessThanOrEqualTo(Byte value) {
            addCriterion("time_type <=", value, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeIn(List<Byte> values) {
            addCriterion("time_type in", values, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeNotIn(List<Byte> values) {
            addCriterion("time_type not in", values, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeBetween(Byte value1, Byte value2) {
            addCriterion("time_type between", value1, value2, "timeType");
            return (Criteria) this;
        }

        public Criteria andTimeTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("time_type not between", value1, value2, "timeType");
            return (Criteria) this;
        }

        public Criteria andObjectIsNull() {
            addCriterion("object is null");
            return (Criteria) this;
        }

        public Criteria andObjectIsNotNull() {
            addCriterion("object is not null");
            return (Criteria) this;
        }

        public Criteria andObjectEqualTo(Byte value) {
            addCriterion("object =", value, "object");
            return (Criteria) this;
        }

        public Criteria andObjectNotEqualTo(Byte value) {
            addCriterion("object <>", value, "object");
            return (Criteria) this;
        }

        public Criteria andObjectGreaterThan(Byte value) {
            addCriterion("object >", value, "object");
            return (Criteria) this;
        }

        public Criteria andObjectGreaterThanOrEqualTo(Byte value) {
            addCriterion("object >=", value, "object");
            return (Criteria) this;
        }

        public Criteria andObjectLessThan(Byte value) {
            addCriterion("object <", value, "object");
            return (Criteria) this;
        }

        public Criteria andObjectLessThanOrEqualTo(Byte value) {
            addCriterion("object <=", value, "object");
            return (Criteria) this;
        }

        public Criteria andObjectIn(List<Byte> values) {
            addCriterion("object in", values, "object");
            return (Criteria) this;
        }

        public Criteria andObjectNotIn(List<Byte> values) {
            addCriterion("object not in", values, "object");
            return (Criteria) this;
        }

        public Criteria andObjectBetween(Byte value1, Byte value2) {
            addCriterion("object between", value1, value2, "object");
            return (Criteria) this;
        }

        public Criteria andObjectNotBetween(Byte value1, Byte value2) {
            addCriterion("object not between", value1, value2, "object");
            return (Criteria) this;
        }

        public Criteria andRewardTypeIsNull() {
            addCriterion("reward_type is null");
            return (Criteria) this;
        }

        public Criteria andRewardTypeIsNotNull() {
            addCriterion("reward_type is not null");
            return (Criteria) this;
        }

        public Criteria andRewardTypeEqualTo(Byte value) {
            addCriterion("reward_type =", value, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeNotEqualTo(Byte value) {
            addCriterion("reward_type <>", value, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeGreaterThan(Byte value) {
            addCriterion("reward_type >", value, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("reward_type >=", value, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeLessThan(Byte value) {
            addCriterion("reward_type <", value, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeLessThanOrEqualTo(Byte value) {
            addCriterion("reward_type <=", value, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeIn(List<Byte> values) {
            addCriterion("reward_type in", values, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeNotIn(List<Byte> values) {
            addCriterion("reward_type not in", values, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeBetween(Byte value1, Byte value2) {
            addCriterion("reward_type between", value1, value2, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("reward_type not between", value1, value2, "rewardType");
            return (Criteria) this;
        }

        public Criteria andRewardDetailIsNull() {
            addCriterion("reward_detail is null");
            return (Criteria) this;
        }

        public Criteria andRewardDetailIsNotNull() {
            addCriterion("reward_detail is not null");
            return (Criteria) this;
        }

        public Criteria andRewardDetailEqualTo(String value) {
            addCriterion("reward_detail =", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailNotEqualTo(String value) {
            addCriterion("reward_detail <>", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailGreaterThan(String value) {
            addCriterion("reward_detail >", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailGreaterThanOrEqualTo(String value) {
            addCriterion("reward_detail >=", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailLessThan(String value) {
            addCriterion("reward_detail <", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailLessThanOrEqualTo(String value) {
            addCriterion("reward_detail <=", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailLike(String value) {
            addCriterion("reward_detail like", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailNotLike(String value) {
            addCriterion("reward_detail not like", value, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailIn(List<String> values) {
            addCriterion("reward_detail in", values, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailNotIn(List<String> values) {
            addCriterion("reward_detail not in", values, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailBetween(String value1, String value2) {
            addCriterion("reward_detail between", value1, value2, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andRewardDetailNotBetween(String value1, String value2) {
            addCriterion("reward_detail not between", value1, value2, "rewardDetail");
            return (Criteria) this;
        }

        public Criteria andExplainIsNull() {
            addCriterion("explain is null");
            return (Criteria) this;
        }

        public Criteria andExplainIsNotNull() {
            addCriterion("explain is not null");
            return (Criteria) this;
        }

        public Criteria andExplainEqualTo(String value) {
            addCriterion("explain =", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotEqualTo(String value) {
            addCriterion("explain <>", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThan(String value) {
            addCriterion("explain >", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThanOrEqualTo(String value) {
            addCriterion("explain >=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThan(String value) {
            addCriterion("explain <", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThanOrEqualTo(String value) {
            addCriterion("explain <=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLike(String value) {
            addCriterion("explain like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotLike(String value) {
            addCriterion("explain not like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainIn(List<String> values) {
            addCriterion("explain in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotIn(List<String> values) {
            addCriterion("explain not in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainBetween(String value1, String value2) {
            addCriterion("explain between", value1, value2, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotBetween(String value1, String value2) {
            addCriterion("explain not between", value1, value2, "explain");
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

        public Criteria andRewardLimitIsNull() {
            addCriterion("reward_limit is null");
            return (Criteria) this;
        }

        public Criteria andRewardLimitIsNotNull() {
            addCriterion("reward_limit is not null");
            return (Criteria) this;
        }

        public Criteria andRewardLimitEqualTo(Integer value) {
            addCriterion("reward_limit =", value, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitNotEqualTo(Integer value) {
            addCriterion("reward_limit <>", value, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitGreaterThan(Integer value) {
            addCriterion("reward_limit >", value, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("reward_limit >=", value, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitLessThan(Integer value) {
            addCriterion("reward_limit <", value, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitLessThanOrEqualTo(Integer value) {
            addCriterion("reward_limit <=", value, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitIn(List<Integer> values) {
            addCriterion("reward_limit in", values, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitNotIn(List<Integer> values) {
            addCriterion("reward_limit not in", values, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitBetween(Integer value1, Integer value2) {
            addCriterion("reward_limit between", value1, value2, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardLimitNotBetween(Integer value1, Integer value2) {
            addCriterion("reward_limit not between", value1, value2, "rewardLimit");
            return (Criteria) this;
        }

        public Criteria andRewardTotalIsNull() {
            addCriterion("reward_total is null");
            return (Criteria) this;
        }

        public Criteria andRewardTotalIsNotNull() {
            addCriterion("reward_total is not null");
            return (Criteria) this;
        }

        public Criteria andRewardTotalEqualTo(Integer value) {
            addCriterion("reward_total =", value, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalNotEqualTo(Integer value) {
            addCriterion("reward_total <>", value, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalGreaterThan(Integer value) {
            addCriterion("reward_total >", value, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalGreaterThanOrEqualTo(Integer value) {
            addCriterion("reward_total >=", value, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalLessThan(Integer value) {
            addCriterion("reward_total <", value, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalLessThanOrEqualTo(Integer value) {
            addCriterion("reward_total <=", value, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalIn(List<Integer> values) {
            addCriterion("reward_total in", values, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalNotIn(List<Integer> values) {
            addCriterion("reward_total not in", values, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalBetween(Integer value1, Integer value2) {
            addCriterion("reward_total between", value1, value2, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andRewardTotalNotBetween(Integer value1, Integer value2) {
            addCriterion("reward_total not between", value1, value2, "rewardTotal");
            return (Criteria) this;
        }

        public Criteria andLowTargetIsNull() {
            addCriterion("low_target is null");
            return (Criteria) this;
        }

        public Criteria andLowTargetIsNotNull() {
            addCriterion("low_target is not null");
            return (Criteria) this;
        }

        public Criteria andLowTargetEqualTo(Integer value) {
            addCriterion("low_target =", value, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetNotEqualTo(Integer value) {
            addCriterion("low_target <>", value, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetGreaterThan(Integer value) {
            addCriterion("low_target >", value, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetGreaterThanOrEqualTo(Integer value) {
            addCriterion("low_target >=", value, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetLessThan(Integer value) {
            addCriterion("low_target <", value, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetLessThanOrEqualTo(Integer value) {
            addCriterion("low_target <=", value, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetIn(List<Integer> values) {
            addCriterion("low_target in", values, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetNotIn(List<Integer> values) {
            addCriterion("low_target not in", values, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetBetween(Integer value1, Integer value2) {
            addCriterion("low_target between", value1, value2, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andLowTargetNotBetween(Integer value1, Integer value2) {
            addCriterion("low_target not between", value1, value2, "lowTarget");
            return (Criteria) this;
        }

        public Criteria andPunishIsNull() {
            addCriterion("punish is null");
            return (Criteria) this;
        }

        public Criteria andPunishIsNotNull() {
            addCriterion("punish is not null");
            return (Criteria) this;
        }

        public Criteria andPunishEqualTo(Integer value) {
            addCriterion("punish =", value, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishNotEqualTo(Integer value) {
            addCriterion("punish <>", value, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishGreaterThan(Integer value) {
            addCriterion("punish >", value, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishGreaterThanOrEqualTo(Integer value) {
            addCriterion("punish >=", value, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishLessThan(Integer value) {
            addCriterion("punish <", value, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishLessThanOrEqualTo(Integer value) {
            addCriterion("punish <=", value, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishIn(List<Integer> values) {
            addCriterion("punish in", values, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishNotIn(List<Integer> values) {
            addCriterion("punish not in", values, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishBetween(Integer value1, Integer value2) {
            addCriterion("punish between", value1, value2, "punish");
            return (Criteria) this;
        }

        public Criteria andPunishNotBetween(Integer value1, Integer value2) {
            addCriterion("punish not between", value1, value2, "punish");
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