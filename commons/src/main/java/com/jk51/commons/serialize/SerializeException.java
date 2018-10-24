package com.jk51.commons.serialize;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 序列化或反序列化异常
 * 作者: wangzhengfei
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class SerializeException extends Exception{

    static final long serialVersionUID = -1L;

    public SerializeException() {
        super();
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }

    protected SerializeException(String message, Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
