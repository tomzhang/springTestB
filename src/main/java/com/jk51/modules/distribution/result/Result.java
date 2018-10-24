package com.jk51.modules.distribution.result;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 10:11
 * 修改记录:
 */
public class Result implements Serializable {

    private String code;
    private Integer status;
    private String msg;
    private Object data;

    public static String SUCCESS = "success";
    public static String FAIL = "fail";
    public static Integer SUCCESS_STATUS = Integer.valueOf(1);
    public static Integer FAIL_STATUS = Integer.valueOf(0);

    public Result(String code, Integer status, String msg, Object data) {
        this.code = code;
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Result() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static Result success(Object object){

        return new Result(SUCCESS,SUCCESS_STATUS,SUCCESS,object);
    }

    public static Result success(){
       return success(null);
    }

    public static Result fail(String message){
        return new Result(FAIL,FAIL_STATUS,message,null);
    }

    public static Result fail(Object data){
        return new Result(FAIL,FAIL_STATUS,"",data);
    }

    public static Result fail(String message, Object data){
        return new Result(FAIL, FAIL_STATUS, message, data);
    }

}

