package com.jk51.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BTrades {

    private List<Map<String,Object>> order_list;
    private Long tradesId;

    private Integer sellerId;

    private Integer buyerId;

    private String sellerNick;

    private String buyerNick;

    private Integer goodsId;

    private Date payTime;

    private Date endTime;

    private Date consignTime;

    private String receiverPhone;

    private String recevierMobile;

    private String recevierName;

    private String receiverCity;

    private String receiverAddress;

    private String receiverZip;

    private String sellerPayNo;

    private String sellerPhone;

    private String sellerMobile;

    private String sellerName;

    private Integer tradesStatus;

    private Short isRefund;

    private Integer closedResion;

    private Integer totalFee;

    private Integer postFee;

    private Integer realPay;

    private Integer tradesSplit;

    private Integer platSplit;

    private Integer postStyle;

    private String deliveryName;

    private Integer postId;

    private String postName;

    private String postNumber;

    private String buyerMessage;

    private String sellerMemo;

    private Integer buyerFlag;

    private Integer sellerFlag;

    private Date delvTime;

    private String delvDesc;

    private Date confirmGoodsTime;

    private String buyerPayNo;

    private String payStyle;

    private String payNumber;

    private Integer tradesSource;

    private Integer tradesInvoice;

    private String invoiceTitle;

    private Integer tradesStore;

    private Integer selfTakenStore;

    private String selfTakenCode;

    private Date selfTakenCodeStart;

    private Date selfTakenCodeExpires;

    private Date selfTakenCodeChecktime;

    private Integer assignedStores;

    private Integer stockupStatus;

    private Integer shippingStatus;

    private Boolean prescriptionOrders;

    private Double lng;

    private Double lat;

    private Integer storeUserId;

    private Integer storeShippingClerkId;

    private Integer recommendUserId;

    private Integer cashierId;

    private Integer stockupUserId;

    private Byte userPaying;

    private Integer tradesDel;

    private Byte tradesRank;

    private Date tradesRankTime;

    private Short settlementStatus;

    private Date settlementFinalTime;

    private Date createTime;

    private Date updateTime;

    private Byte settlementType;

    private Integer createOrderAssignedStores;

    private String budgetdate;

    private Boolean isPayment;

    private Boolean accountCheckingStatus;
	
	private Integer id;

    private Integer siteId;


    public List<Map<String, Object>> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<Map<String, Object>> order_list) {
        this.order_list = order_list;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Long getTradesId() {
        return tradesId;
    }

    public void setTradesId(Long tradesId) {
        this.tradesId = tradesId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerNick() {
        return sellerNick;
    }

    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick == null ? null : sellerNick.trim();
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick == null ? null : buyerNick.trim();
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Date consignTime) {
        this.consignTime = consignTime;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }

    public String getRecevierMobile() {
        return recevierMobile;
    }

    public void setRecevierMobile(String recevierMobile) {
        this.recevierMobile = recevierMobile == null ? null : recevierMobile.trim();
    }

    public String getRecevierName() {
        return recevierName;
    }

    public void setRecevierName(String recevierName) {
        this.recevierName = recevierName == null ? null : recevierName.trim();
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity == null ? null : receiverCity.trim();
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress == null ? null : receiverAddress.trim();
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip == null ? null : receiverZip.trim();
    }

    public String getSellerPayNo() {
        return sellerPayNo;
    }

    public void setSellerPayNo(String sellerPayNo) {
        this.sellerPayNo = sellerPayNo == null ? null : sellerPayNo.trim();
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone == null ? null : sellerPhone.trim();
    }

    public String getSellerMobile() {
        return sellerMobile;
    }

    public void setSellerMobile(String sellerMobile) {
        this.sellerMobile = sellerMobile == null ? null : sellerMobile.trim();
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName == null ? null : sellerName.trim();
    }

    public Integer getTradesStatus() {
        return tradesStatus;
    }

    public void setTradesStatus(Integer tradesStatus) {
        this.tradesStatus = tradesStatus;
    }

    public Short getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(Short isRefund) {
        this.isRefund = isRefund;
    }

    public Integer getClosedResion() {
        return closedResion;
    }

    public void setClosedResion(Integer closedResion) {
        this.closedResion = closedResion;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getPostFee() {
        return postFee;
    }

    public void setPostFee(Integer postFee) {
        this.postFee = postFee;
    }

    public Integer getRealPay() {
        return realPay;
    }

    public void setRealPay(Integer realPay) {
        this.realPay = realPay;
    }

    public Integer getTradesSplit() {
        return tradesSplit;
    }

    public void setTradesSplit(Integer tradesSplit) {
        this.tradesSplit = tradesSplit;
    }

    public Integer getPlatSplit() {
        return platSplit;
    }

    public void setPlatSplit(Integer platSplit) {
        this.platSplit = platSplit;
    }

    public Integer getPostStyle() {
        return postStyle;
    }

    public void setPostStyle(Integer postStyle) {
        this.postStyle = postStyle;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName == null ? null : deliveryName.trim();
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName == null ? null : postName.trim();
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber == null ? null : postNumber.trim();
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage == null ? null : buyerMessage.trim();
    }

    public String getSellerMemo() {
        return sellerMemo;
    }

    public void setSellerMemo(String sellerMemo) {
        this.sellerMemo = sellerMemo == null ? null : sellerMemo.trim();
    }

    public Integer getBuyerFlag() {
        return buyerFlag;
    }

    public void setBuyerFlag(Integer buyerFlag) {
        this.buyerFlag = buyerFlag;
    }

    public Integer getSellerFlag() {
        return sellerFlag;
    }

    public void setSellerFlag(Integer sellerFlag) {
        this.sellerFlag = sellerFlag;
    }

    public Date getDelvTime() {
        return delvTime;
    }

    public void setDelvTime(Date delvTime) {
        this.delvTime = delvTime;
    }

    public String getDelvDesc() {
        return delvDesc;
    }

    public void setDelvDesc(String delvDesc) {
        this.delvDesc = delvDesc == null ? null : delvDesc.trim();
    }

    public Date getConfirmGoodsTime() {
        return confirmGoodsTime;
    }

    public void setConfirmGoodsTime(Date confirmGoodsTime) {
        this.confirmGoodsTime = confirmGoodsTime;
    }

    public String getBuyerPayNo() {
        return buyerPayNo;
    }

    public void setBuyerPayNo(String buyerPayNo) {
        this.buyerPayNo = buyerPayNo == null ? null : buyerPayNo.trim();
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle == null ? null : payStyle.trim();
    }

    public String getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(String payNumber) {
        this.payNumber = payNumber == null ? null : payNumber.trim();
    }

    public Integer getTradesSource() {
        return tradesSource;
    }

    public void setTradesSource(Integer tradesSource) {
        this.tradesSource = tradesSource;
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
        this.invoiceTitle = invoiceTitle == null ? null : invoiceTitle.trim();
    }

    public Integer getTradesStore() {
        return tradesStore;
    }

    public void setTradesStore(Integer tradesStore) {
        this.tradesStore = tradesStore;
    }

    public Integer getSelfTakenStore() {
        return selfTakenStore;
    }

    public void setSelfTakenStore(Integer selfTakenStore) {
        this.selfTakenStore = selfTakenStore;
    }

    public String getSelfTakenCode() {
        return selfTakenCode;
    }

    public void setSelfTakenCode(String selfTakenCode) {
        this.selfTakenCode = selfTakenCode == null ? null : selfTakenCode.trim();
    }

    public Date getSelfTakenCodeStart() {
        return selfTakenCodeStart;
    }

    public void setSelfTakenCodeStart(Date selfTakenCodeStart) {
        this.selfTakenCodeStart = selfTakenCodeStart;
    }

    public Date getSelfTakenCodeExpires() {
        return selfTakenCodeExpires;
    }

    public void setSelfTakenCodeExpires(Date selfTakenCodeExpires) {
        this.selfTakenCodeExpires = selfTakenCodeExpires;
    }

    public Date getSelfTakenCodeChecktime() {
        return selfTakenCodeChecktime;
    }

    public void setSelfTakenCodeChecktime(Date selfTakenCodeChecktime) {
        this.selfTakenCodeChecktime = selfTakenCodeChecktime;
    }

    public Integer getAssignedStores() {
        return assignedStores;
    }

    public void setAssignedStores(Integer assignedStores) {
        this.assignedStores = assignedStores;
    }

    public Integer getStockupStatus() {
        return stockupStatus;
    }

    public void setStockupStatus(Integer stockupStatus) {
        this.stockupStatus = stockupStatus;
    }

    public Integer getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(Integer shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Boolean getPrescriptionOrders() {
        return prescriptionOrders;
    }

    public void setPrescriptionOrders(Boolean prescriptionOrders) {
        this.prescriptionOrders = prescriptionOrders;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getStoreUserId() {
        return storeUserId;
    }

    public void setStoreUserId(Integer storeUserId) {
        this.storeUserId = storeUserId;
    }

    public Integer getStoreShippingClerkId() {
        return storeShippingClerkId;
    }

    public void setStoreShippingClerkId(Integer storeShippingClerkId) {
        this.storeShippingClerkId = storeShippingClerkId;
    }

    public Integer getRecommendUserId() {
        return recommendUserId;
    }

    public void setRecommendUserId(Integer recommendUserId) {
        this.recommendUserId = recommendUserId;
    }

    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    public Integer getStockupUserId() {
        return stockupUserId;
    }

    public void setStockupUserId(Integer stockupUserId) {
        this.stockupUserId = stockupUserId;
    }

    public Byte getUserPaying() {
        return userPaying;
    }

    public void setUserPaying(Byte userPaying) {
        this.userPaying = userPaying;
    }

    public Integer getTradesDel() {
        return tradesDel;
    }

    public void setTradesDel(Integer tradesDel) {
        this.tradesDel = tradesDel;
    }

    public Byte getTradesRank() {
        return tradesRank;
    }

    public void setTradesRank(Byte tradesRank) {
        this.tradesRank = tradesRank;
    }

    public Date getTradesRankTime() {
        return tradesRankTime;
    }

    public void setTradesRankTime(Date tradesRankTime) {
        this.tradesRankTime = tradesRankTime;
    }

    public Short getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Short settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Date getSettlementFinalTime() {
        return settlementFinalTime;
    }

    public void setSettlementFinalTime(Date settlementFinalTime) {
        this.settlementFinalTime = settlementFinalTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(Byte settlementType) {
        this.settlementType = settlementType;
    }

    public Integer getCreateOrderAssignedStores() {
        return createOrderAssignedStores;
    }

    public void setCreateOrderAssignedStores(Integer createOrderAssignedStores) {
        this.createOrderAssignedStores = createOrderAssignedStores;
    }

    public String getBudgetdate() {
        return budgetdate;
    }

    public void setBudgetdate(String budgetdate) {
        this.budgetdate = budgetdate == null ? null : budgetdate.trim();
    }

    public Boolean getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Boolean isPayment) {
        this.isPayment = isPayment;
    }

    public Boolean getAccountCheckingStatus() {
        return accountCheckingStatus;
    }

    public void setAccountCheckingStatus(Boolean accountCheckingStatus) {
        this.accountCheckingStatus = accountCheckingStatus;
    }

    public Boolean getPayment() {
        return isPayment;
    }

    public void setPayment(Boolean payment) {
        isPayment = payment;
    }

    @Override
    public String toString() {
        return "BTrades{" +
            "order_list=" + order_list +
            ", tradesId=" + tradesId +
            ", sellerId=" + sellerId +
            ", buyerId=" + buyerId +
            ", sellerNick='" + sellerNick + '\'' +
            ", buyerNick='" + buyerNick + '\'' +
            ", goodsId=" + goodsId +
            ", payTime=" + payTime +
            ", endTime=" + endTime +
            ", consignTime=" + consignTime +
            ", receiverPhone='" + receiverPhone + '\'' +
            ", recevierMobile='" + recevierMobile + '\'' +
            ", recevierName='" + recevierName + '\'' +
            ", receiverCity='" + receiverCity + '\'' +
            ", receiverAddress='" + receiverAddress + '\'' +
            ", receiverZip='" + receiverZip + '\'' +
            ", sellerPayNo='" + sellerPayNo + '\'' +
            ", sellerPhone='" + sellerPhone + '\'' +
            ", sellerMobile='" + sellerMobile + '\'' +
            ", sellerName='" + sellerName + '\'' +
            ", tradesStatus=" + tradesStatus +
            ", isRefund=" + isRefund +
            ", closedResion=" + closedResion +
            ", totalFee=" + totalFee +
            ", postFee=" + postFee +
            ", realPay=" + realPay +
            ", tradesSplit=" + tradesSplit +
            ", platSplit=" + platSplit +
            ", postStyle=" + postStyle +
            ", deliveryName='" + deliveryName + '\'' +
            ", postId=" + postId +
            ", postName='" + postName + '\'' +
            ", postNumber='" + postNumber + '\'' +
            ", buyerMessage='" + buyerMessage + '\'' +
            ", sellerMemo='" + sellerMemo + '\'' +
            ", buyerFlag=" + buyerFlag +
            ", sellerFlag=" + sellerFlag +
            ", delvTime=" + delvTime +
            ", delvDesc='" + delvDesc + '\'' +
            ", confirmGoodsTime=" + confirmGoodsTime +
            ", buyerPayNo='" + buyerPayNo + '\'' +
            ", payStyle='" + payStyle + '\'' +
            ", payNumber='" + payNumber + '\'' +
            ", tradesSource=" + tradesSource +
            ", tradesInvoice=" + tradesInvoice +
            ", invoiceTitle='" + invoiceTitle + '\'' +
            ", tradesStore=" + tradesStore +
            ", selfTakenStore=" + selfTakenStore +
            ", selfTakenCode='" + selfTakenCode + '\'' +
            ", selfTakenCodeStart=" + selfTakenCodeStart +
            ", selfTakenCodeExpires=" + selfTakenCodeExpires +
            ", selfTakenCodeChecktime=" + selfTakenCodeChecktime +
            ", assignedStores=" + assignedStores +
            ", stockupStatus=" + stockupStatus +
            ", shippingStatus=" + shippingStatus +
            ", prescriptionOrders=" + prescriptionOrders +
            ", lng=" + lng +
            ", lat=" + lat +
            ", storeUserId=" + storeUserId +
            ", storeShippingClerkId=" + storeShippingClerkId +
            ", recommendUserId=" + recommendUserId +
            ", cashierId=" + cashierId +
            ", stockupUserId=" + stockupUserId +
            ", userPaying=" + userPaying +
            ", tradesDel=" + tradesDel +
            ", tradesRank=" + tradesRank +
            ", tradesRankTime=" + tradesRankTime +
            ", settlementStatus=" + settlementStatus +
            ", settlementFinalTime=" + settlementFinalTime +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", settlementType=" + settlementType +
            ", createOrderAssignedStores=" + createOrderAssignedStores +
            ", budgetdate='" + budgetdate + '\'' +
            ", isPayment=" + isPayment +
            ", accountCheckingStatus=" + accountCheckingStatus +
            ", id=" + id +
            ", siteId=" + siteId +
            '}';
    }
}
