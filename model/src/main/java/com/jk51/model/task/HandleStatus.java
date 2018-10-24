package com.jk51.model.task;

import com.jk51.model.ValuedEnum;

public enum HandleStatus implements ValuedEnum {
    SUCCESS(10), FAIL(20);

    private int value;

    HandleStatus(int _value) {
        value = _value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
