package com.jk51.modules.common.gaode;

import com.jk51.commons.json.JacksonUtils;
import okhttp3.HttpUrl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * @author
 * @param <T>
 */
public abstract class AbstractWebApi<T> implements GaoDeWebApi<T> {
    private String res;

    public void setRes(String res) {
        this.res = res;
    }

    @Override
    public T getResult(String res) {
        Class <T> clazz = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            return JacksonUtils.json2pojo(res, clazz);
        } catch (Exception e) {
            throw new GaoDeInvokingException(e);
        }
    }

    public T getResult() {
        return getResult(res);
    }

    public HttpUrl toHttpUrl(String host, String key) {
        List<GaoDeWebParameter> queryNamesAndValues = getQueryNamesAndValues();
        HttpUrl.Builder builder = HttpUrl.parse(host + getPath()).newBuilder();

        for (GaoDeWebParameter parameter : queryNamesAndValues) {
            builder.addQueryParameter(parameter.getKey(), parameter.getValue());
        }

        builder.addQueryParameter("key", key);

        return builder.build();
    }

    public String toUrlPath(String key) {
        List<GaoDeWebParameter> parameters = getQueryNamesAndValues();
        parameters.add(new GaoDeWebParameter("key", key));

        return getPath() + "?" + parameters.stream().map(GaoDeWebParameter::toString).collect(joining("&"));
    }
}
