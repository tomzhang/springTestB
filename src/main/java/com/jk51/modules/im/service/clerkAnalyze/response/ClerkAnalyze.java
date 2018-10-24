package com.jk51.modules.im.service.clerkAnalyze.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-23
 * 修改记录:
 */
public class ClerkAnalyze {


    private String name;
    private String mobile;
    private String storeName;

    //满意度
    private Float serviceSatisfaction;
    //服务人次
    private Float advisoryNum;
    //超时关闭次数
    private Float clerkTimeOutTimeNum;
    //首次响应时间
    private Float memberWaitAverageTime;
    //回复平均时间间隔
    private Float clerkReplyAverageTime;
    //平均服务时长
    private Float serviceAverageTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Float getServiceSatisfaction() {
        return serviceSatisfaction;
    }

    public void setServiceSatisfaction(Float serviceSatisfaction) {
        this.serviceSatisfaction = serviceSatisfaction;
    }

    public Float getAdvisoryNum() {
        return advisoryNum;
    }

    public void setAdvisoryNum(Float advisoryNum) {
        this.advisoryNum = advisoryNum;
    }

    public Float getClerkTimeOutTimeNum() {
        return clerkTimeOutTimeNum;
    }

    public void setClerkTimeOutTimeNum(Float clerkTimeOutTimeNum) {
        this.clerkTimeOutTimeNum = clerkTimeOutTimeNum;
    }

    public Float getMemberWaitAverageTime() {
        return memberWaitAverageTime;
    }

    public void setMemberWaitAverageTime(Float memberWaitAverageTime) {
        this.memberWaitAverageTime = memberWaitAverageTime;
    }

    public Float getClerkReplyAverageTime() {
        return clerkReplyAverageTime;
    }

    public void setClerkReplyAverageTime(Float clerkReplyAverageTime) {
        this.clerkReplyAverageTime = clerkReplyAverageTime;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Float getServiceAverageTime() {
        return serviceAverageTime;
    }

    public void setServiceAverageTime(Float serviceAverageTime) {
        this.serviceAverageTime = serviceAverageTime;
    }
}
