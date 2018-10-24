package com.jk51.modules.task.domain.dto;

/**
 * @author
 */
public class RewardRankQueryDTO {
	private Byte joinType;
	private Integer executeId;
    private Integer siteId;
    private Integer joinId;
    private Integer storeId;
	private Integer top;

    public Byte getJoinType() {
        return joinType;
    }

    public void setJoinType(Byte joinType) {
        this.joinType = joinType;
    }

    public Integer getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Integer executeId) {
        this.executeId = executeId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getJoinId() {
        return joinId;
    }

    public void setJoinId(Integer joinId) {
        this.joinId = joinId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return "RewardRankQueryDTO{" +
            "joinType=" + joinType +
            ", executeId=" + executeId +
            ", siteId=" + siteId +
            ", joinId=" + joinId +
            ", storeId=" + storeId +
            ", top=" + top +
            '}';
    }
}
