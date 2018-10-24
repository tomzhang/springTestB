package com.jk51.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.math.NumberUtils;

import java.util.HashMap;
import java.util.Objects;

public class JKHashMap<K, V> extends HashMap<K, V> {
    public int getInt(Object key) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).intValue();
        }

        return NumberUtils.toInt(Objects.toString(v));
    }

    public int getInt(Object key, int defaultValue) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).intValue();
        }

        return NumberUtils.toInt(Objects.toString(v), defaultValue);
    }

    public long getLong(Object key) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).longValue();
        }

        return NumberUtils.toLong(Objects.toString(v));
    }

    public long getLong(Object key, long defaultValue) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).longValue();
        }

        return NumberUtils.toLong(Objects.toString(v), defaultValue);
    }

    public float getFloat(Object key) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).floatValue();
        }

        return NumberUtils.toFloat(Objects.toString(v));
    }

    public float getFloat(Object key, float defaultValue) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).floatValue();
        }

        return NumberUtils.toFloat(Objects.toString(v), defaultValue);
    }

    public double getDouble(Object key) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }

        return NumberUtils.toDouble(Objects.toString(v));
    }

    public double getDouble(Object key, double defaultValue) {
        V v = get(key);

        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }

        return NumberUtils.toDouble(Objects.toString(v), defaultValue);
    }

    public String getString(Object key) {
        V v = get(key);

        return Objects.toString(v);
    }

    public String getString(Object key, String defaultValue) {
        V v = get(key);

        return Objects.toString(v, defaultValue);
    }

    public <T> T getType(Object key, Class<T> clazz) {
        V v = get(key);
        ObjectMapper objectMapper = new ObjectMapper();

        if (clazz.isInstance(v)) {
            return (T)v;
        }

        return objectMapper.convertValue(v, clazz);
    }

    public <T> T getType(Object key, TypeReference<T> toValueTypeRef) {
        V v = get(key);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.convertValue(v, toValueTypeRef);
    }
}
