package com.jk51.modules.es.entity;

public class GoodsInfoResp {

	private String code;
	
	private String message;

	private GoodsInfo gInfo;

	public GoodsInfo getgInfo() {
		return gInfo;
	}

	public void setgInfo(GoodsInfo gInfo) {
		this.gInfo = gInfo;
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


	public static GoodsInfoResp buildErrorResp(){
		GoodsInfoResp resp = new GoodsInfoResp();
		resp.setCode(Constants.ES_INTERFACE_RESP_MISSING_PARAMS);
		resp.setMessage("Missing required parameter!");
		return resp;
	}

	public static GoodsInfoResp buildSuccessResp(GoodsInfo gInfo){
		GoodsInfoResp resp = new GoodsInfoResp();
		resp.setgInfo(gInfo);
		resp.setCode(Constants.ES_INTERFACE_RESP_SUCCESS);
		resp.setMessage("success!");
		return resp;
	}
	
	public static GoodsInfoResp buildGoodsIdNotExistResp(String goodsId){
		GoodsInfoResp resp = new GoodsInfoResp();
		resp.setCode(Constants.ES_INTERFACE_RESP_GOODSID_NOT_EXIST);
		resp.setMessage("GoodsID(" + goodsId + ") is not exist!");
		return resp;
	}
	
	public static GoodsInfoResp buildSystemErrorResp(){
		GoodsInfoResp resp = new GoodsInfoResp();
		resp.setCode(Constants.ES_INTERFACE_RESP_SYSTEM_ERROR);
		resp.setMessage("System error");
		return resp;
	}
}
