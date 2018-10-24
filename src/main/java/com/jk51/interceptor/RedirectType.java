package com.jk51.interceptor;

/**
 * 文件名:com.jk51.interceptor.
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-02-03
 * 修改记录:
 */
public enum RedirectType {

    NONE(null),

    REDIRECT("redirect"),

    FORWARD("forward");

    private String value;

    private RedirectType(String value){
        this.value = value;
    }

    public static RedirectType of(String value){
        RedirectType[] types = RedirectType.values();
        for(RedirectType type : types){
            if(type.toValue() == value || (value != null && value.equals(type.toValue()))){
                return type;
            }
        }
        return NONE;
    }

    public String toValue(){
        return this.value;
    }
}
