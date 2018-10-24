package com.jk51.model.registration.requestParams;

import java.sql.Timestamp;

/**
 * filename :com.jk51.model.registration.requestParams.
 * author   :zw
 * date     :2017/4/7
 * Update   :
 */
public class TemplateSonParams {
    private Integer templateId;
    private long startTime;
    private long endTime;
    private Integer accountSource;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Integer getAccountSource() {
        return accountSource;
    }

    public void setAccountSource(Integer accountSource) {
        this.accountSource = accountSource;
    }
}
