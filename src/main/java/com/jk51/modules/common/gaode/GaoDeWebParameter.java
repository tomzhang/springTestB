package com.jk51.modules.common.gaode;

import okhttp3.HttpUrl;

import java.util.List;

/**
 * @author
 * 高德WEB接口url参数
 */
public class GaoDeWebParameter {
    private final String key;
    private final String value;

    public GaoDeWebParameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
