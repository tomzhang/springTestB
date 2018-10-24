package com.jk51.model.order;

import java.sql.Timestamp;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  商户总部后台-订单管理-订单列表页面查询订单信息入参
 * 作者: baixiongfei
 * 创建日期: 2017/3/8
 * 修改记录:
 */
public class QueryOrdersReq extends Page{

    /**
     * null 时: 查询所有订单
     * -1 时: 查询所有分销商订单（未用0，是因为库里订单默认是0，Integer默认是0，避免默认问题造成订单查询数据不正常）
     * >0 时: 查询某个分销商订单
     */
    private Integer distributorId;

    private String siteId;//商户ID

    private String tradesId;//订单ID

    private String storesId;//服务门店ID

    private String phone;//联系电话

    private String mobile;//会员手机

    private Integer integralUsed;//消费积分;
    private Integer is_up_price;//改价标记

    public Integer getIs_up_price() {
        return is_up_price;
    }

    public void setIs_up_price(Integer is_up_price) {
        this.is_up_price = is_up_price;
    }

    /**
     * 交易状态:
     * 110(等侍买家付款),
     * 120(等待卖家发货),
     * 130(等侍买家确认收货),
     * 140(买家已签收，货到付款专用)，
     * 150(交易成功)，
     * 160(用户未付款主动关闭)，
     * 170(超时未付款，系统关闭)，
     * 180(商家关闭订单)，
     * 200( 待取货|待自提，直购和自提专用),
     * 210（ 已取货|已自提 直购和自提专用），
     * 900（已退款），
     * 220(用户确认收货)，
     * 230(门店确认收货)，
     * 800（系统确认收货）
     */
    private String tradesStatus;
    /**新订单状态
     订单状态如下（10种）：
     1-待付款(110)
     2-已取消(160，170，180)
     3-待备货(120+110)
     4-待发货(120+120)
     5-已发货（130）
     6-待自提（200）
     7-交易成功（210,220,230,800）
     8-退款中（is_refund：100）
     9-拒绝退款（is_refund：120）
     10-退款成功（900）
     */
    private Integer newTradesStatus;

    public void setNewTradesStatus(Integer newTradesStatus) {
        this.newTradesStatus = newTradesStatus;
    }

    public Integer getNewTradesStatus() {
        return newTradesStatus;
    }

    /**
     * 配送方式(物流方式)：
     * 110(卖家包邮),
     * 120(平邮),
     * 130(快递),
     * 140(EMS),
     * 150(送货上门),
     * 160(门店自提)，
     * 170(门店直销)，
     * 180(货到付款),
     * 9999(其它)
     */
    private String postStyle;

    /**
     * 订单来源
     * 110 (网站)，
     * 120（微信），
     * 130（app）,
     * 140（店员帮用户下单），
     * 9999（其它）
     */
    private String tradesSource;

    /**
     * 是否有申请退款
     * 0 (无退款)
     * 100=等待受理（退款中）
     * 110=受理失败 （拒绝退款）
     * 120=退款成功
     */
    private String isRefund;

    /**
     * 买家支付方式:
     * ali (支付宝) ，
     * wx (微信)，
     * bil(快钱)，
     * unionPay(银联)，
     * health_insurance（医保），
     * cash（现金）
     */
    private String payStyle;

    //店员邀请码，该字段在b_store_adminext表中
    private String clerkInvitationCode;

    //下单时间区间，开始；对应create_time字段
    private String orderTimeStart;

    //下单时间区间，结束；对应create_time字段
    private String orderTimeEnd;

    //配送状态
    private String logisticsStatus;

    /**
     * 订单评分(满意度)
     * 0：未评价
     * 1：很差
     * 2：较差
     * 3：一般
     * 4：很好
     * 5：非常满意
     *
     * 表：b_trades_comments
     */
    private String commentRank;

    /**
     * 订单可结算状态
     */
    private String settlementStatus;

    //订单计算时间区间，开始;对应settlement_final_time字段
    private String settlementFinalTimeStart;

    //订单计算时间区间，结束;对应settlement_final_time字段
    private String settlementFinalTimeEnd;
    //会员ID
    private String buyerId;

    private Timestamp payTimeStart;

    private Timestamp payTimeEnd;

    private Integer isPayment;

