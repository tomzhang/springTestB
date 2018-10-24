package com.jk51.model.distribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DWithdrawRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private Integer limit;

    private Integer offset;

    public DWithdrawRecordExample() {
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

        public Criteria andTradesIdIsNull() {
            addCriterion("trades_id is null");
            return (Criteria) this;
        }

        public Criteria andTradesIdIsNotNull() {
            addCriterion("trades_id is not null");
            return (Criteria) this;
        }

        public Criteria andTradesIdEqualTo(Long value) {
            addCriterion("trades_id =", value, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdNotEqualTo(Long value) {
            addCriterion("trades_id <>", value, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdGreaterThan(Long value) {
            addCriterion("trades_id >", value, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdGreaterThanOrEqualTo(Long value) {
            addCriterion("trades_id >=", value, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdLessThan(Long value) {
            addCriterion("trades_id <", value, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdLessThanOrEqualTo(Long value) {
            addCriterion("trades_id <=", value, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdIn(List<Long> values) {
            addCriterion("trades_id in", values, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdNotIn(List<Long> values) {
            addCriterion("trades_id not in", values, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdBetween(Long value1, Long value2) {
            addCriterion("trades_id between", value1, value2, "tradesId");
            return (Criteria) this;
        }

        public Criteria andTradesIdNotBetween(Long value1, Long value2) {
            addCriterion("trades_id not between", value1, value2, "tradesId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdIsNull() {
            addCriterion("distributor_id is null");
            return (Criteria) this;
        }

        public Criteria andDistributorIdIsNotNull() {
            addCriterion("distributor_id is not null");
            return (Criteria) this;
        }

        public Criteria andDistributorIdEqualTo(Integer value) {
            addCriterion("distributor_id =", value, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdNotEqualTo(Integer value) {
            addCriterion("distributor_id <>", value, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdGreaterThan(Integer value) {
            addCriterion("distributor_id >", value, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("distributor_id >=", value, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdLessThan(Integer value) {
            addCriterion("distributor_id <", value, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdLessThanOrEqualTo(Integer value) {
            addCriterion("distributor_id <=", value, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdIn(List<Integer> values) {
            addCriterion("distributor_id in", values, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdNotIn(List<Integer> values) {
            addCriterion("distributor_id not in", values, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdBetween(Integer value1, Integer value2) {
            addCriterion("distributor_id between", value1, value2, "distributorId");
            return (Criteria) this;
        }

        public Criteria andDistributorIdNotBetween(Integer value1, Integer value2) {
            addCriterion("distributor_id not between", value1, value2, "distributorId");
            return (Criteria) this;
        }

        public Criteria andOwnerIsNull() {
            addCriterion("owner is null");
            return (Criteria) this;
        }

        public Criteria andOwnerIsNotNull() {
            addCriterion("owner is not null");
            return (Criteria) this;
        }

        public Criteria andOwnerEqualTo(Integer value) {
            addCriterion("owner =", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotEqualTo(Integer value) {
            addCriterion("owner <>", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerGreaterThan(Integer value) {
            addCriterion("owner >", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerGreaterThanOrEqualTo(Integer value) {
            addCriterion("owner >=", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLessThan(Integer value) {
            addCriterion("owner <", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerLessThanOrEqualTo(Integer value) {
            addCriterion("owner <=", value, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerIn(List<Integer> values) {
            addCriterion("owner in", values, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotIn(List<Integer> values) {
            addCriterion("owner not in", values, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerBetween(Integer value1, Integer value2) {
            addCriterion("owner between", value1, value2, "owner");
            return (Criteria) this;
        }

        public Criteria andOwnerNotBetween(Integer value1, Integer value2) {
            addCriterion("owner not between", value1, value2, "owner");
            return (Criteria) this;
        }

        public Criteria andAccountIsNull() {
            addCriterion("account is null");
            return (Criteria) this;
        }

        public Criteria andAccountIsNotNull() {
            addCriterion("account is not null");
            return (Criteria) this;
        }

        public Criteria andAccountEqualTo(String value) {
            addCriterion("account =", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotEqualTo(String value) {
            addCriterion("account <>", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountGreaterThan(String value) {
            addCriterion("account >", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountGreaterThanOrEqualTo(String value) {
            addCriterion("account >=", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountLessThan(String value) {
            addCriterion("account <", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountLessThanOrEqualTo(String value) {
            addCriterion("account <=", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountLike(String value) {
            addCriterion("account like", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotLike(String value) {
            addCriterion("account not like", value, "account");
            return (Criteria) this;
        }

        public Criteria andAccountIn(List<String> values) {
            addCriterion("account in", values, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotIn(List<String> values) {
            addCriterion("account not in", values, "account");
            return (Criteria) this;
        }

        public Criteria andAccountBetween(String value1, String value2) {
            addCriterion("account between", value1, value2, "account");
            return (Criteria) this;
        }

        public Criteria andAccountNotBetween(String value1, String value2) {
            addCriterion("account not between", value1, value2, "account");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Byte value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Byte value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Byte value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Byte value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Byte value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Byte> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Byte> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Byte value1, Byte value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andAmountIsNull() {
            addCriterion("amount is null");
            return (Criteria) this;
        }

        public Criteria andAmountIsNotNull() {
            addCriterion("amount is not null");
            return (Criteria) this;
        }

        public Criteria andAmountEqualTo(Integer value) {
            addCriterion("amount =", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotEqualTo(Integer value) {
            addCriterion("amount <>", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThan(Integer value) {
            addCriterion("amount >", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThanOrEqualTo(Integer value) {
            addCriterion("amount >=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThan(Integer value) {
            addCriterion("amount <", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThanOrEqualTo(Integer value) {
            addCriterion("amount <=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountIn(List<Integer> values) {
            addCriterion("amount in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotIn(List<Integer> values) {
            addCriterion("amount not in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountBetween(Integer value1, Integer value2) {
            addCriterion("amount between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotBetween(Integer value1, Integer value2) {
            addCriterion("amount not between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleIsNull() {
            addCriterion("withdraw_style is null");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleIsNotNull() {
            addCriterion("withdraw_style is not null");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleEqualTo(String value) {
            addCriterion("withdraw_style =", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleNotEqualTo(String value) {
            addCriterion("withdraw_style <>", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleGreaterThan(String value) {
            addCriterion("withdraw_style >", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleGreaterThanOrEqualTo(String value) {
            addCriterion("withdraw_style >=", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleLessThan(String value) {
            addCriterion("withdraw_style <", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleLessThanOrEqualTo(String value) {
            addCriterion("withdraw_style <=", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleLike(String value) {
            addCriterion("withdraw_style like", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleNotLike(String value) {
            addCriterion("withdraw_style not like", value, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleIn(List<String> values) {
            addCriterion("withdraw_style in", values, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleNotIn(List<String> values) {
            addCriterion("withdraw_style not in", values, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleBetween(String value1, String value2) {
            addCriterion("withdraw_style between", value1, value2, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawStyleNotBetween(String value1, String value2) {
            addCriterion("withdraw_style not between", value1, value2, "withdrawStyle");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeIsNull() {
            addCriterion("withdraw_fee is null");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeIsNotNull() {
            addCriterion("withdraw_fee is not null");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeEqualTo(Integer value) {
            addCriterion("withdraw_fee =", value, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeNotEqualTo(Integer value) {
            addCriterion("withdraw_fee <>", value, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeGreaterThan(Integer value) {
            addCriterion("withdraw_fee >", value, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeGreaterThanOrEqualTo(Integer value) {
            addCriterion("withdraw_fee >=", value, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeLessThan(Integer value) {
            addCriterion("withdraw_fee <", value, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeLessThanOrEqualTo(Integer value) {
            addCriterion("withdraw_fee <=", value, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeIn(List<Integer> values) {
            addCriterion("withdraw_fee in", values, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeNotIn(List<Integer> values) {
            addCriterion("withdraw_fee not in", values, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeBetween(Integer value1, Integer value2) {
            addCriterion("withdraw_fee between", value1, value2, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andWithdrawFeeNotBetween(Integer value1, Integer value2) {
            addCriterion("withdraw_fee not between", value1, value2, "withdrawFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeIsNull() {
            addCriterion("total_fee is null");
            return (Criteria) this;
        }

        public Criteria andTotalFeeIsNotNull() {
            addCriterion("total_fee is not null");
            return (Criteria) this;
        }

        public Criteria andTotalFeeEqualTo(Integer value) {
            addCriterion("total_fee =", value, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeNotEqualTo(Integer value) {
            addCriterion("total_fee <>", value, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeGreaterThan(Integer value) {
            addCriterion("total_fee >", value, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeGreaterThanOrEqualTo(Integer value) {
            addCriterion("total_fee >=", value, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeLessThan(Integer value) {
            addCriterion("total_fee <", value, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeLessThanOrEqualTo(Integer value) {
            addCriterion("total_fee <=", value, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeIn(List<Integer> values) {
            addCriterion("total_fee in", values, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeNotIn(List<Integer> values) {
            addCriterion("total_fee not in", values, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeBetween(Integer value1, Integer value2) {
            addCriterion("total_fee between", value1, value2, "totalFee");
            return (Criteria) this;
        }

        public Criteria andTotalFeeNotBetween(Integer value1, Integer value2) {
            addCriterion("total_fee not between", value1, value2, "totalFee");
            return (Criteria) this;
        }

        public Criteria andPayStatusIsNull() {
            addCriterion("pay_status is null");
            return (Criteria) this;
        }

        public Criteria andPayStatusIsNotNull() {
            addCriterion("pay_status is not null");
            return (Criteria) this;
        }

        public Criteria andPayStatusEqualTo(Byte value) {
            addCriterion("pay_status =", value, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusNotEqualTo(Byte value) {
            addCriterion("pay_status <>", value, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusGreaterThan(Byte value) {
            addCriterion("pay_status >", value, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("pay_status >=", value, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusLessThan(Byte value) {
            addCriterion("pay_status <", value, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusLessThanOrEqualTo(Byte value) {
            addCriterion("pay_status <=", value, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusIn(List<Byte> values) {
            addCriterion("pay_status in", values, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusNotIn(List<Byte> values) {
            addCriterion("pay_status not in", values, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusBetween(Byte value1, Byte value2) {
            addCriterion("pay_status between", value1, value2, "payStatus");
            return (Criteria) this;
        }

        public Criteria andPayStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("pay_status not between", value1, value2, "payStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusIsNull() {
            addCriterion("checking_status is null");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusIsNotNull() {
            addCriterion("checking_status is not null");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusEqualTo(Boolean value) {
            addCriterion("checking_status =", value, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusNotEqualTo(Boolean value) {
            addCriterion("checking_status <>", value, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusGreaterThan(Boolean value) {
            addCriterion("checking_status >", value, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusGreaterThanOrEqualTo(Boolean value) {
            addCriterion("checking_status >=", value, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusLessThan(Boolean value) {
            addCriterion("checking_status <", value, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusLessThanOrEqualTo(Boolean value) {
            addCriterion("checking_status <=", value, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusIn(List<Boolean> values) {
            addCriterion("checking_status in", values, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusNotIn(List<Boolean> values) {
            addCriterion("checking_status not in", values, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusBetween(Boolean value1, Boolean value2) {
            addCriterion("checking_status between", value1, value2, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andCheckingStatusNotBetween(Boolean value1, Boolean value2) {
            addCriterion("checking_status not between", value1, value2, "checkingStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusIsNull() {
            addCriterion("settlement_status is null");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusIsNotNull() {
            addCriterion("settlement_status is not null");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusEqualTo(Short value) {
            addCriterion("settlement_status =", value, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusNotEqualTo(Short value) {
            addCriterion("settlement_status <>", value, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusGreaterThan(Short value) {
            addCriterion("settlement_status >", value, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusGreaterThanOrEqualTo(Short value) {
            addCriterion("settlement_status >=", value, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusLessThan(Short value) {
            addCriterion("settlement_status <", value, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusLessThanOrEqualTo(Short value) {
            addCriterion("settlement_status <=", value, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusIn(List<Short> values) {
            addCriterion("settlement_status in", values, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusNotIn(List<Short> values) {
            addCriterion("settlement_status not in", values, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusBetween(Short value1, Short value2) {
            addCriterion("settlement_status between", value1, value2, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andSettlementStatusNotBetween(Short value1, Short value2) {
            addCriterion("settlement_status not between", value1, value2, "settlementStatus");
            return (Criteria) this;
        }

        public Criteria andTradesTimeIsNull() {
            addCriterion("trades_time is null");
            return (Criteria) this;
        }

        public Criteria andTradesTimeIsNotNull() {
            addCriterion("trades_time is not null");
            return (Criteria) this;
        }

        public Criteria andTradesTimeEqualTo(Date value) {
            addCriterion("trades_time =", value, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeNotEqualTo(Date value) {
            addCriterion("trades_time <>", value, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeGreaterThan(Date value) {
            addCriterion("trades_time >", value, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("trades_time >=", value, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeLessThan(Date value) {
            addCriterion("trades_time <", value, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeLessThanOrEqualTo(Date value) {
            addCriterion("trades_time <=", value, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeIn(List<Date> values) {
            addCriterion("trades_time in", values, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeNotIn(List<Date> values) {
            addCriterion("trades_time not in", values, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeBetween(Date value1, Date value2) {
            addCriterion("trades_time between", value1, value2, "tradesTime");
            return (Criteria) this;
        }

        public Criteria andTradesTimeNotBetween(Date value1, Date value2) {
            addCriterion("trades_time not between", value1, value2, "tradesTime");
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

        public Criteria andMoneyRecordIdIsNull() {
            addCriterion("money_record_id is null");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdIsNotNull() {
            addCriterion("money_record_id is not null");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdEqualTo(Integer value) {
            addCriterion("money_record_id =", value, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdNotEqualTo(Integer value) {
            addCriterion("money_record_id <>", value, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdGreaterThan(Integer value) {
            addCriterion("money_record_id >", value, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("money_record_id >=", value, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdLessThan(Integer value) {
            addCriterion("money_record_id <", value, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdLessThanOrEqualTo(Integer value) {
            addCriterion("money_record_id <=", value, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdIn(List<Integer> values) {
            addCriterion("money_record_id in", values, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdNotIn(List<Integer> values) {
            addCriterion("money_record_id not in", values, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdBetween(Integer value1, Integer value2) {
            addCriterion("money_record_id between", value1, value2, "moneyRecordId");
            return (Criteria) this;
        }

        public Criteria andMoneyRecordIdNotBetween(Integer value1, Integer value2) {
            addCriterion("money_record_id not between", value1, value2, "moneyRecordId");
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