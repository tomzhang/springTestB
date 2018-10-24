package com.jk51.modules.es.entity;

import java.util.List;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName AppGoodsInfosResp
 * @Description APP搜索商品返回的实体
 * @Date 2018-06-25 9:55
 */
public class AppGoodsInfosResp {

    private String code;

    private String message;

    private long total;

    private List<AppGoodsInfo> gInfos;

    public AppGoodsInfosResp() {
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

    public List<AppGoodsInfo> getgInfos() {
        return gInfos;
    }

    public void setgInfos(List<AppGoodsInfo> gInfos) {
        this.gInfos = gInfos;
    }

    public static AppGoodsInfosResp buildErrorResp(){
        AppGoodsInfosResp goodsInfosResp=new AppGoodsInfosResp();
        goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_MISSING_PARAMS);
        goodsInfosResp.setMessage("missing required parameter!");
        goodsInfosResp.setTotal(0);
        return goodsInfosResp;
    }

    public static AppGoodsInfosResp buildSystemErrorResp(){
        AppGoodsInfosResp goodsInfosResp=new AppGoodsInfosResp();
        goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_SYSTEM_ERROR);
        goodsInfosResp.setMessage("system error!");
        goodsInfosResp.setTotal(0);
        return goodsInfosResp;
    }

    public static AppGoodsInfosResp buildNotExistResp(){
        AppGoodsInfosResp goodsInfosResp=new AppGoodsInfosResp();
        goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_GOODSID_NOT_EXIST);
        goodsInfosResp.setMessage("index is not exist!");
        goodsInfosResp.setTotal(0);
        return goodsInfosResp;
    }

    public static AppGoodsInfosResp buildSuccess(List<AppGoodsInfo> gInfos, long rl){
        AppGoodsInfosResp goodsInfosResp=new AppGoodsInfosResp();
        goodsInfosResp.setCode(Constants.ES_INTERFACE_RESP_SUCCESS);
        goodsInfosResp.setMessage("success");
        goodsInfosResp.setTotal(rl);
        goodsInfosResp.setgInfos(gInfos);
        return goodsInfosResp;
    }
}
