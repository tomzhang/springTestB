package com.jk51.modules.es.entity;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class GoodsInfoReq {

	@JsonProperty("dbname")
	private String dbname;//数据库名称
	
	@JsonProperty("goodsid")
	private String goodsid;//商品ID
	
	@JsonProperty("bar_code")
	private String bar_code;//条形码
	
	@JsonProperty("goods_status")
	private int goods_status;

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}

	public String getBar_code() {
		return bar_code;
	}

	public void setBar_code(String bar_code) {
		this.bar_code = bar_code;
	}
	
	public int getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public boolean isFail(){
		if(StringUtils.isBlank(this.dbname) || (StringUtils.isBlank(this.goodsid) && StringUtils.isBlank(this.bar_code))){
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "GoodsInfoReq [dbname=" + dbname + ", goodsid=" + goodsid + ", bar_code=" + bar_code + ", goods_status="
				+ goods_status + "]";
	}
	
}
