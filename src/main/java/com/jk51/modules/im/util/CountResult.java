package com.jk51.modules.im.util;

import com.jk51.commons.date.DateUtils;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:统计返回类
 * 作者: gaojie
 * 创建日期: 2017-06-22
 * 修改记录:
 */
public class CountResult implements Comparable<CountResult>{


    private String day_str;
    private Object value;

    public CountResult(String day_str,Object value){
        this.day_str = day_str;
        this.value = value;
    }

    public CountResult(){

    }
    public String getDay_str() {
        return day_str;
    }

    public void setDay_str(String day_str) {
        this.day_str = day_str;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int compareTo(CountResult o) {

        if(o==null){
            return 1;
        }

        return DateUtils.parseDate(day_str,"yyy-MM-dd").compareTo(DateUtils.parseDate(o.day_str,"yyy-MM-dd"));
    }
}
