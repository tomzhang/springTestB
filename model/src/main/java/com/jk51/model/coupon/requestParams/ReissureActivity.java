package com.jk51.model.coupon.requestParams;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/7/31.
 */
public class ReissureActivity {
    //补发操作日志表
    private Integer siteId;

    private Integer id;

    private Integer totalNum;

    private Integer successNum;

    private Timestamp createTime;

    private Timestamp updateTime;

    private  Integer reManagerId;

    private  String  reManagerName;

    private  Integer activityId;

    public ReissureActivity(ReissureActivityParams reissureActivityParams){
        long times=System.currentTimeMillis();
        this.siteId=reissureActivityParams.getSiteId();
        this.activityId=reissureActivityParams.getActiveId();
        this.totalNum=reissureActivityParams.getVipMembers().split(",").length;
        this.successNum=0;
        this.createTime=new Timestamp(times);
        this.updateTime=new Timestamp(times);
        this.reManagerId=reissureActivityParams.getUserID();
        this.reManagerName=reissureActivityParams.getUserName();
        this.activityId=reissureActivityParams.getActiveId();
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReManagerId() {
        return reManagerId;
    }

    public void setReManagerId(Integer reManagerId) {
        this.reManagerId = reManagerId;
    }

    public String getReManagerName() {
        return reManagerName;
    }

    public void setReManagerName(String reManagerName) {
        this.reManagerName = reManagerName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

}
