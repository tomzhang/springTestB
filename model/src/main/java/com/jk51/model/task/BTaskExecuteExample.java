package com.jk51.model.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BTaskExecuteExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public BTaskExecuteExample() {
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

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
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

        public Criteria andCompleteIsNull() {
            addCriterion("complete is null");
            return (Criteria) this;
        }

        public Criteria andCompleteIsNotNull() {
            addCriterion("complete is not null");
            return (Criteria) this;
        }

        public Criteria andCompleteEqualTo(Boolean value) {
            addCriterion("complete =", value, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteNotEqualTo(Boolean value) {
            addCriterion("complete <>", value, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteGreaterThan(Boolean value) {
            addCriterion("complete >", value, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteGreaterThanOrEqualTo(Boolean value) {
            addCriterion("complete >=", value, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteLessThan(Boolean value) {
            addCriterion("complete <", value, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteLessThanOrEqualTo(Boolean value) {
            addCriterion("complete <=", value, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteIn(List<Boolean> values) {
            addCriterion("complete in", values, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteNotIn(List<Boolean> values) {
            addCriterion("complete not in", values, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteBetween(Boolean value1, Boolean value2) {
            addCriterion("complete between", value1, value2, "complete");
            return (Criteria) this;
        }

        public Criteria andCompleteNotBetween(Boolean value1, Boolean value2) {
            addCriterion("complete not between", value1, value2, "complete");
            return (Criteria) this;
        }

        public Criteria andStartDayIsNull() {
            addCriterion("start_day is null");
            return (Criteria) this;
        }

        public Criteria andStartDayIsNotNull() {
            addCriterion("start_day is not null");
            return (Criteria) this;
        }

        public Criteria andStartDayEqualTo(Date value) {
            addCriterionForJDBCDate("start_day =", value, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayNotEqualTo(Date value) {
            addCriterionForJDBCDate("start_day <>", value, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayGreaterThan(Date value) {
            addCriterionForJDBCDate("start_day >", value, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("start_day >=", value, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayLessThan(Date value) {
            addCriterionForJDBCDate("start_day <", value, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("start_day <=", value, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayIn(List<Date> values) {
            addCriterionForJDBCDate("start_day in", values, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayNotIn(List<Date> values) {
            addCriterionForJDBCDate("start_day not in", values, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("start_day between", value1, value2, "startDay");
            return (Criteria) this;
        }

        public Criteria andStartDayNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("start_day not between", value1, value2, "startDay");
            return (Criteria) this;
        }

        public Criteria andEndDayIsNull() {
            addCriterion("end_day is null");
            return (Criteria) this;
        }

        public Criteria andEndDayIsNotNull() {
            addCriterion("end_day is not null");
            return (Criteria) this;
        }

        public Criteria andEndDayEqualTo(Date value) {
            addCriterionForJDBCDate("end_day =", value, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayNotEqualTo(Date value) {
            addCriterionForJDBCDate("end_day <>", value, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayGreaterThan(Date value) {
            addCriterionForJDBCDate("end_day >", value, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("end_day >=", value, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayLessThan(Date value) {
            addCriterionForJDBCDate("end_day <", value, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("end_day <=", value, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayIn(List<Date> values) {
            addCriterionForJDBCDate("end_day in", values, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayNotIn(List<Date> values) {
            addCriterionForJDBCDate("end_day not in", values, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("end_day between", value1, value2, "endDay");
            return (Criteria) this;
        }

        public Criteria andEndDayNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("end_day not between", value1, value2, "endDay");
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