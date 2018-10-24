package com.jk51.model.task;

import com.jk51.model.ValuedEnum;

public enum MsgType implements ValuedEnum {
    COUNT(10);

    private int value;


    MsgType(int _value) {
        value = _value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
