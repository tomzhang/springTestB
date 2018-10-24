package com.jk51.modules.meituan.request;

/**
 * Created by gw on 2018/1/26.
 */
public class CheckRequest extends AbstractRequest {
	/**
	 * 取货门店 id，即合作方向美团提供的门店 id。注：测试门店的 shop_id 固定为 test_0001，仅用于对接时联调测试。
	 */
	private String shopId;
	/**
	 * 配送服务代码，详情见合同
	 * 光速达: 4001
	 * 快速达: 4011
	 * 及时达: 4012
	 * 集中送: 4013
	 * 当天达: 4021
	 */
	private Integer deliveryServiceCode;
	/**
	 * 收件人地址，最长不超过 512 个字符
	 */
	private String receiverAddress;
	/**
	 * 收件人经度（高德坐标），高德坐标 *（ 10 的六次方），如 116398419
	 */
	private Integer receiverLng;
	/**
	 * 收件人纬度（高德坐标），高德坐标 *（ 10 的六次方），如 39985005
	 */
	private Integer receiverLat;
	/**
	 * 预留字段，方便以后扩充校验规则，check_type = 1
	 */
	private Integer checkType;
	/**
	 * 模拟发单时间，时区为 GMT+8，当前距离 Epoch（1970年1月1日) 以秒计算的时间，即 unix-timestamp。
	 */
	private Long mockOrderTime;

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Integer getDeliveryServiceCode() {
		return deliveryServiceCode;
	}

	public void setDeliveryServiceCode(Integer deliveryServiceCode) {
		this.deliveryServiceCode = deliveryServiceCode;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
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

	public Integer getCheckType() {
		return checkType;
	}

	public void setCheckType(Integer checkType) {
		this.checkType = checkType;
	}

	public Long getMockOrderTime() {
		return mockOrderTime;
	}

	public void setMockOrderTime(Long mockOrderTime) {
		this.mockOrderTime = mockOrderTime;
	}

	@Override
	public String toString() {
		return "CheckRequest{" +
				"shopId='" + shopId + '\'' +
				", deliveryServiceCode=" + deliveryServiceCode +
				", receiverAddress='" + receiverAddress + '\'' +
				", receiverLng=" + receiverLng +
				", receiverLat=" + receiverLat +
				", checkType=" + checkType +
				", mockOrderTime=" + mockOrderTime +
				'}';
	}
}
