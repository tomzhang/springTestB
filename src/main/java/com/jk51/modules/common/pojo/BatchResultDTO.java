package com.jk51.modules.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.util.Map;

/**
 * @author
 * 高德地图批量接口
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIgnoreType
public class BatchResultDTO {
    private Integer status;
    private Map<String, ?> body;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, ?> getBody() {
        return body;
    }

    public void setBody(Map<String, ?> body) {
        this.body = body;
    }
}
