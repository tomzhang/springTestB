package com.jk51.modules.meituan.request;

import com.jk51.modules.meituan.constants.OrderType;
import com.jk51.modules.meituan.vo.OpenApiGoods;

import java.math.BigDecimal;

/**
 * 创建订单（门店方式）参数
 */
public class CreateOrderByShopRequest extends AbstractRequest {

    /**
     * 即配送活动标识，由外部系统生成，不同order_id应对应 不同的delivery_id，若因美团系统故障导致发单接口失败，可利用相同的delivery_id重新发单，
     * 系统视为同一次 配送活动，若更换delivery_id，则系统视为两次独立配送 活动。
     */
    private long deliveryId;

    /**
     * 订单id，即该订单在合作方系统中的id，最长不超过32个字符
     * 注：目前若某一订单正在配送中（状态不为取消），再次发送同一订单 （order_id相同）将返回同一mt_peisong_id
     */
    private String orderId;

    /**
     * 取货门店id，即合作方向美团提供的门店id
     */
    private String shopId;

    /**
     * 配送服务代码，详情见合同
     * 光速达:4001
     * 快速达:4011
     * 及时达:4012
     * 集中送:4013
     * 当天达:4021
     */
    private Integer deliveryServiceCode;

    /**
     * 收件人名称，最长不超过256个字符
     */
    private String receiverName;

    /**
     * 收件人地址，最长不超过512个字符
     */
    private String receiverAddress;

    /**
     * 收件人电话，最长不超过64个字符
     */
    private String receiverPhone;

    /**
     * 收件人经度（高德坐标），高德坐标 * 10^6
     */
    private Integer receiverLng;

    /**
     * 收件人纬度（高德坐标），高德坐标 * 10^6
     */
    private Integer receiverLat;

    /**
     * 货物价格，单位为元，精确到小数点后两位，范围为0-99999999.99
     */
    private BigDecimal goodsValue;

    /**
     * 货物高度，单位为cm，精确到小数点后两位，范围为0-99999999.99
     */
    private BigDecimal goodsHeight;

    /**
     * 货物宽度，单位为cm，精确到小数点后两位，范围为0-99999999.99
     */
    private BigDecimal goodsWidth;

    /**
     * 货物长度，单位为cm，精确到小数点后两位，范围为0-99999999.99
     */
    private BigDecimal goodsLength;

    /**
     * 货物重量，单位为kg，精确到小数点后两位，范围为0-99999999.99
     */
    private BigDecimal goodsWeight;

    /**
     * 货物详情，最长不超过10240个字符，内容为JSON格式，示例如下：
     * <p>
     * {
     * "goods":
     * [
     * {
     * "goodCount": 1,
     * "goodName": "货品名称",
     * "goodPrice": 9.99，单位为元,
     * "goodUnit": "个"
     * }
     * ]
     * }
     * 其中，goods对应货物列表；
     * goodCount表示货物数量，int类型；
     * goodName表示货品名称，String类型；
     * goodPrice表示货品单价，double类型；
     * goodUnit表示货品单位，String类型。
     * <p>
     * 强烈建议提供，方便骑手在取货时确认货品信息
     */
    private OpenApiGoods goodsDetail;

    /**
     * 货物取货信息，用于骑手到店取货，最长不超过100个字符
     */
    private String goodsPickupInfo;

    /**
     * 货物交付信息，最长不超过100个字符
     */
    private String goodsDeliveryInfo;

    /**
     * 期望取货时间，时区为GMT+8，当前距离 Epoch（1970年1月1日) 以秒计算的时间，即unix-timestamp。
     */
    private Long expectedPickupTime;

    /**
     * 期望送达时间，时区为GMT+8，当前距离 Epoch（1970年1月1日) 以秒计算的时间，即unix-timestamp。
     */
    private Long expectedDeliveryTime;

    /**
     * 门店订单流水号，格式类似 #1
     * 建议提供，方便骑手门店取货
     */
    private String poiSeq;

    /**
     * 备注，最长不超过200个字符。
     */
    private String note;

    /**
     * 骑手应付金额，单位为元，精确到分【预留字段】
     */
    private BigDecimal cashOnDelivery;

    /**
     * 骑手应收金额，单位为元，精确到分【预留字段】
     */
    private BigDecimal cashOnPickup;

    /**
     * 发票抬头，最长不超过256个字符【预留字段】
     */
    private String invoiceTitle;

