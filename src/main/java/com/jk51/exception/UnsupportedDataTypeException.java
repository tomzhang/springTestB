package com.jk51.exception;

/**
 * Created by ztq on 2018/1/2
 * Description:
 */
public class UnsupportedDataTypeException extends Exception {
    private static final long serialVersionUID = 8343216245968770245L;

    public UnsupportedDataTypeException() {
        super();
    }

    public UnsupportedDataTypeException(String message) {
        super(message);
    }

    public UnsupportedDataTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedDataTypeException(Throwable cause) {
        super(cause);
    }

    protected UnsupportedDataTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
