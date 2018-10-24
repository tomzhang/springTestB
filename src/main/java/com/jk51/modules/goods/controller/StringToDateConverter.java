package com.jk51.modules.goods.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 自定义消息转化器
 * 作者: yeah
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class StringToDateConverter implements Converter<String, Date> {
    private static final String dateFormat      = "yyyy-MM-dd HH:mm:ss";
    private static final String shortDateFormat = "yyyy-MM-dd";

    /**
     * @see Converter#convert(Object)
     */
    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        source = source.trim();
        try {
            if (source.contains("-")) {
                SimpleDateFormat formatter;
                if (source.contains(":")) {
                    formatter = new SimpleDateFormat(dateFormat);
                } else {
                    formatter = new SimpleDateFormat(shortDateFormat);
                }
                Date dtDate = formatter.parse(source);
                return dtDate;
            } else if (source.matches("^\\d+$")) {
                Long lDate = new Long(source);
                return new Date(lDate);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", source));
        }
        throw new RuntimeException(String.format("parser %s to Date fail", source));
    }
}
