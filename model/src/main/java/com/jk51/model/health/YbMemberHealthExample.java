package com.jk51.model.health;

import java.util.ArrayList;
import java.util.List;

public class YbMemberHealthExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public YbMemberHealthExample() {
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

        public Criteria andMemberIdIsNull() {
            addCriterion("memberId is null");
            return (Criteria) this;
        }

        public Criteria andMemberIdIsNotNull() {
            addCriterion("memberId is not null");
            return (Criteria) this;
        }

        public Criteria andMemberIdEqualTo(Integer value) {
            addCriterion("memberId =", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotEqualTo(Integer value) {
            addCriterion("memberId <>", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdGreaterThan(Integer value) {
            addCriterion("memberId >", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("memberId >=", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdLessThan(Integer value) {
            addCriterion("memberId <", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdLessThanOrEqualTo(Integer value) {
            addCriterion("memberId <=", value, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdIn(List<Integer> values) {
            addCriterion("memberId in", values, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotIn(List<Integer> values) {
            addCriterion("memberId not in", values, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdBetween(Integer value1, Integer value2) {
            addCriterion("memberId between", value1, value2, "memberId");
            return (Criteria) this;
        }

        public Criteria andMemberIdNotBetween(Integer value1, Integer value2) {
            addCriterion("memberId not between", value1, value2, "memberId");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNull() {
            addCriterion("cardNo is null");
            return (Criteria) this;
        }

        public Criteria andCardNoIsNotNull() {
            addCriterion("cardNo is not null");
            return (Criteria) this;
        }

        public Criteria andCardNoEqualTo(String value) {
            addCriterion("cardNo =", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotEqualTo(String value) {
            addCriterion("cardNo <>", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThan(String value) {
            addCriterion("cardNo >", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoGreaterThanOrEqualTo(String value) {
            addCriterion("cardNo >=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThan(String value) {
            addCriterion("cardNo <", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLessThanOrEqualTo(String value) {
            addCriterion("cardNo <=", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoLike(String value) {
            addCriterion("cardNo like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotLike(String value) {
            addCriterion("cardNo not like", value, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoIn(List<String> values) {
            addCriterion("cardNo in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotIn(List<String> values) {
            addCriterion("cardNo not in", values, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoBetween(String value1, String value2) {
            addCriterion("cardNo between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andCardNoNotBetween(String value1, String value2) {
            addCriterion("cardNo not between", value1, value2, "cardNo");
            return (Criteria) this;
        }

        public Criteria andHeightIsNull() {
            addCriterion("height is null");
            return (Criteria) this;
        }

        public Criteria andHeightIsNotNull() {
            addCriterion("height is not null");
            return (Criteria) this;
        }

        public Criteria andHeightEqualTo(Float value) {
            addCriterion("height =", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightNotEqualTo(Float value) {
            addCriterion("height <>", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightGreaterThan(Float value) {
            addCriterion("height >", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightGreaterThanOrEqualTo(Float value) {
            addCriterion("height >=", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightLessThan(Float value) {
            addCriterion("height <", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightLessThanOrEqualTo(Float value) {
            addCriterion("height <=", value, "height");
            return (Criteria) this;
        }

        public Criteria andHeightIn(List<Float> values) {
            addCriterion("height in", values, "height");
            return (Criteria) this;
        }

        public Criteria andHeightNotIn(List<Float> values) {
            addCriterion("height not in", values, "height");
            return (Criteria) this;
        }

        public Criteria andHeightBetween(Float value1, Float value2) {
            addCriterion("height between", value1, value2, "height");
            return (Criteria) this;
        }

        public Criteria andHeightNotBetween(Float value1, Float value2) {
            addCriterion("height not between", value1, value2, "height");
            return (Criteria) this;
        }

        public Criteria andWeightIsNull() {
            addCriterion("weight is null");
            return (Criteria) this;
        }

        public Criteria andWeightIsNotNull() {
            addCriterion("weight is not null");
            return (Criteria) this;
        }

        public Criteria andWeightEqualTo(Float value) {
            addCriterion("weight =", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotEqualTo(Float value) {
            addCriterion("weight <>", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightGreaterThan(Float value) {
            addCriterion("weight >", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightGreaterThanOrEqualTo(Float value) {
            addCriterion("weight >=", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightLessThan(Float value) {
            addCriterion("weight <", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightLessThanOrEqualTo(Float value) {
            addCriterion("weight <=", value, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightIn(List<Float> values) {
            addCriterion("weight in", values, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotIn(List<Float> values) {
            addCriterion("weight not in", values, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightBetween(Float value1, Float value2) {
            addCriterion("weight between", value1, value2, "weight");
            return (Criteria) this;
        }

        public Criteria andWeightNotBetween(Float value1, Float value2) {
            addCriterion("weight not between", value1, value2, "weight");
            return (Criteria) this;
        }

        public Criteria andBmiIsNull() {
            addCriterion("bmi is null");
            return (Criteria) this;
        }

        public Criteria andBmiIsNotNull() {
            addCriterion("bmi is not null");
            return (Criteria) this;
        }

        public Criteria andBmiEqualTo(Float value) {
            addCriterion("bmi =", value, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiNotEqualTo(Float value) {
            addCriterion("bmi <>", value, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiGreaterThan(Float value) {
            addCriterion("bmi >", value, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiGreaterThanOrEqualTo(Float value) {
            addCriterion("bmi >=", value, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiLessThan(Float value) {
            addCriterion("bmi <", value, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiLessThanOrEqualTo(Float value) {
            addCriterion("bmi <=", value, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiIn(List<Float> values) {
            addCriterion("bmi in", values, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiNotIn(List<Float> values) {
            addCriterion("bmi not in", values, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiBetween(Float value1, Float value2) {
            addCriterion("bmi between", value1, value2, "bmi");
            return (Criteria) this;
        }

        public Criteria andBmiNotBetween(Float value1, Float value2) {
            addCriterion("bmi not between", value1, value2, "bmi");
            return (Criteria) this;
        }

        public Criteria andSbpIsNull() {
            addCriterion("sbp is null");
            return (Criteria) this;
        }

        public Criteria andSbpIsNotNull() {
            addCriterion("sbp is not null");
            return (Criteria) this;
        }

        public Criteria andSbpEqualTo(Integer value) {
            addCriterion("sbp =", value, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpNotEqualTo(Integer value) {
            addCriterion("sbp <>", value, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpGreaterThan(Integer value) {
            addCriterion("sbp >", value, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpGreaterThanOrEqualTo(Integer value) {
            addCriterion("sbp >=", value, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpLessThan(Integer value) {
            addCriterion("sbp <", value, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpLessThanOrEqualTo(Integer value) {
            addCriterion("sbp <=", value, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpIn(List<Integer> values) {
            addCriterion("sbp in", values, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpNotIn(List<Integer> values) {
            addCriterion("sbp not in", values, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpBetween(Integer value1, Integer value2) {
            addCriterion("sbp between", value1, value2, "sbp");
            return (Criteria) this;
        }

        public Criteria andSbpNotBetween(Integer value1, Integer value2) {
            addCriterion("sbp not between", value1, value2, "sbp");
            return (Criteria) this;
        }

        public Criteria andDbpIsNull() {
            addCriterion("dbp is null");
            return (Criteria) this;
        }

        public Criteria andDbpIsNotNull() {
            addCriterion("dbp is not null");
            return (Criteria) this;
        }

        public Criteria andDbpEqualTo(Integer value) {
            addCriterion("dbp =", value, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpNotEqualTo(Integer value) {
            addCriterion("dbp <>", value, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpGreaterThan(Integer value) {
            addCriterion("dbp >", value, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpGreaterThanOrEqualTo(Integer value) {
            addCriterion("dbp >=", value, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpLessThan(Integer value) {
            addCriterion("dbp <", value, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpLessThanOrEqualTo(Integer value) {
            addCriterion("dbp <=", value, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpIn(List<Integer> values) {
            addCriterion("dbp in", values, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpNotIn(List<Integer> values) {
            addCriterion("dbp not in", values, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpBetween(Integer value1, Integer value2) {
            addCriterion("dbp between", value1, value2, "dbp");
            return (Criteria) this;
        }

        public Criteria andDbpNotBetween(Integer value1, Integer value2) {
            addCriterion("dbp not between", value1, value2, "dbp");
            return (Criteria) this;
        }

        public Criteria andPulseIsNull() {
            addCriterion("pulse is null");
            return (Criteria) this;
        }

        public Criteria andPulseIsNotNull() {
            addCriterion("pulse is not null");
            return (Criteria) this;
        }

        public Criteria andPulseEqualTo(Integer value) {
            addCriterion("pulse =", value, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseNotEqualTo(Integer value) {
            addCriterion("pulse <>", value, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseGreaterThan(Integer value) {
            addCriterion("pulse >", value, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseGreaterThanOrEqualTo(Integer value) {
            addCriterion("pulse >=", value, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseLessThan(Integer value) {
            addCriterion("pulse <", value, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseLessThanOrEqualTo(Integer value) {
            addCriterion("pulse <=", value, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseIn(List<Integer> values) {
            addCriterion("pulse in", values, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseNotIn(List<Integer> values) {
            addCriterion("pulse not in", values, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseBetween(Integer value1, Integer value2) {
            addCriterion("pulse between", value1, value2, "pulse");
            return (Criteria) this;
        }

        public Criteria andPulseNotBetween(Integer value1, Integer value2) {
            addCriterion("pulse not between", value1, value2, "pulse");
            return (Criteria) this;
        }

        public Criteria andBoIsNull() {
            addCriterion("bo is null");
            return (Criteria) this;
        }

        public Criteria andBoIsNotNull() {
            addCriterion("bo is not null");
            return (Criteria) this;
        }

        public Criteria andBoEqualTo(Integer value) {
            addCriterion("bo =", value, "bo");
            return (Criteria) this;
        }

        public Criteria andBoNotEqualTo(Integer value) {
            addCriterion("bo <>", value, "bo");
            return (Criteria) this;
        }

        public Criteria andBoGreaterThan(Integer value) {
            addCriterion("bo >", value, "bo");
            return (Criteria) this;
        }

        public Criteria andBoGreaterThanOrEqualTo(Integer value) {
            addCriterion("bo >=", value, "bo");
            return (Criteria) this;
        }

        public Criteria andBoLessThan(Integer value) {
            addCriterion("bo <", value, "bo");
            return (Criteria) this;
        }

        public Criteria andBoLessThanOrEqualTo(Integer value) {
            addCriterion("bo <=", value, "bo");
            return (Criteria) this;
        }

        public Criteria andBoIn(List<Integer> values) {
            addCriterion("bo in", values, "bo");
            return (Criteria) this;
        }

        public Criteria andBoNotIn(List<Integer> values) {
            addCriterion("bo not in", values, "bo");
            return (Criteria) this;
        }

        public Criteria andBoBetween(Integer value1, Integer value2) {
            addCriterion("bo between", value1, value2, "bo");
            return (Criteria) this;
        }

        public Criteria andBoNotBetween(Integer value1, Integer value2) {
            addCriterion("bo not between", value1, value2, "bo");
            return (Criteria) this;
        }

        public Criteria andFatIsNull() {
            addCriterion("fat is null");
            return (Criteria) this;
        }

        public Criteria andFatIsNotNull() {
            addCriterion("fat is not null");
            return (Criteria) this;
        }

        public Criteria andFatEqualTo(Float value) {
            addCriterion("fat =", value, "fat");
            return (Criteria) this;
        }

        public Criteria andFatNotEqualTo(Float value) {
            addCriterion("fat <>", value, "fat");
            return (Criteria) this;
        }

        public Criteria andFatGreaterThan(Float value) {
            addCriterion("fat >", value, "fat");
            return (Criteria) this;
        }

        public Criteria andFatGreaterThanOrEqualTo(Float value) {
            addCriterion("fat >=", value, "fat");
            return (Criteria) this;
        }

        public Criteria andFatLessThan(Float value) {
            addCriterion("fat <", value, "fat");
            return (Criteria) this;
        }

        public Criteria andFatLessThanOrEqualTo(Float value) {
            addCriterion("fat <=", value, "fat");
            return (Criteria) this;
        }

        public Criteria andFatIn(List<Float> values) {
            addCriterion("fat in", values, "fat");
            return (Criteria) this;
        }

        public Criteria andFatNotIn(List<Float> values) {
            addCriterion("fat not in", values, "fat");
            return (Criteria) this;
        }

        public Criteria andFatBetween(Float value1, Float value2) {
            addCriterion("fat between", value1, value2, "fat");
            return (Criteria) this;
        }

        public Criteria andFatNotBetween(Float value1, Float value2) {
            addCriterion("fat not between", value1, value2, "fat");
            return (Criteria) this;
        }

        public Criteria andBmrIsNull() {
            addCriterion("bmr is null");
            return (Criteria) this;
        }

        public Criteria andBmrIsNotNull() {
            addCriterion("bmr is not null");
            return (Criteria) this;
        }

        public Criteria andBmrEqualTo(Integer value) {
            addCriterion("bmr =", value, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrNotEqualTo(Integer value) {
            addCriterion("bmr <>", value, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrGreaterThan(Integer value) {
            addCriterion("bmr >", value, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrGreaterThanOrEqualTo(Integer value) {
            addCriterion("bmr >=", value, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrLessThan(Integer value) {
            addCriterion("bmr <", value, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrLessThanOrEqualTo(Integer value) {
            addCriterion("bmr <=", value, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrIn(List<Integer> values) {
            addCriterion("bmr in", values, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrNotIn(List<Integer> values) {
            addCriterion("bmr not in", values, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrBetween(Integer value1, Integer value2) {
            addCriterion("bmr between", value1, value2, "bmr");
            return (Criteria) this;
        }

        public Criteria andBmrNotBetween(Integer value1, Integer value2) {
            addCriterion("bmr not between", value1, value2, "bmr");
            return (Criteria) this;
        }

        public Criteria andWaterIsNull() {
            addCriterion("water is null");
            return (Criteria) this;
        }

        public Criteria andWaterIsNotNull() {
            addCriterion("water is not null");
            return (Criteria) this;
        }

        public Criteria andWaterEqualTo(Integer value) {
            addCriterion("water =", value, "water");
            return (Criteria) this;
        }

        public Criteria andWaterNotEqualTo(Integer value) {
            addCriterion("water <>", value, "water");
            return (Criteria) this;
        }

        public Criteria andWaterGreaterThan(Integer value) {
            addCriterion("water >", value, "water");
            return (Criteria) this;
        }

        public Criteria andWaterGreaterThanOrEqualTo(Integer value) {
            addCriterion("water >=", value, "water");
            return (Criteria) this;
        }

        public Criteria andWaterLessThan(Integer value) {
            addCriterion("water <", value, "water");
            return (Criteria) this;
        }

        public Criteria andWaterLessThanOrEqualTo(Integer value) {
            addCriterion("water <=", value, "water");
            return (Criteria) this;
        }

        public Criteria andWaterIn(List<Integer> values) {
            addCriterion("water in", values, "water");
            return (Criteria) this;
        }

        public Criteria andWaterNotIn(List<Integer> values) {
            addCriterion("water not in", values, "water");
            return (Criteria) this;
        }

        public Criteria andWaterBetween(Integer value1, Integer value2) {
            addCriterion("water between", value1, value2, "water");
            return (Criteria) this;
        }

        public Criteria andWaterNotBetween(Integer value1, Integer value2) {
            addCriterion("water not between", value1, value2, "water");
            return (Criteria) this;
        }

        public Criteria andWaistlineIsNull() {
            addCriterion("waistline is null");
            return (Criteria) this;
        }

        public Criteria andWaistlineIsNotNull() {
            addCriterion("waistline is not null");
            return (Criteria) this;
        }

        public Criteria andWaistlineEqualTo(Float value) {
            addCriterion("waistline =", value, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineNotEqualTo(Float value) {
            addCriterion("waistline <>", value, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineGreaterThan(Float value) {
            addCriterion("waistline >", value, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineGreaterThanOrEqualTo(Float value) {
            addCriterion("waistline >=", value, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineLessThan(Float value) {
            addCriterion("waistline <", value, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineLessThanOrEqualTo(Float value) {
            addCriterion("waistline <=", value, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineIn(List<Float> values) {
            addCriterion("waistline in", values, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineNotIn(List<Float> values) {
            addCriterion("waistline not in", values, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineBetween(Float value1, Float value2) {
            addCriterion("waistline between", value1, value2, "waistline");
            return (Criteria) this;
        }

        public Criteria andWaistlineNotBetween(Float value1, Float value2) {
            addCriterion("waistline not between", value1, value2, "waistline");
            return (Criteria) this;
        }

        public Criteria andHiplineIsNull() {
            addCriterion("hipline is null");
            return (Criteria) this;
        }

        public Criteria andHiplineIsNotNull() {
            addCriterion("hipline is not null");
            return (Criteria) this;
        }

        public Criteria andHiplineEqualTo(Float value) {
            addCriterion("hipline =", value, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineNotEqualTo(Float value) {
            addCriterion("hipline <>", value, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineGreaterThan(Float value) {
            addCriterion("hipline >", value, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineGreaterThanOrEqualTo(Float value) {
            addCriterion("hipline >=", value, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineLessThan(Float value) {
            addCriterion("hipline <", value, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineLessThanOrEqualTo(Float value) {
            addCriterion("hipline <=", value, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineIn(List<Float> values) {
            addCriterion("hipline in", values, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineNotIn(List<Float> values) {
            addCriterion("hipline not in", values, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineBetween(Float value1, Float value2) {
            addCriterion("hipline between", value1, value2, "hipline");
            return (Criteria) this;
        }

        public Criteria andHiplineNotBetween(Float value1, Float value2) {
            addCriterion("hipline not between", value1, value2, "hipline");
            return (Criteria) this;
        }

        public Criteria andWhrIsNull() {
            addCriterion("whr is null");
            return (Criteria) this;
        }

        public Criteria andWhrIsNotNull() {
            addCriterion("whr is not null");
            return (Criteria) this;
        }

        public Criteria andWhrEqualTo(Float value) {
            addCriterion("whr =", value, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrNotEqualTo(Float value) {
            addCriterion("whr <>", value, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrGreaterThan(Float value) {
            addCriterion("whr >", value, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrGreaterThanOrEqualTo(Float value) {
            addCriterion("whr >=", value, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrLessThan(Float value) {
            addCriterion("whr <", value, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrLessThanOrEqualTo(Float value) {
            addCriterion("whr <=", value, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrIn(List<Float> values) {
            addCriterion("whr in", values, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrNotIn(List<Float> values) {
            addCriterion("whr not in", values, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrBetween(Float value1, Float value2) {
            addCriterion("whr between", value1, value2, "whr");
            return (Criteria) this;
        }

        public Criteria andWhrNotBetween(Float value1, Float value2) {
            addCriterion("whr not between", value1, value2, "whr");
            return (Criteria) this;
        }

        public Criteria andPEFIsNull() {
            addCriterion("PEF is null");
            return (Criteria) this;
        }

        public Criteria andPEFIsNotNull() {
            addCriterion("PEF is not null");
            return (Criteria) this;
        }

        public Criteria andPEFEqualTo(Integer value) {
            addCriterion("PEF =", value, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFNotEqualTo(Integer value) {
            addCriterion("PEF <>", value, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFGreaterThan(Integer value) {
            addCriterion("PEF >", value, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFGreaterThanOrEqualTo(Integer value) {
            addCriterion("PEF >=", value, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFLessThan(Integer value) {
            addCriterion("PEF <", value, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFLessThanOrEqualTo(Integer value) {
            addCriterion("PEF <=", value, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFIn(List<Integer> values) {
            addCriterion("PEF in", values, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFNotIn(List<Integer> values) {
            addCriterion("PEF not in", values, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFBetween(Integer value1, Integer value2) {
            addCriterion("PEF between", value1, value2, "PEF");
            return (Criteria) this;
        }

        public Criteria andPEFNotBetween(Integer value1, Integer value2) {
            addCriterion("PEF not between", value1, value2, "PEF");
            return (Criteria) this;
        }

        public Criteria andFVCIsNull() {
            addCriterion("FVC is null");
            return (Criteria) this;
        }

        public Criteria andFVCIsNotNull() {
            addCriterion("FVC is not null");
            return (Criteria) this;
        }

        public Criteria andFVCEqualTo(Float value) {
            addCriterion("FVC =", value, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCNotEqualTo(Float value) {
            addCriterion("FVC <>", value, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCGreaterThan(Float value) {
            addCriterion("FVC >", value, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCGreaterThanOrEqualTo(Float value) {
            addCriterion("FVC >=", value, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCLessThan(Float value) {
            addCriterion("FVC <", value, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCLessThanOrEqualTo(Float value) {
            addCriterion("FVC <=", value, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCIn(List<Float> values) {
            addCriterion("FVC in", values, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCNotIn(List<Float> values) {
            addCriterion("FVC not in", values, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCBetween(Float value1, Float value2) {
            addCriterion("FVC between", value1, value2, "FVC");
            return (Criteria) this;
        }

        public Criteria andFVCNotBetween(Float value1, Float value2) {
            addCriterion("FVC not between", value1, value2, "FVC");
            return (Criteria) this;
        }

        public Criteria andFEV1IsNull() {
            addCriterion("FEV1 is null");
            return (Criteria) this;
        }

        public Criteria andFEV1IsNotNull() {
            addCriterion("FEV1 is not null");
            return (Criteria) this;
        }

        public Criteria andFEV1EqualTo(Float value) {
            addCriterion("FEV1 =", value, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1NotEqualTo(Float value) {
            addCriterion("FEV1 <>", value, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1GreaterThan(Float value) {
            addCriterion("FEV1 >", value, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1GreaterThanOrEqualTo(Float value) {
            addCriterion("FEV1 >=", value, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1LessThan(Float value) {
            addCriterion("FEV1 <", value, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1LessThanOrEqualTo(Float value) {
            addCriterion("FEV1 <=", value, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1In(List<Float> values) {
            addCriterion("FEV1 in", values, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1NotIn(List<Float> values) {
            addCriterion("FEV1 not in", values, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1Between(Float value1, Float value2) {
            addCriterion("FEV1 between", value1, value2, "FEV1");
            return (Criteria) this;
        }

        public Criteria andFEV1NotBetween(Float value1, Float value2) {
            addCriterion("FEV1 not between", value1, value2, "FEV1");
            return (Criteria) this;
        }

        public Criteria andTTIsNull() {
            addCriterion("TT is null");
            return (Criteria) this;
        }

        public Criteria andTTIsNotNull() {
            addCriterion("TT is not null");
            return (Criteria) this;
        }

        public Criteria andTTEqualTo(Float value) {
            addCriterion("TT =", value, "TT");
            return (Criteria) this;
        }

        public Criteria andTTNotEqualTo(Float value) {
            addCriterion("TT <>", value, "TT");
            return (Criteria) this;
        }

        public Criteria andTTGreaterThan(Float value) {
            addCriterion("TT >", value, "TT");
            return (Criteria) this;
        }

        public Criteria andTTGreaterThanOrEqualTo(Float value) {
            addCriterion("TT >=", value, "TT");
            return (Criteria) this;
        }

        public Criteria andTTLessThan(Float value) {
            addCriterion("TT <", value, "TT");
            return (Criteria) this;
        }

        public Criteria andTTLessThanOrEqualTo(Float value) {
            addCriterion("TT <=", value, "TT");
            return (Criteria) this;
        }

        public Criteria andTTIn(List<Float> values) {
            addCriterion("TT in", values, "TT");
            return (Criteria) this;
        }

        public Criteria andTTNotIn(List<Float> values) {
            addCriterion("TT not in", values, "TT");
            return (Criteria) this;
        }

        public Criteria andTTBetween(Float value1, Float value2) {
            addCriterion("TT between", value1, value2, "TT");
            return (Criteria) this;
        }

        public Criteria andTTNotBetween(Float value1, Float value2) {
            addCriterion("TT not between", value1, value2, "TT");
            return (Criteria) this;
        }

        public Criteria andVisionLeftIsNull() {
            addCriterion("VisionLeft is null");
            return (Criteria) this;
        }

        public Criteria andVisionLeftIsNotNull() {
            addCriterion("VisionLeft is not null");
            return (Criteria) this;
        }

        public Criteria andVisionLeftEqualTo(Float value) {
            addCriterion("VisionLeft =", value, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftNotEqualTo(Float value) {
            addCriterion("VisionLeft <>", value, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftGreaterThan(Float value) {
            addCriterion("VisionLeft >", value, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftGreaterThanOrEqualTo(Float value) {
            addCriterion("VisionLeft >=", value, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftLessThan(Float value) {
            addCriterion("VisionLeft <", value, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftLessThanOrEqualTo(Float value) {
            addCriterion("VisionLeft <=", value, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftIn(List<Float> values) {
            addCriterion("VisionLeft in", values, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftNotIn(List<Float> values) {
            addCriterion("VisionLeft not in", values, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftBetween(Float value1, Float value2) {
            addCriterion("VisionLeft between", value1, value2, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionLeftNotBetween(Float value1, Float value2) {
            addCriterion("VisionLeft not between", value1, value2, "visionLeft");
            return (Criteria) this;
        }

        public Criteria andVisionRightIsNull() {
            addCriterion("VisionRight is null");
            return (Criteria) this;
        }

        public Criteria andVisionRightIsNotNull() {
            addCriterion("VisionRight is not null");
            return (Criteria) this;
        }

        public Criteria andVisionRightEqualTo(Float value) {
            addCriterion("VisionRight =", value, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightNotEqualTo(Float value) {
            addCriterion("VisionRight <>", value, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightGreaterThan(Float value) {
            addCriterion("VisionRight >", value, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightGreaterThanOrEqualTo(Float value) {
            addCriterion("VisionRight >=", value, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightLessThan(Float value) {
            addCriterion("VisionRight <", value, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightLessThanOrEqualTo(Float value) {
            addCriterion("VisionRight <=", value, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightIn(List<Float> values) {
            addCriterion("VisionRight in", values, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightNotIn(List<Float> values) {
            addCriterion("VisionRight not in", values, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightBetween(Float value1, Float value2) {
            addCriterion("VisionRight between", value1, value2, "visionRight");
            return (Criteria) this;
        }

        public Criteria andVisionRightNotBetween(Float value1, Float value2) {
            addCriterion("VisionRight not between", value1, value2, "visionRight");
            return (Criteria) this;
        }

        public Criteria andCMPIsNull() {
            addCriterion("CMP is null");
            return (Criteria) this;
        }

        public Criteria andCMPIsNotNull() {
            addCriterion("CMP is not null");
            return (Criteria) this;
        }

        public Criteria andCMPEqualTo(String value) {
            addCriterion("CMP =", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPNotEqualTo(String value) {
            addCriterion("CMP <>", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPGreaterThan(String value) {
            addCriterion("CMP >", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPGreaterThanOrEqualTo(String value) {
            addCriterion("CMP >=", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPLessThan(String value) {
            addCriterion("CMP <", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPLessThanOrEqualTo(String value) {
            addCriterion("CMP <=", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPLike(String value) {
            addCriterion("CMP like", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPNotLike(String value) {
            addCriterion("CMP not like", value, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPIn(List<String> values) {
            addCriterion("CMP in", values, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPNotIn(List<String> values) {
            addCriterion("CMP not in", values, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPBetween(String value1, String value2) {
            addCriterion("CMP between", value1, value2, "CMP");
            return (Criteria) this;
        }

        public Criteria andCMPNotBetween(String value1, String value2) {
            addCriterion("CMP not between", value1, value2, "CMP");
            return (Criteria) this;
        }

        public Criteria andGluIsNull() {
            addCriterion("glu is null");
            return (Criteria) this;
        }

        public Criteria andGluIsNotNull() {
            addCriterion("glu is not null");
            return (Criteria) this;
        }

        public Criteria andGluEqualTo(Float value) {
            addCriterion("glu =", value, "glu");
            return (Criteria) this;
        }

        public Criteria andGluNotEqualTo(Float value) {
            addCriterion("glu <>", value, "glu");
            return (Criteria) this;
        }

        public Criteria andGluGreaterThan(Float value) {
            addCriterion("glu >", value, "glu");
            return (Criteria) this;
        }

        public Criteria andGluGreaterThanOrEqualTo(Float value) {
            addCriterion("glu >=", value, "glu");
            return (Criteria) this;
        }

        public Criteria andGluLessThan(Float value) {
            addCriterion("glu <", value, "glu");
            return (Criteria) this;
        }

        public Criteria andGluLessThanOrEqualTo(Float value) {
            addCriterion("glu <=", value, "glu");
            return (Criteria) this;
        }

        public Criteria andGluIn(List<Float> values) {
            addCriterion("glu in", values, "glu");
            return (Criteria) this;
        }

        public Criteria andGluNotIn(List<Float> values) {
            addCriterion("glu not in", values, "glu");
            return (Criteria) this;
        }

        public Criteria andGluBetween(Float value1, Float value2) {
            addCriterion("glu between", value1, value2, "glu");
            return (Criteria) this;
        }

        public Criteria andGluNotBetween(Float value1, Float value2) {
            addCriterion("glu not between", value1, value2, "glu");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealIsNull() {
            addCriterion("hoursAfterMeal is null");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealIsNotNull() {
            addCriterion("hoursAfterMeal is not null");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealEqualTo(Integer value) {
            addCriterion("hoursAfterMeal =", value, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealNotEqualTo(Integer value) {
            addCriterion("hoursAfterMeal <>", value, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealGreaterThan(Integer value) {
            addCriterion("hoursAfterMeal >", value, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealGreaterThanOrEqualTo(Integer value) {
            addCriterion("hoursAfterMeal >=", value, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealLessThan(Integer value) {
            addCriterion("hoursAfterMeal <", value, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealLessThanOrEqualTo(Integer value) {
            addCriterion("hoursAfterMeal <=", value, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealIn(List<Integer> values) {
            addCriterion("hoursAfterMeal in", values, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealNotIn(List<Integer> values) {
            addCriterion("hoursAfterMeal not in", values, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealBetween(Integer value1, Integer value2) {
            addCriterion("hoursAfterMeal between", value1, value2, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andHoursAfterMealNotBetween(Integer value1, Integer value2) {
            addCriterion("hoursAfterMeal not between", value1, value2, "hoursAfterMeal");
            return (Criteria) this;
        }

        public Criteria andUaIsNull() {
            addCriterion("ua is null");
            return (Criteria) this;
        }

        public Criteria andUaIsNotNull() {
            addCriterion("ua is not null");
            return (Criteria) this;
        }

        public Criteria andUaEqualTo(Float value) {
            addCriterion("ua =", value, "ua");
            return (Criteria) this;
        }

        public Criteria andUaNotEqualTo(Float value) {
            addCriterion("ua <>", value, "ua");
            return (Criteria) this;
        }

        public Criteria andUaGreaterThan(Float value) {
            addCriterion("ua >", value, "ua");
            return (Criteria) this;
        }

        public Criteria andUaGreaterThanOrEqualTo(Float value) {
            addCriterion("ua >=", value, "ua");
            return (Criteria) this;
        }

        public Criteria andUaLessThan(Float value) {
            addCriterion("ua <", value, "ua");
            return (Criteria) this;
        }

        public Criteria andUaLessThanOrEqualTo(Float value) {
            addCriterion("ua <=", value, "ua");
            return (Criteria) this;
        }

        public Criteria andUaIn(List<Float> values) {
            addCriterion("ua in", values, "ua");
            return (Criteria) this;
        }

        public Criteria andUaNotIn(List<Float> values) {
            addCriterion("ua not in", values, "ua");
            return (Criteria) this;
        }

        public Criteria andUaBetween(Float value1, Float value2) {
            addCriterion("ua between", value1, value2, "ua");
            return (Criteria) this;
        }

        public Criteria andUaNotBetween(Float value1, Float value2) {
            addCriterion("ua not between", value1, value2, "ua");
            return (Criteria) this;
        }

        public Criteria andCholIsNull() {
            addCriterion("chol is null");
            return (Criteria) this;
        }

        public Criteria andCholIsNotNull() {
            addCriterion("chol is not null");
            return (Criteria) this;
        }

        public Criteria andCholEqualTo(Float value) {
            addCriterion("chol =", value, "chol");
            return (Criteria) this;
        }

        public Criteria andCholNotEqualTo(Float value) {
            addCriterion("chol <>", value, "chol");
            return (Criteria) this;
        }

        public Criteria andCholGreaterThan(Float value) {
            addCriterion("chol >", value, "chol");
            return (Criteria) this;
        }

        public Criteria andCholGreaterThanOrEqualTo(Float value) {
            addCriterion("chol >=", value, "chol");
            return (Criteria) this;
        }

        public Criteria andCholLessThan(Float value) {
            addCriterion("chol <", value, "chol");
            return (Criteria) this;
        }

        public Criteria andCholLessThanOrEqualTo(Float value) {
            addCriterion("chol <=", value, "chol");
            return (Criteria) this;
        }

        public Criteria andCholIn(List<Float> values) {
            addCriterion("chol in", values, "chol");
            return (Criteria) this;
        }

        public Criteria andCholNotIn(List<Float> values) {
            addCriterion("chol not in", values, "chol");
            return (Criteria) this;
        }

        public Criteria andCholBetween(Float value1, Float value2) {
            addCriterion("chol between", value1, value2, "chol");
            return (Criteria) this;
        }

        public Criteria andCholNotBetween(Float value1, Float value2) {
            addCriterion("chol not between", value1, value2, "chol");
            return (Criteria) this;
        }

        public Criteria andNGSPIsNull() {
            addCriterion("NGSP is null");
            return (Criteria) this;
        }

        public Criteria andNGSPIsNotNull() {
            addCriterion("NGSP is not null");
            return (Criteria) this;
        }

        public Criteria andNGSPEqualTo(Float value) {
            addCriterion("NGSP =", value, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPNotEqualTo(Float value) {
            addCriterion("NGSP <>", value, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPGreaterThan(Float value) {
            addCriterion("NGSP >", value, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPGreaterThanOrEqualTo(Float value) {
            addCriterion("NGSP >=", value, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPLessThan(Float value) {
            addCriterion("NGSP <", value, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPLessThanOrEqualTo(Float value) {
            addCriterion("NGSP <=", value, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPIn(List<Float> values) {
            addCriterion("NGSP in", values, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPNotIn(List<Float> values) {
            addCriterion("NGSP not in", values, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPBetween(Float value1, Float value2) {
            addCriterion("NGSP between", value1, value2, "NGSP");
            return (Criteria) this;
        }

        public Criteria andNGSPNotBetween(Float value1, Float value2) {
            addCriterion("NGSP not between", value1, value2, "NGSP");
            return (Criteria) this;
        }

        public Criteria andCHOL1IsNull() {
            addCriterion("CHOL1 is null");
            return (Criteria) this;
        }

        public Criteria andCHOL1IsNotNull() {
            addCriterion("CHOL1 is not null");
            return (Criteria) this;
        }

        public Criteria andCHOL1EqualTo(Float value) {
            addCriterion("CHOL1 =", value, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1NotEqualTo(Float value) {
            addCriterion("CHOL1 <>", value, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1GreaterThan(Float value) {
            addCriterion("CHOL1 >", value, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1GreaterThanOrEqualTo(Float value) {
            addCriterion("CHOL1 >=", value, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1LessThan(Float value) {
            addCriterion("CHOL1 <", value, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1LessThanOrEqualTo(Float value) {
            addCriterion("CHOL1 <=", value, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1In(List<Float> values) {
            addCriterion("CHOL1 in", values, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1NotIn(List<Float> values) {
            addCriterion("CHOL1 not in", values, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1Between(Float value1, Float value2) {
            addCriterion("CHOL1 between", value1, value2, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andCHOL1NotBetween(Float value1, Float value2) {
            addCriterion("CHOL1 not between", value1, value2, "CHOL1");
            return (Criteria) this;
        }

        public Criteria andTGIsNull() {
            addCriterion("TG is null");
            return (Criteria) this;
        }

        public Criteria andTGIsNotNull() {
            addCriterion("TG is not null");
            return (Criteria) this;
        }

        public Criteria andTGEqualTo(Float value) {
            addCriterion("TG =", value, "TG");
            return (Criteria) this;
        }

        public Criteria andTGNotEqualTo(Float value) {
            addCriterion("TG <>", value, "TG");
            return (Criteria) this;
        }

        public Criteria andTGGreaterThan(Float value) {
            addCriterion("TG >", value, "TG");
            return (Criteria) this;
        }

        public Criteria andTGGreaterThanOrEqualTo(Float value) {
            addCriterion("TG >=", value, "TG");
            return (Criteria) this;
        }

        public Criteria andTGLessThan(Float value) {
            addCriterion("TG <", value, "TG");
            return (Criteria) this;
        }

        public Criteria andTGLessThanOrEqualTo(Float value) {
            addCriterion("TG <=", value, "TG");
            return (Criteria) this;
        }

        public Criteria andTGIn(List<Float> values) {
            addCriterion("TG in", values, "TG");
            return (Criteria) this;
        }

        public Criteria andTGNotIn(List<Float> values) {
            addCriterion("TG not in", values, "TG");
            return (Criteria) this;
        }

        public Criteria andTGBetween(Float value1, Float value2) {
            addCriterion("TG between", value1, value2, "TG");
            return (Criteria) this;
        }

        public Criteria andTGNotBetween(Float value1, Float value2) {
            addCriterion("TG not between", value1, value2, "TG");
            return (Criteria) this;
        }

        public Criteria andHDLIsNull() {
            addCriterion("HDL is null");
            return (Criteria) this;
        }

        public Criteria andHDLIsNotNull() {
            addCriterion("HDL is not null");
            return (Criteria) this;
        }

        public Criteria andHDLEqualTo(Float value) {
            addCriterion("HDL =", value, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLNotEqualTo(Float value) {
            addCriterion("HDL <>", value, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLGreaterThan(Float value) {
            addCriterion("HDL >", value, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLGreaterThanOrEqualTo(Float value) {
            addCriterion("HDL >=", value, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLLessThan(Float value) {
            addCriterion("HDL <", value, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLLessThanOrEqualTo(Float value) {
            addCriterion("HDL <=", value, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLIn(List<Float> values) {
            addCriterion("HDL in", values, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLNotIn(List<Float> values) {
            addCriterion("HDL not in", values, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLBetween(Float value1, Float value2) {
            addCriterion("HDL between", value1, value2, "HDL");
            return (Criteria) this;
        }

        public Criteria andHDLNotBetween(Float value1, Float value2) {
            addCriterion("HDL not between", value1, value2, "HDL");
            return (Criteria) this;
        }

        public Criteria andLDLIsNull() {
            addCriterion("LDL is null");
            return (Criteria) this;
        }

        public Criteria andLDLIsNotNull() {
            addCriterion("LDL is not null");
            return (Criteria) this;
        }

        public Criteria andLDLEqualTo(Float value) {
            addCriterion("LDL =", value, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLNotEqualTo(Float value) {
            addCriterion("LDL <>", value, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLGreaterThan(Float value) {
            addCriterion("LDL >", value, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLGreaterThanOrEqualTo(Float value) {
            addCriterion("LDL >=", value, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLLessThan(Float value) {
            addCriterion("LDL <", value, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLLessThanOrEqualTo(Float value) {
            addCriterion("LDL <=", value, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLIn(List<Float> values) {
            addCriterion("LDL in", values, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLNotIn(List<Float> values) {
            addCriterion("LDL not in", values, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLBetween(Float value1, Float value2) {
            addCriterion("LDL between", value1, value2, "LDL");
            return (Criteria) this;
        }

        public Criteria andLDLNotBetween(Float value1, Float value2) {
            addCriterion("LDL not between", value1, value2, "LDL");
            return (Criteria) this;
        }

        public Criteria andFGCParamIsNull() {
            addCriterion("FGCParam is null");
            return (Criteria) this;
        }

        public Criteria andFGCParamIsNotNull() {
            addCriterion("FGCParam is not null");
            return (Criteria) this;
        }

        public Criteria andFGCParamEqualTo(String value) {
            addCriterion("FGCParam =", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamNotEqualTo(String value) {
            addCriterion("FGCParam <>", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamGreaterThan(String value) {
            addCriterion("FGCParam >", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamGreaterThanOrEqualTo(String value) {
            addCriterion("FGCParam >=", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamLessThan(String value) {
            addCriterion("FGCParam <", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamLessThanOrEqualTo(String value) {
            addCriterion("FGCParam <=", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamLike(String value) {
            addCriterion("FGCParam like", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamNotLike(String value) {
            addCriterion("FGCParam not like", value, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamIn(List<String> values) {
            addCriterion("FGCParam in", values, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamNotIn(List<String> values) {
            addCriterion("FGCParam not in", values, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamBetween(String value1, String value2) {
            addCriterion("FGCParam between", value1, value2, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCParamNotBetween(String value1, String value2) {
            addCriterion("FGCParam not between", value1, value2, "FGCParam");
            return (Criteria) this;
        }

        public Criteria andFGCDataIsNull() {
            addCriterion("FGCData is null");
            return (Criteria) this;
        }

        public Criteria andFGCDataIsNotNull() {
            addCriterion("FGCData is not null");
            return (Criteria) this;
        }

        public Criteria andFGCDataEqualTo(String value) {
            addCriterion("FGCData =", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataNotEqualTo(String value) {
            addCriterion("FGCData <>", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataGreaterThan(String value) {
            addCriterion("FGCData >", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataGreaterThanOrEqualTo(String value) {
            addCriterion("FGCData >=", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataLessThan(String value) {
            addCriterion("FGCData <", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataLessThanOrEqualTo(String value) {
            addCriterion("FGCData <=", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataLike(String value) {
            addCriterion("FGCData like", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataNotLike(String value) {
            addCriterion("FGCData not like", value, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataIn(List<String> values) {
            addCriterion("FGCData in", values, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataNotIn(List<String> values) {
            addCriterion("FGCData not in", values, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataBetween(String value1, String value2) {
            addCriterion("FGCData between", value1, value2, "FGCData");
            return (Criteria) this;
        }

        public Criteria andFGCDataNotBetween(String value1, String value2) {
            addCriterion("FGCData not between", value1, value2, "FGCData");
            return (Criteria) this;
        }

        public Criteria andUROIsNull() {
            addCriterion("URO is null");
            return (Criteria) this;
        }

        public Criteria andUROIsNotNull() {
            addCriterion("URO is not null");
            return (Criteria) this;
        }

        public Criteria andUROEqualTo(String value) {
            addCriterion("URO =", value, "URO");
            return (Criteria) this;
        }

        public Criteria andURONotEqualTo(String value) {
            addCriterion("URO <>", value, "URO");
            return (Criteria) this;
        }

        public Criteria andUROGreaterThan(String value) {
            addCriterion("URO >", value, "URO");
            return (Criteria) this;
        }

        public Criteria andUROGreaterThanOrEqualTo(String value) {
            addCriterion("URO >=", value, "URO");
            return (Criteria) this;
        }

        public Criteria andUROLessThan(String value) {
            addCriterion("URO <", value, "URO");
            return (Criteria) this;
        }

        public Criteria andUROLessThanOrEqualTo(String value) {
            addCriterion("URO <=", value, "URO");
            return (Criteria) this;
        }

        public Criteria andUROLike(String value) {
            addCriterion("URO like", value, "URO");
            return (Criteria) this;
        }

        public Criteria andURONotLike(String value) {
            addCriterion("URO not like", value, "URO");
            return (Criteria) this;
        }

        public Criteria andUROIn(List<String> values) {
            addCriterion("URO in", values, "URO");
            return (Criteria) this;
        }

        public Criteria andURONotIn(List<String> values) {
            addCriterion("URO not in", values, "URO");
            return (Criteria) this;
        }

        public Criteria andUROBetween(String value1, String value2) {
            addCriterion("URO between", value1, value2, "URO");
            return (Criteria) this;
        }

        public Criteria andURONotBetween(String value1, String value2) {
            addCriterion("URO not between", value1, value2, "URO");
            return (Criteria) this;
        }

        public Criteria andBLDIsNull() {
            addCriterion("BLD is null");
            return (Criteria) this;
        }

        public Criteria andBLDIsNotNull() {
            addCriterion("BLD is not null");
            return (Criteria) this;
        }

        public Criteria andBLDEqualTo(String value) {
            addCriterion("BLD =", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDNotEqualTo(String value) {
            addCriterion("BLD <>", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDGreaterThan(String value) {
            addCriterion("BLD >", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDGreaterThanOrEqualTo(String value) {
            addCriterion("BLD >=", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDLessThan(String value) {
            addCriterion("BLD <", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDLessThanOrEqualTo(String value) {
            addCriterion("BLD <=", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDLike(String value) {
            addCriterion("BLD like", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDNotLike(String value) {
            addCriterion("BLD not like", value, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDIn(List<String> values) {
            addCriterion("BLD in", values, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDNotIn(List<String> values) {
            addCriterion("BLD not in", values, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDBetween(String value1, String value2) {
            addCriterion("BLD between", value1, value2, "BLD");
            return (Criteria) this;
        }

        public Criteria andBLDNotBetween(String value1, String value2) {
            addCriterion("BLD not between", value1, value2, "BLD");
            return (Criteria) this;
        }

        public Criteria andBILIsNull() {
            addCriterion("BIL is null");
            return (Criteria) this;
        }

        public Criteria andBILIsNotNull() {
            addCriterion("BIL is not null");
            return (Criteria) this;
        }

        public Criteria andBILEqualTo(String value) {
            addCriterion("BIL =", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILNotEqualTo(String value) {
            addCriterion("BIL <>", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILGreaterThan(String value) {
            addCriterion("BIL >", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILGreaterThanOrEqualTo(String value) {
            addCriterion("BIL >=", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILLessThan(String value) {
            addCriterion("BIL <", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILLessThanOrEqualTo(String value) {
            addCriterion("BIL <=", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILLike(String value) {
            addCriterion("BIL like", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILNotLike(String value) {
            addCriterion("BIL not like", value, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILIn(List<String> values) {
            addCriterion("BIL in", values, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILNotIn(List<String> values) {
            addCriterion("BIL not in", values, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILBetween(String value1, String value2) {
            addCriterion("BIL between", value1, value2, "BIL");
            return (Criteria) this;
        }

        public Criteria andBILNotBetween(String value1, String value2) {
            addCriterion("BIL not between", value1, value2, "BIL");
            return (Criteria) this;
        }

        public Criteria andKETIsNull() {
            addCriterion("KET is null");
            return (Criteria) this;
        }

        public Criteria andKETIsNotNull() {
            addCriterion("KET is not null");
            return (Criteria) this;
        }

        public Criteria andKETEqualTo(String value) {
            addCriterion("KET =", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETNotEqualTo(String value) {
            addCriterion("KET <>", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETGreaterThan(String value) {
            addCriterion("KET >", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETGreaterThanOrEqualTo(String value) {
            addCriterion("KET >=", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETLessThan(String value) {
            addCriterion("KET <", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETLessThanOrEqualTo(String value) {
            addCriterion("KET <=", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETLike(String value) {
            addCriterion("KET like", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETNotLike(String value) {
            addCriterion("KET not like", value, "KET");
            return (Criteria) this;
        }

        public Criteria andKETIn(List<String> values) {
            addCriterion("KET in", values, "KET");
            return (Criteria) this;
        }

        public Criteria andKETNotIn(List<String> values) {
            addCriterion("KET not in", values, "KET");
            return (Criteria) this;
        }

        public Criteria andKETBetween(String value1, String value2) {
            addCriterion("KET between", value1, value2, "KET");
            return (Criteria) this;
        }

        public Criteria andKETNotBetween(String value1, String value2) {
            addCriterion("KET not between", value1, value2, "KET");
            return (Criteria) this;
        }

        public Criteria andBC_GLUIsNull() {
            addCriterion("BC_GLU is null");
            return (Criteria) this;
        }

        public Criteria andBC_GLUIsNotNull() {
            addCriterion("BC_GLU is not null");
            return (Criteria) this;
        }

        public Criteria andBC_GLUEqualTo(String value) {
            addCriterion("BC_GLU =", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUNotEqualTo(String value) {
            addCriterion("BC_GLU <>", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUGreaterThan(String value) {
            addCriterion("BC_GLU >", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUGreaterThanOrEqualTo(String value) {
            addCriterion("BC_GLU >=", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLULessThan(String value) {
            addCriterion("BC_GLU <", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLULessThanOrEqualTo(String value) {
            addCriterion("BC_GLU <=", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLULike(String value) {
            addCriterion("BC_GLU like", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUNotLike(String value) {
            addCriterion("BC_GLU not like", value, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUIn(List<String> values) {
            addCriterion("BC_GLU in", values, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUNotIn(List<String> values) {
            addCriterion("BC_GLU not in", values, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUBetween(String value1, String value2) {
            addCriterion("BC_GLU between", value1, value2, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andBC_GLUNotBetween(String value1, String value2) {
            addCriterion("BC_GLU not between", value1, value2, "BC_GLU");
            return (Criteria) this;
        }

        public Criteria andPROIsNull() {
            addCriterion("PRO is null");
            return (Criteria) this;
        }

        public Criteria andPROIsNotNull() {
            addCriterion("PRO is not null");
            return (Criteria) this;
        }

        public Criteria andPROEqualTo(String value) {
            addCriterion("PRO =", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPRONotEqualTo(String value) {
            addCriterion("PRO <>", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROGreaterThan(String value) {
            addCriterion("PRO >", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROGreaterThanOrEqualTo(String value) {
            addCriterion("PRO >=", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROLessThan(String value) {
            addCriterion("PRO <", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROLessThanOrEqualTo(String value) {
            addCriterion("PRO <=", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROLike(String value) {
            addCriterion("PRO like", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPRONotLike(String value) {
            addCriterion("PRO not like", value, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROIn(List<String> values) {
            addCriterion("PRO in", values, "PRO");
            return (Criteria) this;
        }

        public Criteria andPRONotIn(List<String> values) {
            addCriterion("PRO not in", values, "PRO");
            return (Criteria) this;
        }

        public Criteria andPROBetween(String value1, String value2) {
            addCriterion("PRO between", value1, value2, "PRO");
            return (Criteria) this;
        }

        public Criteria andPRONotBetween(String value1, String value2) {
            addCriterion("PRO not between", value1, value2, "PRO");
            return (Criteria) this;
        }

        public Criteria andPHIsNull() {
            addCriterion("PH is null");
            return (Criteria) this;
        }

        public Criteria andPHIsNotNull() {
            addCriterion("PH is not null");
            return (Criteria) this;
        }

        public Criteria andPHEqualTo(Float value) {
            addCriterion("PH =", value, "PH");
            return (Criteria) this;
        }

        public Criteria andPHNotEqualTo(Float value) {
            addCriterion("PH <>", value, "PH");
            return (Criteria) this;
        }

        public Criteria andPHGreaterThan(Float value) {
            addCriterion("PH >", value, "PH");
            return (Criteria) this;
        }

        public Criteria andPHGreaterThanOrEqualTo(Float value) {
            addCriterion("PH >=", value, "PH");
            return (Criteria) this;
        }

        public Criteria andPHLessThan(Float value) {
            addCriterion("PH <", value, "PH");
            return (Criteria) this;
        }

        public Criteria andPHLessThanOrEqualTo(Float value) {
            addCriterion("PH <=", value, "PH");
            return (Criteria) this;
        }

        public Criteria andPHIn(List<Float> values) {
            addCriterion("PH in", values, "PH");
            return (Criteria) this;
        }

        public Criteria andPHNotIn(List<Float> values) {
            addCriterion("PH not in", values, "PH");
            return (Criteria) this;
        }

        public Criteria andPHBetween(Float value1, Float value2) {
            addCriterion("PH between", value1, value2, "PH");
            return (Criteria) this;
        }

        public Criteria andPHNotBetween(Float value1, Float value2) {
            addCriterion("PH not between", value1, value2, "PH");
            return (Criteria) this;
        }

        public Criteria andNITIsNull() {
            addCriterion("NIT is null");
            return (Criteria) this;
        }

        public Criteria andNITIsNotNull() {
            addCriterion("NIT is not null");
            return (Criteria) this;
        }

        public Criteria andNITEqualTo(String value) {
            addCriterion("NIT =", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITNotEqualTo(String value) {
            addCriterion("NIT <>", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITGreaterThan(String value) {
            addCriterion("NIT >", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITGreaterThanOrEqualTo(String value) {
            addCriterion("NIT >=", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITLessThan(String value) {
            addCriterion("NIT <", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITLessThanOrEqualTo(String value) {
            addCriterion("NIT <=", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITLike(String value) {
            addCriterion("NIT like", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITNotLike(String value) {
            addCriterion("NIT not like", value, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITIn(List<String> values) {
            addCriterion("NIT in", values, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITNotIn(List<String> values) {
            addCriterion("NIT not in", values, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITBetween(String value1, String value2) {
            addCriterion("NIT between", value1, value2, "NIT");
            return (Criteria) this;
        }

        public Criteria andNITNotBetween(String value1, String value2) {
            addCriterion("NIT not between", value1, value2, "NIT");
            return (Criteria) this;
        }

        public Criteria andLEUIsNull() {
            addCriterion("LEU is null");
            return (Criteria) this;
        }

        public Criteria andLEUIsNotNull() {
            addCriterion("LEU is not null");
            return (Criteria) this;
        }

        public Criteria andLEUEqualTo(String value) {
            addCriterion("LEU =", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUNotEqualTo(String value) {
            addCriterion("LEU <>", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUGreaterThan(String value) {
            addCriterion("LEU >", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUGreaterThanOrEqualTo(String value) {
            addCriterion("LEU >=", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEULessThan(String value) {
            addCriterion("LEU <", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEULessThanOrEqualTo(String value) {
            addCriterion("LEU <=", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEULike(String value) {
            addCriterion("LEU like", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUNotLike(String value) {
            addCriterion("LEU not like", value, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUIn(List<String> values) {
            addCriterion("LEU in", values, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUNotIn(List<String> values) {
            addCriterion("LEU not in", values, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUBetween(String value1, String value2) {
            addCriterion("LEU between", value1, value2, "LEU");
            return (Criteria) this;
        }

        public Criteria andLEUNotBetween(String value1, String value2) {
            addCriterion("LEU not between", value1, value2, "LEU");
            return (Criteria) this;
        }

        public Criteria andSGIsNull() {
            addCriterion("SG is null");
            return (Criteria) this;
        }

        public Criteria andSGIsNotNull() {
            addCriterion("SG is not null");
            return (Criteria) this;
        }

        public Criteria andSGEqualTo(Float value) {
            addCriterion("SG =", value, "SG");
            return (Criteria) this;
        }

        public Criteria andSGNotEqualTo(Float value) {
            addCriterion("SG <>", value, "SG");
            return (Criteria) this;
        }

        public Criteria andSGGreaterThan(Float value) {
            addCriterion("SG >", value, "SG");
            return (Criteria) this;
        }

        public Criteria andSGGreaterThanOrEqualTo(Float value) {
            addCriterion("SG >=", value, "SG");
            return (Criteria) this;
        }

        public Criteria andSGLessThan(Float value) {
            addCriterion("SG <", value, "SG");
            return (Criteria) this;
        }

        public Criteria andSGLessThanOrEqualTo(Float value) {
            addCriterion("SG <=", value, "SG");
            return (Criteria) this;
        }

        public Criteria andSGIn(List<Float> values) {
            addCriterion("SG in", values, "SG");
            return (Criteria) this;
        }

        public Criteria andSGNotIn(List<Float> values) {
            addCriterion("SG not in", values, "SG");
            return (Criteria) this;
        }

        public Criteria andSGBetween(Float value1, Float value2) {
            addCriterion("SG between", value1, value2, "SG");
            return (Criteria) this;
        }

        public Criteria andSGNotBetween(Float value1, Float value2) {
            addCriterion("SG not between", value1, value2, "SG");
            return (Criteria) this;
        }

        public Criteria andVCIsNull() {
            addCriterion("VC is null");
            return (Criteria) this;
        }

        public Criteria andVCIsNotNull() {
            addCriterion("VC is not null");
            return (Criteria) this;
        }

        public Criteria andVCEqualTo(String value) {
            addCriterion("VC =", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCNotEqualTo(String value) {
            addCriterion("VC <>", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCGreaterThan(String value) {
            addCriterion("VC >", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCGreaterThanOrEqualTo(String value) {
            addCriterion("VC >=", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCLessThan(String value) {
            addCriterion("VC <", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCLessThanOrEqualTo(String value) {
            addCriterion("VC <=", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCLike(String value) {
            addCriterion("VC like", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCNotLike(String value) {
            addCriterion("VC not like", value, "VC");
            return (Criteria) this;
        }

        public Criteria andVCIn(List<String> values) {
            addCriterion("VC in", values, "VC");
            return (Criteria) this;
        }

        public Criteria andVCNotIn(List<String> values) {
            addCriterion("VC not in", values, "VC");
            return (Criteria) this;
        }

        public Criteria andVCBetween(String value1, String value2) {
            addCriterion("VC between", value1, value2, "VC");
            return (Criteria) this;
        }

        public Criteria andVCNotBetween(String value1, String value2) {
            addCriterion("VC not between", value1, value2, "VC");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlIsNull() {
            addCriterion("ecgpng_url is null");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlIsNotNull() {
            addCriterion("ecgpng_url is not null");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlEqualTo(String value) {
            addCriterion("ecgpng_url =", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlNotEqualTo(String value) {
            addCriterion("ecgpng_url <>", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlGreaterThan(String value) {
            addCriterion("ecgpng_url >", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlGreaterThanOrEqualTo(String value) {
            addCriterion("ecgpng_url >=", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlLessThan(String value) {
            addCriterion("ecgpng_url <", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlLessThanOrEqualTo(String value) {
            addCriterion("ecgpng_url <=", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlLike(String value) {
            addCriterion("ecgpng_url like", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlNotLike(String value) {
            addCriterion("ecgpng_url not like", value, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlIn(List<String> values) {
            addCriterion("ecgpng_url in", values, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlNotIn(List<String> values) {
            addCriterion("ecgpng_url not in", values, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlBetween(String value1, String value2) {
            addCriterion("ecgpng_url between", value1, value2, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcgpng_urlNotBetween(String value1, String value2) {
            addCriterion("ecgpng_url not between", value1, value2, "ecgpng_url");
            return (Criteria) this;
        }

        public Criteria andEcg_resultIsNull() {
            addCriterion("ecg_result is null");
            return (Criteria) this;
        }

        public Criteria andEcg_resultIsNotNull() {
            addCriterion("ecg_result is not null");
            return (Criteria) this;
        }

        public Criteria andEcg_resultEqualTo(String value) {
            addCriterion("ecg_result =", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultNotEqualTo(String value) {
            addCriterion("ecg_result <>", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultGreaterThan(String value) {
            addCriterion("ecg_result >", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultGreaterThanOrEqualTo(String value) {
            addCriterion("ecg_result >=", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultLessThan(String value) {
            addCriterion("ecg_result <", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultLessThanOrEqualTo(String value) {
            addCriterion("ecg_result <=", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultLike(String value) {
            addCriterion("ecg_result like", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultNotLike(String value) {
            addCriterion("ecg_result not like", value, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultIn(List<String> values) {
            addCriterion("ecg_result in", values, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultNotIn(List<String> values) {
            addCriterion("ecg_result not in", values, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultBetween(String value1, String value2) {
            addCriterion("ecg_result between", value1, value2, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andEcg_resultNotBetween(String value1, String value2) {
            addCriterion("ecg_result not between", value1, value2, "ecg_result");
            return (Criteria) this;
        }

        public Criteria andBd_pngIsNull() {
            addCriterion("bd_png is null");
            return (Criteria) this;
        }

        public Criteria andBd_pngIsNotNull() {
            addCriterion("bd_png is not null");
            return (Criteria) this;
        }

        public Criteria andBd_pngEqualTo(String value) {
            addCriterion("bd_png =", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngNotEqualTo(String value) {
            addCriterion("bd_png <>", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngGreaterThan(String value) {
            addCriterion("bd_png >", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngGreaterThanOrEqualTo(String value) {
            addCriterion("bd_png >=", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngLessThan(String value) {
            addCriterion("bd_png <", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngLessThanOrEqualTo(String value) {
            addCriterion("bd_png <=", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngLike(String value) {
            addCriterion("bd_png like", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngNotLike(String value) {
            addCriterion("bd_png not like", value, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngIn(List<String> values) {
            addCriterion("bd_png in", values, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngNotIn(List<String> values) {
            addCriterion("bd_png not in", values, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngBetween(String value1, String value2) {
            addCriterion("bd_png between", value1, value2, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_pngNotBetween(String value1, String value2) {
            addCriterion("bd_png not between", value1, value2, "bd_png");
            return (Criteria) this;
        }

        public Criteria andBd_xmlIsNull() {
            addCriterion("bd_xml is null");
            return (Criteria) this;
        }

        public Criteria andBd_xmlIsNotNull() {
            addCriterion("bd_xml is not null");
            return (Criteria) this;
        }

        public Criteria andBd_xmlEqualTo(String value) {
            addCriterion("bd_xml =", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlNotEqualTo(String value) {
            addCriterion("bd_xml <>", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlGreaterThan(String value) {
            addCriterion("bd_xml >", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlGreaterThanOrEqualTo(String value) {
            addCriterion("bd_xml >=", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlLessThan(String value) {
            addCriterion("bd_xml <", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlLessThanOrEqualTo(String value) {
            addCriterion("bd_xml <=", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlLike(String value) {
            addCriterion("bd_xml like", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlNotLike(String value) {
            addCriterion("bd_xml not like", value, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlIn(List<String> values) {
            addCriterion("bd_xml in", values, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlNotIn(List<String> values) {
            addCriterion("bd_xml not in", values, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlBetween(String value1, String value2) {
            addCriterion("bd_xml between", value1, value2, "bd_xml");
            return (Criteria) this;
        }

        public Criteria andBd_xmlNotBetween(String value1, String value2) {
            addCriterion("bd_xml not between", value1, value2, "bd_xml");
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