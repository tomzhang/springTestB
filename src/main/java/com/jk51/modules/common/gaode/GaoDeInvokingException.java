package com.jk51.modules.common.gaode;

/**
 * 高德调用异常
 */
public class GaoDeInvokingException extends RuntimeException {
    public GaoDeInvokingException(Exception e) {
        super(e);
    }
}
