package com.jk51.modules.pay.exception;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 支付异常:用于描述与支付平台任何操作时可能引发的异常的基类
 * 作者: wangzhengfei
 * 创建日期: 2017-02-16
 * 修改记录:
 */
public class PayException extends Exception{

    public PayException() {
        super();
    }

    public PayException(String message) {
        super(message);
    }

    public PayException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayException(Throwable cause) {
        super(cause);
    }

    protected PayException(String message, Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
