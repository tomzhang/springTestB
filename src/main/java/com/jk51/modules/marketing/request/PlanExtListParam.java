package com.jk51.modules.marketing.request;

import java.util.List;

public class PlanExtListParam {
    private List<MarketingPlanExt> list;

    private Integer siteId;

    private Integer planId;

    private Integer operatorType;

    private Integer operatorId;

    public List<MarketingPlanExt> getList() {
        return list;
    }

    public void setList(List<MarketingPlanExt> list) {
        this.list = list;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return "PlanExtListParam{" +
            "list=" + list +
            ", siteId=" + siteId +
            ", planId=" + planId +
            ", operatorType=" + operatorType +
            ", operatorId=" + operatorId +
            '}';
    }

    public static class MarketingPlanExt {
        private Integer id;

        private Integer type;

        private Integer typeId;

        private String typeInfo;

        private Float chances;

        private Integer ceiling;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getTypeId() {
            return typeId;
        }

        public void setTypeId(Integer typeId) {
            this.typeId = typeId;
        }

        public String getTypeInfo() {
            return typeInfo;
        }

        public void setTypeInfo(String typeInfo) {
            this.typeInfo = typeInfo;
        }

        public Float getChances() {
            return chances;
        }

        public void setChances(Float chances) {
            this.chances = chances;
        }

        public Integer getCeiling() {
            return ceiling;
        }

        public void setCeiling(Integer ceiling) {
            this.ceiling = ceiling;
        }

        @Override
        public String toString() {
            return "MarketingPlanExt{" +
                "id=" + id +
                ", type=" + type +
                ", typeId=" + typeId +
                ", typeInfo='" + typeInfo + '\'' +
                ", chances=" + chances +
                ", ceiling=" + ceiling +
                '}';
        }
    }
}