    private List<Integer> list;

    private String selfTakenCode;

    private Timestamp selfTakenTime;

    private String tradesFlag;

    private String stockupStatus;//备货状态

    private String shippingStatus;//发货状态

    private String storeShippingClerkId;//送货店员ID

    private String tradesStartTime;//交易结束时间（搜索：起始时间）

    private String tradesEndTime;//交易结束时间（搜索：结束时间）

    //前端网页不展示APP下单
    private String isApp;

    //app展示店员邀请会员下的APP订单
    private String isAppNotpay;

    private String storesIdcope;//服务门店ID app展示收款订单使用

    private String assignFlag;//订单调配(商户后台、门店后台使用)

    private String assignStoreFlag;//订单调配（门店后台使用）

    private int platform;//0-微信商城，1-pc商城，2-商户后台，3-门店后台，4-门店APP，

    private int type;//0-列表，1-详情

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getPlatform() {
        return platform;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getAssignStoreFlag() {
        return assignStoreFlag;
    }

    public void setAssignStoreFlag(String assignStoreFlag) {
        this.assignStoreFlag = assignStoreFlag;
    }

    public String getAssignFlag() {
        return assignFlag;
    }

    public void setAssignFlag(String assignFlag) {
        this.assignFlag = assignFlag;
    }

    public void setStoresIdcope(String storesIdcope) {
        this.storesIdcope = storesIdcope;
    }

    public String getStoresIdcope() {
        return storesIdcope;
    }

    public void setIsAppNotpay(String isAppNotpay) {
        this.isAppNotpay = isAppNotpay;
    }

    public String getIsAppNotpay() {
        return isAppNotpay;
    }

    public void setIsApp(String isApp) {
        this.isApp = isApp;
    }

    public String getIsApp() {
        return isApp;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    private String goodsCode;   //商品编码

    private String goodsTitle; //商品标题

    private String isSelfTakenCode;//显示提货码

    public void setIsSelfTakenCode(String isSelfTakenCode) {
        this.isSelfTakenCode = isSelfTakenCode;
    }

    public String getIsSelfTakenCode() {
        return isSelfTakenCode;
    }

    public String getTradesStartTime() {
        return tradesStartTime;
    }

    public void setTradesStartTime(String tradesStartTime) {
        this.tradesStartTime = tradesStartTime;
    }

    public String getTradesEndTime() {
        return tradesEndTime;
    }

    public void setTradesEndTime(String tradesEndTime) {
        this.tradesEndTime = tradesEndTime;
    }

    public String getStoreShippingClerkId() {
        return storeShippingClerkId;
    }

    public void setStoreShippingClerkId(String storeShippingClerkId) {
        this.storeShippingClerkId = storeShippingClerkId;
    }

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getStockupStatus() {
        return stockupStatus;
    }

    public void setStockupStatus(String stockupStatus) {
        this.stockupStatus = stockupStatus;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public Integer getIntegralUsed() {return integralUsed;}

    public void setIntegralUsed(Integer integralUsed) {this.integralUsed = integralUsed;}

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public String getSelfTakenCode() {
        return selfTakenCode;
    }

    public void setSelfTakenCode(String selfTakenCode) {
        this.selfTakenCode = selfTakenCode;
    }

    public Timestamp getSelfTakenTime() {
        return selfTakenTime;
    }

    public void setSelfTakenTime(Timestamp selfTakenTime) {
        this.selfTakenTime = selfTakenTime;
    }

    public Integer getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(Integer isPayment) {
        this.isPayment = isPayment;
    }

    public Timestamp getPayTimeStart() {
        return payTimeStart;
    }

    public void setPayTimeStart(Timestamp payTimeStart) {
        this.payTimeStart = payTimeStart;
    }

    public Timestamp getPayTimeEnd() {
        return payTimeEnd;
    }

    public void setPayTimeEnd(Timestamp payTimeEnd) {
        this.payTimeEnd = payTimeEnd;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getTradesId() {
        return tradesId;
    }

    public void setTradesId(String tradesId) {
        this.tradesId = tradesId;
    }

    public String getStoresId() {
        return storesId;
    }

    public void setStoresId(String storesId) {
        this.storesId = storesId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTradesStatus() {
        return tradesStatus;
    }

    public void setTradesStatus(String tradesStatus) {
        this.tradesStatus = tradesStatus;
    }

    public String getPostStyle() {
        return postStyle;
    }

    public void setPostStyle(String postStyle) {
        this.postStyle = postStyle;
    }

    public String getTradesSource() {
        return tradesSource;
    }

    public void setTradesSource(String tradesSource) {
        this.tradesSource = tradesSource;
    }

    public String getPayStyle() {
        return payStyle;
    }

    public void setPayStyle(String payStyle) {
        this.payStyle = payStyle;
    }

    public String getClerkInvitationCode() {
        return clerkInvitationCode;
    }

    public void setClerkInvitationCode(String clerkInvitationCode) {
        this.clerkInvitationCode = clerkInvitationCode;
    }

    public String getOrderTimeStart() {
        return orderTimeStart;
    }

    public void setOrderTimeStart(String orderTimeStart) {
        this.orderTimeStart = orderTimeStart;
    }

    public String getOrderTimeEnd() {
        return orderTimeEnd;
    }

    public void setOrderTimeEnd(String orderTimeEnd) {
        this.orderTimeEnd = orderTimeEnd;
    }

    public String getCommentRank() {
        return commentRank;
    }

    public void setCommentRank(String commentRank) {
        this.commentRank = commentRank;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getSettlementFinalTimeStart() {
        return settlementFinalTimeStart;
    }

    public void setSettlementFinalTimeStart(String settlementFinalTimeStart) {
        this.settlementFinalTimeStart = settlementFinalTimeStart;
    }

    public String getSettlementFinalTimeEnd() {
        return settlementFinalTimeEnd;
    }

    public void setSettlementFinalTimeEnd(String settlementFinalTimeEnd) {
        this.settlementFinalTimeEnd = settlementFinalTimeEnd;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public String getTradesFlag() {
        return tradesFlag;
    }

    public void setTradesFlag(String tradesFlag) {
        this.tradesFlag = tradesFlag;
    }

    @Override
    public String toString() {
        return "QueryOrdersReq{" +
                "distributorId=" + distributorId +
                ", siteId='" + siteId + '\'' +
                ", tradesId='" + tradesId + '\'' +
                ", storesId='" + storesId + '\'' +
                ", phone='" + phone + '\'' +
                ", mobile='" + mobile + '\'' +
                ", tradesStatus='" + tradesStatus + '\'' +
                ", postStyle='" + postStyle + '\'' +
                ", tradesSource='" + tradesSource + '\'' +
                ", payStyle='" + payStyle + '\'' +
                ", clerkInvitationCode='" + clerkInvitationCode + '\'' +
                ", orderTimeStart='" + orderTimeStart + '\'' +
                ", orderTimeEnd='" + orderTimeEnd + '\'' +
                ", logisticsStatus='" + logisticsStatus + '\'' +
                ", commentRank='" + commentRank + '\'' +
                ", settlementStatus='" + settlementStatus + '\'' +
                ", settlementFinalTimeStart='" + settlementFinalTimeStart + '\'' +
                ", settlementFinalTimeEnd='" + settlementFinalTimeEnd + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", payTimeStart=" + payTimeStart +
                ", payTimeEnd=" + payTimeEnd +
                ", isPayment=" + isPayment +
                ", list=" + list +
                ", selfTakenCode='" + selfTakenCode + '\'' +
                ", selfTakenTime=" + selfTakenTime +
                ", tradesFlag='" + tradesFlag + '\'' +
                ", stockupStatus='" + stockupStatus + '\'' +
                ", shippingStatus='" + shippingStatus + '\'' +
                ", storeShippingClerkId='" + storeShippingClerkId + '\'' +
                ", tradesStartTime='" + tradesStartTime + '\'' +
                ", tradesEndTime='" + tradesEndTime + '\'' +
                ", goodsCode='" + goodsCode + '\'' +
                ", isSelfTakenCode='" + isSelfTakenCode + '\'' +
                ", isApp='" + isApp + '\'' +
                ", isAppNotpay='" + isAppNotpay + '\'' +
                ", storesIdcope='" + storesIdcope + '\'' +
                ", newTradesStatus='" + newTradesStatus + '\'' +
                '}';
    }
}
