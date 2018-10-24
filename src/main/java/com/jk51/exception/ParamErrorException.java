package com.jk51.exception;

/**
 * Created by ztq on 2018/1/4
 * Description: 参数异常
 */
public class ParamErrorException extends RuntimeException {

    private static final long serialVersionUID = -6701957160083910110L;

    public ParamErrorException() {
        super();
    }

    public ParamErrorException(String message) {
        super(message);
    }

    public ParamErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamErrorException(Throwable cause) {
        super(cause);
    }

    protected ParamErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
