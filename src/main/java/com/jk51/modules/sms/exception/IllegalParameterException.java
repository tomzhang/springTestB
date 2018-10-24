package com.jk51.modules.sms.exception;

public class IllegalParameterException extends RuntimeException{
    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException() {
        super();
    }
}
