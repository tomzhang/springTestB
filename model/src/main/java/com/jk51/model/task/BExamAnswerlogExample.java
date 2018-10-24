package com.jk51.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BExamAnswerlogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public BExamAnswerlogExample() {
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

        public Criteria andPlanIdIsNull() {
            addCriterion("plan_id is null");
            return (Criteria) this;
        }

        public Criteria andPlanIdIsNotNull() {
            addCriterion("plan_id is not null");
            return (Criteria) this;
        }

        public Criteria andPlanIdEqualTo(Integer value) {
            addCriterion("plan_id =", value, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdNotEqualTo(Integer value) {
            addCriterion("plan_id <>", value, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdGreaterThan(Integer value) {
            addCriterion("plan_id >", value, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("plan_id >=", value, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdLessThan(Integer value) {
            addCriterion("plan_id <", value, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdLessThanOrEqualTo(Integer value) {
            addCriterion("plan_id <=", value, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdIn(List<Integer> values) {
            addCriterion("plan_id in", values, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdNotIn(List<Integer> values) {
            addCriterion("plan_id not in", values, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdBetween(Integer value1, Integer value2) {
            addCriterion("plan_id between", value1, value2, "planId");
            return (Criteria) this;
        }

        public Criteria andPlanIdNotBetween(Integer value1, Integer value2) {
            addCriterion("plan_id not between", value1, value2, "planId");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNull() {
            addCriterion("task_id is null");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNotNull() {
            addCriterion("task_id is not null");
            return (Criteria) this;
        }

        public Criteria andTaskIdEqualTo(Integer value) {
            addCriterion("task_id =", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotEqualTo(Integer value) {
            addCriterion("task_id <>", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThan(Integer value) {
            addCriterion("task_id >", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("task_id >=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThan(Integer value) {
            addCriterion("task_id <", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThanOrEqualTo(Integer value) {
            addCriterion("task_id <=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdIn(List<Integer> values) {
            addCriterion("task_id in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotIn(List<Integer> values) {
            addCriterion("task_id not in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdBetween(Integer value1, Integer value2) {
            addCriterion("task_id between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotBetween(Integer value1, Integer value2) {
            addCriterion("task_id not between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdIsNull() {
            addCriterion("execute_id is null");
            return (Criteria) this;
        }

        public Criteria andExecuteIdIsNotNull() {
            addCriterion("execute_id is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteIdEqualTo(Integer value) {
            addCriterion("execute_id =", value, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdNotEqualTo(Integer value) {
            addCriterion("execute_id <>", value, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdGreaterThan(Integer value) {
            addCriterion("execute_id >", value, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("execute_id >=", value, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdLessThan(Integer value) {
            addCriterion("execute_id <", value, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdLessThanOrEqualTo(Integer value) {
            addCriterion("execute_id <=", value, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdIn(List<Integer> values) {
            addCriterion("execute_id in", values, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdNotIn(List<Integer> values) {
            addCriterion("execute_id not in", values, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdBetween(Integer value1, Integer value2) {
            addCriterion("execute_id between", value1, value2, "executeId");
            return (Criteria) this;
        }

        public Criteria andExecuteIdNotBetween(Integer value1, Integer value2) {
            addCriterion("execute_id not between", value1, value2, "executeId");
            return (Criteria) this;
        }

        public Criteria andExamIdIsNull() {
            addCriterion("exam_id is null");
            return (Criteria) this;
        }

        public Criteria andExamIdIsNotNull() {
            addCriterion("exam_id is not null");
            return (Criteria) this;
        }

        public Criteria andExamIdEqualTo(Integer value) {
            addCriterion("exam_id =", value, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdNotEqualTo(Integer value) {
            addCriterion("exam_id <>", value, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdGreaterThan(Integer value) {
            addCriterion("exam_id >", value, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("exam_id >=", value, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdLessThan(Integer value) {
            addCriterion("exam_id <", value, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdLessThanOrEqualTo(Integer value) {
            addCriterion("exam_id <=", value, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdIn(List<Integer> values) {
            addCriterion("exam_id in", values, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdNotIn(List<Integer> values) {
            addCriterion("exam_id not in", values, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdBetween(Integer value1, Integer value2) {
            addCriterion("exam_id between", value1, value2, "examId");
            return (Criteria) this;
        }

        public Criteria andExamIdNotBetween(Integer value1, Integer value2) {
            addCriterion("exam_id not between", value1, value2, "examId");
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

        public Criteria andNumIsNull() {
            addCriterion("num is null");
            return (Criteria) this;
        }

        public Criteria andNumIsNotNull() {
            addCriterion("num is not null");
            return (Criteria) this;
        }

        public Criteria andNumEqualTo(Byte value) {
            addCriterion("num =", value, "num");
            return (Criteria) this;
        }

        public Criteria andNumNotEqualTo(Byte value) {
            addCriterion("num <>", value, "num");
            return (Criteria) this;
        }

        public Criteria andNumGreaterThan(Byte value) {
            addCriterion("num >", value, "num");
            return (Criteria) this;
        }

        public Criteria andNumGreaterThanOrEqualTo(Byte value) {
            addCriterion("num >=", value, "num");
            return (Criteria) this;
        }

        public Criteria andNumLessThan(Byte value) {
            addCriterion("num <", value, "num");
            return (Criteria) this;
        }

        public Criteria andNumLessThanOrEqualTo(Byte value) {
            addCriterion("num <=", value, "num");
            return (Criteria) this;
        }

        public Criteria andNumIn(List<Byte> values) {
            addCriterion("num in", values, "num");
            return (Criteria) this;
        }

        public Criteria andNumNotIn(List<Byte> values) {
            addCriterion("num not in", values, "num");
            return (Criteria) this;
        }

        public Criteria andNumBetween(Byte value1, Byte value2) {
            addCriterion("num between", value1, value2, "num");
            return (Criteria) this;
        }

        public Criteria andNumNotBetween(Byte value1, Byte value2) {
            addCriterion("num not between", value1, value2, "num");
            return (Criteria) this;
        }

        public Criteria andTotalIsNull() {
            addCriterion("total is null");
            return (Criteria) this;
        }

        public Criteria andTotalIsNotNull() {
            addCriterion("total is not null");
            return (Criteria) this;
        }

        public Criteria andTotalEqualTo(Byte value) {
            addCriterion("total =", value, "total");
            return (Criteria) this;
        }

        public Criteria andTotalNotEqualTo(Byte value) {
            addCriterion("total <>", value, "total");
            return (Criteria) this;
        }

        public Criteria andTotalGreaterThan(Byte value) {
            addCriterion("total >", value, "total");
            return (Criteria) this;
        }

        public Criteria andTotalGreaterThanOrEqualTo(Byte value) {
            addCriterion("total >=", value, "total");
            return (Criteria) this;
        }

        public Criteria andTotalLessThan(Byte value) {
            addCriterion("total <", value, "total");
            return (Criteria) this;
        }

        public Criteria andTotalLessThanOrEqualTo(Byte value) {
            addCriterion("total <=", value, "total");
            return (Criteria) this;
        }

        public Criteria andTotalIn(List<Byte> values) {
            addCriterion("total in", values, "total");
            return (Criteria) this;
        }

        public Criteria andTotalNotIn(List<Byte> values) {
            addCriterion("total not in", values, "total");
            return (Criteria) this;
        }

        public Criteria andTotalBetween(Byte value1, Byte value2) {
            addCriterion("total between", value1, value2, "total");
            return (Criteria) this;
        }

        public Criteria andTotalNotBetween(Byte value1, Byte value2) {
            addCriterion("total not between", value1, value2, "total");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdIsNull() {
            addCriterion("store_admin_id is null");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdIsNotNull() {
            addCriterion("store_admin_id is not null");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdEqualTo(Integer value) {
            addCriterion("store_admin_id =", value, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdNotEqualTo(Integer value) {
            addCriterion("store_admin_id <>", value, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdGreaterThan(Integer value) {
            addCriterion("store_admin_id >", value, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("store_admin_id >=", value, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdLessThan(Integer value) {
            addCriterion("store_admin_id <", value, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdLessThanOrEqualTo(Integer value) {
            addCriterion("store_admin_id <=", value, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdIn(List<Integer> values) {
            addCriterion("store_admin_id in", values, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdNotIn(List<Integer> values) {
            addCriterion("store_admin_id not in", values, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdBetween(Integer value1, Integer value2) {
            addCriterion("store_admin_id between", value1, value2, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreAdminIdNotBetween(Integer value1, Integer value2) {
            addCriterion("store_admin_id not between", value1, value2, "storeAdminId");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNull() {
            addCriterion("store_id is null");
            return (Criteria) this;
        }

        public Criteria andStoreIdIsNotNull() {
            addCriterion("store_id is not null");
            return (Criteria) this;
        }

        public Criteria andStoreIdEqualTo(Integer value) {
            addCriterion("store_id =", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotEqualTo(Integer value) {
            addCriterion("store_id <>", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThan(Integer value) {
            addCriterion("store_id >", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("store_id >=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThan(Integer value) {
            addCriterion("store_id <", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdLessThanOrEqualTo(Integer value) {
            addCriterion("store_id <=", value, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdIn(List<Integer> values) {
            addCriterion("store_id in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotIn(List<Integer> values) {
            addCriterion("store_id not in", values, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdBetween(Integer value1, Integer value2) {
            addCriterion("store_id between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreIdNotBetween(Integer value1, Integer value2) {
            addCriterion("store_id not between", value1, value2, "storeId");
            return (Criteria) this;
        }

        public Criteria andStoreNameIsNull() {
            addCriterion("store_name is null");
            return (Criteria) this;
        }

        public Criteria andStoreNameIsNotNull() {
            addCriterion("store_name is not null");
            return (Criteria) this;
        }

        public Criteria andStoreNameEqualTo(String value) {
            addCriterion("store_name =", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotEqualTo(String value) {
            addCriterion("store_name <>", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameGreaterThan(String value) {
            addCriterion("store_name >", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameGreaterThanOrEqualTo(String value) {
            addCriterion("store_name >=", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameLessThan(String value) {
            addCriterion("store_name <", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameLessThanOrEqualTo(String value) {
            addCriterion("store_name <=", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameLike(String value) {
            addCriterion("store_name like", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotLike(String value) {
            addCriterion("store_name not like", value, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameIn(List<String> values) {
            addCriterion("store_name in", values, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotIn(List<String> values) {
            addCriterion("store_name not in", values, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameBetween(String value1, String value2) {
            addCriterion("store_name between", value1, value2, "storeName");
            return (Criteria) this;
        }

        public Criteria andStoreNameNotBetween(String value1, String value2) {
            addCriterion("store_name not between", value1, value2, "storeName");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeIsNull() {
            addCriterion("clerk_invitation_code is null");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeIsNotNull() {
            addCriterion("clerk_invitation_code is not null");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeEqualTo(String value) {
            addCriterion("clerk_invitation_code =", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeNotEqualTo(String value) {
            addCriterion("clerk_invitation_code <>", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeGreaterThan(String value) {
            addCriterion("clerk_invitation_code >", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeGreaterThanOrEqualTo(String value) {
            addCriterion("clerk_invitation_code >=", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeLessThan(String value) {
            addCriterion("clerk_invitation_code <", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeLessThanOrEqualTo(String value) {
            addCriterion("clerk_invitation_code <=", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeLike(String value) {
            addCriterion("clerk_invitation_code like", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeNotLike(String value) {
            addCriterion("clerk_invitation_code not like", value, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeIn(List<String> values) {
            addCriterion("clerk_invitation_code in", values, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeNotIn(List<String> values) {
            addCriterion("clerk_invitation_code not in", values, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeBetween(String value1, String value2) {
            addCriterion("clerk_invitation_code between", value1, value2, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andClerkInvitationCodeNotBetween(String value1, String value2) {
            addCriterion("clerk_invitation_code not between", value1, value2, "clerkInvitationCode");
            return (Criteria) this;
        }

        public Criteria andRewardIsNull() {
            addCriterion("reward is null");
            return (Criteria) this;
        }

        public Criteria andRewardIsNotNull() {
            addCriterion("reward is not null");
            return (Criteria) this;
        }

        public Criteria andRewardEqualTo(Integer value) {
            addCriterion("reward =", value, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardNotEqualTo(Integer value) {
            addCriterion("reward <>", value, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardGreaterThan(Integer value) {
            addCriterion("reward >", value, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardGreaterThanOrEqualTo(Integer value) {
            addCriterion("reward >=", value, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardLessThan(Integer value) {
            addCriterion("reward <", value, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardLessThanOrEqualTo(Integer value) {
            addCriterion("reward <=", value, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardIn(List<Integer> values) {
            addCriterion("reward in", values, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardNotIn(List<Integer> values) {
            addCriterion("reward not in", values, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardBetween(Integer value1, Integer value2) {
            addCriterion("reward between", value1, value2, "reward");
            return (Criteria) this;
        }

        public Criteria andRewardNotBetween(Integer value1, Integer value2) {
            addCriterion("reward not between", value1, value2, "reward");
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

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andSnapshotIsNull() {
            addCriterion("snapshot is null");
            return (Criteria) this;
        }

        public Criteria andSnapshotIsNotNull() {
            addCriterion("snapshot is not null");
            return (Criteria) this;
        }

        public Criteria andSnapshotEqualTo(String value) {
            addCriterion("snapshot =", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotNotEqualTo(String value) {
            addCriterion("snapshot <>", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotGreaterThan(String value) {
            addCriterion("snapshot >", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotGreaterThanOrEqualTo(String value) {
            addCriterion("snapshot >=", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotLessThan(String value) {
            addCriterion("snapshot <", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotLessThanOrEqualTo(String value) {
            addCriterion("snapshot <=", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotLike(String value) {
            addCriterion("snapshot like", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotNotLike(String value) {
            addCriterion("snapshot not like", value, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotIn(List<String> values) {
            addCriterion("snapshot in", values, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotNotIn(List<String> values) {
            addCriterion("snapshot not in", values, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotBetween(String value1, String value2) {
            addCriterion("snapshot between", value1, value2, "snapshot");
            return (Criteria) this;
        }

        public Criteria andSnapshotNotBetween(String value1, String value2) {
            addCriterion("snapshot not between", value1, value2, "snapshot");
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