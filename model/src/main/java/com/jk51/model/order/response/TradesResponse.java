package com.jk51.model.order.response;

/**
 * 文件名:com.jk51.model.order.response
 * 描述:订单状态变更接口response
 * 作者: hulan
 * 创建日期: 2017/2/15
 * 修改记录:
 */
public class TradesResponse {
    private String code;
    private String message;
    private String value;

    public TradesResponse(String code, String message,String value) {
        this.code = code;
        this.message = message;
        this.value = value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static TradesResponse buildSuccessReturnDto(String message,String value){
        return new TradesResponse("000",message,value);
    }

    public static TradesResponse buildFailedReturnDto(String failMessage){
        return new TradesResponse("101",failMessage,null);
    }

    public static TradesResponse buildSystemErrorReturnDto(){
        return new TradesResponse("599","System Error",null);
    }

    @Override
    public String toString() {
        return "TradesResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