    /**
     * 订单类型，默认为0
     * 0: 及时单(尽快送达，限当日订单)
     * 1: 预约单
     */
    private OrderType orderType;

    public long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getDeliveryServiceCode() {
        return deliveryServiceCode;
    }

    public void setDeliveryServiceCode(Integer deliveryServiceCode) {
        this.deliveryServiceCode = deliveryServiceCode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Integer getReceiverLng() {
        return receiverLng;
    }

    public void setReceiverLng(Integer receiverLng) {
        this.receiverLng = receiverLng;
    }

    public Integer getReceiverLat() {
        return receiverLat;
    }

    public void setReceiverLat(Integer receiverLat) {
        this.receiverLat = receiverLat;
    }

    public BigDecimal getGoodsValue() {
        return goodsValue;
    }

    public void setGoodsValue(BigDecimal goodsValue) {
        this.goodsValue = goodsValue;
    }

    public BigDecimal getGoodsHeight() {
        return goodsHeight;
    }

    public void setGoodsHeight(BigDecimal goodsHeight) {
        this.goodsHeight = goodsHeight;
    }

    public BigDecimal getGoodsWidth() {
        return goodsWidth;
    }

    public void setGoodsWidth(BigDecimal goodsWidth) {
        this.goodsWidth = goodsWidth;
    }

    public BigDecimal getGoodsLength() {
        return goodsLength;
    }

    public void setGoodsLength(BigDecimal goodsLength) {
        this.goodsLength = goodsLength;
    }

    public BigDecimal getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(BigDecimal goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public OpenApiGoods getGoodsDetail() {
        return goodsDetail;
    }

    public void setGoodsDetail(OpenApiGoods goodsDetail) {
        this.goodsDetail = goodsDetail;
    }

    public String getGoodsPickupInfo() {
        return goodsPickupInfo;
    }

    public void setGoodsPickupInfo(String goodsPickupInfo) {
        this.goodsPickupInfo = goodsPickupInfo;
    }

    public String getGoodsDeliveryInfo() {
        return goodsDeliveryInfo;
    }

    public void setGoodsDeliveryInfo(String goodsDeliveryInfo) {
        this.goodsDeliveryInfo = goodsDeliveryInfo;
    }

    public Long getExpectedPickupTime() {
        return expectedPickupTime;
    }

    public void setExpectedPickupTime(Long expectedPickupTime) {
        this.expectedPickupTime = expectedPickupTime;
    }

    public Long getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(Long expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public String getPoiSeq() {
        return poiSeq;
    }

    public void setPoiSeq(String poiSeq) {
        this.poiSeq = poiSeq;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getCashOnDelivery() {
        return cashOnDelivery;
    }

    public void setCashOnDelivery(BigDecimal cashOnDelivery) {
        this.cashOnDelivery = cashOnDelivery;
    }

    public BigDecimal getCashOnPickup() {
        return cashOnPickup;
    }

    public void setCashOnPickup(BigDecimal cashOnPickup) {
        this.cashOnPickup = cashOnPickup;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "CreateOrderByShopRequest [" +
                "deliveryId=" + deliveryId +
                ", orderId=" + orderId +
                ", shopId=" + shopId +
                ", deliveryServiceCode=" + deliveryServiceCode +
                ", receiverName=" + receiverName +
                ", receiverAddress=" + receiverAddress +
                ", receiverPhone=" + receiverPhone +
                ", receiverLng=" + receiverLng +
                ", receiverLat=" + receiverLat +
                ", goodsValue=" + goodsValue +
                ", goodsHeight=" + goodsHeight +
                ", goodsWidth=" + goodsWidth +
                ", goodsLength=" + goodsLength +
                ", goodsWeight=" + goodsWeight +
                ", goodsDetail=" + goodsDetail +
                ", goodsPickupInfo=" + goodsPickupInfo +
                ", goodsDeliveryInfo=" + goodsDeliveryInfo +
                ", expectedPickupTime=" + expectedPickupTime +
                ", expectedDeliveryTime=" + expectedDeliveryTime +
                ", poiSeq=" + poiSeq +
                ", note=" + note +
                ", cashOnDelivery=" + cashOnDelivery +
                ", cashOnPickup=" + cashOnPickup +
                ", invoiceTitle=" + invoiceTitle +
                ", orderType=" + orderType + "]";
    }
}
