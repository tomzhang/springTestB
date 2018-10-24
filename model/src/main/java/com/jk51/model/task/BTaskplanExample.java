package com.jk51.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BTaskplanExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public BTaskplanExample() {
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

        public Criteria andTaskIdsIsNull() {
            addCriterion("task_ids is null");
            return (Criteria) this;
        }

        public Criteria andTaskIdsIsNotNull() {
            addCriterion("task_ids is not null");
            return (Criteria) this;
        }

        public Criteria andTaskIdsEqualTo(String value) {
            addCriterion("task_ids =", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsNotEqualTo(String value) {
            addCriterion("task_ids <>", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsGreaterThan(String value) {
            addCriterion("task_ids >", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsGreaterThanOrEqualTo(String value) {
            addCriterion("task_ids >=", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsLessThan(String value) {
            addCriterion("task_ids <", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsLessThanOrEqualTo(String value) {
            addCriterion("task_ids <=", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsLike(String value) {
            addCriterion("task_ids like", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsNotLike(String value) {
            addCriterion("task_ids not like", value, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsIn(List<String> values) {
            addCriterion("task_ids in", values, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsNotIn(List<String> values) {
            addCriterion("task_ids not in", values, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsBetween(String value1, String value2) {
            addCriterion("task_ids between", value1, value2, "taskIds");
            return (Criteria) this;
        }

        public Criteria andTaskIdsNotBetween(String value1, String value2) {
            addCriterion("task_ids not between", value1, value2, "taskIds");
            return (Criteria) this;
        }

        public Criteria andJoinTypeIsNull() {
            addCriterion("join_type is null");
            return (Criteria) this;
        }

        public Criteria andJoinTypeIsNotNull() {
            addCriterion("join_type is not null");
            return (Criteria) this;
        }

        public Criteria andJoinTypeEqualTo(Byte value) {
            addCriterion("join_type =", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotEqualTo(Byte value) {
            addCriterion("join_type <>", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeGreaterThan(Byte value) {
            addCriterion("join_type >", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("join_type >=", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeLessThan(Byte value) {
            addCriterion("join_type <", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeLessThanOrEqualTo(Byte value) {
            addCriterion("join_type <=", value, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeIn(List<Byte> values) {
            addCriterion("join_type in", values, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotIn(List<Byte> values) {
            addCriterion("join_type not in", values, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeBetween(Byte value1, Byte value2) {
            addCriterion("join_type between", value1, value2, "joinType");
            return (Criteria) this;
        }

        public Criteria andJoinTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("join_type not between", value1, value2, "joinType");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNull() {
            addCriterion("start_time is null");
            return (Criteria) this;
        }

        public Criteria andStartTimeIsNotNull() {
            addCriterion("start_time is not null");
            return (Criteria) this;
        }

        public Criteria andStartTimeEqualTo(Date value) {
            addCriterion("start_time =", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotEqualTo(Date value) {
            addCriterion("start_time <>", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThan(Date value) {
            addCriterion("start_time >", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("start_time >=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThan(Date value) {
            addCriterion("start_time <", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("start_time <=", value, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeIn(List<Date> values) {
            addCriterion("start_time in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotIn(List<Date> values) {
            addCriterion("start_time not in", values, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeBetween(Date value1, Date value2) {
            addCriterion("start_time between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("start_time not between", value1, value2, "startTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNull() {
            addCriterion("end_time is null");
            return (Criteria) this;
        }

        public Criteria andEndTimeIsNotNull() {
            addCriterion("end_time is not null");
            return (Criteria) this;
        }

        public Criteria andEndTimeEqualTo(Date value) {
            addCriterion("end_time =", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotEqualTo(Date value) {
            addCriterion("end_time <>", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThan(Date value) {
            addCriterion("end_time >", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("end_time >=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThan(Date value) {
            addCriterion("end_time <", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("end_time <=", value, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeIn(List<Date> values) {
            addCriterion("end_time in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotIn(List<Date> values) {
            addCriterion("end_time not in", values, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeBetween(Date value1, Date value2) {
            addCriterion("end_time between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("end_time not between", value1, value2, "endTime");
            return (Criteria) this;
        }

        public Criteria andActiveTypeIsNull() {
            addCriterion("active_type is null");
            return (Criteria) this;
        }

        public Criteria andActiveTypeIsNotNull() {
            addCriterion("active_type is not null");
            return (Criteria) this;
        }

        public Criteria andActiveTypeEqualTo(Byte value) {
            addCriterion("active_type =", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeNotEqualTo(Byte value) {
            addCriterion("active_type <>", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeGreaterThan(Byte value) {
            addCriterion("active_type >", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("active_type >=", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeLessThan(Byte value) {
            addCriterion("active_type <", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeLessThanOrEqualTo(Byte value) {
            addCriterion("active_type <=", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeLike(Byte value) {
            addCriterion("active_type like", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeNotLike(Byte value) {
            addCriterion("active_type not like", value, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeIn(List<Byte> values) {
            addCriterion("active_type in", values, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeNotIn(List<Byte> values) {
            addCriterion("active_type not in", values, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeBetween(Byte value1, Byte value2) {
            addCriterion("active_type between", value1, value2, "activeType");
            return (Criteria) this;
        }

        public Criteria andActiveTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("active_type not between", value1, value2, "activeType");
            return (Criteria) this;
        }

        public Criteria andDayNumIsNull() {
            addCriterion("day_num is null");
            return (Criteria) this;
        }

        public Criteria andDayNumIsNotNull() {
            addCriterion("day_num is not null");
            return (Criteria) this;
        }

        public Criteria andDayNumEqualTo(String value) {
            addCriterion("day_num =", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumNotEqualTo(String value) {
            addCriterion("day_num <>", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumGreaterThan(String value) {
            addCriterion("day_num >", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumGreaterThanOrEqualTo(String value) {
            addCriterion("day_num >=", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumLessThan(String value) {
            addCriterion("day_num <", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumLessThanOrEqualTo(String value) {
            addCriterion("day_num <=", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumLike(String value) {
            addCriterion("day_num like", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumNotLike(String value) {
            addCriterion("day_num not like", value, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumIn(List<String> values) {
            addCriterion("day_num in", values, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumNotIn(List<String> values) {
            addCriterion("day_num not in", values, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumBetween(String value1, String value2) {
            addCriterion("day_num between", value1, value2, "dayNum");
            return (Criteria) this;
        }

        public Criteria andDayNumNotBetween(String value1, String value2) {
            addCriterion("day_num not between", value1, value2, "dayNum");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNull() {
            addCriterion("source_type is null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIsNotNull() {
            addCriterion("source_type is not null");
            return (Criteria) this;
        }

        public Criteria andSourceTypeEqualTo(Byte value) {
            addCriterion("source_type =", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotEqualTo(Byte value) {
            addCriterion("source_type <>", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThan(Byte value) {
            addCriterion("source_type >", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("source_type >=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThan(Byte value) {
            addCriterion("source_type <", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeLessThanOrEqualTo(Byte value) {
            addCriterion("source_type <=", value, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeIn(List<Byte> values) {
            addCriterion("source_type in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotIn(List<Byte> values) {
            addCriterion("source_type not in", values, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeBetween(Byte value1, Byte value2) {
            addCriterion("source_type between", value1, value2, "sourceType");
            return (Criteria) this;
        }

        public Criteria andSourceTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("source_type not between", value1, value2, "sourceType");
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

        public Criteria andAppPositionIsNull() {
            addCriterion("app_position is null");
            return (Criteria) this;
        }

        public Criteria andAppPositionIsNotNull() {
            addCriterion("app_position is not null");
            return (Criteria) this;
        }

        public Criteria andAppPositionEqualTo(Byte value) {
            addCriterion("app_position =", value, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionNotEqualTo(Byte value) {
            addCriterion("app_position <>", value, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionGreaterThan(Byte value) {
            addCriterion("app_position >", value, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionGreaterThanOrEqualTo(Byte value) {
            addCriterion("app_position >=", value, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionLessThan(Byte value) {
            addCriterion("app_position <", value, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionLessThanOrEqualTo(Byte value) {
            addCriterion("app_position <=", value, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionIn(List<Byte> values) {
            addCriterion("app_position in", values, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionNotIn(List<Byte> values) {
            addCriterion("app_position not in", values, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionBetween(Byte value1, Byte value2) {
            addCriterion("app_position between", value1, value2, "appPosition");
            return (Criteria) this;
        }

        public Criteria andAppPositionNotBetween(Byte value1, Byte value2) {
            addCriterion("app_position not between", value1, value2, "appPosition");
            return (Criteria) this;
        }

        public Criteria andExplanIsNull() {
            addCriterion("explan is null");
            return (Criteria) this;
        }

        public Criteria andExplanIsNotNull() {
            addCriterion("explan is not null");
            return (Criteria) this;
        }

        public Criteria andExplanEqualTo(String value) {
            addCriterion("explan =", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanNotEqualTo(String value) {
            addCriterion("explan <>", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanGreaterThan(String value) {
            addCriterion("explan >", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanGreaterThanOrEqualTo(String value) {
            addCriterion("explan >=", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanLessThan(String value) {
            addCriterion("explan <", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanLessThanOrEqualTo(String value) {
            addCriterion("explan <=", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanLike(String value) {
            addCriterion("explan like", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanNotLike(String value) {
            addCriterion("explan not like", value, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanIn(List<String> values) {
            addCriterion("explan in", values, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanNotIn(List<String> values) {
            addCriterion("explan not in", values, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanBetween(String value1, String value2) {
            addCriterion("explan between", value1, value2, "explan");
            return (Criteria) this;
        }

        public Criteria andExplanNotBetween(String value1, String value2) {
            addCriterion("explan not between", value1, value2, "explan");
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
