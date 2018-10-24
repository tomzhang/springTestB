package com.jk51.modules.es.entity;

public class ReturnDto {

    private String code;
    private String message;
    private Object value;

    public ReturnDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ReturnDto(String code, String message,Object value) {
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

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ReturnDto buildSuccessReturnDto(){
        return new ReturnDto("000","Success");
    }

    public static ReturnDto buildSuccessReturnDto(Object value){
        return new ReturnDto("000","Success",value);
    }

    public static ReturnDto buildFailedReturnDto(String failMessage){
        return new ReturnDto("101",failMessage);
    }

    public static ReturnDto buildSystemErrorReturnDto(){
        return new ReturnDto("599","System Error");
    }

    @Override
    public String toString() {
        return "ReturnDto{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
