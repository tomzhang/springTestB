package com.jk51.model.order;

import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.concession.result.Result;
import com.jk51.model.grouppurchase.GroupPurchase;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:送货上门订单和门店自提订单入参模型
 * 作者: baixiongfei
 * 创建日期: 2017/2/28
 * 修改记录:
 */
public class HomeDeliveryAndStoresInvite {

    private int siteId;//商家ID

    private String mobile;//用户手机号码

    private String orderType;//订单类型：1：送货上门订单；2：门店自提订单

    private List<OrderGoods> orderGoods;//商品信息

    private String receiverName;//收货人姓名

    private String receiverProvinceCode;//收货人所在省或者直辖市的编码

    private String receiverCityCode;//收货人城市编码

    private String receiverCountryCode;//收货人所在城市的区编码

    private String receiverAddress;//收货人地址

    private String receiverMobile;//收货人手机号码

    private String receiverPhone;//收货人电话(座机)

    private String receiverZip;//收货人地址邮编

    private Integer tradesInvoice;//是否要发票: 0(不需要)，1（需要）

    private String invoiceTitle;//发票抬头

    private Integer tradesSource;//订单来源: 110 (网站)，120（微信），130（app）, 140（店员帮用户下单），9999（其它）, 160（支付宝）

    private String tradesStore;//订单来源门店

    private Integer selfTakenStore;//自提门店ID

    private String buyerMessage;//买家留言

    private Integer integralUse;//使用积分

    private String lng;//用户共享的经度

    private String lat;//用户共享的纬度

    private Integer storeUserId;//门店促销员ID

    private String clerkInvitationCode;//门店促销员的邀请码

    private Integer userCouponId;//使用的优惠券ID

    private Integer distributCharge;//推荐人优惠

    private Integer flag;// 1:医生服务类订单

    private Integer userDetailId;//排版详情表（b_servce_use_detail）的id

    private Integer schedulePersonId;

    private Boolean exchange;

    private Integer userId;

    private Integer erpStoreId;

    private Integer erpAreaCode;

    private Map activityMap;

    /* -- 优惠券/活动 相关参数 -- */

    private List<GiftResult> giftGoods;//赠品信息

    private GroupPurchase groupPurchase;//拼团活动参数信息

    private String groupPurchaseJson;

    private String promActivityIdArr; // 优惠活动ids

    private Result concessionResult;

    /* todo 超级优惠上线后删除
    private Map discountDetail;//优惠活动，优惠券详情*/

    /* -- 优惠券/活动 相关参数 End -- */

    /**
     * 平安健康数据
     */
    private String orderNo;
    private String filePdf;
    private String secondToken;
    private String diagnosticResults;

    private String prescriptionNo;


    /* -- setter & getter -- */

    public void setPrescriptionNo(String prescriptionNo) {
        this.prescriptionNo = prescriptionNo;
    }

    public String getPrescriptionNo() {
        return prescriptionNo;
    }

    public String getDiagnosticResults() {
        return diagnosticResults;
    }

    public void setDiagnosticResults(String diagnosticResults) {
        this.diagnosticResults = diagnosticResults;
    }

