package com.jk51.modules.erpprice.domain;

import com.jk51.model.ValuedEnum;

/**
 * @author
 */
public enum ERPPriceType implements ValuedEnum {
    UNIQUE_BASE((byte)10), CITY_BASE((byte)20), AREA_BASE((byte)30), STORE_BASE((byte)40);

    private byte value;
    ERPPriceType(byte b) {
        value = b;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public byte toByte() {
        return value;
    }
}
