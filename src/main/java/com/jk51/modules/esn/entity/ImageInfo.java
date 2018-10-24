package com.jk51.modules.esn.entity;

public class ImageInfo {

	private String goods_id;

	private String host_id;
	
	private String hash;
	
	private Integer is_default;

	public String getHost_id() {
		return host_id;
	}

	public void setHost_id(String host_id) {
		this.host_id = host_id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Integer getIs_default() {
		return is_default;
	}

	public void setIs_default(Integer is_default) {
		this.is_default = is_default;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String toString(){
		
		return this.host_id + "|" + this.hash + "|" + this.is_default;
	}
}
