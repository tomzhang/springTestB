package com.jk51.modules.distribution.result;

import java.io.Serializable;

public class Resultful implements Serializable {
    private Integer code;
    private String msg;
    private Object data;

    public static final Integer SUCCESS = 200;
    public static final Integer FAILED = 500;
    public Resultful(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Resultful() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Resultful buildFailedResult(String failMessage){
        return new Resultful(FAILED,failMessage,null);
    }

    public static Resultful buildSuccessResult(Object value){
        return new Resultful(SUCCESS,"success",value);
    }
}
