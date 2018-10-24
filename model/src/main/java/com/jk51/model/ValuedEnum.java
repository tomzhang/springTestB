package com.jk51.model;

public interface ValuedEnum {
    int getValue();

    default byte toByte() {
        return (byte)getValue();
    }
}
