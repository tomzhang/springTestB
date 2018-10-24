package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-15
 * 修改记录:
 */
public class Trades extends Page {
    private Integer siteId;
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long tradesId;
    private Integer sellerId;
    private Integer buyerId;
    private String sellerNick;
    private String shop_weixin;
    private String storeNames;
    private Integer sum; //拼团活动参团总人数

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    private Integer payingPeople;//
    private String groupStatus; //拼团活动的状态
    private int finance_type;  //设置结算方式 0以结束状态结算  1以付款状态结算


    public int getFinance_type() {
        return finance_type;
    }

    public void setFinance_type(int finance_type) {
        this.finance_type = finance_type;
    }


    public Integer getPayingPeople() {

        return payingPeople;
    }

    public void setPayingPeople(Integer payingPeople) {
        this.payingPeople = payingPeople;
    }

    public Integer getSum() {

        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public String getStoreNames() {
        return storeNames;
    }

    public void setStoreNames(String storeNames) {
        this.storeNames = storeNames;
    }

    public String getShop_weixin() {
        return shop_weixin;
    }

    public void setShop_weixin(String shop_weixin) {
        this.shop_weixin = shop_weixin;
    }

    private String buyerNick;

    private Integer goodsId;
    private Timestamp payTime; //付款时间
    private Date payDay;
    private Timestamp endTime; //交易成功时间
    private Timestamp consignTime; //商家发货时间
    private String receiverPhone;  //收货人电话

    public Date getPayDay() {
        return payDay;
    }

    public void setPayDay(Date payDay) {
        this.payDay = payDay;
    }

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
    private Integer peisongstatus;

    public Integer getPeisongstatus() {
        return peisongstatus;
    }

    public void setPeisongstatus(Integer peisongstatus) {
        this.peisongstatus = peisongstatus;
    }

    private Integer isRefund;  //是否申请退款
    private Integer closedResion;
    private Integer totalFee;
    private Integer postFee;
    private Integer realPay;  //实际支付金额
    private Integer tradesSplit;
    private Integer platSplit;
    private Integer O2OFreight;
    private Integer postStyle;
    private String deliveryName; //物流配送方式
    private Integer postId;
    private String postName;
    private String postNumber;
    private String buyerMessage;
    private String sellerMemo;
    private Integer buyerFlag;
    private Integer sellerFlag;
    private Timestamp delvTime;
    private String delvDesc;
    private Timestamp confirmGoodsTime;
    private String buyerPayNo;
    private String payStyle;
    private String payNumber;
    private Integer tradesSource;
    private Integer tradesInvoice;
    private String invoiceTitle;
    private Integer tradesStore;
    private Integer selfTakenStore;
    private String selfTakenCode;
    private Timestamp selfTakenCodeStart;
    private Timestamp selfTakenCodeExpires;
    private Timestamp selfTakenCodeChecktime;
    private Integer assignedStores;
    private Integer stockupStatus;
    private Integer shippingStatus;
    private Integer prescriptionOrders;
    private double lng;
    private double lat;
    private Integer storeUserId;
    private Integer storeShippingClerkId;
    private Integer recommendUserId;
    private Integer cashierId;
    private Integer stockupUserId;
    private Integer userPaying;
    private Integer tradesDel;
    private Integer tradesRank;
    private Timestamp tradesRankTime;
    private Integer settlementStatus;
    private Integer settlementStatusA;
    private Timestamp settlementFinalTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer settlementType;  //0 直购，1分销，2 预存金'
    private Integer createOrderAssignedStores;//送货上门时系统分的最优门店
    private Integer isPayment;  //0 表示未付款  1 表示已付款  默认为0
    private String budgetdate;  //结算预期时间
    private Integer accountCheckingStatus;  //对账状态 0=待处理 1=已处理
    private List<Orders> ordersList;

    private Store store;

    private Integer integralUsed;  //使用的积分

    private String storeUserName;//促销员名
    private String storeShippingName;//送货员名
    private String storeName; //门店名称  （服务门店）
    private String memberMobile;   //会员手机号
    private Integer realRefundMoney;   //退款金额
    private Map<String, Object> map;  //优惠券
    private Integer memberIntegrate;  //会员所剩积分
    private Integer integralFinalAward;  //实送积分
    private String memberName;
    private String clerkInvitationCode;//店员邀请码

    //快递支付
    private String logisticsName;
    private String logisticsStatus;
    private String stockupId;  // 备货编码

    private Integer cashPaymentPay;   //现金付款金额 单位：分
    private Integer medicalInsuranceCardPay;  //医保卡付款金额 单位：分
    private Integer lineBreaksPay;  //线下优惠金额 单位：分

    private Integer dealFinishStatus;  //交易结束状态
    private Integer servceTpye;  //交易结束状态
    private Integer useDetailId;//模版第几条数据id
    private Integer useCount;//使用次数
    private Integer amount;//总次数
    private Integer serveStatus;//服务状态
    private Integer schedulePersonId;//预约人id
    private Integer diagnoseStatus;//初复诊
    private String diseaseInfo;//病例详情
    private Timestamp templateStartTime;//预约的开始时间
    private Timestamp templateEndTime;//预约的结束时间
    private String goodsTitle;//商品标题
    private Integer servceType;//服务类型
    private String name;//预约联系人
    private String mobile;//联系人方式
    private Integer distributorId; //分销商id
    private String financeNo;
    private String financeNoRefund;
    private Integer refundFee;
    private Long orderNumber;
    private Integer isUpPrice;
    private String is_up_price;
    //o2o超过4-6公里向客户收的运费
    private int freightCommission;
    private List<TradesUpdatePriceLog> tradesUpdatePriceLogs;

    private String beginTime;

    private String liveTime;

    private String aftergroupLiveTime;

    private int role;

    private int remainPeople;

    private Integer fightGroupId;

    private Integer proActivityId;

    private Pair<String, List<Map<String, String>>> groupOrders;// 团购详情

    private List<Map<String,String>> discountList;// 优惠信息集合

    public List<Map<String, String>> getDiscountList() {
        return discountList;
    }

    public void setDiscountList(List<Map<String, String>> discountList) {
        this.discountList = discountList;
    }

    private String assign_store;
    private Integer assign_post_style;
    private String assign_store_curr;
    private Integer assign_post_style_curr;

    private Integer self_taken_flag;//是否允许任意门店提货

    private Map pageShow;
    private Integer postageDiscount;

    private int isServiceOrder;
    private Integer tradeTypePayLine;

    public void setTradeTypePayLine(Integer tradeTypePayLine) {
        this.tradeTypePayLine = tradeTypePayLine;
    }

    public Integer getTradeTypePayLine() {
        return tradeTypePayLine;
    }

    public void setIsServiceOrder(int isServiceOrder) {
        this.isServiceOrder = isServiceOrder;
    }

    public int getIsServiceOrder() {
        return isServiceOrder;
    }

    public void setPostageDiscount(Integer postageDiscount) {
        this.postageDiscount = postageDiscount;
    }

    public Integer getPostageDiscount() {
        return postageDiscount;
    }

    public void setPageShow(Map pageShow) {
        this.pageShow = pageShow;
    }

    public Map getPageShow() {
        return pageShow;
    }

    public Integer getSelf_taken_flag() {
        return self_taken_flag;
    }

    public void setSelf_taken_flag(Integer self_taken_flag) {
        this.self_taken_flag = self_taken_flag;
    }

    public String getAssign_store() {
        return assign_store;
    }

    public void setAssign_store(String assign_store) {
        this.assign_store = assign_store;
    }

    public Integer getAssign_post_style() {
        return assign_post_style;
    }

    public void setAssign_post_style(Integer assign_post_style) {
        this.assign_post_style = assign_post_style;
    }

    public String getAssign_store_curr() {
        return assign_store_curr;
    }

    public void setAssign_store_curr(String assign_store_curr) {
        this.assign_store_curr = assign_store_curr;
    }

    public Integer getAssign_post_style_curr() {
        return assign_post_style_curr;
    }

    public void setAssign_post_style_curr(Integer assign_post_style_curr) {
        this.assign_post_style_curr = assign_post_style_curr;
    }

    public Pair<String, List<Map<String, String>>> getGroupOrders() {
        return groupOrders;
    }

    public void setGroupOrders(Pair<String, List<Map<String, String>>> groupOrders) {
        this.groupOrders = groupOrders;
    }

    public Integer getProActivityId() {
        return proActivityId;
    }

    public void setProActivityId(Integer proActivityId) {
        this.proActivityId = proActivityId;
    }

    public Integer getFightGroupId() {

        return fightGroupId;
    }

    public void setFightGroupId(Integer fightGroupId) {
        this.fightGroupId = fightGroupId;
    }

    public int getRemainPeople() {
        return remainPeople;
    }

    public void setRemainPeople(int remainPeople) {
        this.remainPeople = remainPeople;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAftergroupLiveTime() {
        return aftergroupLiveTime;
    }

    public void setAftergroupLiveTime(String aftergroupLiveTime) {
        this.aftergroupLiveTime = aftergroupLiveTime;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }

    public String getBeginTime() {

        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getFinanceNoRefund() {
        return financeNoRefund;
    }

    public void setFinanceNoRefund(String financeNoRefund) {
        this.financeNoRefund = financeNoRefund;
    }

    public List<TradesUpdatePriceLog> getTradesUpdatePriceLogs() {
        return tradesUpdatePriceLogs;
    }

    public void setTradesUpdatePriceLogs(List<TradesUpdatePriceLog> tradesUpdatePriceLogs) {
        this.tradesUpdatePriceLogs = tradesUpdatePriceLogs;
    }

    public String getIs_up_price() {
        return is_up_price;
    }

    public void setIs_up_price(String is_up_price) {
        this.is_up_price = is_up_price;
    }

    public Integer getIsUpPrice() {
        return this.isUpPrice;
    }

    public void setIsUpPrice(Integer isUpPrice) {
        this.isUpPrice = isUpPrice;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    private String sTime;
    private String eTime;

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public Integer getSettlementStatusA() {
        return settlementStatusA;
    }

    public void setSettlementStatusA(Integer settlementStatusA) {
        this.settlementStatusA = settlementStatusA;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public String getClerkInvitationCode() {
        return clerkInvitationCode;
    }

    public void setClerkInvitationCode(String clerkInvitationCode) {
        this.clerkInvitationCode = clerkInvitationCode;
    }

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

    public Integer getServceType() {
        return servceType;
    }

    public void setServceType(Integer servceType) {
        this.servceType = servceType;
    }


    public Timestamp getTemplateStartTime() {
        return templateStartTime;
    }

    public void setTemplateStartTime(Timestamp templateStartTime) {
        this.templateStartTime = templateStartTime;
    }

    public Timestamp getTemplateEndTime() {
        return templateEndTime;
    }

    public void setTemplateEndTime(Timestamp templateEndTime) {
        this.templateEndTime = templateEndTime;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Integer getUseDetailId() {
        return useDetailId;
    }

    public void setUseDetailId(Integer useDetailId) {
        this.useDetailId = useDetailId;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getServeStatus() {
        return serveStatus;
    }

    public void setServeStatus(Integer serveStatus) {
        this.serveStatus = serveStatus;
    }

    public Integer getSchedulePersonId() {
        return schedulePersonId;
    }

    public void setSchedulePersonId(Integer schedulePersonId) {
        this.schedulePersonId = schedulePersonId;
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

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getStoreUserName() {
        return storeUserName;
    }

    public void setStoreUserName(String storeUserName) {
        this.storeUserName = storeUserName;
    }

    public String getStoreShippingName() {
        return storeShippingName;
    }

    public void setStoreShippingName(String storeShippingName) {
        this.storeShippingName = storeShippingName;
    }


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        this.sellerNick = sellerNick;
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Timestamp consignTime) {
        this.consignTime = consignTime;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getRecevierMobile() {
        return recevierMobile;
    }

    public void setRecevierMobile(String recevierMobile) {
        this.recevierMobile = recevierMobile;
    }

    public String getRecevierName() {
        return recevierName;
    }

    public void setRecevierName(String recevierName) {
        this.recevierName = recevierName;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }

    public String getSellerPayNo() {
        return sellerPayNo;
    }

    public void setSellerPayNo(String sellerPayNo) {
        this.sellerPayNo = sellerPayNo;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerMobile() {
        return sellerMobile;
    }

    public void setSellerMobile(String sellerMobile) {
        this.sellerMobile = sellerMobile;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Integer getTradesStatus() {
        return tradesStatus;
    }

    public void setTradesStatus(Integer tradesStatus) {
        this.tradesStatus = tradesStatus;
    }

    public Integer getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(Integer isRefund) {
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
        this.deliveryName = deliveryName;
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
        this.postName = postName;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getSellerMemo() {
        return sellerMemo;
    }

    public void setSellerMemo(String sellerMemo) {
        this.sellerMemo = sellerMemo;
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

    public Timestamp getDelvTime() {
        return delvTime;
    }

    public void setDelvTime(Timestamp delvTime) {
        this.delvTime = delvTime;
    }

    public String getDelvDesc() {
        return delvDesc;
    }

    public void setDelvDesc(String delvDesc) {
        this.delvDesc = delvDesc;
    }

    public Timestamp getConfirmGoodsTime() {
        return confirmGoodsTime;
    }

    public void setConfirmGoodsTime(Timestamp confirmGoodsTime) {
        this.confirmGoodsTime = confirmGoodsTime;
    }

    public String getBuyerPayNo() {
        return buyerPayNo;
    }

    public void setBuyerPayNo(String buyerPayNo) {
        this.buyerPayNo = buyerPayNo;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public String getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(String payNumber) {
        this.payNumber = payNumber;
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
        this.invoiceTitle = invoiceTitle;
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
        this.selfTakenCode = selfTakenCode;
    }

    public Timestamp getSelfTakenCodeStart() {
        return selfTakenCodeStart;
    }

    public void setSelfTakenCodeStart(Timestamp selfTakenCodeStart) {
        this.selfTakenCodeStart = selfTakenCodeStart;
    }

    public Timestamp getSelfTakenCodeExpires() {
        return selfTakenCodeExpires;
    }

    public void setSelfTakenCodeExpires(Timestamp selfTakenCodeExpires) {
        this.selfTakenCodeExpires = selfTakenCodeExpires;
    }

    public Timestamp getSelfTakenCodeChecktime() {
        return selfTakenCodeChecktime;
    }

    public void setSelfTakenCodeChecktime(Timestamp selfTakenCodeChecktime) {
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

    public Integer getPrescriptionOrders() {
        return prescriptionOrders;
    }

    public void setPrescriptionOrders(Integer prescriptionOrders) {
        this.prescriptionOrders = prescriptionOrders;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
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

    public Integer getUserPaying() {
        return userPaying;
    }

    public void setUserPaying(Integer userPaying) {
        this.userPaying = userPaying;
    }

    public Integer getTradesDel() {
        return tradesDel;
    }

    public void setTradesDel(Integer tradesDel) {
        this.tradesDel = tradesDel;
    }

    public Integer getTradesRank() {
        return tradesRank;
    }

    public void setTradesRank(Integer tradesRank) {
        this.tradesRank = tradesRank;
    }

    public Timestamp getTradesRankTime() {
        return tradesRankTime;
    }

    public void setTradesRankTime(Timestamp tradesRankTime) {
        this.tradesRankTime = tradesRankTime;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Timestamp getSettlementFinalTime() {
        return settlementFinalTime;
    }

    public void setSettlementFinalTime(Timestamp settlementFinalTime) {
        this.settlementFinalTime = settlementFinalTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }

    public Integer getCreateOrderAssignedStores() {
        return createOrderAssignedStores;
    }

    public void setCreateOrderAssignedStores(Integer createOrderAssignedStores) {
        this.createOrderAssignedStores = createOrderAssignedStores;
    }

    public Integer getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Integer isPayment) {
        this.isPayment = isPayment;
    }

    public String getBudgetdate() {
        return budgetdate;
    }

    public void setBudgetdate(String budgetdate) {
        this.budgetdate = budgetdate;
    }

    public Integer getAccountCheckingStatus() {
        return accountCheckingStatus;
    }

    public void setAccountCheckingStatus(Integer accountCheckingStatus) {
        this.accountCheckingStatus = accountCheckingStatus;
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    public Integer getIntegralUsed() {
        return integralUsed;
    }

    public void setIntegralUsed(Integer integralUsed) {
        this.integralUsed = integralUsed;
    }

    public Integer getO2OFreight() {
        return O2OFreight;
    }

    public void setO2OFreight(Integer o2OFreight) {
        O2OFreight = o2OFreight;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }

    public Integer getRealRefundMoney() {
        return realRefundMoney;
    }

    public void setRealRefundMoney(Integer realRefundMoney) {
        this.realRefundMoney = realRefundMoney;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public Integer getMemberIntegrate() {
        return memberIntegrate;
    }

    public void setMemberIntegrate(Integer memberIntegrate) {
        this.memberIntegrate = memberIntegrate;
    }

    public Integer getIntegralFinalAward() {
        return integralFinalAward;
    }

    public void setIntegralFinalAward(Integer integralFinalAward) {
        this.integralFinalAward = integralFinalAward;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getLogisticsName() {
        return logisticsName;
    }

    public void setLogisticsName(String logisticsName) {
        this.logisticsName = logisticsName;
    }

    public String getStockupId() {
        return stockupId;
    }

    public void setStockupId(String stockupId) {
        this.stockupId = stockupId;
    }

    public Integer getCashPaymentPay() {
        return cashPaymentPay;
    }

    public void setCashPaymentPay(Integer cashPaymentPay) {
        this.cashPaymentPay = cashPaymentPay;
    }

    public Integer getMedicalInsuranceCardPay() {
        return medicalInsuranceCardPay;
    }

    public void setMedicalInsuranceCardPay(Integer medicalInsuranceCardPay) {
        this.medicalInsuranceCardPay = medicalInsuranceCardPay;
    }

    public Integer getLineBreaksPay() {
        return lineBreaksPay;
    }

    public void setLineBreaksPay(Integer lineBreaksPay) {
        this.lineBreaksPay = lineBreaksPay;
    }

    public Integer getDealFinishStatus() {
        return dealFinishStatus;
    }

    public void setDealFinishStatus(Integer dealFinishStatus) {
        this.dealFinishStatus = dealFinishStatus;
    }

    public Integer getServceTpye() {
        return servceTpye;
    }

    public void setServceTpye(Integer servceTpye) {
        this.servceTpye = servceTpye;
    }

    public void setFreightCommission(int freightCommission) {
        this.freightCommission = freightCommission;
    }

    public int getFreightCommission() {
        return freightCommission;
    }

    private  String employee_number;

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    @Override
    public String toString() {
        return "Trades{" +
            "siteId=" + siteId +
            ", id=" + id +
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
            ", O2OFreight=" + O2OFreight +
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
            ", isPayment=" + isPayment +
            ", budgetdate='" + budgetdate + '\'' +
            ", accountCheckingStatus=" + accountCheckingStatus +
            ", store=" + store +
            ", integralUsed=" + integralUsed +
            ", storeUserName='" + storeUserName + '\'' +
            ", storeShippingName='" + storeShippingName + '\'' +
            ", storeName='" + storeName + '\'' +
            ", memberMobile='" + memberMobile + '\'' +
            ", realRefundMoney=" + realRefundMoney +
            ", map=" + map +
            ", memberIntegrate=" + memberIntegrate +
            ", integralFinalAward=" + integralFinalAward +
            ", templateStartTime=" + templateStartTime +
            ", templateEndTime=" + templateEndTime +
            ", servceTpye=" + servceTpye +
            ", name=" + name +
            ", mobile=" + mobile +
            ", freightCommission=" + freightCommission +
            '}';
    }
}
