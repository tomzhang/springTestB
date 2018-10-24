package com.jk51.model.order;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 预约订单(商品)查询的请求参数
 * 作者: baixiongfei
 * 创建日期: 2017/3/11
 * 修改记录:
 */
public class OrderPreQueryReq {

    private String siteId;

    private String preNumber; //预约编号

    private String mobile;

    private Integer preGoodsId;

    private String goodsName;

    private Integer preGoodsNum;

    private String preAddress;

    private Integer preStatus;//预约状态

    private Date preTime; //预约时间

    private Integer preStyle; //预约方式

    private Integer preStoreId;//自提门店ID

    private Integer preAddressId;//收货地址ID

    private Integer pageNum=1;//当前第几页，默认为第一页

    private Integer pageSize=10;//默认每页多少条数据，默认每页10条

    private String receiverName;

    private String receiverCityCode;

    private String receiverProvinceCode;

    private String receiverCountryCode;

    private String receiverAddress;

    private String receiverMobile;

    private String receiverPhone;

    private String receiverZip;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverCityCode() {
        return receiverCityCode;
    }

    public void setReceiverCityCode(String receiverCityCode) {
        this.receiverCityCode = receiverCityCode;
    }

    public String getReceiverProvinceCode() {
        return receiverProvinceCode;
    }

    public void setReceiverProvinceCode(String receiverProvinceCode) {
        this.receiverProvinceCode = receiverProvinceCode;
    }

    public String getReceiverCountryCode() {
        return receiverCountryCode;
    }

    public void setReceiverCountryCode(String receiverCountryCode) {
        this.receiverCountryCode = receiverCountryCode;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getPreGoodsId() {
        return preGoodsId;
    }

    public void setPreGoodsId(Integer preGoodsId) {
        this.preGoodsId = preGoodsId;
    }

    public Integer getPreGoodsNum() {
        return preGoodsNum;
    }

    public void setPreGoodsNum(Integer preGoodsNum) {
        this.preGoodsNum = preGoodsNum;
    }

    public String getPreAddress() {
        return preAddress;
    }

    public void setPreAddress(String preAddress) {
        this.preAddress = preAddress;
    }

    public String getPreNumber() {
        return preNumber;
    }

    public void setPreNumber(String preNumber) {
        this.preNumber = preNumber;
    }

    public Integer getPreStatus() {
        return preStatus;
    }

    public void setPreStatus(Integer preStatus) {
        this.preStatus = preStatus;
    }

    public Date getPreTime() {
        return preTime;
    }

    public void setPreTime(Date preTime) {
        this.preTime = preTime;
    }

    public Integer getPreStyle() {
        return preStyle;
    }

    public void setPreStyle(Integer preStyle) {
        this.preStyle = preStyle;
    }

    public Integer getPreStoreId() {
        return preStoreId;
    }

    public void setPreStoreId(Integer preStoreId) {
        this.preStoreId = preStoreId;
    }

    public Integer getPreAddressId() {
        return preAddressId;
    }

    public void setPreAddressId(Integer preAddressId) {
        this.preAddressId = preAddressId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }


    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "OrderPreQueryReq{" +
            "siteId='" + siteId + '\'' +
            ", preNumber='" + preNumber + '\'' +
            ", mobile='" + mobile + '\'' +
            ", preGoodsId='" + preGoodsId + '\'' +
            ", goodsName='" + goodsName + '\'' +
            ", preGoodsNum='" + preGoodsNum + '\'' +
            ", preAddress='" + preAddress + '\'' +
            ", preStatus='" + preStatus + '\'' +
            ", preTime='" + preTime + '\'' +
            ", preStyle='" + preStyle + '\'' +
            ", preStoreId='" + preStoreId + '\'' +
            ", preAddressId='" + preAddressId + '\'' +
            ", pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            '}';
    }
}
