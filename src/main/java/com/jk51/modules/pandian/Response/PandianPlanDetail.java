package com.jk51.modules.pandian.Response;

import java.util.List;

public class PandianPlanDetail {
    private Integer id;
    private String source;
    private String planType;
    private String uploadType;
    private String stockShow;
    private String checkType;
    private String planCheck;
    private String againType;
    private Integer planStop;
    private Integer planDelete;
    private List<PandianStore> stores;
    private List<String> clerks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getStockShow() {
        return stockShow;
    }

    public void setStockShow(String stockShow) {
        this.stockShow = stockShow;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getPlanCheck() {
        return planCheck;
    }

    public void setPlanCheck(String planCheck) {
        this.planCheck = planCheck;
    }

    public String getAgainType() {
        return againType;
    }

    public void setAgainType(String againType) {
        this.againType = againType;
    }

    public Integer getPlanStop() {
        return planStop;
    }

    public void setPlanStop(Integer planStop) {
        this.planStop = planStop;
    }

    public Integer getPlanDelete() {
        return planDelete;
    }

    public void setPlanDelete(Integer planDelete) {
        this.planDelete = planDelete;
    }

    public List<PandianStore> getStores() {
        return stores;
    }

    public void setStores(List<PandianStore> stores) {
        this.stores = stores;
    }

    public List<String> getClerks() {
        return clerks;
    }

    public void setClerks(List<String> clerks) {
        this.clerks = clerks;
    }

    @Override
    public String toString() {
        return "PandianPlanDetail{" +
            "id=" + id +
            ", source='" + source + '\'' +
            ", planType='" + planType + '\'' +
            ", uploadType='" + uploadType + '\'' +
            ", stockShow='" + stockShow + '\'' +
            ", checkType='" + checkType + '\'' +
            ", planCheck='" + planCheck + '\'' +
            ", againType='" + againType + '\'' +
            ", planStop=" + planStop +
            ", planDelete=" + planDelete +
            ", stores=" + stores +
            ", clerks=" + clerks +
            '}';
    }




    public static class PandianStore {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "PandianStore{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
        }
    }
}
