package com.jk51.modules.es.entity;

import java.util.List;

public class GoodsInfosResp {

	private String code;
	
	private String message;
	
	private long total;
	
	private List<GoodsInfos> gInfos;

	public GoodsInfosResp() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<GoodsInfos> getgInfos() {
		return gInfos;
	}

	public void setgInfos(List<GoodsInfos> gInfos) {
		this.gInfos = gInfos;
	}

	public static GoodsInfosResp buildErrorResp(){
		GoodsInfosResp goodsInfosResp=new GoodsInfosResp();
		goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_MISSING_PARAMS);
		goodsInfosResp.setMessage("missing required parameter!");
		goodsInfosResp.setTotal(0);
		return goodsInfosResp;
	}

	public static GoodsInfosResp buildSystemErrorResp(){
		GoodsInfosResp goodsInfosResp=new GoodsInfosResp();
		goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_SYSTEM_ERROR);
		goodsInfosResp.setMessage("system error!");
		goodsInfosResp.setTotal(0);
		return goodsInfosResp;
	}

	public static GoodsInfosResp buildNotExistResp(){
		GoodsInfosResp goodsInfosResp=new GoodsInfosResp();
		goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_GOODSID_NOT_EXIST);
		goodsInfosResp.setMessage("index is not exist!");
		goodsInfosResp.setTotal(0);
		return goodsInfosResp;
	}

	public static GoodsInfosResp buildSuccess(List<GoodsInfos> gInfos, long rl){
		GoodsInfosResp goodsInfosResp=new GoodsInfosResp();
		goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_SUCCESS);
		goodsInfosResp.setMessage("success");
		goodsInfosResp.setTotal(rl);
		goodsInfosResp.setgInfos(gInfos);
		return goodsInfosResp;
	}
	
}
