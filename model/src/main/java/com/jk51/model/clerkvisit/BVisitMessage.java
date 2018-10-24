package com.jk51.model.clerkvisit;

/**
 * Created by Administrator on 2018/1/17.
 */
public class BVisitMessage {
    /**
     * 任务编号
     */
    private Integer id;
    /**
     * buyerId
     */
    private Integer bid;
    /**
     * goods_ids
     */
    private String gid;
    /**
     * activity_ids
     */
    private String aid;

    /**
     * buyerName
     */
    private String bName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }
}
