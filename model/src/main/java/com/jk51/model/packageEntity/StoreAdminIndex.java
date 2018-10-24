package com.jk51.model.packageEntity;

import com.jk51.model.FirstWeight;
import com.jk51.model.Target;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 * 店员各指标分数类
 */
public class StoreAdminIndex {

    private int site_id;//商家ID
    private int storeadmin_id;//店员ID
    private String user_name;//店员名
    private List<Target> targetList;
    private int store_id;//门店ID
    private String clerk_job;//职称
    private String mobile;
    private Integer target_record_id;//指标记录ID
    //职称指标分
    private double titleIndex;

    //满意度指标分
    private double satisfactionIndex;

    //历史服务人数指标分
    private double serviceTotalIndex;

    //历史服务人数
    private int serviceTotalNum;

    //历史抢答记录指标分
    private double historyAnswerSpeedIndex;

    //门店属性：旗舰店分
    private int storeAttributesFlagshipstoreIndex;

    //门店属性：门店订单量分
    private double storeAttributesOrderquantityIndex;

    //门店店员数量
    private double storeAttributesClerkQuantityIndex;

    //抢答后繁忙度
    private double busyIndex;

    //店员下单量指标
    private double orderNumIndex;

    //与用户的历史数据（交集指标）
    private double intersectionIndex;

    //指标的json字符串
    private String indexJson;

    //各指标总分
    private double countIndex;

    //一级权重
    private List<FirstWeight> firstWeightList;

    public String getClerk_job() {
        return clerk_job;
    }

    public void setClerk_job(String clerk_job) {
        this.clerk_job = clerk_job;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {

        this.mobile = mobile;
    }

    public Integer getTarget_record_id() {
        return target_record_id;
    }

    public void setTarget_record_id(Integer target_record_id) {
        this.target_record_id = target_record_id;
    }

    public List<FirstWeight> getFirstWeightList() {
        return firstWeightList;
    }

    public void setFirstWeightList(List<FirstWeight> firstWeightList) {
        this.firstWeightList = firstWeightList;
    }

    public double getIntersectionIndex() {
        return intersectionIndex;
    }

    public void setIntersectionIndex(double intersectionIndex) {
        this.intersectionIndex = intersectionIndex;
    }

    public double getOrderNumIndex() {
        return orderNumIndex;
    }

    public void setOrderNumIndex(double orderNumIndex) {
        this.orderNumIndex = orderNumIndex;
    }

    public double getBusyIndex() {
        return busyIndex;
    }

    public void setBusyIndex(double busyIndex) {
        this.busyIndex = busyIndex;
    }

    public double getHistoryAnswerSpeedIndex() {
        return historyAnswerSpeedIndex;
    }

    public void setHistoryAnswerSpeedIndex(double historyAnswerSpeedIndex) {
        this.historyAnswerSpeedIndex = historyAnswerSpeedIndex;
    }

    public int getServiceTotalNum() {
        return serviceTotalNum;
    }

    public void setServiceTotalNum(int serviceTotalNum) {
        this.serviceTotalNum = serviceTotalNum;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getStoreadmin_id() {
        return storeadmin_id;
    }

    public void setStoreadmin_id(int storeadmin_id) {
        this.storeadmin_id = storeadmin_id;
    }

    public double getTitleIndex() {
        return titleIndex;
    }

    public void setTitleIndex(double titleIndex) {
        this.titleIndex = titleIndex;
    }

    public double getSatisfactionIndex() {
        return satisfactionIndex;
    }

    public void setSatisfactionIndex(double satisfactionIndex) {
        this.satisfactionIndex = satisfactionIndex;
    }

    public double getServiceTotalIndex() {
        return serviceTotalIndex;
    }

    public void setServiceTotalIndex(double serviceTotalIndex) {
        this.serviceTotalIndex = serviceTotalIndex;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public List<Target> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<Target> targetList) {
        this.targetList = targetList;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getStoreAttributesFlagshipstoreIndex() {
        return storeAttributesFlagshipstoreIndex;
    }

    public void setStoreAttributesFlagshipstoreIndex(int storeAttributesFlagshipstoreIndex) {
        this.storeAttributesFlagshipstoreIndex = storeAttributesFlagshipstoreIndex;
    }

    public double getStoreAttributesOrderquantityIndex() {
        return storeAttributesOrderquantityIndex;
    }

    public void setStoreAttributesOrderquantityIndex(double storeAttributesOrderquantityIndex) {
        this.storeAttributesOrderquantityIndex = storeAttributesOrderquantityIndex;
    }

    public double getStoreAttributesClerkQuantityIndex() {
        return storeAttributesClerkQuantityIndex;
    }

    public void setStoreAttributesClerkQuantityIndex(double storeAttributesClerkQuantityIndex) {
        this.storeAttributesClerkQuantityIndex = storeAttributesClerkQuantityIndex;
    }

    public String getIndexJson() {
        return indexJson;
    }

    public void setIndexJson(String indexJson) {
        this.indexJson = indexJson;
    }

    public double getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(double countIndex) {
        this.countIndex = countIndex;
    }
}
