package com.jk51.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BTaskcountExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public BTaskcountExample() {
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

        public Criteria andJoinIdIsNull() {
            addCriterion("join_id is null");
            return (Criteria) this;
        }

        public Criteria andJoinIdIsNotNull() {
            addCriterion("join_id is not null");
            return (Criteria) this;
        }

        public Criteria andJoinIdEqualTo(Integer value) {
            addCriterion("join_id =", value, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdNotEqualTo(Integer value) {
            addCriterion("join_id <>", value, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdGreaterThan(Integer value) {
            addCriterion("join_id >", value, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("join_id >=", value, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdLessThan(Integer value) {
            addCriterion("join_id <", value, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdLessThanOrEqualTo(Integer value) {
            addCriterion("join_id <=", value, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdIn(List<Integer> values) {
            addCriterion("join_id in", values, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdNotIn(List<Integer> values) {
            addCriterion("join_id not in", values, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdBetween(Integer value1, Integer value2) {
            addCriterion("join_id between", value1, value2, "joinId");
            return (Criteria) this;
        }

        public Criteria andJoinIdNotBetween(Integer value1, Integer value2) {
            addCriterion("join_id not between", value1, value2, "joinId");
            return (Criteria) this;
        }

        public Criteria andCountValueIsNull() {
            addCriterion("count_value is null");
            return (Criteria) this;
        }

        public Criteria andCountValueIsNotNull() {
            addCriterion("count_value is not null");
            return (Criteria) this;
        }

        public Criteria andCountValueEqualTo(Integer value) {
            addCriterion("count_value =", value, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueNotEqualTo(Integer value) {
            addCriterion("count_value <>", value, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueGreaterThan(Integer value) {
            addCriterion("count_value >", value, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("count_value >=", value, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueLessThan(Integer value) {
            addCriterion("count_value <", value, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueLessThanOrEqualTo(Integer value) {
            addCriterion("count_value <=", value, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueIn(List<Integer> values) {
            addCriterion("count_value in", values, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueNotIn(List<Integer> values) {
            addCriterion("count_value not in", values, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueBetween(Integer value1, Integer value2) {
            addCriterion("count_value between", value1, value2, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountValueNotBetween(Integer value1, Integer value2) {
            addCriterion("count_value not between", value1, value2, "countValue");
            return (Criteria) this;
        }

        public Criteria andCountStartIsNull() {
            addCriterion("count_start is null");
            return (Criteria) this;
        }

        public Criteria andCountStartIsNotNull() {
            addCriterion("count_start is not null");
            return (Criteria) this;
        }

        public Criteria andCountStartEqualTo(Date value) {
            addCriterion("count_start =", value, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartNotEqualTo(Date value) {
            addCriterion("count_start <>", value, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartGreaterThan(Date value) {
            addCriterion("count_start >", value, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartGreaterThanOrEqualTo(Date value) {
            addCriterion("count_start >=", value, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartLessThan(Date value) {
            addCriterion("count_start <", value, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartLessThanOrEqualTo(Date value) {
            addCriterion("count_start <=", value, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartIn(List<Date> values) {
            addCriterion("count_start in", values, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartNotIn(List<Date> values) {
            addCriterion("count_start not in", values, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartBetween(Date value1, Date value2) {
            addCriterion("count_start between", value1, value2, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountStartNotBetween(Date value1, Date value2) {
            addCriterion("count_start not between", value1, value2, "countStart");
            return (Criteria) this;
        }

        public Criteria andCountEndIsNull() {
            addCriterion("count_end is null");
            return (Criteria) this;
        }

        public Criteria andCountEndIsNotNull() {
            addCriterion("count_end is not null");
            return (Criteria) this;
        }

        public Criteria andCountEndEqualTo(Date value) {
            addCriterion("count_end =", value, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndNotEqualTo(Date value) {
            addCriterion("count_end <>", value, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndGreaterThan(Date value) {
            addCriterion("count_end >", value, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndGreaterThanOrEqualTo(Date value) {
            addCriterion("count_end >=", value, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndLessThan(Date value) {
            addCriterion("count_end <", value, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndLessThanOrEqualTo(Date value) {
            addCriterion("count_end <=", value, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndIn(List<Date> values) {
            addCriterion("count_end in", values, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndNotIn(List<Date> values) {
            addCriterion("count_end not in", values, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndBetween(Date value1, Date value2) {
            addCriterion("count_end between", value1, value2, "countEnd");
            return (Criteria) this;
        }

        public Criteria andCountEndNotBetween(Date value1, Date value2) {
            addCriterion("count_end not between", value1, value2, "countEnd");
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