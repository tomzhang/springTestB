package com.jk51.modules.goods.library;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:  app接口自定义返回值
 * 作者: yeah
 * 创建日期: 2017-03-09
 * 修改记录:
 */
public class ResultMap extends HashMap<String, Object> implements Serializable {
    /**
     * 正确的样式
     *
     * @param results
     * @param status
     */
    public ResultMap(String status, Map<String, Object> results) {
        this.put("status", status);
        this.put("results", results);
    }

    /**
     * 错误的样式
     *
     * @param status
     * @param errorMsg
     */
    public ResultMap(String status, String errorMsg) {
        this.put("status", status);
        this.put("errorMsg", errorMsg);
    }

    public static ResultMap successResult(Map<String, Object> map) {
        return new ResultMap("ok", map);
    }

    public static ResultMap errorResult(String errorMsg) {
        return new ResultMap("error", errorMsg);
    }

    public ResultMap() {
    }
}
