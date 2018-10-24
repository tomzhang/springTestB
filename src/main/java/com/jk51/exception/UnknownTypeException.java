package com.jk51.exception;

/**
 * Created by ztq on 2018/1/2
 * Description:
 */
public class UnknownTypeException extends RuntimeException {

    private static final long serialVersionUID = 5228538040706696513L;

    public UnknownTypeException() {
        super();
    }

    public UnknownTypeException(String message) {
        super(message);
    }

    public UnknownTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTypeException(Throwable cause) {
        super(cause);
    }

    protected UnknownTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
