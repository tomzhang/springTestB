package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:快递运费规则
 * 作者: baixiongfei
 * 创建日期: 2017/2/22
 * 修改记录:
 */
public class DeliveryMethod {
    private int	siteId;//商家ID
    private int	devlId;//主键ID
    private int	postStyleId;//配送方式:110(卖家包邮),120(平邮),130(快递),140(EMS),150(送货上门),160(门店自提)，170(门店直销)，9999(其它)
    private String deliveryName;//物流配送方式名
    private int	valuation;//计费方式: 10(表示按宝贝件数计算运费), 11(表示按宝贝重量计算运费), 12(表示按宝贝体积计算运费)
    private int	firstWeight;//首重值：如果值为1000，如果按重量计算表示为1000g, 如果按件计算表示1件。
    private int	addWeight;//超重值：如果超出值设为1000，如果按重量计算表示为1000g, 如果按件计算表示1件。
    private int	defFirstprice;//默认首重运费（指定地区除外）
    private int	defAddprice;//默认续重运费（指定地区除外）
    private String appointFirstweight;//指定地区首重(g/件)，如果有三组指定的地区： 1;2;1(分号隔开，每组分号对应指定地区的首重)
    private String appointAddweight;//指定地区增重(g/件)，如果有三组指定的地区： 1;2;1(分号隔开，每组分号对应指定地区的增重)
    private String appointFirstprice;//指定地区首重运费,如果有三组指定的地区： 5;6;4(分号隔开，每组分号对应指定地区的首重运费)
    private String appointAddprice;//指定地区续重运费,,如果有三组指定的地区： 10;10;9(分号隔开，每组分号对应指定指定地区的续重)
    private String appointArea;//指定地区代码，如果有三组指定地区，格式为: 110000;310000,21800,20900;21000
    private String devlDesc;//各地区的预计到货时间，文字描述
    private String devDesc;//备注
    private int	isActivation;//是否启用这个物流公司： 0（不启用）, 1（启用）
    private int	defaultFlag;//是否默认: 1 (默认)  0（非默认）
    private Timestamp updateTime;//更新时间

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getDevlId() {
        return devlId;
    }

    public void setDevlId(int devlId) {
        this.devlId = devlId;
    }

    public int getPostStyleId() {
        return postStyleId;
    }

    public void setPostStyleId(int postStyleId) {
        this.postStyleId = postStyleId;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public int getValuation() {
        return valuation;
    }

    public void setValuation(int valuation) {
        this.valuation = valuation;
    }

    public int getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(int firstWeight) {
        this.firstWeight = firstWeight;
    }

    public int getAddWeight() {
        return addWeight;
    }

    public void setAddWeight(int addWeight) {
        this.addWeight = addWeight;
    }

    public int getDefFirstprice() {
        return defFirstprice;
    }

    public void setDefFirstprice(int defFirstprice) {
        this.defFirstprice = defFirstprice;
    }

    public int getDefAddprice() {
        return defAddprice;
    }

    public void setDefAddprice(int defAddprice) {
        this.defAddprice = defAddprice;
    }

    public String getAppointFirstweight() {
        return appointFirstweight;
    }

    public void setAppointFirstweight(String appointFirstweight) {
        this.appointFirstweight = appointFirstweight;
    }

    public String getAppointAddweight() {
        return appointAddweight;
    }

    public void setAppointAddweight(String appointAddweight) {
        this.appointAddweight = appointAddweight;
    }

    public String getAppointFirstprice() {
        return appointFirstprice;
    }

    public void setAppointFirstprice(String appointFirstprice) {
        this.appointFirstprice = appointFirstprice;
    }

    public String getAppointAddprice() {
        return appointAddprice;
    }

    public void setAppointAddprice(String appointAddprice) {
        this.appointAddprice = appointAddprice;
    }

    public String getAppointArea() {
        return appointArea;
    }

    public void setAppointArea(String appointArea) {
        this.appointArea = appointArea;
    }

    public String getDevlDesc() {
        return devlDesc;
    }

    public void setDevlDesc(String devlDesc) {
        this.devlDesc = devlDesc;
    }

    public String getDevDesc() {
        return devDesc;
    }

    public void setDevDesc(String devDesc) {
        this.devDesc = devDesc;
    }

    public int getIsActivation() {
        return isActivation;
    }

    public void setIsActivation(int isActivation) {
        this.isActivation = isActivation;
    }

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DeliveryMethod{" +
                "siteId=" + siteId +
                ", devlId=" + devlId +
                ", postStyleId=" + postStyleId +
                ", deliveryName='" + deliveryName + '\'' +
                ", valuation=" + valuation +
                ", firstWeight=" + firstWeight +
                ", addWeight=" + addWeight +
                ", defFirstprice=" + defFirstprice +
                ", defAddprice=" + defAddprice +
                ", appointFirstweight='" + appointFirstweight + '\'' +
                ", appointAddweight='" + appointAddweight + '\'' +
                ", appointFirstprice='" + appointFirstprice + '\'' +
                ", appointAddprice='" + appointAddprice + '\'' +
                ", appointArea='" + appointArea + '\'' +
                ", devlDesc='" + devlDesc + '\'' +
                ", devDesc='" + devDesc + '\'' +
                ", isActivation=" + isActivation +
                ", defaultFlag=" + defaultFlag +
                ", updateTime=" + updateTime +
                '}';
    }
}
