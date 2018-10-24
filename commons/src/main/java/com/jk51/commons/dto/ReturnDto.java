package com.jk51.commons.dto;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/20-02-20
 * 修改记录 :
 */
public class ReturnDto {

    private String code;
    private String message;
    private Object value;
    private String status;
    private String errorMessage;

    public ReturnDto() {

    }

    public ReturnDto(String code, String message, Object value) {
        this.code = code;
        this.message = message;
        this.value = value;
    }

    private ReturnDto(String code, String message, Object value, String status) {
        this.code = code;
        this.message = message;
        this.value = value;
        this.status = status;
    }


    private ReturnDto(String status) {
        this.status = status;
    }

    private ReturnDto(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public static ReturnDto buildStatusOK() {
        return new ReturnDto("OK");
    }

    public static ReturnDto buildStatusERRO(String errorMessage) {
        return new ReturnDto("ERROR", errorMessage);
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

    public void setValue(Object value) {
        this.value = value;
    }

    public static ReturnDto buildSuccessReturnDto() {
        return new ReturnDto("000", "Success", null, "OK");
    }

    public static ReturnDto buildSuccessReturnDto(Object value) {

        return new ReturnDto("000", "Success", value, "OK");
    }
    public static ReturnDto buildSuccessReturnDtoByMsg(String msg) {

        return new ReturnDto("000", msg, null, "OK");
    }

    public static ReturnDto buildFailedReturnDto(String failMessage) {
        return new ReturnDto("101", failMessage, null, "ERROR");
    }

    public static ReturnDto buildSystemErrorReturnDto() {
        return new ReturnDto("599", "操作失败", null);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ReturnDto buildListOnEmptyFail(List s) {
        return buildListOnEmptyFail(s, "暂无数据");
    }

    /**
     * 返回一个列表 如果是空返回失败的响应
     * @param s
     * @return
     */
    public static ReturnDto buildListOnEmptyFail(List s, String message) {
        if (CollectionUtils.isEmpty(s)) {
            return buildFailedReturnDto(message);
        }

        return buildSuccessReturnDto(s);
    }

    /**
     * true返回成功 false 返回失败
     * @param b
     * @param s
     * @return
     */
    public static ReturnDto buildIfFalse(boolean b, String s) {
        if (b) {
            return buildSuccessReturnDto();
        }

        return buildFailedReturnDto(s);
    }


    public static ReturnDto buildIfNull(Object o, String s) {
        if (o != null) {
            return buildSuccessReturnDto(o);
        }

        return buildFailedReturnDto(s);
    }

    public static ReturnDto buildIfEmpty(Map m, String s) {
        if (MapUtils.isNotEmpty(m)) {
            return buildSuccessReturnDto(m);
        }

        return buildFailedReturnDto(s);
    }

    public static ReturnDto buildIfEmpty(Collection c, String s) {
        if (CollectionUtils.isNotEmpty(c)) {
            return buildSuccessReturnDto(c);
        }

        return buildFailedReturnDto(s);
    }

    @Override
    public String toString() {
        return "ReturnDto{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", value=" + value +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
