package com.jk51.commons.ccprest.result;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public enum BaseResultEnum {
    SUCCESS(0000,"请求成功"),
    FAILED(1111,"请求失败");

    private Integer code;
    private String msg;

    private BaseResultEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
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
}
