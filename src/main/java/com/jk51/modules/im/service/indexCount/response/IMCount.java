package com.jk51.modules.im.service.indexCount.response;

import com.jk51.modules.im.service.indexCount.IMCountUnit;
import com.jk51.modules.im.util.IMCountConstant;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-20
 * 修改记录:
 */
public class IMCount {


    //服务满意度
    private Float serviceSatisfaction;
    //较前一日的满意度波动比例(用当日的数据指标除以前一日的数据指标，然后结果-1)
    private Float serviceSatisfactionProportion;

    //咨询次数
    private Integer advisoryNum;
    //咨询次数较前一天比较
    private Float advisoryNumProportion;

    //会员流失人次
    private Integer lostNum;
    //会员流失人次较前一天比较
    private Float lostNumProportion;

    //回复店员人数
    private Integer clerkNum;
    //回复店员人数,较前一天比较
    private Float clerkNumProportion;

    //顾客平均等待时间
    private Float memberWaitAverageTime;
    //顾客平均等待时间,较前一天比较
    private Float memberWaitAverageTimeProportion;

    //店员回复间隔平均时间
    private Float clerkReplyAverageTime;
    //店员回复间隔平均时间,较前一天比较
    private Float clerkReplyAverageTimeProportion;

    //店员平均服务时长
    private Float serviceAverageTime;
    //店员平均服务时长,较前一天比较
    private Float serviceAverageTimeProportion;

    //店员超时关闭次数
    private Integer clerkTimeOutTimeNum;
    //店员超时关闭次数，较前一天比较
    private Float clerkTimeOutTimeNumProportion;


    private List<Proportion> proportions = new ArrayList<>();


    public void addProportion(float proportionValue,String name,float value,String unit){

        Proportion proportion = Proportion.createProportion(proportionValue,name,value,unit);
        proportions.add(proportion);
    }

    //proportionMap根据Value排降序
    public void orderProportions(){

        proportions.sort((p1,p2)->((Float)Math.abs(p1.getProportion())).compareTo((Float)Math.abs(p2.getProportion())));
        Collections.reverse(proportions);
    }


    public Float getServiceSatisfaction() {
        return serviceSatisfaction;
    }

    public void setServiceSatisfaction(Float serviceSatisfaction) {
        this.serviceSatisfaction = serviceSatisfaction;

    }

    public Float getServiceSatisfactionProportion() {
        return serviceSatisfactionProportion;

    }

    public void setServiceSatisfactionProportion(Float serviceSatisfactionProportion) {
        this.serviceSatisfactionProportion = serviceSatisfactionProportion;

        addProportion(serviceSatisfactionProportion,IMCountConstant.serviceSatisfaction,getServiceSatisfaction(), IMCountUnit.minute);
    }

    public Integer getAdvisoryNum() {
        return advisoryNum;
    }

    public void setAdvisoryNum(Integer advisoryNum) {
        this.advisoryNum = advisoryNum;
    }

    public Float getAdvisoryNumProportion() {
        return advisoryNumProportion;
    }

    public void setAdvisoryNumProportion(Float advisoryNumProportion) {
        this.advisoryNumProportion = advisoryNumProportion;

        addProportion(advisoryNumProportion,IMCountConstant.advisoryNum,getAdvisoryNum(),IMCountUnit.times);
    }

    public Integer getLostNum() {
        return lostNum;
    }

    public void setLostNum(Integer lostNum) {
        this.lostNum = lostNum;
    }

    public Float getLostNumProportion() {
        return lostNumProportion;
    }

    public void setLostNumProportion(Float lostNumProportion) {
        this.lostNumProportion = lostNumProportion;

        addProportion(lostNumProportion,IMCountConstant.lostNum,getLostNum(),IMCountUnit.times);
    }

    public Integer getClerkNum() {
        return clerkNum;
    }

    public void setClerkNum(Integer clerkNum) {
        this.clerkNum = clerkNum;
    }

    public Float getClerkNumProportion() {
        return clerkNumProportion;
    }

    public void setClerkNumProportion(Float clerkNumProportion) {

        this.clerkNumProportion = clerkNumProportion;

        addProportion(clerkNumProportion,IMCountConstant.clerkNum,getClerkNum(),IMCountUnit.times);
    }

    public Float getMemberWaitAverageTime() {
        return memberWaitAverageTime;
    }

    public void setMemberWaitAverageTime(Float memberWaitAverageTime) {
        this.memberWaitAverageTime = memberWaitAverageTime;
    }

    public Float getMemberWaitAverageTimeProportion() {
        return memberWaitAverageTimeProportion;
    }

    public void setMemberWaitAverageTimeProportion(Float memberWaitAverageTimeProportion) {

        this.memberWaitAverageTimeProportion = memberWaitAverageTimeProportion;

        addProportion(memberWaitAverageTimeProportion,IMCountConstant.memberWaitAverageTime,getMemberWaitAverageTime(),IMCountUnit.second);
    }

    public Float getClerkReplyAverageTime() {
        return clerkReplyAverageTime;
    }

    public void setClerkReplyAverageTime(Float clerkReplyAverageTime) {
        this.clerkReplyAverageTime = clerkReplyAverageTime;
    }

    public Float getClerkReplyAverageTimeProportion() {
        return clerkReplyAverageTimeProportion;
    }

    public void setClerkReplyAverageTimeProportion(Float clerkReplyAverageTimeProportion) {
        this.clerkReplyAverageTimeProportion = clerkReplyAverageTimeProportion;

        addProportion(clerkReplyAverageTimeProportion,IMCountConstant.clerkReplyAverageTime,getClerkReplyAverageTime(),IMCountUnit.second);
    }

    public Float getServiceAverageTime() {
        return serviceAverageTime;
    }

    public void setServiceAverageTime(Float serviceAverageTime) {
        this.serviceAverageTime = serviceAverageTime;
    }

    public Float getServiceAverageTimeProportion() {
        return serviceAverageTimeProportion;
    }

    public void setServiceAverageTimeProportion(Float serviceAverageTimeProportion) {
        this.serviceAverageTimeProportion = serviceAverageTimeProportion;

        addProportion(serviceAverageTimeProportion,IMCountConstant.serviceAverageTime,getServiceAverageTime(),IMCountUnit.second);
    }

    public Integer getClerkTimeOutTimeNum() {
        return clerkTimeOutTimeNum;
    }

    public void setClerkTimeOutTimeNum(Integer clerkTimeOutTimeNum) {
        this.clerkTimeOutTimeNum = clerkTimeOutTimeNum;
    }

    public Float getClerkTimeOutTimeNumProportion() {
        return clerkTimeOutTimeNumProportion;
    }

    public void setClerkTimeOutTimeNumProportion(Float clerkTimeOutTimeNumProportion) {

        this.clerkTimeOutTimeNumProportion = clerkTimeOutTimeNumProportion;
        addProportion(clerkTimeOutTimeNumProportion,IMCountConstant.clerkTimeOutTimeNum,getClerkTimeOutTimeNum(),IMCountUnit.times);
    }


    public List<Proportion> getProportions() {
        return proportions;
    }

    public void setProportions(List<Proportion> proportions) {
        this.proportions = proportions;
    }
}
