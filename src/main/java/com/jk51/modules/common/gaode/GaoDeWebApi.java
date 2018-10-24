package com.jk51.modules.common.gaode;

import java.util.List;

/**
 * @author
 * 高德web api
 */
public interface GaoDeWebApi<T> {
    /**
     * 获取接口地址 不包含host
     * @return
     */
    String getPath();

    /**
     * 获取接口参数
     * @return
     */
    List<GaoDeWebParameter> getQueryNamesAndValues();

    /**
     * 获取接口结果
     * @param res
     * @return
     */
    T getResult(String res);
}