    public String getFilePdf() {
        return filePdf;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setFilePdf(String filePdf) {
        this.filePdf = filePdf;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setSecondToken(String secondToken) {
        this.secondToken = secondToken;
    }

    public String getSecondToken() {
        return secondToken;
    }

    public Result getConcessionResult() {
        return concessionResult;
    }

    public void setConcessionResult(Result concessionResult) {
        this.concessionResult = concessionResult;
    }

    public Integer getSchedulePersonId() {
        return schedulePersonId;
    }

    public void setSchedulePersonId(Integer schedulePersonId) {
        this.schedulePersonId = schedulePersonId;
    }

    public void setActivityMap(Map activityMap) {
        this.activityMap = activityMap;
    }

    public Map getActivityMap() {
        return activityMap;
    }

    public Integer getDiagnoseStatus() {
        return diagnoseStatus;
    }

    public void setDiagnoseStatus(Integer diagnoseStatus) {
        this.diagnoseStatus = diagnoseStatus;
    }

    public String getDiseaseInfo() {
        return diseaseInfo;
    }

    public void setDiseaseInfo(String diseaseInfo) {
        this.diseaseInfo = diseaseInfo;
    }

    public Integer getUseDetailId() {
        return useDetailId;
    }

    public void setUseDetailId(Integer useDetailId) {
        this.useDetailId = useDetailId;
    }

    private Integer diagnoseStatus;

    private String diseaseInfo;

    private Integer useDetailId;

    private Integer accountSource;

    private Integer useCount;

    private String templateNo;

    private Integer mineClassesId;

    private Integer distributorId;

    private String giftSource;
    private String receiverProvince;//收货人所在省或者直辖市

    private String receiverCity;//收货人城市

    private String receiverCountry;//收货人所在城市的区

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCountry(String receiverCountry) {
        this.receiverCountry = receiverCountry;
    }

    public String getReceiverCountry() {
        return receiverCountry;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public Integer getMineClassesId() {
        return mineClassesId;
    }

    public void setMineClassesId(Integer mineClassesId) {
        this.mineClassesId = mineClassesId;
    }

    public Integer getAccountSource() {
        return accountSource;
    }

    public void setAccountSource(Integer accountSource) {
        this.accountSource = accountSource;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Integer getUserDetailId() {
        return userDetailId;
    }

    public void setUserDetailId(Integer userDetailId) {
        this.userDetailId = userDetailId;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<GiftResult> getGiftGoods() {
        return giftGoods;
    }

    public void setGiftGoods(List<GiftResult> giftGoods) {
        this.giftGoods = giftGoods;
    }

    public String getPromActivityIdArr() {
        return promActivityIdArr;
    }

    public void setPromActivityIdArr(String promActivityIdArr) {
        this.promActivityIdArr = promActivityIdArr;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<OrderGoods> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(List<OrderGoods> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverProvinceCode() {
        return receiverProvinceCode;
    }

    public void setReceiverProvinceCode(String receiverProvinceCode) {
        this.receiverProvinceCode = receiverProvinceCode;
    }

    public String getReceiverCityCode() {
        return receiverCityCode;
    }

    public void setReceiverCityCode(String receiverCityCode) {
        this.receiverCityCode = receiverCityCode;
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

    public Integer getTradesInvoice() {
        return tradesInvoice;
    }

    public void setTradesInvoice(Integer tradesInvoice) {
        this.tradesInvoice = tradesInvoice;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public Integer getTradesSource() {
        return tradesSource;
    }

    public void setTradesSource(Integer tradesSource) {
        this.tradesSource = tradesSource;
    }

    public String getTradesStore() {
        return tradesStore;
    }

    public void setTradesStore(String tradesStore) {
        this.tradesStore = tradesStore;
    }

    public Integer getSelfTakenStore() {
        return selfTakenStore;
    }

    public void setSelfTakenStore(Integer selfTakenStore) {
        this.selfTakenStore = selfTakenStore;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public Integer getIntegralUse() {
        return integralUse;
    }

    public void setIntegralUse(Integer integralUse) {
        this.integralUse = integralUse;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Integer getStoreUserId() {
        return storeUserId;
    }

    public void setStoreUserId(Integer storeUserId) {
        this.storeUserId = storeUserId;
    }

    public String getClerkInvitationCode() {
        return clerkInvitationCode;
    }

    public void setClerkInvitationCode(String clerkInvitationCode) {
        this.clerkInvitationCode = clerkInvitationCode;
    }

    public Integer getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Integer userCouponId) {
        this.userCouponId = userCouponId;
    }

    public Integer getDistributCharge() {
        return distributCharge;
    }

    public void setDistributCharge(Integer distributCharge) {
        this.distributCharge = distributCharge;
    }

    public String getGiftSource() {
        return giftSource;
    }

    public void setGiftSource(String giftSource) {
        this.giftSource = giftSource;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getErpStoreId() {
        return erpStoreId;
    }

    public void setErpStoreId(Integer erpStoreId) {
        this.erpStoreId = erpStoreId;
    }

    public Integer getErpAreaCode() {
        return erpAreaCode;
    }

    public void setErpAreaCode(Integer erpAreaCode) {
        this.erpAreaCode = erpAreaCode;
    }

    public Boolean getExchange() {
        return exchange;
    }

    public void setExchange(Boolean exchange) {
        this.exchange = exchange;
    }

    public GroupPurchase getGroupPurchase() {
        return groupPurchase;
    }

    public void setGroupPurchase(GroupPurchase groupPurchase) {
        this.groupPurchase = groupPurchase;
    }

    public String getGroupPurchaseJson() {
        return groupPurchaseJson;
    }

    public void setGroupPurchaseJson(String groupPurchaseJson) {
        this.groupPurchaseJson = groupPurchaseJson;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
