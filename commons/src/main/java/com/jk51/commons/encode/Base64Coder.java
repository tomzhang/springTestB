package com.jk51.commons.encode;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-01
 * 修改记录:
 */
public class Base64Coder {
private static String DEFAULT_CHARSET="UTF-8";
	/**
	 * 使用Base64的方式加密
	 * @param data 需要加密的数据
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String data) throws UnsupportedEncodingException {
		return Base64.encode(data.getBytes(DEFAULT_CHARSET));
	}

	/**
	 * 使用Base64的方式解密
	 * @param data 需要解密的数据
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String data) throws UnsupportedEncodingException {
		return new String(Base64.decode(data), DEFAULT_CHARSET);
	}

    /**
     *  判断参数是否进行转码
     * @param data 需要判断的参数
     * @return
     */
    public static Boolean isBase64(String data){
        String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return data.matches(regex);
    }
}
