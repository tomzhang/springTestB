package com.jk51.model.task;

import java.util.ArrayList;
import java.util.List;

public class TCounttypeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public TCounttypeExample() {
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

        public Criteria andGroupIdIsNull() {
            addCriterion("group_id is null");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNotNull() {
            addCriterion("group_id is not null");
            return (Criteria) this;
        }

        public Criteria andGroupIdEqualTo(Integer value) {
            addCriterion("group_id =", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotEqualTo(Integer value) {
            addCriterion("group_id <>", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThan(Integer value) {
            addCriterion("group_id >", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("group_id >=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThan(Integer value) {
            addCriterion("group_id <", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThanOrEqualTo(Integer value) {
            addCriterion("group_id <=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdIn(List<Integer> values) {
            addCriterion("group_id in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotIn(List<Integer> values) {
            addCriterion("group_id not in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdBetween(Integer value1, Integer value2) {
            addCriterion("group_id between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotBetween(Integer value1, Integer value2) {
            addCriterion("group_id not between", value1, value2, "groupId");
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

        public Criteria andTblNameIsNull() {
            addCriterion("tbl_name is null");
            return (Criteria) this;
        }

        public Criteria andTblNameIsNotNull() {
            addCriterion("tbl_name is not null");
            return (Criteria) this;
        }

        public Criteria andTblNameEqualTo(String value) {
            addCriterion("tbl_name =", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameNotEqualTo(String value) {
            addCriterion("tbl_name <>", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameGreaterThan(String value) {
            addCriterion("tbl_name >", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameGreaterThanOrEqualTo(String value) {
            addCriterion("tbl_name >=", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameLessThan(String value) {
            addCriterion("tbl_name <", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameLessThanOrEqualTo(String value) {
            addCriterion("tbl_name <=", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameLike(String value) {
            addCriterion("tbl_name like", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameNotLike(String value) {
            addCriterion("tbl_name not like", value, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameIn(List<String> values) {
            addCriterion("tbl_name in", values, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameNotIn(List<String> values) {
            addCriterion("tbl_name not in", values, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameBetween(String value1, String value2) {
            addCriterion("tbl_name between", value1, value2, "tblName");
            return (Criteria) this;
        }

        public Criteria andTblNameNotBetween(String value1, String value2) {
            addCriterion("tbl_name not between", value1, value2, "tblName");
            return (Criteria) this;
        }

        public Criteria andFilterConditionIsNull() {
            addCriterion("filter_condition is null");
            return (Criteria) this;
        }

        public Criteria andFilterConditionIsNotNull() {
            addCriterion("filter_condition is not null");
            return (Criteria) this;
        }

        public Criteria andFilterConditionEqualTo(String value) {
            addCriterion("filter_condition =", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionNotEqualTo(String value) {
            addCriterion("filter_condition <>", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionGreaterThan(String value) {
            addCriterion("filter_condition >", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionGreaterThanOrEqualTo(String value) {
            addCriterion("filter_condition >=", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionLessThan(String value) {
            addCriterion("filter_condition <", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionLessThanOrEqualTo(String value) {
            addCriterion("filter_condition <=", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionLike(String value) {
            addCriterion("filter_condition like", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionNotLike(String value) {
            addCriterion("filter_condition not like", value, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionIn(List<String> values) {
            addCriterion("filter_condition in", values, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionNotIn(List<String> values) {
            addCriterion("filter_condition not in", values, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionBetween(String value1, String value2) {
            addCriterion("filter_condition between", value1, value2, "filterCondition");
            return (Criteria) this;
        }

        public Criteria andFilterConditionNotBetween(String value1, String value2) {
            addCriterion("filter_condition not between", value1, value2, "filterCondition");
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